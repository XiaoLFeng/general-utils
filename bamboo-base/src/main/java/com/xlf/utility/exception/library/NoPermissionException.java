package com.xlf.utility.exception.library;

/**
 * 无权限异常类，用于定义无权限异常。
 * <p>
 * 该异常类继承自 {@link RuntimeException}，并包含了异常信息和权限字段。
 * 可以通过构造函数初始化，以适应特定的权限异常处理需求。
 *
 * @since v1.0.9-beta.1.0
 * @version v1.0.9-beta.1.0
 * @author xiao_lfeng
 */
@SuppressWarnings("unused")
public class NoPermissionException extends RuntimeException {

    /**
     * 异常信息，描述无权限异常的详细信息。
     */
    private final String message;

    /**
     * 权限，指示与此异常相关的权限信息。
     */
    private final String permission;

    /**
     * 构造函数，初始化无权限异常的所有属性。
     *
     * @param message    异常的错误消息
     * @param permission 与异常相关的权限
     */
    public NoPermissionException(String message, String permission) {
        super(message);
        this.message = message;
        this.permission = permission;
    }

    /**
     * 获取异常信息。
     *
     * @return 返回异常的错误消息
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 获取权限信息。
     *
     * @return 返回与异常相关的权限
     */
    public String getPermission() {
        return permission;
    }
}