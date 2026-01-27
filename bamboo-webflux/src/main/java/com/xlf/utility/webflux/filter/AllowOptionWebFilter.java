package com.xlf.utility.webflux.filter;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 允许 OPTIONS 请求的WebFlux过滤器类。
 * <p>
 * 此过滤器类用于处理 HTTP OPTIONS 预检请求，通过直接返回成功响应（HTTP 200）来允许客户端进行跨域请求。
 * OPTIONS 请求通常由浏览器在进行某些跨域请求时自动发送，用于检查服务器是否允许该请求。
 * <p>
 * WebFlux版本使用响应式编程模型，所有操作都是非阻塞的。
 * 该过滤器通过检测请求方法是否为 {@code OPTIONS}，如果是，则直接返回成功状态，无需继续处理。
 *
 * <p>
 * 适用场景：
 * <ul>
 *     <li>支持前端跨域请求的预检处理</li>
 *     <li>简化 RESTful API 的 OPTIONS 请求处理</li>
 *     <li>避免在业务逻辑中单独处理 OPTIONS 请求</li>
 * </ul>
 *
 * <p>
 * NOTICE: 该过滤器应该配合 CORS 过滤器使用，确保跨域请求的完整支持。建议将此过滤器的执行顺序设置为较高优先级。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class AllowOptionWebFilter implements WebFilter {
    private static final Logger log = LoggerFactory.getLogger(AllowOptionWebFilter.class);

    @Override
    public @NotNull Mono<Void> filter(@NotNull ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            log.debug("WebFlux过滤器 AllowOptionWebFilter 执行「处理 OPTIONS 预检请求」");

            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }
}
