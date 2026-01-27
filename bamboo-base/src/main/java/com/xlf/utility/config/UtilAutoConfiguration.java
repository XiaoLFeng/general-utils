package com.xlf.utility.config;

import com.xlf.utility.app.auto.BaseCoreAutoConfiguration;
import com.xlf.utility.app.auto.CoreDatabaseAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 兼容式自动配置入口
 * <p>
 * 兼容 spring.factories 的 EnableAutoConfiguration 机制。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Configuration
@Import({BaseCoreAutoConfiguration.class, CoreDatabaseAutoConfiguration.class})
public class UtilAutoConfiguration {
}
