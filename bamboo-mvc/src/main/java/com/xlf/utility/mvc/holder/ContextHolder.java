package com.xlf.utility.mvc.holder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * MVC请求上下文管理器
 * <p>
 * 使用ThreadLocal管理每个请求的上下文信息，包括：
 * - 请求唯一标识UUID (UUIDv4)
 * - 请求开始时间戳（用于计算执行时间）
 * <p>
 * 该类采用ThreadLocal实现，确保在多线程环境下每个线程都有独立的上下文信息，
 * 避免并发问题。同时提供自动清理机制，防止内存泄漏。
 * 支持微服务间UUID传递，可从Header中读取已有UUID。
 * 整合了时间统计功能，替代原有的TimingAspectHandler。
 * <p>
 * NOTICE: 该类专为Spring MVC环境设计，在传统Servlet环境下使用ThreadLocal机制。
 * 请确保在请求结束时调用clear()方法清理ThreadLocal，避免在长期运行的线程池中造成内存泄漏。
 *
 * @author xiao_lfeng
 * @since 2.0.0-beta1
 */
@SuppressWarnings("unused")
public class ContextHolder {

    private static final Logger log = LoggerFactory.getLogger("com.xlf.utility.context");

    /**
     * 存储请求上下文信息的ThreadLocal
     */
    private static final ThreadLocal<RequestContext> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 私有构造函数，防止实例化
     */
    @Contract(value = " -> fail", pure = true)
    private ContextHolder() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 初始化请求上下文
     * <p>
     * 为当前线程创建新的请求上下文，生成唯一的UUIDv4作为请求标识，
     * 同时记录请求开始时间戳。如果当前线程已存在上下文，将会被覆盖。
     *
     * @return 新创建的请求上下文UUID
     */
    public static String initContext() {
        String contextId = generateContextId();
        long startTime = System.currentTimeMillis();
        RequestContext context = new RequestContext(contextId, startTime);
        CONTEXT_HOLDER.set(context);
        log.debug("初始化请求上下文 [{}] | 开始时间: {}", contextId, startTime);
        return contextId;
    }

    /**
     * 初始化请求上下文（使用自定义UUID）
     * <p>
     * 使用指定的UUID初始化请求上下文，适用于需要传递外部生成的
     * 上下文标识的场景（如分布式链路追踪、微服务间传递）。
     * 同时记录请求开始时间戳。
     *
     * @param contextId 自定义的上下文UUID
     * @return 设置的上下文UUID
     */
    public static String initContext(String contextId) {
        if (contextId == null || contextId.trim().isEmpty()) {
            return initContext();
        }

        String trimmedId = contextId.trim();
        long startTime = System.currentTimeMillis();
        RequestContext context = new RequestContext(trimmedId, startTime);
        CONTEXT_HOLDER.set(context);
        log.debug("请求上下文 [{}] | 开始时间: {}", trimmedId, startTime);
        return trimmedId;
    }

    /**
     * 获取当前请求的上下文ID
     *
     * @return 当前请求的上下文UUID，如果未初始化则返回null
     */
    @Nullable
    public static String getContextId() {
        RequestContext context = CONTEXT_HOLDER.get();
        return context != null ? context.contextId() : null;
    }

    /**
     * 获取当前请求的开始时间戳
     *
     * @return 当前请求的开始时间戳，如果未初始化则返回null
     */
    @Nullable
    public static Long getStartTime() {
        RequestContext context = CONTEXT_HOLDER.get();
        return context != null ? context.startTime() : null;
    }

    /**
     * 计算当前请求的执行时间
     *
     * @return 当前请求的执行时间（毫秒），如果未初始化则返回null
     */
    @Nullable
    public static Long getDuration() {
        RequestContext context = CONTEXT_HOLDER.get();
        if (context != null) {
            return System.currentTimeMillis() - context.startTime();
        }
        return null;
    }

    /**
     * 检查当前线程是否已初始化上下文
     *
     * @return 如果已初始化返回true，否则返回false
     */
    public static boolean hasContext() {
        return CONTEXT_HOLDER.get() != null;
    }

    /**
     * 清理当前线程的上下文信息
     * <p>
     * 清理ThreadLocal中存储的上下文信息，防止内存泄漏。
     * 建议在请求处理完成后（如在Filter或AOP的finally块中）调用此方法。
     * 在清理前会计算并记录总执行时间。
     * <p>
     * NOTICE: 此方法必须在请求处理完成后调用，否则可能导致内存泄漏。
     */
    public static void clear() {
        RequestContext context = CONTEXT_HOLDER.get();
        if (context != null) {
            long duration = System.currentTimeMillis() - context.startTime();
            log.debug("清理上下文 [{}] | 总执行时间: {}ms", context.contextId(), duration);
        }
        CONTEXT_HOLDER.remove();
    }

    /**
     * 生成上下文ID
     * <p>
     * 生成标准的UUIDv4格式
     * 示例：550e8400-e29b-41d4-a716-446655440000
     *
     * @return 生成的UUIDv4字符串
     */
    private static String generateContextId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 请求上下文数据结构
     */
    private record RequestContext(String contextId, long startTime) {
    }

}
