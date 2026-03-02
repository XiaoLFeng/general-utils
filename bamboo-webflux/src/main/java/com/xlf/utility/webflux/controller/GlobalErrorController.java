package com.xlf.utility.webflux.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.webflux.ResultUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 全局错误处理器（WebFlux 版本）
 * <p>
 * 实现 Spring WebFlux 的 {@link ErrorWebExceptionHandler} 接口，用于统一处理所有未捕获的异常和错误。
 * 该处理器具有较高的优先级，确保在默认错误处理器之前执行。
 * <p>
 * 功能特性：
 * <ul>
 *     <li>从 {@link ServerWebExchange} 提取错误信息</li>
 *     <li>将 HTTP 状态码映射到对应的 {@link ErrorCode}</li>
 *     <li>使用 {@link ResultUtil} 构造标准化的错误响应</li>
 *     <li>返回统一的 {@link BaseResponse} 格式</li>
 *     <li>自动注入上下文 ID 和执行耗时</li>
 * </ul>
 * <p>
 * 注意：该类通过 {@link com.xlf.utility.webflux.auto.WebFluxSdkAutoConfiguration} 以 {@code @Bean} 方式注册，
 * 无需用户额外配置 {@code @ComponentScan}。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
public class GlobalErrorController implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorController.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 处理全局错误
     * <p>
     * 拦截所有未处理的异常和错误，从交换机中提取错误信息，
     * 构造并返回标准化的错误响应。
     *
     * @param exchange 当前的服务器 Web 交换机
     * @param ex       抛出的异常
     * @return 完成信号，表示响应已写入
     */
    @Override
    public @NotNull Mono<Void> handle(ServerWebExchange exchange, @NotNull Throwable ex) {
        // 确定状态码
        HttpStatus status = this.determineHttpStatus(ex);
        String requestPath = exchange.getRequest().getPath().value();

        // 映射状态码到 ErrorCode
        ErrorCode errorCode = this.mapStatusCodeToErrorCode(status.value());

        // 确定错误信息
        String errorMessage = Optional.ofNullable(ex.getMessage())
                .orElse(errorCode.getMessage());

        // 构建错误详情
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("path", requestPath);
        errorDetails.put("status", status.value());

        // 记录错误日志
        log.warn("[{}]{} | 路径: {} | 异常: {}",
                errorCode.getCode(),
                errorCode.getOutput(),
                requestPath,
                ex.getClass().getSimpleName(),
                ex);

        // 使用 ResultUtil 构造响应
        return ResultUtil.error(errorCode, errorMessage, errorDetails)
                .flatMap(response -> this.writeResponse(exchange, response));
    }

    /**
     * 确定HTTP状态码
     *
     * @param ex 异常对象
     * @return 对应的HTTP状态码
     */
    private @NotNull HttpStatus determineHttpStatus(Throwable ex) {
        // 处理 ResponseStatusException
        if (ex instanceof org.springframework.web.server.ResponseStatusException rse) {
            return HttpStatus.valueOf(rse.getStatusCode().value());
        }
        // 处理 IllegalArgumentException
        if (ex instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        }
        // 默认返回 500 内部服务器错误
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * 将HTTP状态码映射到对应的ErrorCode
     *
     * @param statusCode HTTP状态码
     * @return 对应的ErrorCode枚举值
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
            case 500 -> ErrorCode.SERVER_INTERNAL_ERROR;
            case 502 -> ErrorCode.GATEWAY_ERROR;
            case 503 -> ErrorCode.SERVICE_UNAVAILABLE;
            default -> ErrorCode.SERVER_INTERNAL_ERROR;
        };
    }

    /**
     * 将响应写入ServerWebExchange
     *
     * @param exchange 当前的服务器Web交换机
     * @param response 响应实体
     * @return 完成信号
     */
    private @NotNull Mono<Void> writeResponse(
            ServerWebExchange exchange,
            ResponseEntity<BaseResponse<Map<String, Object>>> response
    ) {
        ServerHttpResponse serverResponse = exchange.getResponse();

        // 设置状态码
        serverResponse.setStatusCode(response.getStatusCode());

        // 设置内容类型
        serverResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 序列化响应体
        try {
            String jsonBody = OBJECT_MAPPER.writeValueAsString(response.getBody());
            DataBuffer buffer = serverResponse.bufferFactory()
                    .wrap(jsonBody.getBytes(StandardCharsets.UTF_8));
            return serverResponse.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("序列化错误响应失败", e);
            String fallbackBody = "{\"output\":\"UnknownError\",\"code\":50999,\"message\":\"序列化错误响应失败\"}";
            DataBuffer buffer = serverResponse.bufferFactory()
                    .wrap(fallbackBody.getBytes(StandardCharsets.UTF_8));
            return serverResponse.writeWith(Mono.just(buffer));
        }
    }
}
