package com.xlf.utility.triple;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * Triple 协议统一响应格式
 * <p>
 * 所有基于 Dubbo Triple 协议的 RPC 服务都应该使用此响应格式，
 * 确保对外提供的服务接口具有一致的响应结构。
 * <p>
 * 新增功能：
 * - context: 请求上下文UUID，用于链路追踪和问题定位
 * - duration: 接口执行耗时(毫秒)，用于性能监控
 *
 * @param <T> 响应数据类型
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TripleResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 4819559813396507004L;

    /**
     * 请求上下文UUID（用于链路追踪）
     */
    private String context;

    /**
     * 响应是否成功
     */
    private Boolean success;

    /**
     * 响应状态码
     */
    private String code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 接口执行耗时（毫秒，用于性能监控）
     */
    private Long duration;

    /**
     * 响应时间戳（毫秒）
     */
    private Long timestamp;
}
