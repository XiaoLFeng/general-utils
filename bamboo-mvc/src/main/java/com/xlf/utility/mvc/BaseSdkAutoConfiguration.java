package com.xlf.utility.mvc;

import com.xlf.utility.app.config.MybatisPlusConfigHandler;
import com.xlf.utility.app.properties.UtilityBaseProperties;
import com.xlf.utility.mvc.aspect.DebugAspectHandler;
import com.xlf.utility.mvc.aspect.LogAspectHandler;
import com.xlf.utility.mvc.controller.GlobalErrorController;
import com.xlf.utility.mvc.exception.SystemExceptionHandler;
import com.xlf.utility.mvc.filter.ContextFilter;
import com.xlf.utility.mvc.properties.ContextProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Base SDK 自动配置类。
 * <p>
 * 该配置类用于集成基础 SDK 的核心功能，包括切面编程、MyBatis-Plus 拦截器、上下文管理过滤器、日志处理以及异常处理。
 * 所有组件通过 {@code @Bean} 方法显式注册，避免 {@code @ComponentScan} 导致的 Bean 冲突问题。
 * </p>
 * <p>
 * 主要配置功能如下：
 * </p>
 * <ul>
 *     <li>启用 AspectJ 自动代理支持。</li>
 *     <li>绑定 MVC 上下文管理配置属性 ({@code bamboo.context})。</li>
 *     <li>自动配置 MyBatis-Plus 分页拦截器和 ID 生成器（基于 {@code bamboo.base} 配置）。</li>
 *     <li>注册上下文过滤器，用于管理请求上下文及 UUID 传递。</li>
 *     <li>注册日志、调试及错误处理的切面与控制器。</li>
 *     <li>注册统一异常处理器。</li>
 * </ul>
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@AutoConfiguration(before = ErrorMvcAutoConfiguration.class)
@EnableAspectJAutoProxy
@EnableConfigurationProperties(ContextProperties.class)
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
    @ConditionalOnMissingBean
    public GlobalErrorController globalErrorController() {
        return new GlobalErrorController();
    }

    @Bean
    @ConditionalOnMissingBean
    public SystemExceptionHandler systemExceptionHandler() {
        return new SystemExceptionHandler();
    }
}
