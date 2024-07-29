package com.xlf.utility.util;

import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * HeaderUtil
 * <hr/>
 * 用于处理请求头的工具类
 * <p>
 * 用于获取授权用户UUID
 *
 * @author xiao_lfeng
 * @version v1.0.1
 * @since v1.0.1
 */
@SuppressWarnings("unused")
public class HeaderUtil {

    /**
     * 获取授权用户UUID
     * <hr/>
     * 用于获取授权用户UUID
     * <p>
     * 1. 从请求头中获取授权用户UUID
     * 2. 处理 Bearer Token
     * 3. 返回授权用户UUID
     * 4. 如果获取失败, 返回默认UUID
     *
     * @param request 请求
     * @return UUID
     */
    @Nullable
    public static UUID getAuthorizeUserUuid(@NotNull HttpServletRequest request) {
        String userUuid = request.getHeader("Authorization");
        // 处理 Bearer Token
        if (userUuid != null && userUuid.startsWith("Bearer ")) {
            userUuid = userUuid.substring(7);
            return UuidUtil.convertToUuid(userUuid);
        } else {
            return null;
        }
    }
}
