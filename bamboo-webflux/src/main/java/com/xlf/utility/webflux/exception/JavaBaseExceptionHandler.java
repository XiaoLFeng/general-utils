package com.xlf.utility.webflux.exception;

import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.exception.IJavaException;
import com.xlf.utility.webflux.ResultUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Java基础异常处理器（WebFlux版本）
 * <p>
 * 该类实现了IJavaException接口，专门用于在Spring WebFlux响应式环境中统一处理各种Java基础异常。
 * 通过提供多个基于不同类型异常的处理方法，系统可以捕获特定异常并返回对应的标准化响应消息。
 * <p>
 * 该类使用Spring的 {@code @ExceptionHandler} 注解，监听并捕获运行时可能出现的多种异常，
 * 并对每种异常进行独立处理。例如：文件未找到异常、IO异常、算术异常等。
 * 响应的格式将包含具体错误信息与业务类型化的数据。
 * <p>
 * WebFlux版本返回 Mono<ResponseEntity<BaseResponse<T>>> 格式的响应式响应，
 * 适配Spring WebFlux的响应式HTTP响应处理机制。
 * <p>
 * NOTICE: 该类仅为Spring WebFlux响应式框架环境下定制，依赖于注解驱动的异常处理机制。
 * 在实现此类时，需要确保具体的响应输出格式与业务逻辑保持一致。
 * <p>
 * 适用范围：
 * <ul>
 *   <li>Spring WebFlux响应式Web后端异常监控</li>
 *   <li>大型企业级响应式系统的异常捕获</li>
 *   <li>需要对常见运行时错误进行统一处理的响应式场景</li>
 * </ul>
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
class JavaBaseExceptionHandler implements IJavaException {

    /**
     * 日志记录器。
     */
    protected static final Logger log = LoggerFactory.getLogger(JavaBaseExceptionHandler.class);

    /**
     * 处理IO异常。
     *
     * @param e IO异常
     * @return 返回IO异常信息的响应式响应
     */
    @ExceptionHandler(IOException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<IOException>>> handleIoException(@NotNull IOException e) {
        log.error("IO操作失败 | {}", e.getMessage(), e);
        return ResultUtil.error(ErrorCode.OPERATION_FAILED, "IO操作失败: " + e.getMessage(), null);
    }

    /**
     * 处理空指针异常。
     *
     * @param e 空指针异常
     * @return 返回空指针异常信息的响应式响应
     */
    @ExceptionHandler(NullPointerException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<Object>>> handleNullPointerException(@NotNull NullPointerException e) {
        log.error("空指针异常 | {}", e.getMessage(), e);
        Map<String, String> errorData = Map.of(
                "message", e.getMessage() != null ? e.getMessage() : "null pointer"
        );
        return ResultUtil.error(ErrorCode.SERVER_INTERNAL_ERROR, "空指针异常", errorData);
    }

    /**
     * 处理文件未找到异常。
     *
     * @param e 文件未找到异常
     * @return 返回文件未找到异常信息的响应式响应
     */
    @ExceptionHandler(FileNotFoundException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<FileNotFoundException>>> handleFileNotFoundException(@NotNull FileNotFoundException e) {
        log.warn("文件未找到 | {}", e.getMessage());
        return ResultUtil.error(ErrorCode.NOT_EXIST, "文件未找到: " + e.getMessage(), null);
    }

    /**
     * 处理非法参数异常。
     *
     * @param e 非法参数异常
     * @return 返回非法参数异常信息的响应式响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<IllegalArgumentException>>> handleIllegalArgumentException(@NotNull IllegalArgumentException e) {
        log.warn("非法参数 | {}", e.getMessage());
        return ResultUtil.error(ErrorCode.PARAMETER_ERROR, "非法参数: " + e.getMessage(), null);
    }

    /**
     * 处理类型转换异常。
     *
     * @param e 类型转换异常
     * @return 返回类型转换异常信息的响应式响应
     */
    @ExceptionHandler(ClassCastException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<ClassCastException>>> handleClassCastException(@NotNull ClassCastException e) {
        log.error("类型转换异常 | {}", e.getMessage(), e);
        return ResultUtil.error(ErrorCode.SERVER_INTERNAL_ERROR, "类型转换错误: " + e.getMessage(), null);
    }

    /**
     * 处理数组索引越界异常。
     *
     * @param e 数组索引越界异常
     * @return 返回数组索引越界异常信息的响应式响应
     */
    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<ArrayIndexOutOfBoundsException>>> handleArrayIndexOutOfBoundsException(@NotNull ArrayIndexOutOfBoundsException e) {
        log.error("数组索引越界异常 | {}", e.getMessage(), e);
        return ResultUtil.error(ErrorCode.SERVER_INTERNAL_ERROR, "数组索引越界: " + e.getMessage(), null);
    }

    /**
     * 处理算术异常。
     *
     * @param e 算术异常
     * @return 返回算术异常信息的响应式响应
     */
    @ExceptionHandler(ArithmeticException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<ArithmeticException>>> handleArithmeticException(@NotNull ArithmeticException e) {
        log.error("算术异常 | {}", e.getMessage(), e);
        return ResultUtil.error(ErrorCode.SERVER_INTERNAL_ERROR, "算术运算错误: " + e.getMessage(), null);
    }

    /**
     * 处理不支持操作异常。
     *
     * @param e 不支持操作异常
     * @return 返回不支持操作异常信息的响应式响应
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<UnsupportedOperationException>>> handleUnsupportedOperationException(@NotNull UnsupportedOperationException e) {
        log.warn("不支持的操作异常 | {}", e.getMessage());
        return ResultUtil.error(ErrorCode.OPERATION_FAILED, "不支持的操作: " + e.getMessage(), null);
    }

    /**
     * 处理安全异常。
     *
     * @param e 安全异常
     * @return 返回安全异常信息的响应式响应
     */
    @ExceptionHandler(SecurityException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<SecurityException>>> handleSecurityException(@NotNull SecurityException e) {
        log.error("安全异常 | {}", e.getMessage(), e);
        return ResultUtil.error(ErrorCode.UNAUTHORIZED, "安全违规: " + e.getMessage(), null);
    }

    /**
     * 处理并发修改异常。
     *
     * @param e 并发修改异常
     * @return 返回并发修改异常信息的响应式响应
     */
    @ExceptionHandler(java.util.ConcurrentModificationException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<java.util.ConcurrentModificationException>>> handleConcurrentModificationException(@NotNull java.util.ConcurrentModificationException e) {
        log.error("并发修改异常 | {}", e.getMessage(), e);
        return ResultUtil.error(ErrorCode.SERVER_INTERNAL_ERROR, "并发修改冲突: " + e.getMessage(), null);
    }

    /**
     * 处理超时异常。
     *
     * @param e 超时异常
     * @return 返回超时异常信息的响应式响应
     */
    @ExceptionHandler(TimeoutException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<TimeoutException>>> handleTimeoutException(@NotNull TimeoutException e) {
        log.warn("操作超时异常 | {}", e.getMessage());
        return ResultUtil.error(ErrorCode.TIMEOUT, "操作超时: " + e.getMessage(), null);
    }

    /**
     * 处理非法状态异常。
     *
     * @param e 非法状态异常
     * @return 返回非法状态异常信息的响应式响应
     */
    @ExceptionHandler(IllegalStateException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<IllegalStateException>>> handleIllegalStateException(@NotNull IllegalStateException e) {
        log.error("非法状态异常 | {}", e.getMessage(), e);
        return ResultUtil.error(ErrorCode.OPERATION_FAILED, "非法状态: " + e.getMessage(), null);
    }

    /**
     * 处理未定义的异常。
     *
     * @param e 异常信息
     * @return 返回异常信息的响应式响应
     */
    @ExceptionHandler(Exception.class)
    @Override
    @NotNull
    public Mono<ResponseEntity<BaseResponse<Exception>>> handleException(@NotNull Exception e) {
        log.error("未定义输出异常 | {}", e.getMessage(), e);
        return ResultUtil.error(ErrorCode.SERVER_INTERNAL_ERROR, "未定义输出异常", e);
    }
}
