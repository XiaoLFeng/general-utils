package com.xlf.utility.webflux.filter;

import com.xlf.utility.constant.ContextConstant;
import com.xlf.utility.constant.HttpHeaderConstant;
import com.xlf.utility.utility.UrlMatchUtil;
import com.xlf.utility.webflux.properties.ContextWebFluxProperties;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFlux请求上下文过滤器
 * <p>
 * 实现WebFilter接口，专为Spring WebFlux环境设计，适合Gateway等场景。
 * 集成上下文和时间统计功能：
 * - 支持从请求头传递的上下文ID（微服务间传递）
 * - 自动生成UUIDv4作为上下文标识
 * - 自动记录请求开始时间，支持执行时间统计
 * - 在响应头中返回上下文ID
 * - 同时使用ThreadLocal和Reactor Context
 * - 替代原有的TimingAspectHandler，统一管理
 * - 支持通过配置排除指定 URL（不生成上下文）
 * <p>
 * 优先级设计：如果请求头中包含X-Context-UUID，优先使用该ID作为context；
 * 否则自动生成新的UUIDv4。
 * <p>
 * 上下文跳过规则（按优先级）：
 * <ol>
 *     <li>OPTIONS 请求直接放行</li>
 *     <li>URL 在排除列表中（通过配置文件 {@code utility.context.webflux.exclude-urls}）</li>
 * </ol>
 * <p>
 * NOTICE: 该过滤器在Spring WebFlux环境中生效，对所有HTTP请求进行处理。
 * WebFlux 不支持 {@code @IgnoreContext} 注解，请使用配置文件的 {@code exclude-urls} 替代。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@AllArgsConstructor
@SuppressWarnings("unused")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ContextFilter implements WebFilter {
    private static final Logger log = LoggerFactory.getLogger(ContextFilter.class);

    /**
     * WebFlux上下文配置属性
     */
    private final ContextWebFluxProperties contextProperties;

    /**
     * 过滤器核心处理方法
     * <p>
     * 处理流程：
     * 1. OPTIONS 请求直接放行
     * 2. 检查 URL 是否在排除列表中（配置文件）
     * 3. 从请求头中提取外部UUID（如果存在）
     * 4. 初始化上下文信息
     * 5. 将上下文ID写入Reactor Context
     * 6. 在响应头中返回上下文ID
     * 7. 在请求头中写入上下文ID（Gateway场景传递给下游服务）
     * 8. 处理完成后自动清理
     * <p>
     * 支持微服务间UUID传递，优先使用Header中的UUID。
     *
     * @param exchange 服务器Web交换对象
     * @param chain    过滤器链
     * @return Mono&lt;Void&gt; 异步处理结果
     */
    @Override
    public @NotNull Mono<Void> filter(@NotNull ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        // 1. OPTIONS 请求直接放行
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // 2. 检查 URL 是否在排除列表中
        String requestPath = exchange.getRequest().getPath().value();
        if (UrlMatchUtil.isUrlExcluded(requestPath, contextProperties.getExcludeUrls())) {
            log.debug("URL [{}] 在排除列表中，跳过上下文处理", requestPath);
            return chain.filter(exchange);
        }

        // 3. 根据配置决定是否从请求头中获取外部UUID
        String externalUuid = null;
        if (contextProperties.isEnableInput()) {
            externalUuid = extractUuidFromRequest(exchange);
        }

        String contextId;
        if (externalUuid != null && !externalUuid.trim().isEmpty()) {
            contextId = externalUuid.trim();
        } else {
            contextId = java.util.UUID.randomUUID().toString();
            log.debug("生成新UUIDv4写入ThreadLocal: {}", contextId);
        }

        // 4. 在响应头中返回上下文ID，方便下游服务或客户端使用
        exchange.getResponse().getHeaders().set(HttpHeaderConstant.X_CONTEXT_UUID, contextId);
        MDC.put("CONTEXT_ID", contextId);

        // 5. 根据配置决定是否向下游服务传递上下文ID
        ServerWebExchange targetExchange = exchange;
        if (contextProperties.isEnableOutput()) {
            // 为Gateway场景：在请求头中写入上下文ID，传递给下游微服务
            targetExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header(HttpHeaderConstant.X_CONTEXT_UUID, contextId)
                            .build())
                    .build();
            log.info("创建上下文信息 [{}]", contextId);
        } else {
            log.debug("配置禁用Output，不向下游传递上下文ID: {}", contextId);
        }

        long startTime = System.currentTimeMillis();

        // 6. 将上下文写入 Reactor Context 并处理请求
        return chain.filter(targetExchange)
                .contextWrite(ctx -> ctx.put(ContextConstant.CONTEXT_KEY, contextId).put(ContextConstant.START_TIME_KEY, startTime))
                .doFinally(signal -> {
                    // 7. 清理 MDC
                    log.debug("清理上下文: {}", contextId);
                    MDC.remove("CONTEXT_ID");
                });
    }

    /**
     * 从ServerWebExchange请求头中提取UUID
     * <p>
     * 只从标准的 X-Context-UUID 请求头中提取UUID。
     * 这是微服务间传递上下文的标准方式。
     *
     * @param exchange ServerWebExchange对象
     * @return 提取的UUID，如果未找到则返回null
     */
    @Contract("null -> null")
    private String extractUuidFromRequest(ServerWebExchange exchange) {
        if (exchange == null) {
            return null;
        }

        String uuid = exchange.getRequest().getHeaders().getFirst(HttpHeaderConstant.X_CONTEXT_UUID);
        if (uuid != null && !uuid.trim().isEmpty()) {
            log.debug("从请求头 {} 提取UUID: {}", HttpHeaderConstant.X_CONTEXT_UUID, uuid.trim());
            return uuid.trim();
        }
        return null;
    }
}
