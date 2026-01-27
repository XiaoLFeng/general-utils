package com.xlf.utility.mvc.filter;

import com.xlf.utility.annotations.IgnoreContext;
import com.xlf.utility.constant.HttpHeaderConstant;
import com.xlf.utility.mvc.holder.ContextHolder;
import com.xlf.utility.mvc.properties.ContextProperties;
import com.xlf.utility.utility.UrlMatchUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.Optional;

/**
 * MVC请求上下文过滤器
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@AllArgsConstructor
@SuppressWarnings("unused")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ContextFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(ContextFilter.class);

    private final RequestMappingHandlerMapping handlerMapping;
    private final ContextProperties contextProperties;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (request.getMethod() == null || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestUri = request.getRequestURI();
        if (UrlMatchUtil.isUrlExcluded(requestUri, contextProperties.getExcludeUrls())) {
            log.debug("URL [{}] 在排除列表中，跳过上下文处理", requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            HandlerExecutionChain handler = handlerMapping.getHandler(request);
            if (handler != null && handler.getHandler() instanceof HandlerMethod handlerMethod) {
                Boolean hasIgnore = Optional.ofNullable(handlerMethod.getMethodAnnotation(IgnoreContext.class))
                        .or(() -> Optional.ofNullable(handlerMethod.getBeanType().getAnnotation(IgnoreContext.class)))
                        .map(IgnoreContext::value)
                        .orElse(false);
                if (hasIgnore) {
                    log.debug("忽略上下文注入处理");
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        } catch (Exception e) {
            log.warn("检查忽略上下文注解时发生异常: {}", e.getMessage(), e);
        }

        String externalUuid = null;
        if (contextProperties.isEnableInput()) {
            externalUuid = extractUuidFromRequest(request);
        }

        String contextId;
        if (externalUuid != null && !externalUuid.trim().isEmpty()) {
            contextId = externalUuid.trim();
        } else {
            contextId = java.util.UUID.randomUUID().toString();
            log.debug("生成新 UUID 写入 [{}]", contextId);
        }

        response.setHeader(HttpHeaderConstant.X_CONTEXT_UUID, contextId);
        MDC.put("CONTEXT_ID", contextId);

        try {
            ContextHolder.initContext(contextId);
            filterChain.doFilter(request, response);
        } finally {
            ContextHolder.clear();
            MDC.remove("CONTEXT_ID");
        }
    }

    @Contract("null -> null")
    private String extractUuidFromRequest(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String uuid = request.getHeader(HttpHeaderConstant.X_CONTEXT_UUID);
        if (uuid != null && !uuid.trim().isEmpty()) {
            log.debug("从请求头 {} 提取UUID: {}", HttpHeaderConstant.X_CONTEXT_UUID, uuid.trim());
            return uuid.trim();
        }
        return null;
    }
}
