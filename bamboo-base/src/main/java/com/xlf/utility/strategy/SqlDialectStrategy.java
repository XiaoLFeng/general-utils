package com.xlf.utility.strategy;

import com.baomidou.mybatisplus.annotation.DbType;
import com.xlf.utility.models.entity.sql.BaseTableDO;

/**
 * SQL 方言策略接口
 * <p>
 * 定义不同数据库方言的 SQL 生成策略，用于支持多数据库类型的迁移表创建。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
public interface SqlDialectStrategy {

    /**
     * 获取创建迁移记录表的 SQL 语句
     *
     * @return 建表 SQL
     */
    String getCreateMigrateTableSql();

    /**
     * 判断是否支持指定的数据库类型
     *
     * @param dbType 数据库类型
     * @return true 如果支持该数据库类型
     */
    boolean supports(DbType dbType);

    /**
     * 获取对应的表实体类
     *
     * @return 表实体类 Class 对象
     */
    Class<? extends BaseTableDO> getTableClass();
}
