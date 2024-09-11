package com.xlf.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.NotNull;

/**
 * 自定义返回结果
 * <p>
 * 用于返回结果, 包含输出, 状态码, 消息, 错误消息, 数据。
 *
 * @author xiao_lfeng
 * @version v1.0.1
 * @since v1.0.1
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("unused")
public record BaseResponse<E>(
        @NotNull String output,
        @NotNull Integer code,
        @NotNull String message,
        String errorMessage,
        E data
) {
    /**
     * 构造函数, 用于初始化返回结果
     *
     * @param output       输出
     * @param code         状态码
     * @param message      消息
     * @param errorMessage 错误消息
     * @param data         数据
     */
    public BaseResponse(String output, Integer code, String message, String errorMessage, E data) {
        this.output = output;
        this.code = code;
        this.message = message;
        this.errorMessage = errorMessage;
        this.data = data;
    }
}
