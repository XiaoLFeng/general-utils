package com.xlf.utility.triple.auto;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Triple 自动配置
 * <p>
 * 用于自动扫描 bamboo-triple 模块的组件与切面，
 * 使 TripleRequest 校验与 TripleResult 能够在引入模块后即刻生效。
 * </p>
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Configuration
@ComponentScan("com.xlf.utility.triple")
@SuppressWarnings("unused")
public class TripleAutoConfiguration {
}
