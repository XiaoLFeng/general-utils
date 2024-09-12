package com.xlf.utility.exception;

import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.ResultUtil;
import com.xlf.utility.exception.library.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * 业务异常处理类。
 * 用于处理业务异常，当业务异常发生时，将会自动捕获并处理，不会影响系统的正常运行。
 *
 * @author xiao_lfeng
 * @version v1.0.0
 * @since v1.0.0
 */
@SuppressWarnings("unused")
public class PublicExceptionHandlerAbstract {

    /**
     * 日志记录器。
     */
    private static final Logger log = LoggerFactory.getLogger(PublicExceptionHandlerAbstract.class);

    /**
     * 处理未定义的异常。
     *
     * @param e 异常信息
     * @return 返回异常信息
     */
    @ExceptionHandler(Exception.class)
    public @NotNull ResponseEntity<BaseResponse<Exception>> handleException(Exception e) {
        log.error("[ERRO] 未定义输出异常 | {}", e.getMessage(), e);
        return ResultUtil.error(ErrorCode.SERVER_INTERNAL_ERROR, "未定义输出异常", e);
    }

    /**
     * 处理页面未找到异常。
     *
     * @param e 页面未找到异常
     * @return 返回异常信息
     */
    @ExceptionHandler(PageNotFoundedException.class)
    public ResponseEntity<BaseResponse<PageNotFoundedException>> handlePageNotFoundException(@NotNull PageNotFoundedException e) {
        log.warn("[ERRO] 页面未找到异常 | {}<{}>", e.getMessage(), e.getRoute());
        return ResultUtil.error(ErrorCode.PAGE_NOT_FOUND, "页面未找到", e);
    }

    /**
     * 处理业务异常。
     *
     * @param e 业务异常
     * @return 返回业务异常信息
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Object>> handleBusinessException(@NotNull BusinessException e) {
        if (e.isErrorOutput()) {
            log.warn("[ERRO] <{}>{} | {}", e.getErrorCode().getCode(), e.getErrorCode().getMessage(), e.getErrorMessage(), e);
            return ResultUtil.error(e.getErrorCode(), e.getErrorMessage(), e);
        } else {
            log.warn("[ERRO] <{}>{} | {}", e.getErrorCode().getCode(), e.getErrorCode().getMessage(), e.getErrorMessage());
            return ResultUtil.error(e.getErrorCode(), e.getErrorMessage(), e.getData());
        }
    }

    /**
     * 处理空指针异常。
     *
     * @param e 空指针异常
     * @return 返回空指针异常信息
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<BaseResponse<NullPointerException>> handleNullPointerException(NullPointerException e) {
        log.error("[ERRO] 空指针异常 | {}", e.getMessage(), e);
        return ResultUtil.error(ErrorCode.SERVER_INTERNAL_ERROR, "空指针异常", e);
    }

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
        data.put("method", new ArrayList<String>() {{
            add(Objects.requireNonNull(e.getMethod()));
        }});
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
     * 处理请求头不匹配异常。
     *
     * @param e 请求头不匹配异常
     * @return 返回请求头不匹配异常信息
     */
    @ExceptionHandler(RequestHeaderNotMatchException.class)
    public ResponseEntity<BaseResponse<RequestHeaderNotMatchException>> handleRequestHeaderNotMatchException(@NotNull RequestHeaderNotMatchException e) {
        log.warn("[ERRO] 请求头不匹配异常 | {}", e.getMessage());
        return ResultUtil.error(ErrorCode.METHOD_NOT_ALLOWED, e.getMessage(), null);
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
     * 处理邮件模板不存在异常。
     *
     * @param e 邮件模板不存在异常
     * @return 返回邮件模板不存在异常信息
     */
    @ExceptionHandler(MailTemplateNotFoundException.class)
    public ResponseEntity<BaseResponse<MailTemplateNotFoundException>> handleMailTemplateNotFoundException(@NotNull MailTemplateNotFoundException e) {
        log.warn("[ERRO] 邮件模板不存在 | {}", e.getMessage());
        return ResultUtil.error(ErrorCode.OPERATION_FAILED, "邮件模板 " + e.getMessage() + " 不存在", null);
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
     * 处理用户认证异常。
     *
     * @param e 用户认证异常
     * @return 返回用户认证异常信息
     */
    @ExceptionHandler(UserAuthenticationException.class)
    public ResponseEntity<BaseResponse<UserAuthenticationException.UserInfo>> handleUserAuthenticationException(@NotNull UserAuthenticationException e) {
        log.error("[ERRO] 用户认证异常 | {}", e.getMessage());
        return ResultUtil.error(e.getErrorType().getErrorCode(), e.getErrorType().getMessage(), e.getUserInfo());
    }

    /**
     * 处理服务器内部错误异常。
     *
     * @param e 服务器内部错误
     * @return 返回服务器内部错误异常信息
     */
    @ExceptionHandler(ServerInternalErrorException.class)
    public ResponseEntity<BaseResponse<ServerInternalErrorException>> handleServerInternalErrorException(ServerInternalErrorException e) {
        log.error("[ERRO] 服务器内部错误 | {}", e.getMessage(), e);
        return ResultUtil.error(ErrorCode.SERVER_INTERNAL_ERROR, e.getMessage(), e);
    }

    /**
     * 处理检查失败异常。
     *
     * @param e 检查失败异常
     * @return 返回检查失败异常信息
     */
    @ExceptionHandler(CheckFailureException.class)
    public ResponseEntity<BaseResponse<CheckFailureException>> handleCheckFailureException(@NotNull CheckFailureException e) {
        log.error("[ERRO] 检查失败异常 | {}", e.getMessage());
        return ResultUtil.error(ErrorCode.OPERATION_FAILED, e.getMessage(), null);
    }

    /**
     * 处理资源未找到异常。
     *
     * @param e 资源未找到异常
     * @return 返回资源未找到异常信息
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BaseResponse<NoResourceFoundException>> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error("[ERRO] 资源未找到异常 | {}", e.getMessage(), e);
        return ResultUtil.error(ErrorCode.PAGE_NOT_FOUND, "资源未找到", e);
    }
}
