package com.xlf.utility.constant;

import org.jetbrains.annotations.Contract;

/**
 * 属性常量类
 * <p>
 * 提供系统中常用的全局属性名称常量，以方便在代码中统一管理这些固定的属性标识。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public class AttrConstant {
    /**
     * X-Auth-Type 请求头字段
     */
    public static final String X_AUTH_TYPE = "X-AUTH-TYPE";
    /**
     * X-SSO-USER-ID 请求头字段
     */
    public static final String X_SSO_USER_ID = "X-Sso-User-Id";
    /**
     * X-SSO-USERNAME 请求头字段
     */
    public static final String X_SSO_USERNAME = "X-Sso-Username";
    /**
     * X-SSO-NICKNAME 请求头字段
     */
    public static final String X_SSO_NICKNAME = "X-Sso-Nickname";
    /**
     * X-SSO-AVATAR 请求头字段
     */
    public static final String X_SSO_AVATAR = "X-Sso-Avatar";
    /**
     * X-Office-ID 请求头字段
     */
    public static final String X_OFFICE_ID = "X-Office-Id";
    /**
     * X-SSO-ROLE-ID 请求头字段
     */
    public static final String X_SSO_ROLE_ID = "X-Sso-Role-Id";

    @Contract(value = " -> fail", pure = true)
    private AttrConstant() {
        throw new UnsupportedOperationException("AttrConstant 是常量类，不允许实例化");
    }
}
