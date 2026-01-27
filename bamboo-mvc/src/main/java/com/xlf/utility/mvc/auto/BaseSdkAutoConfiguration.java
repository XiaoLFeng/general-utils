package com.xlf.utility.mvc.auto;

import com.xlf.utility.app.config.MybatisPlusConfigHandler;
import com.xlf.utility.app.properties.UtilityBaseProperties;
import com.xlf.utility.mvc.aspect.DebugAspectHandler;
import com.xlf.utility.mvc.aspect.DubboContextAspect;
import com.xlf.utility.mvc.aspect.LogAspectHandler;
import com.xlf.utility.mvc.controller.ErrorController;
import com.xlf.utility.mvc.filter.ContextFilter;
import com.xlf.utility.mvc.properties.ContextProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Bamboo MVC 自动配置类
 *
 * @author xiao_lfeng
 * @since 2.0.0-beta1
 */
@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(ContextProperties.class)
@ComponentScan("com.xlf.utility.mvc")
public class BaseSdkAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor")
    public MybatisPlusConfigHandler mybatisPlusConfigHandler(UtilityBaseProperties properties) {
        return new MybatisPlusConfigHandler(properties);
    }

    @Bean
    public ContextFilter contextFilter(
            ContextProperties contextProperties,
            @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping
    ) {
        return new ContextFilter(handlerMapping, contextProperties);
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
    public ErrorController errorController() {
        return new ErrorController();
    }

    @Bean
    @ConditionalOnClass(name = "org.apache.dubbo.rpc.RpcContext")
    @ConditionalOnMissingBean
    public DubboContextAspect dubboContextAspect() {
        return new DubboContextAspect();
    }
}
