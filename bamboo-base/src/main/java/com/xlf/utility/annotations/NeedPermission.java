package com.xlf.utility.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记需要权限的接口方法
 * <p>
 * 此注解用于标记某个方法需要特定的权限才能被调用。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("unused")
public @interface NeedPermission {

    /**
     * 权限名称或权限字符串
     *
     * @return 所需权限的名称或字符串
     */
    String[] value() default {};
}
