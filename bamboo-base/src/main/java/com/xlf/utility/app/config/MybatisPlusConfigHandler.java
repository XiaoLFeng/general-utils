package com.xlf.utility.app.config;

import com.xlf.utility.app.properties.UtilityBaseProperties;
import com.xlf.utility.exception.library.ServerInternalErrorException;
import com.xlf.utility.incrementer.OrdinaryGenerator;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

/**
 * MybatisPlus 配置处理器
 * <p>
 * 用于处理 MybatisPlus 的核心配置，包括分页拦截器和 ID 生成策略。
 * <p>
 * 该类主要负责初始化 {@link MybatisPlusInterceptor}，并根据应用配置自动注入分页插件。
 * 它会读取 {@code UtilityBaseProperties} 中的数据源配置来设置数据库类型（DbType），
 * 并支持动态配置分页的最大限制条数。
 * <p>
 * 在初始化过程中，如果配置对象为空，将会抛出 {@link ServerInternalErrorException} 异常，
 * 确保系统在配置缺失时能够快速失败。
 * <p>
 * 此外，该类还负责定制 MybatisPlus 的全局属性，通过注入
 * {@link MybatisPlusPropertiesCustomizer} 将默认的 ID 生成器替换为自定义的
 * {@link OrdinaryGenerator}。
 * <p>
 * NOTICE: 确保配置文件中 {@code bamboo.base.datasource.db-type} 已正确设置，
 * 否则可能无法正确识别数据库方言。同时，分页最大限制设置过大可能导致内存溢出。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class MybatisPlusConfigHandler {
    protected final UtilityBaseProperties properties;

    /**
     * 配置 MybatisPlus 核心拦截器
     * <p>
     * 用于初始化并返回一个配置了分页插件的 {@link MybatisPlusInterceptor} Bean。
     * <p>
     * 该方法通过读取配置文件中的 {@code bamboo.base.datasource} 配置项，
     * 动态设置 {@link PaginationInnerInterceptor} 的数据库类型（DbType）和分页最大限制条数。
     * <p>
     * 该方法使用 {@code Optional} 进行链式调用以保证构建过程的稳定性，
     * 如果构建过程中出现异常（理论上不应发生，除非实例化失败），
     * 将抛出 {@link ServerInternalErrorException}。
     * <p>
     * 支持的配置属性如下：
     * <ul>
     * <li>db-type: 数据库类型（如 MYSQL），未配置时使用 MybatisPlus 默认值。</li>
     * <li>page.max-limit: 单页分页条数的上限，防止恶意查询大量数据导致 OOM。</li>
     * </ul>
     * <p>
     * NOTICE: 请确保 {@code bamboo.base.datasource.db-type} 配置正确，
     * 否则可能导致分页 SQL 生成错误。此外，建议合理配置 {@code maxLimit} 以防止内存溢出。
     *
     * @return 配置好分页插件的 MybatisPlusInterceptor 实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        return Optional.of(new MybatisPlusInterceptor())
                .map(interceptor -> {
                    PaginationInnerInterceptor innerInterceptor = new PaginationInnerInterceptor();
                    if (properties.getDatasource() != null) {
                        innerInterceptor.setDbType(properties.getDatasource().getDbType());
                        if (properties.getDatasource().getPage() != null) {
                            innerInterceptor.setMaxLimit(properties.getDatasource().getPage().getMaxLimit());
                        }
                    }
                    interceptor.addInnerInterceptor(innerInterceptor);
                    return interceptor;
                }).orElseThrow(() -> new ServerInternalErrorException("初始化 MybatisPlusInterceptor 失败"));
    }

    /**
     *
     */
    @Bean
    public MybatisPlusPropertiesCustomizer plusPropertiesCustomizer() {
        return props -> props.getGlobalConfig().setIdentifierGenerator(new OrdinaryGenerator());
    }
}
