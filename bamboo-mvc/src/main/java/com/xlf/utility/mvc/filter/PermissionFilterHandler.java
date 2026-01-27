package com.xlf.utility.mvc.filter;

import com.xlf.utility.annotations.NeedPermission;
import com.xlf.utility.exception.library.DeveloperException;
import com.xlf.utility.mvc.utility.HttpServletUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * 权限过滤处理器类。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public abstract class PermissionFilterHandler extends OncePerRequestFilter {

    private final RequestMappingHandlerMapping handlerMapping;

    public PermissionFilterHandler(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        Optional.ofNullable(HttpServletUtil.getHandlerMethod(request, handlerMapping)).ifPresent(handler -> {
            Method method = handler.getMethod();
            if (method.isAnnotationPresent(NeedPermission.class)) {
                logger.debug("过滤器 PermissionFilterHandler 执行「权限验证」");
                String annotationPermission = Arrays.toString(Optional.of(method.getAnnotation(NeedPermission.class))
                        .filter(value -> value.value() != null && !Arrays.stream(value.value()).allMatch(String::isEmpty))
                        .map(NeedPermission::value)
                        .orElseThrow(() -> new DeveloperException(
                                "未找到所需的权限标识，请检查方法注解配置",
                                DeveloperException.ErrorType.CONFIGURATION_ERROR
                        )));
                this.hasPermissionCheck(request, annotationPermission);
            }
        });
        filterChain.doFilter(request, response);
    }

    /**
     * 检查用户是否具有指定权限
     */
    public abstract void hasPermissionCheck(HttpServletRequest request, String requestPermission);
}
