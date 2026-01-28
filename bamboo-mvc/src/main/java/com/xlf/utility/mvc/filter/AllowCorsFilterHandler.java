package com.xlf.utility.mvc.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.constant.HttpHeaderConstant;
import com.xlf.utility.mvc.holder.ContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * 允许跨域资源共享（CORS）的过滤器类。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AllowCorsFilterHandler extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(AllowCorsFilterHandler.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String[] allowCorsDomain;
    private final String[] allowMethods;
    private final String[] accessHeaders;

    public AllowCorsFilterHandler(String[] domains, String[] methods, String[] headers) {
        this.allowCorsDomain = Optional.ofNullable(domains)
                .orElse(new String[]{"localhost"});
        this.allowMethods = Optional.ofNullable(methods)
                .orElse(new String[]{"GET", "POST", "PUT", "DELETE", "OPTIONS"});
        this.accessHeaders = Optional.ofNullable(headers)
                .orElse(new String[]{"Content-Type", "Authorization", "X-Requested-With"});
    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        log.debug("过滤器 AllowCorsFilter 执行「处理跨域请求」");

        String host = request.getHeader("Host");
        if (host != null && this.isAllowedOrigin(host)) {
            response.setHeader(HttpHeaderConstant.ACCESS_CONTROL_ALLOW_ORIGIN, host);
            this.setCorsHeaders(response);
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");

            BaseResponse<Void> body = new BaseResponse<>(
                    ContextHolder.getContextId(),
                    ErrorCode.HEADER_MISSING.getOutput(),
                    ErrorCode.HEADER_MISSING.getCode(),
                    ErrorCode.HEADER_MISSING.getMessage(),
                    "缺失 Host 请求头或域名不在白名单中",
                    ContextHolder.getDuration(),
                    null
            );
            response.getWriter().write(OBJECT_MAPPER.writeValueAsString(body));
            return;
        }
        response.setHeader(HttpHeaderConstant.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        filterChain.doFilter(request, response);
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

    private void setCorsHeaders(@NotNull HttpServletResponse response) {
        StringBuilder allowedHeaders = new StringBuilder();
        for (String header : accessHeaders) {
            if (!allowedHeaders.isEmpty()) {
                allowedHeaders.append(", ");
            }
            allowedHeaders.append(header);
        }
        StringBuilder allowedMethods = new StringBuilder();
        for (String method : allowMethods) {
            if (!allowedMethods.isEmpty()) {
                allowedMethods.append(", ");
            }
            allowedMethods.append(method);
        }
        response.setHeader(HttpHeaderConstant.ACCESS_CONTROL_ALLOW_HEADERS, allowedHeaders.toString());
        response.setHeader(HttpHeaderConstant.ACCESS_CONTROL_ALLOW_METHODS, allowedMethods.toString());
        response.setHeader(HttpHeaderConstant.ACCESS_CONTROL_MAX_AGE, "3600");
        response.setHeader(HttpHeaderConstant.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    }
}
