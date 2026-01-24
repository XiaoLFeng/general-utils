package com.xlf.utility.mvc.auto;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Bamboo MVC 自动配置类
 * <p>
 * 自动扫描并注册 MVC 模块的所有组件，包括：
 * - 过滤器（Filter）
 * - 切面（Aspect）
 * - 异常处理器（Exception Handler）
 * - 配置类（Configuration）
 * <p>
 * 通过 Spring Boot 的自动配置机制，在使用者引入 bamboo-mvc 依赖后，
 * 无需手动配置即可启用所有 MVC 相关功能。
 *
 * @author xiao_lfeng
 * @since 2.0.0-beta1
 */
@Configuration
@ComponentScan("com.xlf.utility.mvc")
public class BaseSdkAutoConfiguration {
    // 自动配置 MVC 模块组件
}
