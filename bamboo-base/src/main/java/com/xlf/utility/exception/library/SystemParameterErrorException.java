package com.xlf.utility.exception.library;

/**
 * 系统参数异常类，用于定义系统参数异常。
 * <p>
 * 这个异常一般是开发者在编写代码时，由于参数传递错误导致的异常。
 * 该异常类继承自 {@link RuntimeException}，并包含了异常信息和异常数据字段。
 * 可以通过构造函数初始化，以适应特定的系统参数异常处理需求。
 *
 * @author xiao_lfeng
 * @version v1.0.9-beta.1.0
 * @since v1.0.9-beta.1.0
 */
@SuppressWarnings("unused")
public class SystemParameterErrorException extends RuntimeException {

    /**
     * 异常信息，描述系统参数错误的详细信息。
     */
    private final String message;

    /**
     * 异常数据，可能包含与该系统参数异常相关的额外数据。
     */
    private final Object data;

    /**
     * 构造函数，初始化系统参数异常的所有属性。
     *
     * @param message 异常的错误消息
     * @param data    与异常相关的附加数据
     */
    public SystemParameterErrorException(String message, Object data) {
        super(message);
        this.message = message;
        this.data = data;
    }

    /**
     * 构造函数，仅初始化异常信息，默认数据为 {@code null}。
     *
     * @param message 异常的错误消息
     */
    public SystemParameterErrorException(String message) {
        this(message, null);
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
     * 获取异常数据。
     *
     * @return 返回与异常相关的附加数据，可能为 {@code null}
     */
    public Object getData() {
        return data;
    }
}