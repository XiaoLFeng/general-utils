package com.xlf.utility.webflux.auto;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * WebFlux SDK 自动配置兼容入口
 * <p>
 * 为了兼容 awaken-base 的类名约定，提供 BaseSdkAutoConfiguration 作为别名。
 * 实际配置逻辑由 {@link WebFluxSdkAutoConfiguration} 完成。
 * </p>
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Configuration
@Import(WebFluxSdkAutoConfiguration.class)
@SuppressWarnings("unused")
public class BaseSdkAutoConfiguration {
}
