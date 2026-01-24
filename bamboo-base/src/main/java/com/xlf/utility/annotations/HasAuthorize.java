package com.xlf.utility.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户是否授权登录
 * <p>
 * 标记 Controller 方法；用于检测用户是否经过授权登录后访问的页面；
 * 如果用户未经过授权登录，将会返回错误的状态码；
 *
 * @author xiao_lfeng
 * @version v1.0.9-beta.1.0
 * @since v1.0.9-beta.1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("unused")
public @interface HasAuthorize {
}
