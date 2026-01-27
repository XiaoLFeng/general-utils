package com.xlf.utility.webflux.exception.library;

import com.xlf.utility.exception.library.BaseUserAuthenticationException;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.server.ServerWebExchange;

/**
 * 用户认证异常类（WebFlux版本）
 * <p>
 * 该类继承自 BaseUserAuthenticationException，专门为Spring WebFlux响应式环境设计。
 * 在基础功能之上增加了ServerWebExchange相关的信息提取和处理，
 * 包括客户端IP、请求URL、请求方法、User-Agent等响应式Web请求信息。
 * <p>
 * 该类提供了详细的错误信息、响应式请求信息及用户信息，便于开发人员诊断和处理
 * 在WebFlux环境下发生的用户认证相关问题。
 * <p>
 * NOTICE: 该类继承自 {@link RuntimeException}，是一个非受检异常，应确保在适当
 * 的场景中捕获和处理此异常。仅适用于Spring WebFlux环境。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Getter
@SuppressWarnings("unused")
public class UserAuthenticationException extends BaseUserAuthenticationException {

    /**
     * ServerWebExchange对象，包含响应式Web请求和响应的详细信息。
     */
    private final ServerWebExchange exchange;

    /**
     * 用户信息，包含与该响应式请求相关的用户详细信息。
     */
    private final UserInfo userInfo;

    /**
     * 构造函数，初始化用户认证异常的所有属性。
     *
     * @param errorType 用户认证异常的错误类型
     * @param exchange  ServerWebExchange对象
     */
    public UserAuthenticationException(@NotNull ErrorType errorType, @NotNull ServerWebExchange exchange) {
        super(errorType);
        this.exchange = exchange;
        this.userInfo = new UserInfo(
                getClientIpAddress(exchange),
                exchange.getRequest().getURI().toString(),
                exchange.getRequest().getMethod().name(),
                exchange.getRequest().getHeaders().getFirst("User-Agent")
        );
    }

    /**
     * 构造函数，初始化用户认证异常、附加数据和响应式请求信息。
     *
     * @param errorType      用户认证异常的错误类型
     * @param exchange       ServerWebExchange对象
     * @param additionalData 附加数据
     */
    public UserAuthenticationException(@NotNull ErrorType errorType, @NotNull ServerWebExchange exchange, Object additionalData) {
        super(errorType, additionalData);
        this.exchange = exchange;
        this.userInfo = new UserInfo(
                getClientIpAddress(exchange),
                exchange.getRequest().getURI().toString(),
                exchange.getRequest().getMethod().name(),
                exchange.getRequest().getHeaders().getFirst("User-Agent")
        );
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
     * 用户信息类，用于封装处理用户相关的响应式Web请求信息。
     * <p>
     * 该类使用 Java 14 的 Record 特性构建，通过定义不可变对象以便于方便地存储和访问
     * 与用户相关的所有响应式请求数据，包括用户 IP 地址、请求 URL、请求方法、用户代理。
     * 此类常用于后端处理响应式用户请求的上下文中。
     * <p>
     * 例如，您可以通过该类追踪用户行为、鉴权或分析响应式请求的来源信息。
     *
     * <p>
     * 使用场景包括但不限于：
     * <ul>
     *     <li>响应式用户认证与授权</li>
     *     <li>响应式请求记录与跟踪</li>
     *     <li>安全分析与审计</li>
     * </ul>
     *
     * <p>
     * NOTICE: 此类为不可变类，所有字段均为只读。字段必须在实例化时提供，不允许修改。
     *
     * @param userIp        用户 IP 地址，用于标记请求来源 IP。
     * @param requestUrl    请求的 URL，表示客户端请求的资源路径。
     * @param requestMethod HTTP 请求方法，如 GET、POST 等。
     * @param userAgent     用户代理信息，标识发起请求的设备或浏览器信息。
     *
     * @author xiao_lfeng
     * @version v2.0.0-beta1
     * @since v2.0.0-beta1
     */
    public record UserInfo(String userIp, String requestUrl, String requestMethod, String userAgent) {
        /**
         * 获取用户 IP 地址。
         *
         * @return 返回用户 IP 地址
         */
        public String userIp() {
            return userIp;
        }

        /**
         * 获取请求 URL。
         *
         * @return 返回请求 URL
         */
        public String requestUrl() {
            return requestUrl;
        }

        /**
         * 获取请求方法。
         *
         * @return 返回请求方法
         */
        public String requestMethod() {
            return requestMethod;
        }

        /**
         * 获取用户代理。
         *
         * @return 返回用户代理
         */
        public String userAgent() {
            return userAgent;
        }
    }

    // 便捷的工厂方法，用于创建带响应式请求信息的异常

    /**
     * 创建令牌过期异常的便捷方法（WebFlux版本）
     *
     * @param exchange ServerWebExchange对象
     * @return 令牌过期异常实例
     */
    public static UserAuthenticationException tokenExpired(@NotNull ServerWebExchange exchange) {
        return new UserAuthenticationException(ErrorType.TOKEN_EXPIRED, exchange);
    }

    /**
     * 创建权限不足异常的便捷方法（WebFlux版本）
     *
     * @param exchange ServerWebExchange对象
     * @return 权限不足异常实例
     */
    public static UserAuthenticationException permissionDenied(@NotNull ServerWebExchange exchange) {
        return new UserAuthenticationException(ErrorType.PERMISSION_DENIED, exchange);
    }

    /**
     * 创建用户未登录异常的便捷方法（WebFlux版本）
     *
     * @param exchange ServerWebExchange对象
     * @return 用户未登录异常实例
     */
    public static UserAuthenticationException userNotLogin(@NotNull ServerWebExchange exchange) {
        return new UserAuthenticationException(ErrorType.USER_NOT_LOGIN, exchange);
    }

    /**
     * 创建用户不存在异常的便捷方法（WebFlux版本）
     *
     * @param exchange ServerWebExchange对象
     * @return 用户不存在异常实例
     */
    public static UserAuthenticationException userNotExist(@NotNull ServerWebExchange exchange) {
        return new UserAuthenticationException(ErrorType.USER_NOT_EXIST, exchange);
    }

    /**
     * 创建密码错误异常的便捷方法（WebFlux版本）
     *
     * @param exchange ServerWebExchange对象
     * @return 密码错误异常实例
     */
    public static UserAuthenticationException wrongPassword(@NotNull ServerWebExchange exchange) {
        return new UserAuthenticationException(ErrorType.WRONG_PASSWORD, exchange);
    }

    /**
     * 创建用户被封禁异常的便捷方法（WebFlux版本）
     *
     * @param exchange ServerWebExchange对象
     * @return 用户被封禁异常实例
     */
    public static UserAuthenticationException userBanned(@NotNull ServerWebExchange exchange) {
        return new UserAuthenticationException(ErrorType.USER_BANNED, exchange);
    }

    /**
     * 创建登录信息错误异常的便捷方法（WebFlux版本）
     *
     * @param exchange ServerWebExchange对象
     * @return 登录信息错误异常实例
     */
    public static UserAuthenticationException loginWrong(@NotNull ServerWebExchange exchange) {
        return new UserAuthenticationException(ErrorType.LOGIN_WRONG, exchange);
    }

    /**
     * 创建需要API Key异常的便捷方法（WebFlux版本）
     *
     * @param exchange ServerWebExchange对象
     * @return 需要API Key异常实例
     */
    public static UserAuthenticationException needApikey(@NotNull ServerWebExchange exchange) {
        return new UserAuthenticationException(ErrorType.NEED_APIKEY, exchange);
    }

    /**
     * 创建验证码错误异常的便捷方法（WebFlux版本）
     *
     * @param exchange ServerWebExchange对象
     * @return 验证码错误异常实例
     */
    public static UserAuthenticationException verificationCodeError(@NotNull ServerWebExchange exchange) {
        return new UserAuthenticationException(ErrorType.VERIFICATION_CODE, exchange);
    }
}
