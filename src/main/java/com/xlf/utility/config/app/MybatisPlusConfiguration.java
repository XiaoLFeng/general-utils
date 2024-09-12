package com.xlf.utility.config.app;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.xlf.utility.UtilProperties;
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
 * @version 1.0.9-beta.1.0
 * @since 1.0.9-beta.1.0
 * @author xiao_lfeng
 */
@SuppressWarnings("unused")
@Configuration
public class MybatisPlusConfiguration {
    private final UtilProperties properties;

    public MybatisPlusConfiguration(UtilProperties properties) {
        this.properties = properties;
    }

    /**
     * 配置 Mybatis Plus 分页操作
     *
     * @return MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor pageConfig = new PaginationInnerInterceptor();

        pageConfig.setMaxLimit(20L);
        pageConfig.setDbType(properties.getDbType());

        mybatisPlusInterceptor.addInnerInterceptor(pageConfig);
        return mybatisPlusInterceptor;
    }
}
