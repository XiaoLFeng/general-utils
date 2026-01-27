package com.xlf.utility.mvc.utility;

import com.xlf.utility.exception.library.ServerInternalErrorException;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.Optional;

/**
 * HttpServletUtil 工具类
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public class HttpServletUtil {

    @Contract(value = " -> fail", pure = true)
    private HttpServletUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    @Nullable
    public static HandlerMethod getHandlerMethod(HttpServletRequest request, RequestMappingHandlerMapping handlerMapping) {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = Optional.ofNullable(handlerMapping)
                .map(RequestMappingHandlerMapping::getHandlerMethods)
                .orElseThrow(() -> new ServerInternalErrorException("无法获取处理程序映射信息，请检查 RequestMappingHandlerMapping 是否正确配置"));
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo mappingInfo = entry.getKey();
            if (mappingInfo != null && mappingInfo.getPatternsCondition() != null) {
                if (mappingInfo.getPatternsCondition().getMatchingCondition(request) != null) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }
}
