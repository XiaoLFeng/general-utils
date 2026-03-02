package com.xlf.utility.mvc.controller;

import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.mvc.ResultUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 全局错误控制器（MVC 版本）
 * <p>
 * 实现 Spring Boot 的 {@link ErrorController} 接口，用于统一处理所有未捕获的异常和错误。
 * 该控制器会拦截 {@code /error} 路径的请求，从 {@link HttpServletRequest} 中提取错误信息，
 * 并使用 {@link ResultUtil} 构造标准化的错误响应。
 * <p>
 * 功能特性：
 * <ul>
 *     <li>自动提取 HTTP 状态码、错误信息和请求路径</li>
 *     <li>将 HTTP 状态码映射到对应的 {@link ErrorCode}</li>
 *     <li>返回统一的 {@link BaseResponse} 格式</li>
 *     <li>自动注入上下文 ID 和执行耗时</li>
 * </ul>
 * <p>
 * 注意：该类通过 {@link com.xlf.utility.mvc.BaseSdkAutoConfiguration} 以 {@code @Bean} 方式注册，
 * 无需用户额外配置 {@code @ComponentScan}。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@RestController
@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class GlobalErrorController implements ErrorController {

    /**
     * 处理全局错误
     * <p>
     * 拦截所有未处理的异常和错误，从请求属性中提取错误信息，
     * 构造并返回标准化的错误响应。
     *
     * @param request HTTP 请求对象，包含错误相关的属性
     * @return 包含错误信息的标准化响应实体
     */
    @RequestMapping("")
    public @NotNull ResponseEntity<BaseResponse<Map<String, Object>>> error(HttpServletRequest request) {
        // 从 request 属性提取错误信息
        Integer statusCode = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .filter(obj -> obj instanceof Integer)
                .map(obj -> (Integer) obj)
                .orElse(500);

        String errorMessage = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_MESSAGE))
                .filter(obj -> obj instanceof String)
                .map(obj -> (String) obj)
                .orElse(null);

        String requestUri = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI))
                .filter(obj -> obj instanceof String)
                .map(obj -> (String) obj)
                .orElse(request.getRequestURI());

        Throwable throwable = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION))
                .filter(obj -> obj instanceof Throwable)
                .map(obj -> (Throwable) obj)
                .orElse(null);

        // 构建错误详情
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("path", requestUri);
        errorDetails.put("status", statusCode);

        // 映射状态码到 ErrorCode
        ErrorCode errorCode = this.mapStatusCodeToErrorCode(statusCode);

        // 确定显示的错误信息
        String displayMessage = Optional.ofNullable(errorMessage)
                .orElseGet(() -> Optional.ofNullable(throwable)
                        .map(Throwable::getMessage)
                        .orElse(errorCode.getMessage()));

        // 使用 ResultUtil 构造响应
        return ResultUtil.error(errorCode, displayMessage, errorDetails);
    }

    /**
     * 将 HTTP 状态码映射到对应的 ErrorCode
     *
     * @param statusCode HTTP 状态码
     * @return 对应的 ErrorCode 枚举值
     */
    private @NotNull ErrorCode mapStatusCodeToErrorCode(int statusCode) {
        return switch (statusCode) {
            case 400 -> ErrorCode.BAD_REQUEST;
            case 401 -> ErrorCode.UNAUTHORIZED;
            case 403 -> ErrorCode.FORBIDDEN;
            case 404 -> ErrorCode.PAGE_NOT_FOUND;
            case 405 -> ErrorCode.METHOD_NOT_ALLOWED;
            case 406 -> ErrorCode.NOT_ACCEPTABLE;
            case 408 -> ErrorCode.TIMEOUT;
            case 429 -> ErrorCode.TOO_MANY_REQUESTS;
            case 502 -> ErrorCode.GATEWAY_ERROR;
            case 503 -> ErrorCode.SERVICE_UNAVAILABLE;
            default -> ErrorCode.SERVER_INTERNAL_ERROR;
        };
    }
}
