package com.xlf.utility.mvc.exception;

import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.exception.library.BusinessException;
import com.xlf.utility.mvc.ResultUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Objects;

/**
 * 统一 SpringBoot 异常处理类 (MVC版本)
 * <p>
 * 该类专为Spring MVC设计，通过定义一组方法来捕获和处理常见的异常类型，
 * 旨在为 Spring Boot MVC 应用程序提供一个标准化的异常处理机制。
 * 对于每个指定的异常类型，该类会为用户返回一个包含错误信息和对应说明的 HTTP响应。
 * <p>
 * NOTICE: 确保在 Spring 环境中使用该类时添加了相应的 {@code @RestControllerAdvice} 注解，
 *         以便框架能够正确检测并调用这些异常处理方法。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
@RestControllerAdvice
public class SystemExceptionHandler extends PublicExceptionHandler {
    Logger log = LoggerFactory.getLogger(SystemExceptionHandler.class);

    /**
     * 处理请求方法不支持异常。
     *
     * @param e 请求方法不支持异常
     * @return 返回请求方法不支持异常信息
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse<HashMap<String, Object>>> handleHttpRequestMethodNotSupportedException(@NotNull HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不支持 | 获取的方法 [{}] ,需要的方法 {}", e.getMethod(), e.getSupportedHttpMethods());
        HashMap<String, Object> data = new HashMap<>();
        data.put("method", e.getMethod());
        data.put("supported", Objects.requireNonNull(e.getSupportedHttpMethods()).toString());
        return ResultUtil.error(ErrorCode.METHOD_NOT_ALLOWED, "请求方法不支持", data);
    }

    /**
     * 处理请求头缺失异常。
     *
     * @param e 请求头缺失异常
     * @return 返回请求头缺失异常信息
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<BaseResponse<Void>> handleMissingRequestHeaderException(@NotNull MissingRequestHeaderException e) {
        log.warn("请求头缺失 | 缺失的请求头 [{}]", e.getHeaderName());
        if ("Authorization".equals(e.getHeaderName()) || "X-User-UUID".equals(e.getHeaderName())) {
            return ResultUtil.error(ErrorCode.UNAUTHORIZED, "用户未登录", null);
        } else {
            return ResultUtil.error(ErrorCode.METHOD_NOT_ALLOWED, "请求头 " + e.getHeaderName() + " 缺失", null);
        }
    }

    /**
     * 处理请求参数异常。
     *
     * @param e 请求参数异常
     * @return 返回请求参数异常信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<java.util.List<String>>> handleMethodArgumentNotValidException(@NotNull MethodArgumentNotValidException e) {
        log.warn("参数校验错误 | 错误 {} 个 ", e.getBindingResult().getErrorCount());
        e.getFieldErrors().forEach(it -> log.debug("\t\t<{}>[{}]: {}", it.getField(), it.getRejectedValue(), it.getDefaultMessage()));
        return ResultUtil.error(ErrorCode.PARAMETER_ERROR, e.getAllErrors().get(0).getDefaultMessage(), e.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
    }

    /**
     * 处理资源未找到异常。
     *
     * @param e 资源未找到异常
     * @return 返回资源未找到异常信息
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BaseResponse<NoResourceFoundException>> handleNoResourceFoundException(@NotNull NoResourceFoundException e) {
        log.error("资源未找到异常 | {}", e.getMessage());
        return ResultUtil.error(ErrorCode.RESOURCE_NOT_FOUND, "[静态资源] 或 [路由] 不存在", null);
    }


    /**
     * 处理请求参数缺失异常。
     *
     * @param e 请求参数缺失异常
     * @return 返回请求参数缺失异常信息
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse<MissingServletRequestParameterException>> handleMissingServletRequestParameterException(@NotNull MissingServletRequestParameterException e) {
        log.warn("请求参数缺失 | 缺失的请求参数 [{}]", e.getParameterName());
        return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "请求参数 " + e.getParameterName() + " 缺失", null);
    }

    /**
     * 处理请求参数缺失异常。
     *
     * @param e 请求参数缺失异常
     * @return 返回请求参数缺失异常信息
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<BaseResponse<MissingServletRequestPartException>> handleMissingServletRequestPartException(@NotNull MissingServletRequestPartException e) {
        log.warn("请求参数缺失 | 缺失的文件请求参数 [{}]", e.getRequestPartName());
        return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "请求文件参数 " + e.getRequestPartName() + " 缺失", null);
    }

    /**
     * 处理请求参数解析异常。
     *
     * @param e 请求参数解析异常
     * @return 返回请求参数解析异常信息
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<Object>> handleArithmeticException(@NotNull HttpMessageNotReadableException e) {
        log.warn("请求参数解析失败 | 解析异常: {}", e.getMessage());
        if (e.getCause() != null) {
            if (e.getCause().getCause() instanceof BusinessException exception) {
                return this.handleBusinessException(exception);
            }
        }
        return ResultUtil.error(ErrorCode.PARAMETER_ILLEGAL, "请求参数解析失败", e.getCause());
    }
}
