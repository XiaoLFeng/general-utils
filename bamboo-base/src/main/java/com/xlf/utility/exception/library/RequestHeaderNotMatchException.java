package com.xlf.utility.exception.library;

/**
 * 请求头未匹配异常类，用于定义请求头需要匹配对应参数、参数不存在、参数错误等异常。
 * <p>
 * 该异常类继承自 {@link RuntimeException}，并包含了异常信息字段。
 * 可以通过构造函数初始化，以适应特定的请求头未匹配异常处理需求。
 *
 * @author xiao_lfeng
 * @version v1.0.9-beta.1.0
 * @since v1.0.9-beta.1.0
 */
@SuppressWarnings("unused")
public class RequestHeaderNotMatchException extends RuntimeException {

    /**
     * 异常信息，描述请求头未匹配的详细信息。
     */
    private final String message;

    /**
     * 构造函数，初始化请求头未匹配异常的所有属性。
     *
     * @param message 异常的错误消息
     */
    public RequestHeaderNotMatchException(String message) {
        super(message);
        this.message = message;
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
}