package com.xlf.utility.mvc;

import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.mvc.holder.ContextHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 结果处理工具类（MVC版本）
 * <p>
 * 提供一组静态方法，用于封装统一的 API 响应结果。支持成功和失败两大类响应的生成。
 * 本类的目标是规范化项目中的返回结构，提供统一的状态码、提示信息以及可选数据。
 * <p>
 * MVC版本功能：
 * - 自动注入请求上下文UUID (context)
 * - 自动计算接口执行耗时 (duration)
 * - 支持链路追踪和性能监控
 * - 返回 ResponseEntity<BaseResponse<T>> 格式适配MVC
 * - 根据错误码自动映射HTTP状态码
 * <p>
 * NOTICE: 该类专为Spring MVC环境设计，返回的是标准的HTTP响应格式。
 * 推荐在控制层调用该工具类。
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
     * 成功输出
     * <p>
     * 输出一个成功的结果，不包含数据。
     * 自动从 ThreadLocal 获取上下文UUID和执行时间。
     *
     * @param message 输出的 message 信息
     * @return 输出一个成功的结果
     */
    public static @NotNull ResponseEntity<BaseResponse<Void>> success(final String message) {
        String contextId = ContextHolder.getContextId();
        Long duration = ContextHolder.getDuration();

        log.info("[{}]{} | {} (耗时: {}ms, context: {})",
                200, "Success", message, duration != null ? duration : "N/A", contextId);

        BaseResponse<Void> response = new BaseResponse<>(contextId, "Success", 200, message, null, duration, null);
        return ResponseEntity.ok(response);
    }

    /**
     * 成功输出
     * <p>
     * 输出一个成功的结果，包含数据。
     * 自动从 ThreadLocal 获取上下文UUID和执行时间。
     *
     * @param message 输出的 message 信息
     * @param data    输出的数据
     * @return 输出一个成功的结果
     */
    public static <T> @NotNull ResponseEntity<BaseResponse<T>> success(final String message, final @NotNull T data) {
        String contextId = ContextHolder.getContextId();
        Long duration = ContextHolder.getDuration();

        log.info("[{}]{} | {}(数据: {}, 耗时: {}ms, context: {})",
                200, "Success", message, data.getClass().getSimpleName(),
                duration != null ? duration : "N/A", contextId);
        BaseResponse<T> response = new BaseResponse<>(contextId, "Success", 200, message, null, duration, data);
        return ResponseEntity.ok(response);
    }

    /**
     * 失败输出
     * <p>
     * 输出一个失败的结果，包含错误码、错误信息和可选数据。
     * 根据错误码自动映射合适的HTTP状态码。
     * <p>
     * 请注意，失败的输出不应该在此直接调用，应当在业务中出现错误直接进行抛出异常，
     * 然后在全局异常处理中进行处理；不应当在代码中直接处理，否则会导致代码的可读性和可维护性降低；
     * 以及无法处理数据库事务的问题。
     *
     * @param errorCode    错误码 - 用于定义系统中的错误码信息，用于统一管理系统中的错误码信息
     * @param errorMessage 错误信息 - 用于定义系统中的错误信息，用于统一管理系统中的错误信息
     * @param data         输出的数据
     * @return 输出一个失败的结果
     */
    public static <T> @NotNull ResponseEntity<BaseResponse<T>> error(
            final ErrorCode errorCode,
            final String errorMessage,
            final @Nullable T data
    ) {
        String contextId = ContextHolder.getContextId();
        Long duration = ContextHolder.getDuration();

        if (data != null) {
            log.warn(
                    "[{}]{} | {}:{}(数据: {}, 耗时: {}ms, context: {})",
                    errorCode.getCode(),
                    errorCode.getOutput(),
                    errorCode.getMessage(),
                    errorMessage,
                    data.getClass().getSimpleName(),
                    duration != null ? duration : "N/A",
                    contextId
            );
        } else {
            log.warn(
                    "[{}]{} | {}:{}(耗时: {}ms, context: {})",
                    errorCode.getCode(),
                    errorCode.getOutput(),
                    errorCode.getMessage(),
                    errorMessage,
                    duration != null ? duration : "N/A",
                    contextId
            );
        }

        BaseResponse<T> response = new BaseResponse<>(
                contextId,
                errorCode.getOutput(),
                errorCode.getCode(),
                errorCode.getMessage(),
                errorMessage,
                duration,
                data
        );

        return ResponseEntity
                .status(HttpStatus.valueOf(errorCode.getCode()/100))
                .body(response);
    }
}
