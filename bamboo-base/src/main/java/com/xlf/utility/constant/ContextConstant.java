package com.xlf.utility.constant;

/**
 * 上下文常量类
 * <p>
 * 提供系统中上下文管理相关的常量定义。
 * 用于在 ThreadLocal（MVC）和 Reactor Context（WebFlux）中存储和获取上下文信息。
 * <p>
 * 主要功能：
 * - 定义上下文ID的存储键
 * - 定义请求开始时间的存储键
 * - 支持 MVC 和 WebFlux 两种模式
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public class ContextConstant {

    /**
     * 上下文ID键
     * <p>
     * 用于在 ThreadLocal 或 Reactor Context 中存储请求上下文ID。
     * 该ID通常为 UUIDv4 格式，用于分布式链路追踪和日志关联。
     */
    public static final String CONTEXT_KEY = "CONTEXT_ID";

    /**
     * 请求开始时间键
     * <p>
     * 用于在 ThreadLocal 或 Reactor Context 中存储请求开始时间戳。
     * 用于计算接口执行耗时，支持性能监控。
     */
    public static final String START_TIME_KEY = "START_TIME";

    /**
     * 私有构造函数，防止实例化
     */
    private ContextConstant() {
        throw new UnsupportedOperationException("ContextConstant 是常量类，不允许实例化");
    }
}
