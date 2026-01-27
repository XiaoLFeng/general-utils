package com.xlf.utility.mvc.aspect;

import com.xlf.utility.ErrorCode;
import com.xlf.utility.app.aspect.IDebugAspect;
import com.xlf.utility.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * MVC版本的Debug切面实现类
 * <p>
 * 该类实现了IDebugAspect接口，专门为Spring MVC环境设计。
 * 在基础Debug环境检查功能基础上，增加了HTTP请求相关的调试信息记录：
 * - 记录HTTP请求的详细信息（请求方法、URI、客户端IP等）
 * - 增强环境检查的日志输出
 * - 支持MVC特定的调试功能
 * <p>
 * MVC版本包含HTTP请求上下文的完整记录功能。
 * <p>
 * NOTICE: 使用该类需要确保配置了Spring AOP支持和Spring MVC环境。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public class DebugAspectHandler implements IDebugAspect {

    private static final Logger log = LoggerFactory.getLogger(DebugAspectHandler.class);

    /**
     * MVC增强版本的Debug环境检查
     * <p>
     * 在基础环境检查基础上，增加MVC特定的功能：
     * 1. 记录HTTP请求的详细信息
     * 2. 输出客户端IP地址和User-Agent
     * 3. 提供更丰富的调试上下文信息
     * <p>
     * 如果无法获取HTTP请求上下文，则使用基础检查逻辑。
     */
    @Override
    public void checkDebugController() {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();

            log.debug("DebugController 被调用，正在检查MVC运行环境");

            // 记录HTTP请求详细信息
            logHttpRequestInfo(request);

            if (log.isDebugEnabled()) {
                log.debug("当前运行环境为开发模式，允许 DebugController 的调用");
                log.debug("\t\t地址: [{}]{}", request.getMethod(), request.getServletPath());
                log.debug("\t\t客户端IP: {}", getClientIpAddress(request));

                String userAgent = request.getHeader("User-Agent");
                if (userAgent != null && !userAgent.isEmpty()) {
                    log.debug("\t\tUser-Agent: {}", userAgent.length() > 100 ?
                            userAgent.substring(0, 100) + "..." : userAgent);
                }
            } else {
                log.warn("DebugController 在非开发环境中被调用");
                log.warn("\t\t请求: [{}]{}", request.getMethod(), request.getServletPath());
                log.warn("\t\t客户端IP: {}", getClientIpAddress(request));
                throw new BusinessException("非 Debug 环境禁止调用该接口内容", ErrorCode.UNAUTHORIZED);
            }
        } else {
            // 回退到基础版本
            log.debug("无法获取HTTP请求上下文，使用基础检查逻辑");
            performBasicDebugCheck();
        }
    }

    /**
     * 基础Debug环境检查
     * <p>
     * 当无法获取HTTP请求上下文时使用的基础实现
     */
    private void performBasicDebugCheck() {
        log.debug("[DEBU] DebugController 被调用，正在检查当前运行环境");
        if (log.isDebugEnabled()) {
            log.debug("[DEBU] 当前运行环境为开发模式，允许 DebugController 的调用");
        } else {
            log.warn("[DEBU] DebugController 在非开发环境中被调用，请检查配置");
            throw new BusinessException("非 Debug 环境禁止调用该接口内容", ErrorCode.UNAUTHORIZED);
        }
    }

    /**
     * 记录HTTP请求详细信息
     *
     * @param request HTTP请求对象
     */
    private void logHttpRequestInfo(HttpServletRequest request) {
        if (request == null || !log.isDebugEnabled()) {
            return;
        }

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String remoteAddr = getClientIpAddress(request);

        StringBuilder logMessage = new StringBuilder();
        logMessage.append(String.format("Debug请求详情 - %s %s", method, uri));

        if (queryString != null && !queryString.isEmpty()) {
            logMessage.append("?").append(queryString);
        }

        logMessage.append(String.format(" | 客户端IP: %s", remoteAddr));

        log.debug(logMessage.toString());
    }

    /**
     * 获取客户端真实IP地址
     * <p>
     * 支持多种代理服务器的IP传递方式：
     * - X-Forwarded-For
     * - X-Real-IP
     * - Proxy-Client-IP
     * - WL-Proxy-Client-IP
     *
     * @param request HTTP请求对象
     * @return 客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
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
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For可能包含多个IP，取第一个
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "unknown";
    }
}
