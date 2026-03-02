package com.xlf.utility.webflux.auto;

import com.xlf.utility.app.config.MybatisPlusConfigHandler;
import com.xlf.utility.app.properties.UtilityBaseProperties;
import com.xlf.utility.webflux.aspect.DebugAspectHandler;
import com.xlf.utility.webflux.aspect.LogAspectHandler;
import com.xlf.utility.webflux.controller.GlobalErrorController;
import com.xlf.utility.webflux.filter.ContextFilter;
import com.xlf.utility.webflux.properties.ContextWebFluxProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * WebFlux SDK 自动配置类
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@AutoConfiguration(before = ErrorWebFluxAutoConfiguration.class)
@EnableAspectJAutoProxy
@EnableConfigurationProperties(ContextWebFluxProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@SuppressWarnings("unused")
public class WebFluxSdkAutoConfiguration {

    @Bean
    @ConditionalOnProperty(
            prefix = "utility.context.webflux",
            name = "gateway-mode",
            havingValue = "false",
            matchIfMissing = true
    )
    @ConditionalOnMissingBean
    public MybatisPlusConfigHandler mybatisPlusConfigHandler(UtilityBaseProperties properties) {
        return new MybatisPlusConfigHandler(properties);
    }

    @Bean
    public ContextFilter contextFilter(ContextWebFluxProperties properties) {
        return new ContextFilter(properties);
    }

    @Bean
    public LogAspectHandler logAspectHandler() {
        return new LogAspectHandler();
    }

    @Bean
    public DebugAspectHandler debugAspectHandler() {
        return new DebugAspectHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalErrorController globalErrorController() {
        return new GlobalErrorController();
    }
}
