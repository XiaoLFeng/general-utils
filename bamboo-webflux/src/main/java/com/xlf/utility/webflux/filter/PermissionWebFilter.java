package com.xlf.utility.webflux.filter;

import com.xlf.utility.annotations.NeedPermission;
import com.xlf.utility.exception.library.DeveloperException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * 权限过滤处理器类 (WebFlux版本)。
 * <p>
 * 该类实现了 {@link WebFilter} 接口，用于对 HTTP 请求进行权限检查，以确保只有满足权限要求的
 * 请求才能继续运行到下一个过滤器或处理器。它通常用于保护敏感资源或接口，旨在实现基于请求上下文的动态
 * 权限校验。
 * <p>
 * WebFlux版本使用响应式编程模型，所有操作都是非阻塞的。
 * 在请求到达实际业务逻辑之前，此过滤器会执行指定的权限验证逻辑。如果请求未通过验证，则可在此中止请求
 * 并返回适当的响应状态和错误信息。
 * <p>
 * 该过滤器会检查请求对应的方法是否标记了 {@link NeedPermission} 注解，如果标记了则进行权限验证。
 *
 * <p>
 * NOTICE: 在实现权限检查时，需要编写具体的校验逻辑（如解析令牌、验证用户身份等）。该逻辑需要避免引发
 * 性能问题，比如长时间的阻塞验证操作。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public abstract class PermissionWebFilter implements WebFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(PermissionWebFilter.class);
    private final RequestMappingHandlerMapping handlerMapping;

    public PermissionWebFilter(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    /**
     * 权限过滤器核心逻辑的执行方法
     * <p>
     * 此方法用于对进入的 HTTP 请求进行权限检查和过滤。通过检测目标请求的处理方法上是否
     * 标注了权限验证注解 {@code NeedPermission}，决定是否允许请求继续被处理。如果权限不足，
     * 方法会返回未授权的响应，阻止后续流程。
     * <p>
     * 主要逻辑步骤：
     * <ul>
     *   <li>根据请求解析目标方法信息。</li>
     *   <li>若目标方法标注了权限需求注解，则提取权限标识。</li>
     *   <li>验证用户是否具备该权限，若权限不足则返回错误响应。</li>
     *   <li>若权限验证通过，则放行请求到后续处理链。</li>
     * </ul>
     *
     * @param exchange  当前的服务器Web交换对象
     * @param chain     WebFilter链，用于将请求传递到下一个处理端
     * @return 包装在Mono中的过滤结果
     */
    @Override
    public @NotNull Mono<Void> filter(@NotNull ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        return getHandlerMethod(exchange.getRequest())
                .map(handler -> {
                    Method method = handler.getMethod();
                    if (method.isAnnotationPresent(NeedPermission.class)) {
                        log.debug("WebFlux过滤器 PermissionWebFilter 执行「权限验证」");
                        String annotationPermission = Arrays.toString(Optional.of(method.getAnnotation(NeedPermission.class))
                                .filter(value -> value.value() != null && !Arrays.stream(value.value()).allMatch(String::isEmpty))
                                .map(NeedPermission::value)
                                .orElseThrow(() -> new DeveloperException(
                                        "未找到所需的权限标识，请检查方法注解配置",
                                        DeveloperException.ErrorType.CONFIGURATION_ERROR
                                )));
                        // 权限验证逻辑
                        return this.hasPermissionCheck(exchange, annotationPermission);
                    }
                    return Mono.empty();
                })
                .orElse(Mono.empty())
                .then(chain.filter(exchange));
    }

    /**
     * 获取当前请求对应的处理方法。
     * <p>
     * WebFlux版本的处理方法获取逻辑，通过RequestMappingHandlerMapping查找匹配的HandlerMethod
     *
     * @param request 当前的服务器HTTP请求对象
     * @return 匹配的HandlerMethod，如果未找到则返回Optional.empty()
     */
    private Optional<HandlerMethod> getHandlerMethod(ServerHttpRequest request) {
        try {
            Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
            for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
                RequestMappingInfo mappingInfo = entry.getKey();
                if (mappingInfo != null) {
                    // 简化的匹配逻辑，实际应用中可能需要更复杂的匹配
                    if (mappingInfo.getPatternsCondition().getPatterns().stream()
                            .anyMatch(pattern -> request.getPath().pathWithinApplication().value().matches(pattern.getPatternString()))) {
                        return Optional.of(entry.getValue());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("获取HandlerMethod失败", e);
        }
        return Optional.empty();
    }

    /**
     * 检查用户是否具有指定权限 (WebFlux版本)
     * <p>
     * 此方法用于验证客户端请求中，当前用户是否具备执行某操作所需要的权限。
     * 通过接受 {@code ServerWebExchange} 来解析用户的会话信息或认证信息，
     * 并与提供的 {@code requiredPermission} 权限标识进行比较，判断是否允许。
     * <p>
     * WebFlux版本返回Mono包装的结果，支持响应式处理。
     *
     * @param exchange          当前的服务器Web交换对象
     * @param requestPermission 需要验证的权限标识
     * @return 包装在Mono中的权限检查结果，如果权限不足应该返回包含错误信息的Mono
     */
    public abstract Mono<Void> hasPermissionCheck(ServerWebExchange exchange, String requestPermission);

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
