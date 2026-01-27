package com.xlf.utility.mvc.aspect;

import com.xlf.utility.annotations.EnableDataDebug;
import com.xlf.utility.annotations.IgnoreOutputDAO;
import com.xlf.utility.mvc.aspect.ILogAspect;
import com.xlf.utility.constant.HttpHeaderFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * MVC版本的业务日志切面实现类
 * <p>
 * 该类实现了ILogAspect接口，专门为Spring MVC环境设计。
 * 在基础日志记录功能基础上，增加了HTTP请求相关的详细信息：
 * - 记录HTTP请求的详细信息（请求方法、URI、客户端IP等）
 * - 增强控制器、服务、DATA层的日志输出
 * - 支持MVC特定的调试功能和数据输出
 * <p>
 * MVC版本包含HTTP请求上下文的完整记录功能，
 * 为后续的问题排查和性能分析提供详实的日志支持。
 * <p>
 * NOTICE: 使用该类需要确保配置了Spring AOP支持和Spring MVC环境。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Slf4j
@Aspect
@SuppressWarnings("unused")
public class LogAspectHandler implements ILogAspect {

    /**
     * MVC增强版本的控制器日志记录
     * <p>
     * 在基础控制器日志基础上，增加MVC特定的功能：
     * 1. 记录HTTP请求的详细信息
     * 2. 输出客户端IP地址和User-Agent
     * 3. 提供更丰富的请求上下文信息
     * <p>
     * 如果无法获取HTTP请求上下文，则使用基础日志记录逻辑。
     */
    @Override
    @Around("@within(org.springframework.stereotype.Controller) || " +
            "@within(org.springframework.web.bind.annotation.RestController)")
    public Object beforeControllerLog(@NotNull ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (servletRequestAttributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = servletRequestAttributes.getRequest();

        if (log.isInfoEnabled()) {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            String clientIp = getClientIpAddress(request);

            StringBuilder logMessage = new StringBuilder();
            logMessage.append(String.format("控制器调用 - %s %s", method, uri));

            if (queryString != null && !queryString.isEmpty()) {
                logMessage.append("?").append(queryString);
            }

            logMessage.append(String.format(" | 客户端IP: %s", clientIp));
            logMessage.append(String.format(" | 方法: %s", joinPoint.getSignature().toShortString()));

            log.info(logMessage.toString());
        }

        // 显示调试数据
        showDebugData(joinPoint);

        // 格式化并输出请求参数信息（DEBUG级别）
        logFormattedRequestParams(request, joinPoint);

        return joinPoint.proceed();
    }

    /**
     * MVC增强版本的服务层日志记录
     * <p>
     * 记录服务层业务逻辑的执行，包含方法签名和执行上下文。
     */
    @Override
    @Around("@within(org.springframework.stereotype.Service)")
    public Object beforeServiceLog(@NotNull ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("服务调用 - 方法: {}", joinPoint.getSignature().toShortString());
        }

        // 显示调试数据
        showDebugData(joinPoint);
        return joinPoint.proceed();
    }

    /**
     * MVC增强版本的DATA层日志记录
     * <p>
     * 记录数据访问层的操作，支持 @IgnoreOutputDATA 注解控制输出行为。
     * 在MVC环境下提供更详细的数据库操作记录。
     */
    @Override
    @Around("@within(org.springframework.stereotype.Repository)")
    public Object beforeDaoLog(@NotNull ProceedingJoinPoint pjp) throws Throwable {
        long startTime = System.currentTimeMillis();

        if (log.isDebugEnabled()) {
            log.debug("数据访问开始 - 方法: {}", pjp.getSignature().toShortString());
        }

        // 显示调试数据
        showDebugData(pjp);

        try {
            Object result = pjp.proceed();

            long duration = System.currentTimeMillis() - startTime;

            // 检查是否需要忽略输出
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            Method method = signature.getMethod();
            // 检查类或方法是否启用了数据调试
            boolean ignoreOutput = method.isAnnotationPresent(IgnoreOutputDAO.class) ||
                    method.getDeclaringClass().isAnnotationPresent(IgnoreOutputDAO.class);

            if (log.isDebugEnabled()) {
                if (ignoreOutput) {
                    log.debug("数据访问完成 - 方法: {} | 耗时: {}ms | 输出已忽略",
                            pjp.getSignature().toShortString(), duration);
                } else {
                    log.debug("数据访问完成 - 方法: {} | 耗时: {}ms | 结果: {}",
                            pjp.getSignature().toShortString(), duration, result);
                }
            }

            return result;

        } catch (Throwable throwable) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("数据访问异常 - 方法: {} | 耗时: {}ms | 异常: {}",
                    pjp.getSignature().toShortString(), duration, throwable.getMessage());
            throw throwable;
        }
    }

    /**
     * MVC增强版本的调试数据输出
     * <p>
     * 当启用 @EnableDataDebug 注解时，输出方法的入参数据。
     * 在MVC环境下可以额外输出HTTP请求相关的调试信息。
     */
    @Override
    public void showDebugData(@NotNull JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 检查类或方法是否启用了数据调试
        boolean enableDebug = method.isAnnotationPresent(EnableDataDebug.class) ||
                method.getDeclaringClass().isAnnotationPresent(EnableDataDebug.class);

        if (!enableDebug || !log.isDebugEnabled()) {
            return;
        }

        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();

        if (args == null || args.length == 0) {
            log.debug("[DEBU] 方法参数 - 无参数 | 方法: {}", joinPoint.getSignature().toShortString());
            return;
        }

        StringBuilder debugMessage = new StringBuilder();
        debugMessage.append(String.format("[DEBU] 方法参数 - 方法: %s", joinPoint.getSignature().toShortString()));

        for (int i = 0; i < args.length; i++) {
            String paramName = (paramNames != null && i < paramNames.length) ? paramNames[i] : "arg" + i;
            Object paramValue = args[i];

            debugMessage.append(String.format(" | %s: %s", paramName,
                    paramValue != null ? paramValue.toString() : "null"));
        }

        log.debug(debugMessage.toString());

        // 如果在MVC环境中，额外输出HTTP请求信息
        logAdditionalHttpInfo();
    }

    /**
     * 记录额外的HTTP请求信息（用于调试）
     */
    private void logAdditionalHttpInfo() {
        try {
            ServletRequestAttributes servletRequestAttributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (servletRequestAttributes != null) {
                HttpServletRequest request = servletRequestAttributes.getRequest();
                String userAgent = request.getHeader("User-Agent");

                if (userAgent != null && !userAgent.isEmpty()) {
                    log.debug("[DEBU] HTTP信息 - User-Agent: {}",
                            userAgent.length() > 200 ? userAgent.substring(0, 200) + "..." : userAgent);
                }
            }
        } catch (Exception e) {
            log.debug("[DEBU] 获取HTTP信息时出错: {}", e.getMessage());
        }
    }

    /**
     * 格式化输出请求参数信息
     * <p>
     * 当DEBUG级别启用时，详细记录请求的Header、Query参数和Body参数：
     * - Header: 仅显示自定义Header（如X-开头等）
     * - Query: URL查询参数
     * - Body: 方法参数的智能格式化显示
     *
     * @param request HTTP请求对象
     * @param joinPoint 切点信息
     */
    private void logFormattedRequestParams(HttpServletRequest request, ProceedingJoinPoint joinPoint) {
        if (!log.isDebugEnabled()) {
            return;
        }

        StringBuilder paramLog = new StringBuilder();
        paramLog.append("请求参数内容:");

        // Header参数
        Map<String, String> customHeaders = getCustomHeaders(request);
        if (!customHeaders.isEmpty()) {
            paramLog.append("\n    [HEADER]");
            customHeaders.forEach((key, value) ->
                    paramLog.append(String.format("\n        %s: %s", key, truncateValue(value)))
            );
        }

        // Query参数
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            Map<String, List<String>> queryParams = parseQueryParams(queryString);
            if (!queryParams.isEmpty()) {
                paramLog.append("\n    [PARAM]");
                queryParams.forEach((key, values) -> {
                    String value = values.size() == 1 ? values.get(0) : values.toString();
                    paramLog.append(String.format("\n        %s: %s", key, truncateValue(value)));
                });
            }
        }

        // Body参数（方法参数）
        String bodyParams = formatMethodParameters(joinPoint);
        if (bodyParams != null) {
            paramLog.append("\n    [BODY]");
            paramLog.append("\n").append(bodyParams);
        }

        // 如果至少有一种参数类型，则输出日志
        if (customHeaders.isEmpty() && (queryString == null || queryString.isEmpty()) && bodyParams == null) {
            paramLog.append("\n    无参数");
        }

        log.debug(paramLog.toString());
    }

    /**
     * 获取自定义Header（过滤掉标准HTTP头）
     */
    private @NotNull Map<String, String> getCustomHeaders(HttpServletRequest request) {
        Map<String, String> customHeaders = new LinkedHashMap<>();

        if (request == null) {
            return customHeaders;
        }

        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) {
            return customHeaders;
        }

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement().toLowerCase();
            if (isCustomHeader(headerName)) {
                String headerValue = request.getHeader(headerName);
                if (headerValue != null && !headerValue.isEmpty()) {
                    customHeaders.put(headerName, headerValue);
                }
            }
        }

        return customHeaders;
    }

    /**
     * 判断是否为自定义Header
     */
    private boolean isCustomHeader(@NotNull String headerName) {
        String lowerName = headerName.toLowerCase();

        // 排除标准HTTP头
        if (HttpHeaderFilter.STANDARD_HTTP_HEADERS.contains(lowerName)) {
            return false;
        }

        // 检查是否为自定义Header前缀
        return HttpHeaderFilter.CUSTOM_HEADER_PREFIXES.stream().anyMatch(lowerName::startsWith);
    }

    /**
     * 解析查询参数
     */
    private @NotNull Map<String, List<String>> parseQueryParams(String queryString) {
        Map<String, List<String>> params = new LinkedHashMap<>();

        if (queryString == null || queryString.isEmpty()) {
            return params;
        }

        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = java.net.URLDecoder.decode(keyValue[1], java.nio.charset.StandardCharsets.UTF_8);
                params.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }
        }

        return params;
    }

    /**
     * 格式化方法参数
     */
    private @Nullable String formatMethodParameters(@NotNull ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();

        if (args == null || args.length == 0) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            String paramName = (paramNames != null && i < paramNames.length) ? paramNames[i] : "arg" + i;
            Object paramValue = args[i];

            String formattedValue = formatParameterValue(paramValue);
            result.append(String.format("        %s: %s\n", paramName, formattedValue));
        }

        // 移除最后的换行符
        if (result.length() > 0) {
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }

    /**
     * 智能格式化参数值
     */
    @Contract("null -> !null")
    private String formatParameterValue(Object value) {
        if (value == null) {
            return "null";
        }

        // 简单类型直接返回
        Class<?> clazz = value.getClass();
        if (isSimpleType(clazz)) {
            return truncateValue(value.toString());
        }

        // 集合类型
        if (Collection.class.isAssignableFrom(clazz)) {
            Collection<?> collection = (Collection<?>) value;
            return String.format("Collection[%d] (%s)", collection.size(),
                    collection.isEmpty() ? "empty" : collection.iterator().next().getClass().getSimpleName());
        }

        // Map类型
        if (Map.class.isAssignableFrom(clazz)) {
            Map<?, ?> map = (Map<?, ?>) value;
            return String.format("Map[%d entries]", map.size());
        }

        // 数组类型
        if (clazz.isArray()) {
            return String.format("Array[%d]", java.lang.reflect.Array.getLength(value));
        }

        // 复杂对象：显示类名和主要字段
        return formatComplexObject(value);
    }

    /**
     * 格式化复杂对象，显示主要字段信息
     */
    private @NotNull String formatComplexObject(@NotNull Object obj) {
        StringBuilder result = new StringBuilder();
        result.append(obj.getClass().getSimpleName()).append(" {");

        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            int fieldCount = 0;
            int maxFields = 5; // 最多显示5个字段

            for (Field field : fields) {
                if (fieldCount >= maxFields) {
                    break;
                }

                field.setAccessible(true);
                Object fieldValue = field.get(obj);

                if (fieldCount > 0) {
                    result.append(", ");
                }

                result.append(field.getName()).append(": ");
                if (fieldValue == null) {
                    result.append("null");
                } else if (isSimpleType(fieldValue.getClass())) {
                    result.append(truncateValue(fieldValue.toString()));
                } else {
                    result.append(fieldValue.getClass().getSimpleName());
                }

                fieldCount++;
            }

            if (fieldCount < fields.length) {
                result.append(", ...");
            }

        } catch (Exception e) {
            result.append("...");
        }

        result.append("}");
        return result.toString();
    }

    /**
     * 判断是否为简单类型
     */
    private boolean isSimpleType(@NotNull Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == String.class ||
                Number.class.isAssignableFrom(clazz) ||
                Boolean.class.isAssignableFrom(clazz) ||
                Character.class.isAssignableFrom(clazz) ||
                Date.class.isAssignableFrom(clazz);
    }

    /**
     * 截断过长的值
     */
    private @NotNull String truncateValue(String value) {
        if (value == null) {
            return "null";
        }

        int maxLength = 200;
        if (value.length() > maxLength) {
            return value.substring(0, maxLength) + "...";
        }

        return value;
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
    @Contract("null -> !null")
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
