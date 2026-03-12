package com.xlf.utility.strategy;

import com.baomidou.mybatisplus.annotation.DbType;
import com.xlf.utility.exception.library.ServerInternalErrorException;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SQL 方言策略工厂
 * <p>
 * 用于根据数据库类型获取对应的 SQL 方言策略。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Component
public class SqlDialectStrategyFactory {

    private final List<SqlDialectStrategy> strategies;

    public SqlDialectStrategyFactory(List<SqlDialectStrategy> strategies) {
        this.strategies = strategies;
    }

    /**
     * 获取指定数据库类型的策略
     *
     * @param dbType 数据库类型
     * @return 对应的 SQL 方言策略
     * @throws ServerInternalErrorException 如果不支持该数据库类型
     */
    public SqlDialectStrategy getStrategy(DbType dbType) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(dbType))
                .findFirst()
                .orElseThrow(() -> new ServerInternalErrorException("不支持的数据库类型: " + dbType));
    }
}
