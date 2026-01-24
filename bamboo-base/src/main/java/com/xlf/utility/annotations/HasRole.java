package com.xlf.utility.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户是否在某角色组
 * <p>
 * 标记 Controller 方法；用于检测用户是否经过授权登录后访问的页面；
 * 如果用户未经过授权登录，将会返回错误的状态码；
 *
 * @since v1.0.9-beta.1.0
 * @version v1.0.9-beta.1.0
 * @author xiao_lfeng
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("unused")
public @interface HasRole {
    /**
     * 角色组
     * <p>
     * 填写用户访问该方法所需要的角色组，如果用户不在该角色组中，将会返回错误的状态码；
     * 如果用户在该角色组中，将执行接下来的方法内操作；
     * 填写角色组时，应当使用字符串数组的形式或者单个字符串，并非角色的 {@code RoleUuid(Ruuid)}。
     * 对于角色组的填写，请参考如下代码示例：
     * <ul>
     *     <li>{@code @HasRole({"admin", "user"})}</li>
     *     <li>{@code @HasRole("admin")}</li>
     * </ul>
     */
    String[] value();
}
