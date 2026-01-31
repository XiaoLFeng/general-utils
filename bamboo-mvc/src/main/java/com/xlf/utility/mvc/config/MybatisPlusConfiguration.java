package com.xlf.utility.mvc.config;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.xlf.utility.app.properties.UtilityBaseProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MybatisPlus 配置类
 * <p>
 * 该类用于配置 MybatisPlus 相关配置;
 * 该类应当被继承后使用 {@link org.springframework.context.annotation.Configuration} 注解标记；
 * 使用方法如下：
 * <pre>
 * {@code
 *     @Configuration
 *     public class MybatisPlusConfig extends MybatisPlusConfiguration {
 *          public MybatisPlusConfig(DbType dbType) {
 *              super(dbType);
 *          }
 *     }
 * }
 * </pre>
 *
 * @author xiao_lfeng
 * @version 2.0.0-beta1
 * @since 1.0.9-beta.1.0
 */
@SuppressWarnings("unused")
@Configuration
public class MybatisPlusConfiguration {

    /**
     * 配置 Mybatis Plus 分页操作
     *
     * @return MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(@NotNull UtilityBaseProperties properties) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor pageInterceptor = new PaginationInnerInterceptor();
        if (ObjectUtils.isNotEmpty(properties.getDatasource().getDbType())) {
            pageInterceptor.setDbType(properties.getDatasource().getDbType());
        }
        interceptor.addInnerInterceptor(pageInterceptor);
        return interceptor;
    }
}
