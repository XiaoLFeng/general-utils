package com.xlf.utility.webflux.holder;

import com.xlf.utility.constant.ContextConstant;
import org.jetbrains.annotations.Contract;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

/**
 * WebFlux响应式上下文管理器
 * <p>
 * 专为Spring WebFlux响应式编程设计，提供从Reactor Context中获取上下文信息的工具方法。
 * 在WebFlux环境中，使用Reactor Context在响应式管道中传播上下文数据，
 * 完全替代ThreadLocal机制，适用于异步响应式环境。
 * <p>
 * 主要功能：
 * - 从Reactor Context中获取请求上下文ID和执行时间
 * - 专为WebFlux响应式环境设计，替代ThreadLocal
 * - 支持异步操作中的上下文传递和获取
 * <p>
 * 使用场景：
 * - WebFlux异常处理器中获取上下文信息
 * - 异步业务逻辑中的上下文访问
 * - 响应式编程中的链路追踪
 * <p>
 * NOTICE: 该类仅在响应式环境下有效，需要配合使用了contextWrite的响应式流。
 * 对于传统MVC环境，应使用MVC模块的ContextHolder。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public class ContextHolder {

    /**
     * 私有构造函数，防止实例化
     */
    @Contract(value = " -> fail", pure = true)
    private ContextHolder() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 从Reactor Context中获取请求上下文ID
     * <p>
     * 该方法返回一个Mono，在响应式流中延迟获取Context中的上下文ID。
     * 如果Context中不存在上下文ID，返回空的Mono。
     * <p>
     * 使用示例：
     * <pre>
     * ContextHolder.getContextId()
     *     .doOnNext(contextId -> log.info("Context ID: {}", contextId))
     *     .defaultIfEmpty("unknown")
     *     .subscribe();
     * </pre>
     *
     * @return 包含上下文ID的Mono，如果不存在则为空Mono
     */
    public static Mono<String> getContextId() {
        return Mono.deferContextual(contextView ->
                Mono.justOrEmpty(contextView.getOrEmpty(ContextConstant.CONTEXT_KEY))
        );
    }

    /**
     * 从Reactor ContextView中同步获取请求上下文ID
     * <p>
     * 该方法直接从提供的ContextView中提取上下文ID，适用于已经获得
     * ContextView实例的场景。如果Context中不存在上下文ID，返回null。
     * <p>
     * 使用示例：
     * <pre>
     * Mono.deferContextual(contextView -> {
     *     String contextId = ContextHolder.getContextId(contextView);
     *     // 使用contextId...
     *     return Mono.just(result);
     * });
     * </pre>
     *
     * @param contextView Reactor的ContextView实例
     * @return 上下文ID字符串，如果不存在则返回null
     */
    public static String getContextId(ContextView contextView) {
        return contextView.getOrDefault(ContextConstant.CONTEXT_KEY, null);
    }

    /**
     * 检查Reactor Context中是否存在上下文ID
     * <p>
     * 该方法返回一个Mono&lt;Boolean&gt;，用于检查当前响应式上下文中
     * 是否包含请求上下文ID。
     *
     * @return 包含检查结果的Mono，存在返回true，不存在返回false
     */
    public static Mono<Boolean> hasContext() {
        return Mono.deferContextual(contextView ->
                Mono.just(contextView.hasKey(ContextConstant.CONTEXT_KEY))
        );
    }

    /**
     * 从Reactor ContextView中同步检查是否存在上下文ID
     *
     * @param contextView Reactor的ContextView实例
     * @return 如果存在返回true，否则返回false
     */
    public static boolean hasContext(ContextView contextView) {
        return contextView.hasKey(ContextConstant.CONTEXT_KEY);
    }

    /**
     * 从Reactor Context中获取请求开始时间
     * <p>
     * 该方法返回一个Mono，在响应式流中延迟获取Context中的开始时间。
     * 如果Context中不存在开始时间，返回空的Mono。
     *
     * @return 包含开始时间的Mono，如果不存在则为空Mono
     */
    public static Mono<Long> getStartTime() {
        return Mono.deferContextual(contextView ->
                Mono.justOrEmpty(contextView.getOrEmpty(ContextConstant.START_TIME_KEY))
        );
    }

    /**
     * 从Reactor ContextView中同步获取请求开始时间
     *
     * @param contextView Reactor的ContextView实例
     * @return 开始时间戳，如果不存在则返回null
     */
    public static Long getStartTime(ContextView contextView) {
        return contextView.getOrDefault(ContextConstant.START_TIME_KEY, null);
    }

    /**
     * 从Reactor Context中计算请求执行时间
     * <p>
     * 该方法返回一个Mono，计算当前时间与请求开始时间的差值。
     * 如果Context中不存在开始时间，返回空的Mono。
     *
     * @return 包含执行时间（毫秒）的Mono，如果无法计算则为空Mono
     */
    public static Mono<Long> getDuration() {
        return getStartTime().map(startTime -> System.currentTimeMillis() - startTime);
    }

    /**
     * 从Reactor ContextView中同步计算请求执行时间
     *
     * @param contextView Reactor的ContextView实例
     * @return 执行时间（毫秒），如果无法计算则返回null
     */
    public static Long getDuration(ContextView contextView) {
        Long startTime = getStartTime(contextView);
        if (startTime != null) {
            return System.currentTimeMillis() - startTime;
        }
        return null;
    }
}
