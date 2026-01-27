package com.xlf.utility.webflux.aspect;

import com.xlf.utility.ErrorCode;
import com.xlf.utility.app.aspect.IDebugAspect;
import com.xlf.utility.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * WebFlux版本的Debug切面实现类
 * <p>
 * 该类实现了IDebugAspect接口，专门为Spring WebFlux响应式环境设计。
 * 在基础Debug环境检查功能基础上，增加了响应式Web请求相关的调试信息记录：
 * - 记录ServerWebExchange的详细信息（请求方法、路径、客户端IP等）
 * - 支持响应式编程模式的调试功能
 * - 处理WebFlux特定的上下文信息
 * <p>
 * WebFlux版本包含响应式Web请求上下文的记录功能，支持ServerWebExchange上下文。
 * <p>
 * NOTICE: 使用该类需要确保配置了Spring AOP支持和Spring WebFlux环境。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public class DebugAspectHandler implements IDebugAspect {

    private static final Logger log = LoggerFactory.getLogger(DebugAspectHandler.class);

    /**
     * WebFlux增强版本的Debug环境检查
     * <p>
     * 在基础环境检查基础上，增加WebFlux特定的功能：
     * 1. 记录ServerWebExchange的详细信息
     * 2. 输出客户端IP地址和请求路径
     * 3. 提供响应式编程环境下的调试上下文信息
     * <p>
     * 如果无法获取WebFlux请求上下文，则回退到基础实现。
     *
     * @throws BusinessException 如果当前环境未处于开发模式，将抛出业务异常
     */
    @Override
    public void checkDebugController() {
        log.debug("DebugController 被调用，正在检查WebFlux运行环境");

        if (log.isDebugEnabled()) {
            log.debug("当前运行环境为开发模式，允许 DebugController 的调用");
        } else {
            log.warn("DebugController 在非开发环境中被调用，请检查配置");
            throw new BusinessException("非 Debug 环境禁止调用该接口内容", ErrorCode.UNAUTHORIZED);
        }
    }

    /**
     * WebFlux响应式版本的Debug环境检查
     * <p>
     * 该方法专门设计用于响应式编程环境，支持在Mono/Flux链中进行Debug检查。
     * 支持从ServerWebExchange中提取请求信息并记录调试日志。
     *
     * @param exchange ServerWebExchange对象，包含请求和响应信息
     * @return Mono<Void> 响应式流，用于链式调用
     * @throws BusinessException 如果当前环境未处于开发模式，将抛出业务异常
     */
    public Mono<Void> checkDebugControllerReactive(ServerWebExchange exchange) {
        return Mono.fromRunnable(() -> {
            log.debug("ReactiveDebugController 被调用，正在检查WebFlux运行环境");

            // 记录ServerWebExchange详细信息
            logServerWebExchangeInfo(exchange);

            if (log.isDebugEnabled()) {
                log.debug("当前运行环境为开发模式，允许 ReactiveDebugController 的调用");
            } else {
                log.warn("ReactiveDebugController 在非开发环境中被调用，请检查配置");
                throw new BusinessException("非 Debug 环境禁止调用该接口内容", ErrorCode.UNAUTHORIZED);
            }
        });
    }

    /**
     * 用于响应式流中的Debug检查转换函数
     * <p>
     * 该方法返回一个Function，可以在Mono/Flux的transformDeferred中使用，
     * 用于在响应式流的执行过程中进行Debug环境检查。
     *
     * @return Function<Mono<T>, Mono<T>> 用于Mono变换的函数
     */
    public <T> Function<Mono<T>, Mono<T>> debugCheckTransformer() {
        return mono -> mono.doOnSubscribe(subscription -> {
            log.debug("响应式流Debug检查被触发");

            if (log.isDebugEnabled()) {
                log.debug("响应式环境为开发模式，允许继续执行");
            } else {
                log.warn("响应式流在非开发环境中被调用");
                throw new BusinessException("非 Debug 环境禁止调用该接口内容", ErrorCode.UNAUTHORIZED);
            }
        });
    }

    /**
     * 记录ServerWebExchange详细信息
     *
     * @param exchange ServerWebExchange对象
     */
    private void logServerWebExchangeInfo(ServerWebExchange exchange) {
        if (exchange == null || !log.isDebugEnabled()) {
            return;
        }

        try {
            String method = exchange.getRequest().getMethod().name();
            String path = exchange.getRequest().getPath().pathWithinApplication().value();
            String queryString = exchange.getRequest().getURI().getQuery();
            String remoteAddress = getClientIpAddress(exchange);

            StringBuilder logMessage = new StringBuilder();
            logMessage.append(String.format("Debug请求详情 - %s %s", method, path));

            if (queryString != null && !queryString.isEmpty()) {
                logMessage.append("?").append(queryString);
            }

            logMessage.append(String.format(" | 客户端IP: %s", remoteAddress));

            // 检查外部trace ID
            String externalTraceId = getExternalTraceId(exchange);
            if (externalTraceId != null) {
                logMessage.append(String.format(" | 外部TraceID: %s", externalTraceId));
            }

            log.debug(logMessage.toString());

            // 记录User-Agent信息
            String userAgent = exchange.getRequest().getHeaders().getFirst("User-Agent");
            if (userAgent != null && !userAgent.isEmpty()) {
                log.debug("User-Agent: {}", userAgent.length() > 100 ?
                        userAgent.substring(0, 100) + "..." : userAgent);
            }

        } catch (Exception e) {
            log.debug("提取请求信息失败: {}", e.getMessage());
        }
    }

    /**
     * 获取客户端真实IP地址
     *
     * @param exchange ServerWebExchange对象
     * @return 客户端IP地址
     */
    private String getClientIpAddress(ServerWebExchange exchange) {
        if (exchange == null) {
            return "unknown";
        }

        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String ip = exchange.getRequest().getHeaders().getFirst(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For可能包含多个IP，取第一个
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        return exchange.getRequest().getRemoteAddress() != null ?
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }

    /**
     * 获取外部TraceID
     *
     * @param exchange ServerWebExchange对象
     * @return 外部TraceID，如果没有则返回null
     */
    private String getExternalTraceId(ServerWebExchange exchange) {
        if (exchange == null) {
            return null;
        }

        // 按优先级检查多个可能的trace header
        String[] traceHeaders = {"X-Trace-Id", "X-Context-Id", "X-Request-Id", "Trace-Id"};

        for (String header : traceHeaders) {
            String value = exchange.getRequest().getHeaders().getFirst(header);
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }

        return null;
    }
}
