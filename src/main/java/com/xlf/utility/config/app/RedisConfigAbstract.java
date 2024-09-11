/*
 * ***************************************************************************************
 * company: FrontLeaves Technology Co,.Ltd.
 * author: XiaoLFeng(https://www.x-lf.com) | FLASHLACK |
 * about:
 *   The project contains the source code of com.frontleaves.master-control.
 *   All source code for this project is licensed under the Apache License 2.0 open source
 *     license.
 * licenseStatement:
 *   Copyright (c) 2023-2024 FrontLeaves Technology Co,.Ltd. All rights reserved.
 *   For more information about the ApacheLicense-2.0 license, please view the LICENSE file
 *     in the project root directory or visit:
 *   https://opensource.org/license/apache-2-0
 * disclaimer:
 *   Since this project is in the model design stage, we are not responsible for any losses
 *     caused by using this project for commercial purposes.
 *   If you modify the code and redistribute it, you need to clearly indicate what changes
 *     you made in the corresponding file.
 *   If the modification is used for commercial purposes, please contact our corporate
 *     personnel.
 * ***************************************************************************************
 */

package com.xlf.utility.config.app;

import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * Redis 配置类
 * <p>
 * 该类用于配置 Redis 相关配置;
 * 该类使用 {@link org.springframework.context.annotation.Configuration} 注解标记；使用方式如下：
 * <pre>
 * {@code
 *      public class RedisConfig extends RedisConfigAbstract {
 *           public RedisConfig(DbType dbType) {
 *               super(dbType);
 *           }
 *      }
 * }
 * </pre>
 *
 * @author xiao_lfeng
 * @version 1.0.9-beta.1.0
 * @since 1.0.9-beta.1.0
 */
@SuppressWarnings("unused")
public abstract class RedisConfigAbstract {
    /**
     * 环境变量
     */
    private final Environment env;
    /**
     * 是否开启事务
     */
    private final boolean isTransaction;

    /**
     * 构造函数
     *
     * @param env           环境变量
     * @param isTransaction 是否开启事务
     */
    public RedisConfigAbstract(Environment env, boolean isTransaction) {
        this.isTransaction = isTransaction;
        this.env = env;
    }

    /**
     * 配置 StringRedisTemplate
     *
     * @return StringRedisTemplate
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();

        if (env.containsProperty("spring.redis.host") && env.getProperty("spring.redis.host") != null) {
            config.setHostName(Objects.requireNonNull(env.getProperty("spring.redis.host")));
        } else {
            config.setHostName("localhost");
        }
        if (env.containsProperty("spring.redis.port") && env.getProperty("spring.redis.port") != null) {
            config.setPort(Integer.parseInt(Objects.requireNonNull(env.getProperty("spring.redis.port"))));
        } else {
            config.setPort(6379);
        }
        if (env.containsProperty("spring.redis.password") && env.getProperty("spring.redis.password") != null) {
            config.setPassword(Objects.requireNonNull(env.getProperty("spring.redis.password")));
        }
        return new JedisConnectionFactory(config);
    }

    /**
     * 配置 RedisTemplate
     *
     * @param <E>           泛型
     * @return RedisTemplate
     */
    @Bean
    public <E> RedisTemplate<String, E> redisTemplate() {
        RedisTemplate<String, E> redis = new RedisTemplate<>();
        redis.setConnectionFactory(jedisConnectionFactory());
        redis.setEnableTransactionSupport(isTransaction);
        return redis;
    }

    /**
     * 配置事务管理器
     * <p>
     * 用于配置事务管理器, 用于配置事务管理器, 用于配置事务管理器。
     *
     * @param dataSource 数据源
     * @return PlatformTransactionManager
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
