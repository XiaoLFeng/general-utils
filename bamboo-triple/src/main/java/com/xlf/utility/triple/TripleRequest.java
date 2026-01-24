package com.xlf.utility.triple;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * Triple 协议统一请求基类
 * <p>
 * 所有基于 Dubbo Triple 协议的 RPC 请求都应该继承此基类，
 * 提供统一的请求参数结构和上下文传递机制。
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class TripleRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -2744697235476445187L;

    /**
     * 上下文信息（用于链路追踪、身份验证等）
     */
    private String ctx;

    /**
     * 请求时间戳（毫秒）
     */
    private Long timestamp;

    /**
     * 请求来源标识（可选，标识调用方系统）
     */
    private String source;

    /**
     * API版本号（可选，用于接口版本管理）
     */
    private String version;

    /**
     * 初始化请求参数
     *
     * @param source 请求来源标识
     * @param <T>    TripleRequest 的返回内容
     */
    public <T extends TripleRequest> T init(@NotNull String source) {
        this.timestamp = System.currentTimeMillis();
        this.ctx = com.xlf.utility.mvc.holder.ContextHolder.getContextId();
        this.source = source;
        // 移除 StringConstant.SYSTEM_VERSION 依赖，避免模块耦合
        this.version = "1.0.0";
        return (T) this;
    }

    /**
     * 验证请求参数的完整性
     *
     * @return 验证结果
     */
    public abstract boolean validate();

    /**
     * 获取请求摘要信息（用于日志记录）
     *
     * @return 请求摘要
     */
    public abstract String getSummary();
}
