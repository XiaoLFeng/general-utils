package com.xlf.utility.exception.library;

import com.xlf.utility.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;

/**
 * 用户认证异常类，用于定义用户认证异常。
 * <p>
 * 该异常类继承自 {@link RuntimeException}，并包含了错误类型和请求信息。
 * 可以通过构造函数初始化，以适应特定的用户认证异常处理需求。
 *
 * @author xiao_lfeng
 * @version v1.0.9-beta.1.0
 * @since v1.0.9-beta.1.0
 */
@SuppressWarnings("unused")
public class UserAuthenticationException extends RuntimeException {

    /**
     * 错误类型，指示用户认证异常的具体类型。
     */
    private final ErrorType errorType;

    /**
     * 请求信息，包含用户认证请求的详细信息。
     */
    private final HttpServletRequest request;

    /**
     * 用户信息，包含与该请求相关的用户详细信息。
     */
    private final UserInfo userInfo;

    /**
     * 构造函数，初始化用户认证异常的所有属性。
     *
     * @param errorType 用户认证异常的错误类型
     * @param request   请求信息
     */
    public UserAuthenticationException(@NotNull ErrorType errorType, @NotNull HttpServletRequest request) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.request = request;
        this.userInfo = new UserInfo(
                request.getRemoteAddr(),
                request.getRequestURL().toString(),
                request.getMethod(),
                request.getHeader("User-Agent"),
                request.getHeader("Authorization")
        );
    }

    /**
     * 获取错误类型。
     *
     * @return 返回用户认证异常的错误类型
     */
    public ErrorType getErrorType() {
        return errorType;
    }

    /**
     * 获取请求信息。
     *
     * @return 返回用户认证请求的信息
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * 获取用户信息。
     *
     * @return 返回用户的详细信息
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * 错误类型枚举类，用于定义用户认证异常的错误类型。
     *
     * @author xiao_lfeng
     * @version v1.0.9-beta.1.0
     * @since v1.0.9-beta.1.0
     */
    public enum ErrorType {
        TOKEN_EXPIRED("令牌过期或不存在", ErrorCode.OPERATION_DENIED),
        PERMISSION_DENIED("权限不足", ErrorCode.OPERATION_DENIED),
        USER_NOT_LOGIN("用户未登录", ErrorCode.OPERATION_DENIED),
        USER_NOT_EXIST("用户不存在", ErrorCode.OPERATION_DENIED),
        WRONG_PASSWORD("密码错误", ErrorCode.OPERATION_DENIED),
        USER_BANNED("用户被封禁", ErrorCode.OPERATION_DENIED),
        LOGIN_WRONG("账号或者密码错误", ErrorCode.OPERATION_DENIED),
        VERIFICATION_CODE("验证码错误", ErrorCode.OPERATION_DENIED);

        /**
         * 错误信息，描述用户认证异常的详细信息。
         */
        private final String message;
        /**
         * 错误码，用于定义系统中的错误码信息。
         */
        private final ErrorCode errorCode;

        /**
         * 构造函数，初始化用户认证异常的错误类型。
         *
         * @param message   错误信息
         * @param errorCode 错误码
         */
        ErrorType(String message, ErrorCode errorCode) {
            this.message = message;
            this.errorCode = errorCode;
        }

        /**
         * 获取错误信息。
         *
         * @return 返回错误信息
         */
        public String getMessage() {
            return message;
        }

        /**
         * 获取错误码。
         *
         * @return 返回错误码
         */
        public ErrorCode getErrorCode() {
            return errorCode;
        }
    }

    /**
     * 用户信息类，用于定义用户信息。
     *
     * @author xiao_lfeng
     * @version v1.0.9-beta.1.0
     * @since v1.0.9-beta.1.0
     */
    public record UserInfo(String userIp, String requestUrl, String requestMethod, String userAgent, String userToken) {
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

        /**
         * 获取用户令牌。
         *
         * @return 返回用户令牌
         */
        public String userToken() {
            return userToken;
        }
    }
}