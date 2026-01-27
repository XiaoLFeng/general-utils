package com.xlf.utility.mvc.exception;

import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.mvc.ResultUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Objects;

/**
 * Springboot 系统异常处理类。
 * 用于处理 Springboot 系统异常，当 Springboot 系统异常发生时，将会自动捕获并处理，不会影响系统的正常运行。
 *
 * @author xiao_lfeng
 * @version v1.0.9-beta.2.7
 * @since v1.0.9-beta.2.7
 */
@SuppressWarnings("unused")
public class SpringbootSystemExceptionHandler {
    Logger log = LoggerFactory.getLogger(SpringbootSystemExceptionHandler.class);

    /**
     * 处理请求方法不支持异常。
     *
     * @param e 请求方法不支持异常
     * @return 返回请求方法不支持异常信息
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse<HashMap<String, Object>>> handleHttpRequestMethodNotSupportedException(@NotNull HttpRequestMethodNotSupportedException e) {
        log.warn("[ERRO] 请求方法不支持 | 获取的方法 [{}] ,需要的方法 {}", e.getMethod(), e.getSupportedHttpMethods());
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
        log.warn("[ERRO] 请求头缺失 | 缺失的请求头 [{}]", e.getHeaderName());
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
        log.warn("[ERRO] 参数校验错误 | 错误 {} 个 ", e.getBindingResult().getErrorCount());
        e.getFieldErrors().forEach(it -> log.debug("\t\t<{}>[{}]: {}", it.getField(), it.getRejectedValue(), it.getDefaultMessage()));
        return ResultUtil.error(ErrorCode.PARAMETER_ERROR, e.getAllErrors().get(0).getDefaultMessage(), e.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
    }

    /**
     * 处理邮件发送异常。
     *
     * @param e 邮件发送异常
     * @return 返回邮件发送异常信息
     */
    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<BaseResponse<MailSendException>> handleMailSendException(MailSendException e) {
        log.error("[ERRO] 邮件发送异常 | {}", e.getMessage(), e);
        return ResultUtil.error(ErrorCode.OPERATION_FAILED, "邮件发送异常", e);
    }

    /**
     * 处理资源未找到异常。
     *
     * @param e 资源未找到异常
     * @return 返回资源未找到异常信息
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BaseResponse<NoResourceFoundException>> handleNoResourceFoundException(@NotNull NoResourceFoundException e) {
        log.error("[ERRO] 资源未找到异常 | {}", e.getMessage());
        return ResultUtil.error(ErrorCode.PAGE_NOT_FOUND, "资源未找到", null);
    }


    /**
     * 处理请求参数缺失异常。
     *
     * @param e 请求参数缺失异常
     * @return 返回请求参数缺失异常信息
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse<MissingServletRequestParameterException>> handleMissingServletRequestParameterException(@NotNull MissingServletRequestParameterException e) {
        log.warn("[ERRO] 请求参数缺失 | 缺失的请求参数 [{}]", e.getParameterName());
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
        log.warn("[ERRO] 请求参数缺失 | 缺失的文件请求参数 [{}]", e.getRequestPartName());
        return ResultUtil.error(ErrorCode.PARAMETER_MISSING, "请求文件参数 " + e.getRequestPartName() + " 缺失", null);
    }
}
