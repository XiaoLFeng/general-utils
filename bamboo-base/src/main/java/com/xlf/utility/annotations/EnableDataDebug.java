package com.xlf.utility.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用数据调试
 * <p>
 * 使用该注解标记某个 Controller/Service/DAO 方法，表示在此方法中启用数据调试功能。
 * 数据调试主要用于开发和测试阶段，帮助开发者在处理数据时进行更精细的观察和分析。
 * <p>
 * NOTICE: 请勿在生产环境中启用！数据调试可能会输出敏感信息。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("unused")
public @interface EnableDataDebug {
}
