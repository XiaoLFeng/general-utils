package com.xlf.utility.exception.library;

import lombok.Getter;
import org.jetbrains.annotations.Contract;

/**
 * 开发者异常类，用于标识代码运行过程中需要开发者注意的问题。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Getter
@SuppressWarnings("unused")
public class DeveloperException extends RuntimeException {
    /**
     * 错误类型
     */
    private final ErrorType errorType;

    /**
     * 构造函数，初始化开发者错误异常的所有属性。
     *
     * @param message 异常的错误消息
     */
    public DeveloperException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    /**
     * 错误类型枚举类，用于定义不同类型的开发错误信息。
     */
    public enum ErrorType {
        INVALID_INPUT("InvalidInput", "输入参数不合法或格式错误"),
        CONFIGURATION_ERROR("ConfigurationError", "配置错误或缺失"),
        LOGIC_ERROR("LogicError", "程序逻辑错误"),
        UNEXPECTED_BEHAVIOR("UnexpectedBehavior", "程序出现意外行为");

        private final String output;
        private final String description;

        @Contract(pure = true)
        ErrorType(String output, String description) {
            this.output = output;
            this.description = description;
        }

        @Contract(pure = true)
        public String getOutput() {
            return output;
        }

        @Contract(pure = true)
        public String getDescription() {
            return description;
        }
    }
}
