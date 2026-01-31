package com.xlf.utility.exception.library;

import com.xlf.utility.ErrorCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义业务异常类，用于表示程序中与业务逻辑相关的异常情况。
 * <p>
 * 此类继承自 {@code RuntimeException}，用于在程序运行期间抛出非受检异常，主要用于描述特定业务场景中的错误。
 * 可以携带详细的错误消息、错误代码、附加数据以及错误输出标识，使错误更易于诊断和处理。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Slf4j
@Getter
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
        log.warn(
                "业务异常 | 错误代码: {}, 错误消息: {}, 附加数据: {}, 错误输出标识: {}",
                errorCode, errorMessage, data, errorOutput
        );
    }

    /**
     * 构造函数，初始化错误消息和错误代码。
     *
     * @param message   异常的错误消息
     * @param errorCode 异常的错误代码
     */
    public BusinessException(String message, ErrorCode errorCode) {
        this(message, errorCode, null, false);
    }

    /**
     * 构造函数，初始化错误消息、错误代码以及附加数据。
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
     *
     * @param message     异常的错误消息
     * @param errorCode   异常的错误代码
     * @param errorOutput 是否输出详细的错误信息
     */
    public BusinessException(String message, ErrorCode errorCode, boolean errorOutput) {
        this(message, errorCode, null, errorOutput);
    }
}
