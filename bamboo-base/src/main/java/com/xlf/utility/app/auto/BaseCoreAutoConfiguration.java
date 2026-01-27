package com.xlf.utility.app.auto;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

/**
 * bamboo-base 核心模块自动配置类
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Configuration
@ConfigurationPropertiesScan("com.xlf.utility.app.properties")
public class BaseCoreAutoConfiguration {
}
