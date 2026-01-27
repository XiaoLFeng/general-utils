package com.xlf.utility.webflux.exception;

import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.exception.BusinessException;
import com.xlf.utility.exception.IPublicException;
import com.xlf.utility.exception.library.*;
import com.xlf.utility.webflux.ResultUtil;
import com.xlf.utility.webflux.exception.library.UserAuthenticationException;
import jakarta.validation.UnexpectedTypeException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公共异常处理器类（WebFlux版本）
 * <p>
 * PublicExceptionHandler 是专为Spring WebFlux响应式环境设计的异常处理器，
 * 用于统一处理各种在响应式应用程序运行期间可能抛出的异常，
 * 确保这些异常能够被捕获并以标准化的响应式方式返回信息给客户端。
 * <p>
 * WebFlux版本继承了 JavaBaseExceptionHandler 的基础异常处理功能，
 * 并在此基础上增加了业务异常处理，包括用户认证、权限验证、
 * 业务规则检查等应用层面的异常，所有处理都以响应式的方式进行。
 * <p>
 * 通过实现此类中的异常处理方法，可以大大减少响应式应用开发中的异常处理代码冗余，
 * 并增强异常可读性及问题诊断效率。
 * <p>
 * NOTICE: 此类为WebFlux响应式环境专用，需在应用上下文中配置 @RestControllerAdvice 注解以生效。
 * <p>
 * NOTICE: 此类使用了若干标准的异常类及项目中自定义的异常类，请确保了解
 * 项目异常结构后再进行扩展。所有返回值都是响应式的 Mono<ResponseEntity<T>>。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
@RestControllerAdvice
class PublicExceptionHandler extends JavaBaseExceptionHandler implements IPublicException {

    /**
     * 日志记录器。
     */
    private static final Logger log = LoggerFactory.getLogger(PublicExceptionHandler.class);

    @ExceptionHandler(PageNotFoundedException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<PageNotFoundedException>>> handlePageNotFoundException(@NotNull Object e) {
        PageNotFoundedException exception = (PageNotFoundedException) e;
        log.warn("页面未找到异常 | {}<{}>", exception.getMessage(), exception.getRoute());
        if (exception.isHasTrace()) {
            return ResultUtil.error(ErrorCode.PAGE_NOT_FOUND, String.format("路由 %s 未找到", exception.getRoute()), exception);
        } else {
            return ResultUtil.error(ErrorCode.PAGE_NOT_FOUND, String.format("路由 %s 未找到", exception.getRoute()), null);
        }
    }

    @ExceptionHandler(BusinessException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<Object>>> handleBusinessException(@NotNull BusinessException e) {
        if (e.isErrorOutput()) {
            log.warn("<{}>{} | {}", e.getErrorCode().getCode(), e.getErrorCode().getMessage(), e.getErrorMessage(), e);
            return ResultUtil.error(e.getErrorCode(), e.getErrorMessage(), e);
        } else {
            log.warn("<{}>{} | {}", e.getErrorCode().getCode(), e.getErrorCode().getMessage(), e.getErrorMessage());
            return ResultUtil.error(e.getErrorCode(), e.getErrorMessage(), e.getData());
        }
    }

    @ExceptionHandler(RequestHeaderNotMatchException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<RequestHeaderNotMatchException>>> handleRequestHeaderNotMatchException(@NotNull Object e) {
        RequestHeaderNotMatchException exception = (RequestHeaderNotMatchException) e;
        log.warn("请求头不匹配异常 | {}", exception.getMessage());
        return ResultUtil.error(ErrorCode.HEADER_INVALID, exception.getMessage(), null);
    }

    @ExceptionHandler(MailTemplateNotFoundException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<MailTemplateNotFoundException>>> handleMailTemplateNotFoundException(@NotNull Object e) {
        MailTemplateNotFoundException exception = (MailTemplateNotFoundException) e;
        log.warn("邮件模板不存在 | {}", exception.getMessage());
        return ResultUtil.error(ErrorCode.NOT_EXIST, "邮件模板 " + exception.getMessage() + " 不存在", null);
    }

    @ExceptionHandler(UserAuthenticationException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<UserAuthenticationException.UserInfo>>> handleUserAuthenticationException(@NotNull Object e) {
        UserAuthenticationException exception = (UserAuthenticationException) e;
        log.error("用户认证异常 | {}", exception.getMessage());
        return ResultUtil.error(exception.getErrorType().getErrorCode(), exception.getErrorType().getMessage(), exception.getUserInfo());
    }

    @Override
    @ExceptionHandler(BaseUserAuthenticationException.class)
    public Mono<ResponseEntity<BaseResponse<BaseUserAuthenticationException>>> handleBaseUserAuthenticationException(@NotNull Object e) {
        BaseUserAuthenticationException exception = (BaseUserAuthenticationException) e;
        log.error("基础用户认证异常 | {}", exception.getMessage());
        return ResultUtil.error(exception.getErrorType().getErrorCode(), exception.getErrorType().getMessage(), null);
    }

    @ExceptionHandler(ServerInternalErrorException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<ServerInternalErrorException>>> handleServerInternalErrorException(@NotNull Object e) {
        ServerInternalErrorException exception = (ServerInternalErrorException) e;
        log.error("服务器内部错误 | {}", exception.getMessage(), exception);
        return ResultUtil.error(ErrorCode.SERVER_INTERNAL_ERROR, exception.getMessage(), exception);
    }

    @ExceptionHandler(CheckFailureException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<CheckFailureException>>> handleCheckFailureException(@NotNull Object e) {
        CheckFailureException exception = (CheckFailureException) e;
        log.error("检查失败异常 | {}", exception.getMessage());
        return ResultUtil.error(ErrorCode.OPERATION_FAILED, exception.getMessage(), null);
    }

    @ExceptionHandler(NoPermissionException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<NoPermissionException>>> handleNoPermissionException(@NotNull Object e) {
        NoPermissionException exception = (NoPermissionException) e;
        log.warn("无权限异常 | {}", exception.getMessage());
        return ResultUtil.error(ErrorCode.PERMISSION_DENIED, exception.getMessage(), null);
    }

    @ExceptionHandler(SystemParameterErrorException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<SystemParameterErrorException>>> handleSystemParameterErrorException(@NotNull Object e) {
        SystemParameterErrorException exception = (SystemParameterErrorException) e;
        log.error("系统参数错误 | {}", exception.getMessage(), exception);
        return ResultUtil.error(ErrorCode.PARAMETER_ERROR, exception.getMessage(), null);
    }

    @ExceptionHandler(DeveloperException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<DeveloperException.ErrorType>>> handleDeveloperErrorException(@NotNull Object e) {
        DeveloperException exception = (DeveloperException) e;
        log.error("开发者错误 | {}", exception.getMessage(), exception);
        return ResultUtil.error(ErrorCode.SERVER_INTERNAL_ERROR, "开发者错误: " + exception.getMessage(), exception.getErrorType());
    }

    /**
     * 处理远程服务请求异常（WebFlux版本）。
     * <p>
     * 当调用其他微服务的HTTP接口时，如果返回非Success状态，SDK会抛出RequestException。
     * 该方法会自动提取远程服务的errorMessage或使用SDK开发者传入的message，
     * 并记录远程服务的context用于链路追踪。
     * <p>
     * 错误消息提取优先级：
     * <ol>
     *     <li>优先使用远程服务返回的 BaseResponse.errorMessage（如果存在且不为空）</li>
     *     <li>否则使用SDK开发者传入的自定义错误消息</li>
     * </ol>
     * <p>
     * 该方法返回响应式的 Mono 类型，适配 WebFlux 环境。
     *
     * @param e 远程服务请求异常
     * @return 返回远程服务请求异常信息（响应式）
     */
    @ExceptionHandler(RequestException.class)
    @Override
    public Mono<ResponseEntity<BaseResponse<BaseResponse<?>>>> handleRequestException(@NotNull RequestException e) {
        BaseResponse<?> remoteResponse = e.getRemoteResponse();
        String remoteContext = e.getRemoteContext();

        // 提取错误消息：优先使用 remoteResponse.errorMessage，否则使用传入的 message
        String errorMessage;
        if (remoteResponse != null && remoteResponse.getErrorMessage() != null && !remoteResponse.getErrorMessage().isEmpty()) {
            errorMessage = remoteResponse.getErrorMessage();
        } else {
            errorMessage = e.getMessage();
        }

        // 日志记录（包含远程 context）
        if (remoteContext != null) {
            log.warn("远程服务请求失败 | remoteContext={} | {}", remoteContext, errorMessage);
        } else {
            log.warn("远程服务请求失败 | {}", errorMessage);
        }

        return ResultUtil.error(ErrorCode.REQUEST_ERROR, errorMessage, e.getRemoteResponse());
    }

    @Override
    @ExceptionHandler(UnexpectedTypeException.class)
    public Mono<ResponseEntity<BaseResponse<Object>>> handleUnexpectedTypeException(@NotNull Object e) {
        UnexpectedTypeException exception = (UnexpectedTypeException) e;
        String errMessage = exception.getMessage();
        Map<String, String> errorMap = new HashMap<>();
        switch (errMessage.substring(0, errMessage.indexOf(":"))) {
            case "HV000030" -> {
                String originalMessage = errMessage;
                errMessage = "请求参数配置错误";
                // HV000030 错误格式: "HV000030: No validator could be found for constraint 'xxx' validating type 'yyy'. ..."
                // 使用捕获组提取 constraint 和 type
                Pattern compile = Pattern.compile("constraint '(.+?)' validating type '(.+?)'");
                Matcher matcher = compile.matcher(originalMessage);
                if (matcher.find()) {
                    errorMap.put("constraint", matcher.group(1));
                    errorMap.put("type", matcher.group(2));
                }
                log.error("请求参数类型异常 | {}", exception.getMessage(), exception);
                return ResultUtil.error(ErrorCode.SERVER_INTERNAL_ERROR, "开发者错误 | " + errMessage, errorMap);
            }
            case "HV000034" -> errMessage = "请求参数值超出允许范围";
            case "HV000035" -> errMessage = "请求参数值不在允许范围内";
            case "HV000136" -> errMessage = "请求参数类型不匹配";
            default -> errMessage = "请求参数校验失败";
        }
        log.debug("获取完整堆栈", exception);
        log.warn("请求参数类型异常 | {}", exception.getMessage());
        return ResultUtil.error(ErrorCode.BODY_INVALID, errMessage, errorMap);
    }
}
