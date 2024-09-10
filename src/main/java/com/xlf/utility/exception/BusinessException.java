package com.xlf.utility.exception;

import com.xlf.utility.ErrorCode;

/**
 * 自定义业务异常类，用于处理业务中的特定业务异常。
 * <p>
 * 该异常类继承自 {@link RuntimeException}，并包含了错误消息、错误代码、可选的数据对象以及错误输出标识。
 * 可以通过多种构造函数初始化，以适应不同的异常处理需求。
 *
 * @since v1.0.9-beta.1.0
 * @version v1.0.9-beta.1.0
 * @author xiao_lfeng
 */
@SuppressWarnings("unused")
public class BusinessException extends RuntimeException {

    /**
     * 错误消息，描述异常的详细信息。
     */
    private final String errorMessage;

    /**
     * 错误代码，指示具体的错误类型。
     */
    private final ErrorCode errorCode;

    /**
     * 附加数据，用于携带与错误相关的额外信息。
     */
    private final Object data;

    /**
     * 错误输出标识，用于标记是否需要输出详细的错误信息。
     */
    private final boolean errorOutput;

    /**
     * 主构造函数，初始化业务异常的所有属性。
     *
     * @param errorMessage 异常的错误消息
     * @param errorCode    异常的错误代码
     * @param data         与异常相关的附加数据
     * @param errorOutput  是否输出错误详情的标识
     */
    public BusinessException(String errorMessage, ErrorCode errorCode, Object data, boolean errorOutput) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.data = data;
        this.errorOutput = errorOutput;
    }

    /**
     * 构造函数，初始化错误消息和错误代码。
     * <p>
     * 默认情况下，附加数据为 {@code null}，且不输出详细错误信息。
     *
     * @param message   异常的错误消息
     * @param errorCode 异常的错误代码
     */
    public BusinessException(String message, ErrorCode errorCode) {
        this(message, errorCode, null, false);
    }

    /**
     * 构造函数，初始化错误消息、错误代码以及附加数据。
     * <p>
     * 默认情况下，不输出详细错误信息。
     *
     * @param message   异常的错误消息
     * @param errorCode 异常的错误代码
     * @param data      与异常相关的附加数据
     */
    public BusinessException(String message, ErrorCode errorCode, Object data) {
        this(message, errorCode, data, false);
    }

    /**
     * 构造函数，初始化错误消息、错误代码，并指定是否输出详细的错误信息。
     * <p>
     * 默认情况下，附加数据为 {@code null}。
     *
     * @param message     异常的错误消息
     * @param errorCode   异常的错误代码
     * @param errorOutput 是否输出详细的错误信息
     */
    public BusinessException(String message, ErrorCode errorCode, boolean errorOutput) {
        this(message, errorCode, null, errorOutput);
    }

    /**
     * 获取错误消息。
     *
     * @return 返回异常的错误消息
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * 获取错误代码。
     *
     * @return 返回异常的错误代码
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * 获取附加数据。
     *
     * @return 返回与异常相关的附加数据，可能为 {@code null}
     */
    public Object getData() {
        return data;
    }

    /**
     * 获取错误输出标识。
     *
     * @return {@code true} 表示需要输出详细的错误信息，{@code false} 表示不输出详细信息
     */
    public boolean isErrorOutput() {
        return errorOutput;
    }
}