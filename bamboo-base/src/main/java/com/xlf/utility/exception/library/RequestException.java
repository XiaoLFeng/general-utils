package com.xlf.utility.exception.library;

import com.xlf.utility.BaseResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 远程服务请求异常类，用于描述微服务间HTTP调用失败时的异常。
 * <p>
 * 该异常类继承自 {@link RuntimeException}，并包含了远程服务返回的 {@link BaseResponse} 响应信息。
 * SDK 开发者在调用远程微服务时，如果返回非 Success 状态，可以手动抛出此异常。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Getter
@SuppressWarnings("unused")
public class RequestException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(RequestException.class);

    /**
     * 静态单例 ObjectMapper，用于 JSON 解析。
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    /**
     * 远程服务返回的标准响应对象（通配符泛型版本）
     */
    private final BaseResponse<?> remoteResponse;

    /**
     * 远程服务的链路追踪上下文 ID
     */
    private final String remoteContext;

    /**
     * 原始 JSON 响应字符串
     */
    private final String rawResponse;

    /**
     * 指定的 data 类型（Class 版本）
     */
    private final Class<?> specifiedDataClass;

    /**
     * 指定的 data 类型（TypeReference 版本）
     */
    private final TypeReference<?> specifiedTypeReference;

    public RequestException(@NotNull String message, @NotNull String jsonResponse) {
        super(message);
        this.rawResponse = jsonResponse;
        this.specifiedDataClass = null;
        this.specifiedTypeReference = null;
        this.remoteResponse = this.parseResponse(jsonResponse, (Class<?>) null);
        this.remoteContext = (remoteResponse != null && remoteResponse.getContext() != null)
                ? remoteResponse.getContext()
                : null;
    }

    public RequestException(@NotNull String message, @NotNull String jsonResponse, @Nullable Class<?> dataClass) {
        super(message);
        this.rawResponse = jsonResponse;
        this.specifiedDataClass = dataClass;
        this.specifiedTypeReference = null;
        this.remoteResponse = this.parseResponse(jsonResponse, dataClass);
        this.remoteContext = (remoteResponse != null && remoteResponse.getContext() != null)
                ? remoteResponse.getContext()
                : null;
    }

    public RequestException(@NotNull String message, @NotNull String jsonResponse, @Nullable TypeReference<?> typeReference) {
        super(message);
        this.rawResponse = jsonResponse;
        this.specifiedDataClass = null;
        this.specifiedTypeReference = typeReference;
        this.remoteResponse = this.parseResponse(jsonResponse, typeReference);
        this.remoteContext = (remoteResponse != null && remoteResponse.getContext() != null)
                ? remoteResponse.getContext()
                : null;
    }

    /**
     * 获取远程服务响应（通配符泛型版本）
     */
    public BaseResponse<?> getRemoteResponse() {
        return remoteResponse;
    }

    /**
     * 获取指定类型的远程响应
     */
    @SuppressWarnings("unchecked")
    public <T> BaseResponse<T> getTypedRemoteResponse(@NotNull Class<T> dataClass) {
        if (remoteResponse == null) {
            return null;
        }
        if (specifiedDataClass != null && specifiedDataClass.equals(dataClass)) {
            return (BaseResponse<T>) remoteResponse;
        }
        return this.parseResponse(rawResponse, dataClass);
    }

    /**
     * 获取指定泛型类型的远程响应
     */
    @SuppressWarnings("unchecked")
    public <T> BaseResponse<T> getTypedRemoteResponse(@NotNull TypeReference<T> typeReference) {
        if (remoteResponse == null) {
            return null;
        }
        if (specifiedTypeReference != null && specifiedTypeReference.equals(typeReference)) {
            return (BaseResponse<T>) remoteResponse;
        }
        return this.parseResponse(rawResponse, typeReference);
    }

    @SuppressWarnings("unchecked")
    private <T> BaseResponse<T> parseResponse(@NotNull String jsonResponse, @Nullable Class<T> dataClass) {
        try {
            if (dataClass == null) {
                return (BaseResponse<T>) OBJECT_MAPPER.readValue(jsonResponse, new TypeReference<BaseResponse<?>>() {
                });
            }
            JavaType javaType = OBJECT_MAPPER.getTypeFactory()
                    .constructParametricType(BaseResponse.class, dataClass);
            return OBJECT_MAPPER.readValue(jsonResponse, javaType);
        } catch (Exception e) {
            log.warn("解析远程响应失败 | {}", e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> BaseResponse<T> parseResponse(@NotNull String jsonResponse, @Nullable TypeReference<T> typeReference) {
        try {
            if (typeReference == null) {
                return (BaseResponse<T>) OBJECT_MAPPER.readValue(jsonResponse, new TypeReference<BaseResponse<?>>() {
                });
            }
            JavaType javaType = OBJECT_MAPPER.getTypeFactory()
                    .constructType(typeReference.getType());
            return OBJECT_MAPPER.readValue(jsonResponse, javaType);
        } catch (Exception e) {
            log.warn("解析远程响应失败 | {}", e.getMessage());
            return null;
        }
    }
}
