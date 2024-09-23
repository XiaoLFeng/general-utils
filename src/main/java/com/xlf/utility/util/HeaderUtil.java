package com.xlf.utility.util;

import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 用于处理请求头的工具类
 * <p>
 * 用于获取授权用户UUID；获取请求头中的授权用户UUID
 *
 * @author xiao_lfeng
 * @version v1.0.1
 * @since v1.0.1
 */
@SuppressWarnings("unused")
public class HeaderUtil {

    /**
     * 获取授权用户UUID
     * <p>
     * 用于获取授权用户UUID, 用于权限验证
     * <p>
     * 从请求头中获取授权用户UUID，处理 Bearer Token，返回授权用户UUID，如果获取失败, 返回默认UUID。
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

    /**
     * 获取授权用户UUID字符串
     * <p>
     * 用于获取授权用户UUID, 用于权限验证。
     * <p>
     * 从请求头中获取授权用户UUID，处理 Bearer Token，返回授权用户UUID，如果获取失败, 返回默认UUID，返回UUID字符串。
     *
     * @param request 请求
     * @return UUID
     */
    @Nullable
    public static String getAuthorizeUserUuidString(@NotNull HttpServletRequest request) {
        if (getAuthorizeUserUuid(request) != null) {
            return Objects.requireNonNull(getAuthorizeUserUuid(request)).toString();
        } else {
            return null;
        }
    }

    /**
     * 获取请求头中的Referer
     * <p>
     * 用于获取请求头中的Referer，若有Referer则返回Referer，否则返回null。
     *
     * @param request 请求
     * @return Referer
     */
    @Nullable
    public static String getReferer(@NotNull HttpServletRequest request) {
        return request.getHeader("Referer");
    }


    /**
     * 判断请求头中是否有Referer
     * <p>
     * 用于判断请求头中是否有Referer，若有Referer则返回true，否则返回false。
     *
     * @param request 请求
     * @return 是否有Referer
     */
    public static boolean hasReferer(@NotNull HttpServletRequest request) {
        return request.getHeader("Referer") != null;
    }
}
