package com.xlf.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用响应基类，封装了 API 返回的基本信息。
 * <p>
 * BaseResponse 是一个通用类，用于在应用程序的接口中标准化返回结果的结构。
 * 它包含返回的状态码、描述信息、错误消息以及实际返回的数据，
 * 满足大多数 API 响应的需求。
 * <p>
 * 新增功能：
 * - context: 请求上下文UUID，用于链路追踪和问题定位
 * - duration: 接口执行耗时(毫秒)，用于性能监控
 *
 * @param <E> 数据载体的类型，需根据具体场景传入合适泛型。
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<E> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1353845436990556318L;

    /**
     * 请求上下文UUID，用于链路追踪和问题定位
     */
    private String context;

    /**
     * 输出状态（Success/Error等）
     */
    @NotNull
    private String output;

    /**
     * 状态码
     */
    @NotNull
    private Integer code;

    /**
     * 消息
     */
    @NotNull
    private String message;

    /**
     * 错误消息
     */
    private String errorMessage;

    /**
     * 接口执行耗时(毫秒)
     */
    private Long duration;

    /**
     * 数据载体
     */
    private E data;

    /**
     * 兼容旧构造函数（无上下文与耗时信息）
     */
    public BaseResponse(String output, Integer code, String message, String errorMessage, E data) {
        this(null, output, code, message, errorMessage, null, data);
    }

    /**
     * 记录式访问器兼容（与旧 record 版本同名方法）
     */
    public String context() {
        return context;
    }

    public String output() {
        return output;
    }

    public Integer code() {
        return code;
    }

    public String message() {
        return message;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public Long duration() {
        return duration;
    }

    public E data() {
        return data;
    }
}
