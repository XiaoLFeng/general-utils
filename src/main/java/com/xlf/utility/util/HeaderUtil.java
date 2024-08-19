package com.xlf.utility.util;

import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * <h4>HeaderUtil</h4>
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
     * <h5>获取授权用户UUID</h5>
     * <hr/>
     * 用于获取授权用户UUID, 用于权限验证
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
            if (userUuid != null) {
                if (Pattern.matches("^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$", userUuid)) {
                    return UuidUtil.convertToUuid(userUuid);
                }
            }
            return null;
        }
    }
}
