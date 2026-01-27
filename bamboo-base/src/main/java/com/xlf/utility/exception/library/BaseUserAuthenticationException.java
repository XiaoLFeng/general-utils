package com.xlf.utility.exception.library;

import com.xlf.utility.ErrorCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * 基础用户认证异常抽象类（无Web框架依赖）
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Getter
@SuppressWarnings("unused")
public abstract class BaseUserAuthenticationException extends RuntimeException {

    /**
     * 错误类型
     */
    private final ErrorType errorType;

    /**
     * 附加数据
     */
    private final Object additionalData;

    public BaseUserAuthenticationException(@NotNull ErrorType errorType) {
        this(errorType, null);
    }

    public BaseUserAuthenticationException(@NotNull ErrorType errorType, Object additionalData) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.additionalData = additionalData;
    }

    public BaseUserAuthenticationException(@NotNull ErrorType errorType, Throwable cause, Object additionalData) {
        super(errorType.getMessage(), cause);
        this.errorType = errorType;
        this.additionalData = additionalData;
    }

    @Getter
    public enum ErrorType {
        TOKEN_EXPIRED("令牌过期或不存在", ErrorCode.UNAUTHORIZED),
        PERMISSION_DENIED("权限不足", ErrorCode.OPERATION_DENIED),
        USER_NOT_LOGIN("用户未登录", ErrorCode.UNAUTHORIZED),
        USER_NOT_EXIST("用户不存在", ErrorCode.NOT_EXIST),
        WRONG_PASSWORD("密码错误", ErrorCode.UNAUTHORIZED),
        USER_BANNED("用户被封禁", ErrorCode.OPERATION_DENIED),
        LOGIN_WRONG("账号或者密码错误", ErrorCode.UNAUTHORIZED),
        NEED_APIKEY("需要 API-KEY", ErrorCode.UNAUTHORIZED),
        VERIFICATION_CODE("验证码错误", ErrorCode.UNAUTHORIZED);

        private final String message;
        private final ErrorCode errorCode;

        ErrorType(String message, ErrorCode errorCode) {
            this.message = message;
            this.errorCode = errorCode;
        }
    }
}
