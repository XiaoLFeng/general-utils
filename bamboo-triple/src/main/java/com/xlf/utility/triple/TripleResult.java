package com.xlf.utility.triple;

import com.xlf.utility.ErrorCode;
import com.xlf.utility.mvc.holder.ContextHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Triple 协议统一响应工具类
 * <p>
 * 提供便捷的方法用于构建基于 {@link TripleResponse} 的成功与失败响应，
 * 适用于所有基于 Dubbo Triple 协议的 RPC 服务，用于标准化返回结果结构。
 * <p>
 * 该类提供多种构造方法，包括创建带数据、带消息以及包含系统异常的响应，
 * 以便灵活适配不同的返回场景,从而提高代码复用性与可维护性。
 * <p>
 * 新增功能：
 * - 支持 {@link ErrorCode} 枚举，统一错误码管理
 * - 自动注入请求上下文UUID (context) 和执行耗时 (duration)
 * - 支持链路追踪和性能监控
 * - 完整的日志记录功能
 * <p>
 * NOTICE: 方法仅用于 {@link TripleResponse} 标准响应格式的服务中。
 *
 * @author xiao_lfeng
 * @version v1.1.0-SNAPSHOT
 * @since v1.1.0-SNAPSHOT
 */
@SuppressWarnings("unused")
public class TripleResult {

    /**
     * 日志记录器
     */
    private static final Logger log = LoggerFactory.getLogger(TripleResult.class);

    // ============================== 成功响应方法 ==============================

    /**
     * 创建成功响应
     * <p>
     * 自动从 ContextHolder 获取上下文信息和执行耗时。
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> TripleResponse<T> success(T data) {
        return success(data, "操作成功");
    }

    /**
     * 创建成功响应
     * <p>
     * 自动从 ContextHolder 获取上下文信息和执行耗时。
     *
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> TripleResponse<T> success() {
        return success(null, "操作成功");
    }

    /**
     * 创建成功响应（带自定义消息）
     * <p>
     * 自动从 ContextHolder 获取上下文信息和执行耗时。
     *
     * @param data    响应数据
     * @param message 响应消息
     * @param <T>     数据类型
     * @return 成功响应
     */
    public static <T> TripleResponse<T> success(T data, String message) {
        String contextId = ContextHolder.getContextId();
        Long duration = ContextHolder.getDuration();

        log.info("[200]Success | {}(数据: {}, 耗时: {}ms, context: {})",
                message,
                data != null ? data.getClass().getSimpleName() : "null",
                duration != null ? duration : "N/A",
                contextId);

        return TripleResponse.<T>builder()
                .context(contextId)
                .success(true)
                .code("200")
                .message(message)
                .data(data)
                .duration(duration)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    // ============================== 失败响应方法（ErrorCode 版本）==============================

    /**
     * 创建失败响应（使用 ErrorCode）
     * <p>
     * 使用统一的 ErrorCode 枚举创建错误响应，自动获取上下文信息。
     *
     * @param errorCode 错误码枚举
     * @param <T>       数据类型
     * @return 失败响应
     */
    public static <T> TripleResponse<T> error(@NotNull ErrorCode errorCode) {
        return error(errorCode, null, null);
    }

    /**
     * 创建失败响应（使用 ErrorCode 和详细错误信息）
     * <p>
     * 使用统一的 ErrorCode 枚举创建错误响应，支持详细错误信息，自动获取上下文信息。
     *
     * @param errorCode    错误码枚举
     * @param errorMessage 详细错误信息（可选）
     * @param <T>          数据类型
     * @return 失败响应
     */
    public static <T> TripleResponse<T> error(@NotNull ErrorCode errorCode, @Nullable String errorMessage) {
        return error(errorCode, errorMessage, null);
    }

    /**
     * 创建失败响应（使用 ErrorCode、详细错误信息和数据）
     * <p>
     * 使用统一的 ErrorCode 枚举创建错误响应，支持详细错误信息和附加数据，自动获取上下文信息。
     *
     * @param errorCode    错误码枚举
     * @param errorMessage 详细错误信息（可选）
     * @param data         响应数据（可选）
     * @param <T>          数据类型
     * @return 失败响应
     */
    public static <T> TripleResponse<T> error(
            @NotNull ErrorCode errorCode,
            @Nullable String errorMessage,
            @Nullable T data
    ) {
        String contextId = ContextHolder.getContextId();
        Long duration = ContextHolder.getDuration();

        // 组合完整的错误消息
        String fullMessage = errorCode.getMessage();
        if (errorMessage != null && !errorMessage.trim().isEmpty()) {
            fullMessage = errorCode.getMessage() + ": " + errorMessage;
        }

        if (data != null) {
            log.warn(
                    "[{}]{} | {}(数据: {}, 耗时: {}ms, context: {})",
                    errorCode.getCode(),
                    errorCode.getOutput(),
                    fullMessage,
                    data.getClass().getSimpleName(),
                    duration != null ? duration : "N/A",
                    contextId
            );
        } else {
            log.warn(
                    "[{}]{} | {}(耗时: {}ms, context: {})",
                    errorCode.getCode(),
                    errorCode.getOutput(),
                    fullMessage,
                    duration != null ? duration : "N/A",
                    contextId
            );
        }

        return TripleResponse.<T>builder()
                .context(contextId)
                .success(false)
                .code(String.valueOf(errorCode.getCode()))
                .message(fullMessage)
                .data(data)
                .duration(duration)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建失败响应（系统异常）
     * <p>
     * 自动从 ContextHolder 获取上下文信息和执行耗时。
     *
     * @param exception 异常信息
     * @param <T>       数据类型
     * @return 失败响应
     */
    public static <T> TripleResponse<T> error(@NotNull Exception exception) {
        String contextId = ContextHolder.getContextId();
        Long duration = ContextHolder.getDuration();

        log.error("[500]ServerInternalError | 系统异常: {}(耗时: {}ms, context: {})",
                exception.getMessage(), duration != null ? duration : "N/A", contextId, exception);

        return TripleResponse.<T>builder()
                .context(contextId)
                .success(false)
                .code("500")
                .message("系统异常: " + exception.getMessage())
                .duration(duration)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
