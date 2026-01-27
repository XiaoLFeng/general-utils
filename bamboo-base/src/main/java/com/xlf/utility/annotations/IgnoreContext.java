package com.xlf.utility.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 忽略上下文注入
 * <p>
 * 标记 Controller/Service 方法或类，表示忽略上下文注入处理。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("unused")
public @interface IgnoreContext {
    /**
     * 是否启用忽略
     *
     * @return true 表示忽略上下文处理
     */
    boolean value() default true;
}
