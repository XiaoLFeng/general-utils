package com.xlf.utility.webflux.utility;

import com.xlf.utility.exception.library.ServerInternalErrorException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;
import java.util.Optional;

/**
 * ServerWebExchange 工具类 (WebFlux版本)
 * <p>
 * 此工具类提供了 WebFlux ServerWebExchange 请求相关的实用方法，主要应用在处理服务器端 HTTP 请求时，
 * 帮助开发者快速匹配、解析与操作请求信息，从而高效地实现业务功能的处理。
 * <p>
 * WebFlux版本使用响应式编程模型，与传统的Servlet API有所不同，专门处理ServerWebExchange相关的操作。
 * <p>
 * NOTICE: 此类设计为工具类，不允许进行实例化使用。所有方法均为静态方法，确保线程安全。
 *
 * <ul>
 * <li>适用于解析 WebFlux HTTP 请求并在服务端进行路由处理时使用。</li>
 * <li>与 Spring WebFlux 环境深度结合，专注于 {@code RequestMappingHandlerMapping} 的扩展操作。</li>
 * <li>需搭配恰当的异常处理机制以避免影响服务稳定性。</li>
 * </ul>
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public class ServerWebExchangeUtil {

    @Contract(value = " -> fail", pure = true)
    private ServerWebExchangeUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 获取当前请求对应的处理方法 (WebFlux版本)。
     * <p>
     * 此方法用于从给定的 {@link RequestMappingHandlerMapping} 实例中查找并返回与
     * {@link ServerWebExchange} 匹配的 {@link HandlerMethod} 对象。在 Spring WebFlux 中，
     * 该方法可以帮助快速定位处理请求的控制器方法。
     * <p>
     * 方法通过遍历 {@code RequestMappingHandlerMapping} 的映射信息并判断是否与请求匹配，
     * 最终返回一个 {@link HandlerMethod} 对象。如果未找到匹配的处理方法，返回 {@code null}。
     * <p>
     * NOTICE: 在调用此方法时，请确保 {@code RequestMappingHandlerMapping} 已正确配置，
     * 否则可能会导致 {@link ServerInternalErrorException} 被抛出。
     *
     * @param exchange       当前的 {@link ServerWebExchange} 对象，用于查找匹配的请求路径和方法。
     * @param handlerMapping 当前的 {@link RequestMappingHandlerMapping} 实例，用于存储所有
     *                       请求路径与处理方法的映射关系。此参数不能为 {@code null}。
     * @return 匹配当前请求的 {@link HandlerMethod} 对象；可能返回 {@code null}，表示未找到任何匹配的处理方法。
     * @throws ServerInternalErrorException 如果 {@code handlerMapping} 不包含有效的信息或无法找到
     *                                      任何匹配的处理方法。
     */
    @Nullable
    public static HandlerMethod getHandlerMethod(ServerWebExchange exchange, RequestMappingHandlerMapping handlerMapping) {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = Optional.ofNullable(handlerMapping)
                .map(RequestMappingHandlerMapping::getHandlerMethods)
                .orElseThrow(() -> new ServerInternalErrorException("无法获取处理程序映射信息，请检查 RequestMappingHandlerMapping 是否正确配置"));

        ServerHttpRequest request = exchange.getRequest();
        String requestPath = request.getPath().pathWithinApplication().value();
        String httpMethod = request.getMethod().name();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo mappingInfo = entry.getKey();
            if (mappingInfo != null) {
                // 检查路径匹配
                if (mappingInfo.getPatternsCondition() != null &&
                    mappingInfo.getPatternsCondition().getPatterns().stream()
                            .anyMatch(pattern -> matchPath(pattern.getPatternString(), requestPath))) {

                    // 检查HTTP方法匹配
                    if (mappingInfo.getMethodsCondition() == null ||
                        mappingInfo.getMethodsCondition().getMethods().isEmpty() ||
                        mappingInfo.getMethodsCondition().getMethods().stream()
                                .anyMatch(method -> method.name().equals(httpMethod))) {
                        return entry.getValue();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 简单的路径匹配方法
     * <p>
     * 支持基本的Ant风格路径匹配，包括 * 和 ** 通配符
     *
     * @param pattern     路径模式
     * @param requestPath 请求路径
     * @return 是否匹配
     */
    private static boolean matchPath(String pattern, String requestPath) {
        if (pattern.equals(requestPath)) {
            return true;
        }

        // 简单的通配符匹配
        if (pattern.contains("*")) {
            String regex = pattern
                    .replace("**", ".*")
                    .replace("*", "[^/]*")
                    .replace(".*", ".*");
            return requestPath.matches(regex);
        }

        return false;
    }

    /**
     * 从ServerWebExchange中提取客户端IP地址
     * <p>
     * WebFlux版本的IP地址提取方法，支持代理服务器的X-Forwarded-For头部
     *
     * @param exchange 当前的ServerWebExchange对象
     * @return 客户端IP地址，如果无法获取则返回"unknown"
     */
    public static String getClientIp(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();

        // 尝试从各种代理头部获取真实IP
        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String ip = request.getHeaders().getFirst(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For可能包含多个IP，取第一个
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        // 如果都没有，尝试从连接信息中获取
        if (request.getRemoteAddress() != null) {
            return request.getRemoteAddress().getAddress().getHostAddress();
        }

        return "unknown";
    }

    /**
     * 获取User-Agent信息
     * <p>
     * 从请求头中提取User-Agent字符串
     *
     * @param exchange 当前的ServerWebExchange对象
     * @return User-Agent字符串，如果不存在则返回空字符串
     */
    public static String getUserAgent(ServerWebExchange exchange) {
        String userAgent = exchange.getRequest().getHeaders().getFirst("User-Agent");
        return userAgent != null ? userAgent : "";
    }
}
