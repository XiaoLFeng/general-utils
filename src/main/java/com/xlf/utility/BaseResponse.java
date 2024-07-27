package com.xlf.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * BaseResponse
 * <hr/>
 * 自定义返回结果
 *
 * @author xiao_lfeng
 * @version v1.0.1
 * @since v1.0.1
 */
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("unused")
public record BaseResponse<E>(String output, Integer code, String message, String errorMessage, E data) {

    /**
     * BaseResponse
     * <hr/>
     * 构造函数, 用于初始化返回结果
     *
     * @param output 输出
     * @param code 状态码
     * @param message 消息
     * @param data 数据
     */
    public BaseResponse(String output, Integer code, String message, String errorMessage, E data) {
        this.output = output;
        this.code = code;
        this.message = message;
        this.errorMessage = errorMessage;
        this.data = data;
        log.info("============================================================");
    }
}
