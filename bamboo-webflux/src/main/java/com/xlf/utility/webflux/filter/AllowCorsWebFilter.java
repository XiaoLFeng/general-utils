package com.xlf.utility.webflux.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.constant.HttpHeaderConstant;
import com.xlf.utility.webflux.holder.ContextHolder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * 允许跨域资源共享（CORS）的WebFlux过滤器类。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AllowCorsWebFilter implements WebFilter {
    private static final Logger log = LoggerFactory.getLogger(AllowCorsWebFilter.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String[] allowCorsDomain;
    private final String[] allowMethods;
    private final String[] accessHeaders;

    public AllowCorsWebFilter(String[] domains, String[] methods, String[] headers) {
        this.allowCorsDomain = Optional.ofNullable(domains)
                .orElse(new String[]{"localhost"});
        this.allowMethods = Optional.ofNullable(methods)
                .orElse(new String[]{"GET", "POST", "PUT", "DELETE", "OPTIONS"});
        this.accessHeaders = Optional.ofNullable(headers)
                .orElse(new String[]{"Content-Type", "Authorization", "X-Requested-With"});
    }

    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        log.debug("WebFlux过滤器 AllowCorsWebFilter 执行「处理跨域请求」");

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String host = request.getHeaders().getFirst("Host");
        if (Optional.ofNullable(host)
                .filter(this::isAllowedOrigin)
                .isPresent()) {
            response.getHeaders().set(HttpHeaderConstant.ACCESS_CONTROL_ALLOW_ORIGIN, host);
            this.setCorsHeaders(response);
        } else {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

            return Mono.deferContextual(contextView -> {
                String contextId = ContextHolder.getContextId(contextView);
                Long duration = ContextHolder.getDuration(contextView);

                BaseResponse<Object> errorResponse = new BaseResponse<>(
                        contextId,
                        ErrorCode.HEADER_MISSING.getOutput(),
                        ErrorCode.HEADER_MISSING.getCode(),
                        ErrorCode.HEADER_MISSING.getMessage(),
                        "缺失 Host 请求头或域名不在白名单中",
                        duration,
                        null
                );

                try {
                    String responseBody = OBJECT_MAPPER.writeValueAsString(errorResponse);
                    byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
                    return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
                } catch (Exception e) {
                    log.warn("序列化错误响应失败 | {}", e.getMessage());
                    return response.setComplete();
                }
            });
        }

        response.getHeaders().set(HttpHeaderConstant.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        return chain.filter(exchange);
    }

    @Contract(pure = true)
    private boolean isAllowedOrigin(String origin) {
        for (String domain : allowCorsDomain) {
            if ("*".equalsIgnoreCase(domain)) {
                return true;
            }
            if (domain.equalsIgnoreCase(origin)) {
                return true;
            }
        }
        return false;
    }

    private void setCorsHeaders(@NotNull ServerHttpResponse response) {
        StringBuilder allowedHeaders = new StringBuilder();
        for (String header : accessHeaders) {
            if (!allowedHeaders.isEmpty()) {
                allowedHeaders.append(", ");
            }
            allowedHeaders.append(header);
        }
        StringBuilder allowedMethods = new StringBuilder();
        for (String method : this.allowMethods) {
            if (!allowedMethods.isEmpty()) {
                allowedMethods.append(", ");
            }
            allowedMethods.append(method);
        }
        response.getHeaders().set(HttpHeaderConstant.ACCESS_CONTROL_ALLOW_HEADERS, allowedHeaders.toString());
        response.getHeaders().set(HttpHeaderConstant.ACCESS_CONTROL_ALLOW_METHODS, allowedMethods.toString());
        response.getHeaders().set(HttpHeaderConstant.ACCESS_CONTROL_MAX_AGE, "3600");
        response.getHeaders().set(HttpHeaderConstant.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    }
}
