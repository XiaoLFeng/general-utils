package com.xlf.utility.webflux.aspect;

import com.xlf.utility.annotations.EnableDataDebug;
import com.xlf.utility.annotations.IgnoreOutputDAO;
import com.xlf.utility.webflux.aspect.ILogAspect;
import com.xlf.utility.constant.HttpHeaderFilter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * WebFlux版本的业务日志切面实现类
 * <p>
 * 该类实现了ILogAspect接口，专门为Spring WebFlux响应式环境设计。
 * 在基础日志记录功能基础上，增加了响应式Web请求相关的详细信息：
 * - 记录ServerWebExchange的详细信息（请求方法、路径、客户端IP等）
 * - 增强控制器、服务、DAO层的日志输出
 * - 支持WebFlux特定的调试功能和数据输出
 * - 支持响应式流（Mono/Flux）的处理和记录
 * <p>
 * WebFlux版本包含响应式Web请求上下文的完整记录功能，
 * 为后续的问题排查和性能分析提供详实的日志支持。
 * <p>
 * NOTICE: 使用该类需要确保配置了Spring AOP支持和Spring WebFlux环境。
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
     * WebFlux增强版本的控制器日志记录
     * <p>
     * 在基础控制器日志基础上，增加WebFlux特定的功能：
     * 1. 记录ServerWebExchange的详细信息
     * 2. 输出客户端IP地址和User-Agent
     * 3. 提供更丰富的请求上下文信息
     * 4. 支持响应式参数的识别和记录
     * <p>
     * 如果无法获取ServerWebExchange上下文，则使用基础日志记录逻辑。
     */
    @Around("@within(org.springframework.stereotype.Controller) || " +
            "@within(org.springframework.web.bind.annotation.RestController)")
    @Override
    public Object beforeControllerLog(@NotNull ProceedingJoinPoint joinPoint) throws Throwable {
        ServerWebExchange exchange = getServerWebExchange(joinPoint);

        if (exchange != null) {
            if (log.isInfoEnabled()) {
                String method = exchange.getRequest().getMethod().name();
                String path = exchange.getRequest().getPath().pathWithinApplication().value();
                String queryString = exchange.getRequest().getURI().getQuery();
                String clientIp = getClientIpAddress(exchange);

                StringBuilder logMessage = new StringBuilder();
                logMessage.append(String.format("控制器调用 - %s %s", method, path));

                if (queryString != null && !queryString.isEmpty()) {
                    logMessage.append("?").append(queryString);
                }

                logMessage.append(String.format(" | 客户端IP: %s", clientIp));
                logMessage.append(String.format(" | 方法: %s", joinPoint.getSignature().toShortString()));

                log.info(logMessage.toString());
            }

            // 格式化并输出请求参数信息（DEBUG级别）
            this.logFormattedRequestParams(exchange, joinPoint);
        } else {
            // 回退到基础版本
            log.info("控制器调用 - 方法: {}", joinPoint.getSignature().toShortString());
        }

        // 显示调试数据
        this.showDebugData(joinPoint);
        return joinPoint.proceed();
    }

    /**
     * WebFlux增强版本的服务层日志记录
     * <p>
     * 记录服务层业务逻辑的执行，包含方法签名和执行上下文。
     * 对于响应式服务方法，会特别标注其响应式特性。
     */
    @Around("@within(org.springframework.stereotype.Service)")
    @Override
    public Object beforeServiceLog(@NotNull ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            // 检查是否是响应式方法
            boolean isReactive = isReactiveMethod(joinPoint);
            String reactiveMarker = isReactive ? "[Reactive]" : "";

            log.debug("服务调用{} - 方法: {}",
                    reactiveMarker, joinPoint.getSignature().toShortString());
        }

        // 显示调试数据
        this.showDebugData(joinPoint);
        return joinPoint.proceed();
    }

    /**
     * WebFlux增强版本的DAO层日志记录
     * <p>
     * 记录数据访问层的操作，支持 @IgnoreOutputDAO 注解控制输出行为。
     * 在WebFlux环境下提供更详细的数据库操作记录，同时支持响应式DAO方法。
     */
    @Around("@within(org.springframework.stereotype.Repository)")
    @Override
    public Object beforeDaoLog(@NotNull ProceedingJoinPoint pjp) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 检查是否是响应式方法
        boolean isReactive = isReactiveMethod(pjp);
        String reactiveMarker = isReactive ? "[Reactive]" : "";

        if (log.isDebugEnabled()) {
            log.debug("数据访问开始{} - 方法: {}",
                    reactiveMarker, pjp.getSignature().toShortString());
        }

        // 显示调试数据
        this.showDebugData(pjp);

        try {
            Object result = pjp.proceed();

            // 如果是响应式结果，需要特殊处理
            if (result instanceof Mono<?> mono) {
                return this.handleMonoResult(mono, pjp, startTime);
            } else if (result instanceof Flux<?> flux) {
                return this.handleFluxResult(flux, pjp, startTime);
            } else {
                // 非响应式结果的常规处理
                return this.handleNonReactiveResult(result, pjp, startTime);
            }

        } catch (Throwable throwable) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("数据访问异常{} - 方法: {} | 耗时: {}ms | 异常: {}",
                    reactiveMarker, pjp.getSignature().toShortString(), duration, throwable.getMessage());
            throw throwable;
        }
    }

    /**
     * WebFlux增强版本的调试数据输出
     * <p>
     * 当启用 @EnableDataDebug 注解时，输出方法的入参数据。
     * 在WebFlux环境下可以额外输出ServerWebExchange相关的调试信息，
     * 并特殊处理响应式类型的参数。
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

            if (paramValue instanceof Mono<?>) {
                debugMessage.append(String.format(" | %s: Mono<响应式数据>", paramName));
            } else if (paramValue instanceof Flux<?>) {
                debugMessage.append(String.format(" | %s: Flux<响应式数据>", paramName));
            } else if (paramValue instanceof ServerWebExchange exchange) {
                debugMessage.append(String.format(" | %s: ServerWebExchange[%s %s]", paramName,
                        exchange.getRequest().getMethod().name(),
                        exchange.getRequest().getPath().pathWithinApplication().value()));
            } else {
                debugMessage.append(String.format(" | %s: %s", paramName,
                        paramValue != null ? paramValue.toString() : "null"));
            }
        }

        log.debug(debugMessage.toString());

        // 如果在WebFlux环境中，额外输出ServerWebExchange信息
        logAdditionalServerWebExchangeInfo(joinPoint);
    }

    /**
     * 获取ServerWebExchange对象
     *
     * @param joinPoint 切入点
     * @return ServerWebExchange对象，如果不在WebFlux环境中则返回null
     */
    private @Nullable ServerWebExchange getServerWebExchange(@NotNull JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof ServerWebExchange) {
                return (ServerWebExchange) arg;
            }
        }
        return null;
    }

    /**
     * 检查方法是否返回响应式类型
     */
    private boolean isReactiveMethod(JoinPoint joinPoint) {
        try {
            Class<?> returnType = ((MethodSignature) joinPoint.getSignature()).getReturnType();
            return Mono.class.isAssignableFrom(returnType) || Flux.class.isAssignableFrom(returnType);
        } catch (Exception e) {
            log.debug("[WebFlux-LOG] 无法确定方法返回类型: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 处理Mono类型的DAO结果
     */
    private @NotNull Mono<?> handleMonoResult(@NotNull Mono<?> mono, ProceedingJoinPoint pjp, long startTime) {
        return mono
                .doOnSuccess(result -> {
                    long duration = System.currentTimeMillis() - startTime;
                    logDaoResult(pjp, duration, result, "SUCCESS");
                })
                .doOnError(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("Mono数据访问异常 - 方法: {} | 耗时: {}ms | 异常: {}",
                            pjp.getSignature().toShortString(), duration, error.getMessage());
                });
    }

    /**
     * 处理Flux类型的DAO结果
     */
    private @NotNull Flux<?> handleFluxResult(@NotNull Flux<?> flux, ProceedingJoinPoint pjp, long startTime) {
        return flux
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    logDaoResult(pjp, duration, null, "COMPLETE");
                })
                .doOnError(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("Flux数据访问异常 - 方法: {} | 耗时: {}ms | 异常: {}",
                            pjp.getSignature().toShortString(), duration, error.getMessage());
                });
    }

    /**
     * 处理非响应式结果
     */
    private Object handleNonReactiveResult(Object result, ProceedingJoinPoint pjp, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        logDaoResult(pjp, duration, result, "SUCCESS");
        return result;
    }

    /**
     * 记录DAO操作结果
     */
    private void logDaoResult(ProceedingJoinPoint pjp, long duration, Object result, String status) {
        if (!log.isDebugEnabled()) {
            return;
        }

        // 检查是否需要忽略输出
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        boolean ignoreOutput = method.isAnnotationPresent(IgnoreOutputDAO.class) ||
                method.getDeclaringClass().isAnnotationPresent(IgnoreOutputDAO.class);

        if (ignoreOutput) {
            log.debug("数据访问完成 - 方法: {} | 耗时: {}ms | 状态: {} | 输出已忽略",
                    pjp.getSignature().toShortString(), duration, status);
        } else {
            log.debug("数据访问完成 - 方法: {} | 耗时: {}ms | 状态: {} | 结果: {}",
                    pjp.getSignature().toShortString(), duration, status, result);
        }
    }

    /**
     * 记录额外的ServerWebExchange信息（用于调试）
     */
    private void logAdditionalServerWebExchangeInfo(JoinPoint joinPoint) {
        try {
            ServerWebExchange exchange = getServerWebExchange(joinPoint);
            if (exchange != null) {
                String userAgent = exchange.getRequest().getHeaders().getFirst("User-Agent");

                if (userAgent != null && !userAgent.isEmpty()) {
                    log.debug("[DEBU] HTTP信息 - User-Agent: {}",
                            userAgent.length() > 200 ? userAgent.substring(0, 200) + "..." : userAgent);
                }

                // 记录外部trace ID信息
                String externalTraceId = getExternalTraceId(exchange);
                if (externalTraceId != null) {
                    log.debug("[DEBU] 外部TraceID: {}", externalTraceId);
                }
            }
        } catch (Exception e) {
            log.debug("[DEBU] 获取ServerWebExchange信息时出错: {}", e.getMessage());
        }
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
     * 格式化输出请求参数信息（WebFlux版本）
     * <p>
     * 当DEBUG级别启用时，详细记录请求的Header、Query参数和Body参数：
     * - Header: 仅显示自定义Header（如X-开头等）
     * - Query: URL查询参数
     * - Body: 方法参数的响应式友好格式化显示
     *
     * @param exchange  ServerWebExchange对象
     * @param joinPoint 切点信息
     */
    private void logFormattedRequestParams(ServerWebExchange exchange, ProceedingJoinPoint joinPoint) {
        if (!log.isDebugEnabled()) {
            return;
        }

        StringBuilder paramLog = new StringBuilder();
        paramLog.append("请求参数内容:");

        // Header参数
        Map<String, String> customHeaders = getCustomHeaders(exchange);
        if (!customHeaders.isEmpty()) {
            paramLog.append("\n    [HEADER]");
            customHeaders.forEach((key, value) ->
                    paramLog.append(String.format("\n        %s: %s", key, truncateValue(value)))
            );
        }

        // Query参数
        String queryString = exchange.getRequest().getURI().getQuery();
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
    private @NotNull Map<String, String> getCustomHeaders(ServerWebExchange exchange) {
        Map<String, String> customHeaders = new LinkedHashMap<>();

        if (exchange == null) {
            return customHeaders;
        }

        exchange.getRequest().getHeaders().forEach((headerName, headerValues) -> {
            String lowerHeaderName = headerName.toLowerCase();
            if (isCustomHeader(lowerHeaderName) && headerValues != null && !headerValues.isEmpty()) {
                // 对于多值header，只取第一个值
                customHeaders.put(headerName, headerValues.get(0));
            }
        });

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
     * 格式化方法参数（WebFlux版本，响应式友好）
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
        if (!result.isEmpty()) {
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }

    /**
     * 智能格式化参数值（WebFlux版本，响应式友好）
     */
    @Contract("null -> !null")
    private String formatParameterValue(Object value) {
        if (value == null) {
            return "null";
        }

        // ServerWebExchange 特殊处理
        if (value instanceof ServerWebExchange exchange) {
            return String.format("ServerWebExchange[%s %s]",
                    exchange.getRequest().getMethod().name(),
                    exchange.getRequest().getPath().pathWithinApplication().value());
        }

        // 响应式类型处理（不强制解析内容）
        if (value instanceof Mono<?>) {
            // 尝试获取泛型类型信息
            Class<?> elementClass = extractGenericParameterType(value);
            return String.format("Mono<%s>", elementClass != null ? elementClass.getSimpleName() : "?");
        }

        if (value instanceof Flux<?>) {
            Class<?> elementClass = extractGenericParameterType(value);
            return String.format("Flux<%s>", elementClass != null ? elementClass.getSimpleName() : "?");
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
     * 提取泛型参数类型（简单实现）
     */
    private @Nullable Class<?> extractGenericParameterType(Object reactiveObject) {
        try {
            // 这里简化处理，可以通过反射获取更准确的泛型信息
            if (reactiveObject != null) {
                String className = reactiveObject.getClass().getSimpleName();
                if (className.contains("Mono") || className.contains("Flux")) {
                    // 默认返回Object，实际项目中可以通过更复杂的反射获取准确类型
                    return Object.class;
                }
            }
        } catch (Exception e) {
            log.debug("[WebFlux-LOG] 无法提取泛型类型: {}", e.getMessage());
        }
        return null;
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
     * 获取外部TraceID
     *
     * @param exchange ServerWebExchange对象
     * @return 外部TraceID，如果没有则返回null
     */
    @Contract("null -> null")
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
