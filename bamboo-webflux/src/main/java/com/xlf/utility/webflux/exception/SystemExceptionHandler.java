package com.xlf.utility.webflux.exception;

import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.webflux.ResultUtil;
import reactor.core.publisher.Mono;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.MissingRequestValueException;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.util.HashMap;

/**
 * 统一 WebFlux 异常处理类
 * <p>
 * 该类专为Spring WebFlux设计，通过定义一组方法来捕获和处理常见的异常类型，
 * 旨在为 Spring Boot WebFlux 应用程序提供一个标准化的异常处理机制。
 * 对于每个指定的异常类型，该类会为用户返回一个包含错误信息和对应说明的 HTTP响应。
 * <p>
 * WebFlux版本处理的是响应式Web框架特有的异常类型，与传统MVC的异常处理有所不同。
 * <p>
 * NOTICE: 确保在 Spring 环境中使用该类时添加了相应的 {@code @RestControllerAdvice} 注解，
 *         以便框架能够正确检测并调用这些异常处理方法。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
@Order(-1)
@RestControllerAdvice
public class SystemExceptionHandler extends PublicExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(SystemExceptionHandler.class);

    /**
     * 处理方法不被允许异常。
     *
     * @param e 方法不被允许异常
     * @return 返回方法不被允许异常信息
     */
    @ExceptionHandler(MethodNotAllowedException.class)
    public Mono<ResponseEntity<BaseResponse<HashMap<String, Object>>>> handleMethodNotAllowedException(@NotNull MethodNotAllowedException e) {
        log.warn("请求方法不支持 | 获取的方法 [{}] ,支持的方法 {}", e.getHttpMethod(), e.getSupportedMethods());
        HashMap<String, Object> data = new HashMap<>();
        data.put("method", e.getHttpMethod());
        data.put("supported", e.getSupportedMethods().toString());
        return ResultUtil.error(ErrorCode.METHOD_NOT_ALLOWED, "请求方法不支持", data);
    }

    /**
     * 处理请求值缺失异常。
     *
     * @param e 请求值缺失异常
     * @return 返回请求值缺失异常信息
     */
    @ExceptionHandler(MissingRequestValueException.class)
    public Mono<ResponseEntity<BaseResponse<Void>>> handleMissingRequestValueException(@NotNull MissingRequestValueException e) {
        log.warn("请求值缺失 | 缺失的值: [{}]", e.getMessage());
        return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "请求参数缺失: " + e.getMessage(), null);
    }

    /**
     * 处理数据绑定异常。
     *
     * @param e 数据绑定异常
     * @return 返回数据绑定异常信息
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<BaseResponse<java.util.List<String>>>> handleWebExchangeBindException(@NotNull WebExchangeBindException e) {
        log.warn("参数校验错误 | 错误 {} 个 ", e.getBindingResult().getErrorCount());
        e.getFieldErrors().forEach(it -> log.debug("\t\t<{}>[{}]: {}", it.getField(), it.getRejectedValue(), it.getDefaultMessage()));
        return ResultUtil.error(ErrorCode.PARAMETER_ERROR,
            e.getAllErrors().get(0).getDefaultMessage(),
            e.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
    }

    /**
     * 处理服务器Web输入异常。
     *
     * @param e 服务器Web输入异常
     * @return 返回服务器Web输入异常信息
     */
    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<BaseResponse<Void>>> handleServerWebInputException(@NotNull ServerWebInputException e) {
        log.warn("服务器Web输入异常 | {}", e.getMessage());
        return ResultUtil.error(ErrorCode.PARAMETER_ERROR, "请求参数错误: " + e.getMessage(), null);
    }

    /**
     * 处理不支持的媒体类型异常。
     *
     * @param e 不支持的媒体类型异常
     * @return 返回不支持的媒体类型异常信息
     */
    @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
    public Mono<ResponseEntity<BaseResponse<Void>>> handleUnsupportedMediaTypeException(@NotNull UnsupportedMediaTypeStatusException e) {
        log.warn("不支持的媒体类型 | {}", e.getMessage());
        return ResultUtil.error(ErrorCode.METHOD_NOT_ALLOWED, "不支持的媒体类型", null);
    }

    /**
     * 处理不可接受状态异常。
     *
     * @param e 不可接受状态异常
     * @return 返回不可接受状态异常信息
     */
    @ExceptionHandler(NotAcceptableStatusException.class)
    public Mono<ResponseEntity<BaseResponse<Void>>> handleNotAcceptableStatusException(@NotNull NotAcceptableStatusException e) {
        log.warn("不可接受的响应类型 | {}", e.getMessage());
        return ResultUtil.error(ErrorCode.METHOD_NOT_ALLOWED, "不可接受的响应类型", null);
    }

    /**
     * 处理响应状态异常。
     *
     * @param e 响应状态异常
     * @return 返回响应状态异常信息
     */
    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<BaseResponse<Void>>> handleResponseStatusException(@NotNull ResponseStatusException e) {
        log.error("响应状态异常 | 状态码: {} | 消息: {}", e.getStatusCode(), e.getMessage());

        ErrorCode errorCode = switch (e.getStatusCode().value()) {
            case 400 -> ErrorCode.PARAMETER_ERROR;
            case 401 -> ErrorCode.UNAUTHORIZED;
            case 403 -> ErrorCode.FORBIDDEN;
            case 404 -> ErrorCode.RESOURCE_NOT_FOUND;
            case 405 -> ErrorCode.METHOD_NOT_ALLOWED;
            case 503 -> ErrorCode.SERVICE_UNAVAILABLE;
            default -> ErrorCode.SERVER_INTERNAL_ERROR;
        };

        return ResultUtil.error(errorCode, e.getReason() != null ? e.getReason() : "服务器错误", null);
    }
}
