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
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class MybatisPlusConfigHandler {
    protected final UtilityBaseProperties properties;

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

    @Bean
    public MybatisPlusPropertiesCustomizer plusPropertiesCustomizer() {
        return props -> props.getGlobalConfig()
                .setIdentifierGenerator(new OrdinaryGenerator());
    }
}
