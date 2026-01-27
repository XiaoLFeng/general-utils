package com.xlf.utility.webflux;

import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.webflux.holder.ContextHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * 结果处理工具类（WebFlux版本）
 * <p>
 * 提供一组静态方法，用于封装统一的 API 响应结果。支持成功和失败两大类响应的生成。
 * 本类的目标是规范化项目中的返回结构，提供统一的状态码、提示信息以及可选数据。
 * <p>
 * WebFlux版本功能：
 * - 自动注入请求上下文UUID (context)
 * - 自动计算接口执行耗时 (duration)
 * - 支持链路追踪和性能监控
 * - 返回 Mono&lt;ResponseEntity&lt;BaseResponse&lt;T&gt;&gt;&gt; 格式适配WebFlux
 * - 根据错误码自动映射HTTP状态码
 * - 支持响应式编程模式
 * <p>
 * NOTICE: 该类专为Spring WebFlux响应式环境设计，返回的是响应式HTTP响应格式。
 * 推荐在响应式控制层调用该工具类。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public class ResultUtil {

    /**
     * 日志记录器
     */
    private static final Logger log = LoggerFactory.getLogger(ResultUtil.class);

    /**
     * 获取上下文信息（ID和执行时间）
     * <p>
     * 从Reactor Context中获取上下文信息，专为WebFlux响应式环境设计。
     * WebFlux环境下完全使用Reactor Context，不再依赖ThreadLocal。
     *
     * @return 包含上下文信息的Mono
     */
    private static Mono<ContextInfo> getContextInfo() {
        return Mono.deferContextual(contextView -> {
            String contextId = ContextHolder.getContextId(contextView);
            Long duration = ContextHolder.getDuration(contextView);
            return Mono.just(new ContextInfo(contextId, duration));
        });
    }

    /**
     * 上下文信息记录类
     */
    private record ContextInfo(String contextId, Long duration) {
    }

    /**
     * 成功输出
     * <p>
     * 输出一个成功的结果，不包含数据。
     * 从Reactor Context获取上下文UUID和执行时间。
     *
     * @param message 输出的 message 信息
     * @return 输出一个成功的结果的响应式响应
     */
    public static @NotNull Mono<ResponseEntity<BaseResponse<Void>>> success(final String message) {
        return getContextInfo().map(contextInfo -> {
            log.info("[{}]{} | {} (耗时: {}ms, context: {})",
                    200, "Success", message,
                    contextInfo.duration() != null ? contextInfo.duration() : "N/A",
                    contextInfo.contextId());

            BaseResponse<Void> response = new BaseResponse<>(
                    contextInfo.contextId(),
                    "Success",
                    200,
                    message,
                    null,
                    contextInfo.duration(),
                    null
            );
            return ResponseEntity.ok(response);
        });
    }

    /**
     * 成功输出
     * <p>
     * 输出一个成功的结果，包含数据。
     * 从Reactor Context获取上下文UUID和执行时间。
     *
     * @param message 输出的 message 信息
     * @param data    输出的数据
     * @return 输出一个成功的结果的响应式响应
     */
    public static <T> @NotNull Mono<ResponseEntity<BaseResponse<T>>> success(final String message, final @NotNull T data) {
        return getContextInfo().map(contextInfo -> {
            log.info("[{}]{} | {}(数据: {}, 耗时: {}ms, context: {})",
                    200, "Success", message, data.getClass().getSimpleName(),
                    contextInfo.duration() != null ? contextInfo.duration() : "N/A",
                    contextInfo.contextId());

            BaseResponse<T> response = new BaseResponse<>(
                    contextInfo.contextId(),
                    "Success",
                    200,
                    message,
                    null,
                    contextInfo.duration(),
                    data
            );
            return ResponseEntity.ok(response);
        });
    }

    /**
     * 失败输出
     * <p>
     * 输出一个失败的结果，包含错误码、错误信息和可选数据。
     * 根据错误码自动映射合适的HTTP状态码。
     * 从Reactor Context获取上下文UUID和执行时间，确保异步异常处理时也能获取到正确的上下文。
     * <p>
     * 请注意，失败的输出不应该在此直接调用，应当在业务中出现错误直接进行抛出异常，
     * 然后在全局异常处理中进行处理；不应当在代码中直接处理，否则会导致代码的可读性和可维护性降低；
     * 以及无法处理数据库事务的问题。
     *
     * @param errorCode    错误码 - 用于定义系统中的错误码信息，用于统一管理系统中的错误码信息
     * @param errorMessage 错误信息 - 用于定义系统中的错误信息，用于统一管理系统中的错误信息
     * @param data         输出的数据
     * @return 输出一个失败的结果的响应式响应
     */
    public static <T> @NotNull Mono<ResponseEntity<BaseResponse<T>>> error(
            final ErrorCode errorCode,
            final String errorMessage,
            final @Nullable T data
    ) {
        return getContextInfo().map(contextInfo -> {
            if (data != null) {
                log.warn(
                        "[{}]{} | {}:{}(数据: {}, 耗时: {}ms, context: {})",
                        errorCode.getCode(),
                        errorCode.getOutput(),
                        errorCode.getMessage(),
                        errorMessage,
                        data.getClass().getSimpleName(),
                        contextInfo.duration() != null ? contextInfo.duration() : "N/A",
                        contextInfo.contextId()
                );
            } else {
                log.warn(
                        "[{}]{} | {}:{}(耗时: {}ms, context: {})",
                        errorCode.getCode(),
                        errorCode.getOutput(),
                        errorCode.getMessage(),
                        errorMessage,
                        contextInfo.duration() != null ? contextInfo.duration() : "N/A",
                        contextInfo.contextId()
                );
            }

            BaseResponse<T> response = new BaseResponse<>(
                    contextInfo.contextId(),
                    errorCode.getOutput(),
                    errorCode.getCode(),
                    errorCode.getMessage(),
                    errorMessage,
                    contextInfo.duration(),
                    data
            );

            return ResponseEntity
                    .status(HttpStatus.valueOf(errorCode.getCode() / 100))
                    .body(response);
        });
    }
}
