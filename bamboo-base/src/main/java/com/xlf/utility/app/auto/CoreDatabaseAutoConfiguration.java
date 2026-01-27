package com.xlf.utility.app.auto;

import com.xlf.utility.dao.MigrateHandlerDAO;
import com.xlf.utility.dao.TableHandlerDAO;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * bamboo-base 核心数据库自动配置
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Configuration
@ConditionalOnClass(name = {
        "com.baomidou.mybatisplus.core.mapper.BaseMapper",
        "org.apache.ibatis.session.SqlSessionFactory"
})
@MapperScan("com.xlf.utility.mapper")
public class CoreDatabaseAutoConfiguration {

    @Bean
    public TableHandlerDAO tableHandlerDAO() {
        return new TableHandlerDAO();
    }

    @Bean
    public MigrateHandlerDAO migrateHandlerDAO() {
        return new MigrateHandlerDAO();
    }
}
