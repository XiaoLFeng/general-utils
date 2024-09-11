package com.xlf.utility.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户是否有权限
 * <p>
 * 标记 Controller 方法；用于检测用户是否有权限访问的页面；
 * 如果用户没有权限，将会返回错误的状态码；
 *
 * @author xiao_lfeng
 * @version v1.0.9-beta.1.0
 * @since v1.0.9-beta.1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("unused")
public @interface HasPermission {
    /**
     * 权限组
     * <p>
     * 填写用户访问该方法所需要的权限，如果用户不具备该权限，将会返回错误的状态码；
     * 如果用户具备该权限，将执行接下来的方法内操作；
     * 填写权限时，应当使用字符串数组的形式或者单个字符串。
     * 对于权限的填写，请参考如下代码示例：
     * <ul>
     *     <li>{@code @HasPermission({"read", "write"})}</li>
     *     <li>{@code @HasPermission("read")}</li>
     * </ul>
     */
    String[] value();
}
