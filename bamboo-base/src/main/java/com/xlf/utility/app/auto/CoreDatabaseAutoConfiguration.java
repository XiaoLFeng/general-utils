package com.xlf.utility.app.auto;

import com.baomidou.mybatisplus.annotation.DbType;
import com.xlf.utility.app.properties.UtilityBaseProperties;
import com.xlf.utility.dao.MigrateHandlerDAO;
import com.xlf.utility.dao.MysqlTableHandlerDAO;
import com.xlf.utility.dao.PostgresTableHandlerDAO;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
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
@ComponentScan("com.xlf.utility.strategy")
public class CoreDatabaseAutoConfiguration {

    @Bean
    @ConditionalOnProperty(
            name = "utility.base.datasource.db-type",
            havingValue = "mysql",
            matchIfMissing = true
    )
    public MysqlTableHandlerDAO mysqlTableHandlerDAO() {
        return new MysqlTableHandlerDAO();
    }

    @Bean
    @ConditionalOnProperty(
            name = "utility.base.datasource.db-type",
            havingValue = "postgresql"
    )
    public PostgresTableHandlerDAO postgresTableHandlerDAO() {
        return new PostgresTableHandlerDAO();
    }

    @Bean
    public MigrateHandlerDAO migrateHandlerDAO() {
        return new MigrateHandlerDAO();
    }
}
