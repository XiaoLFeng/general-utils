package com.xlf.utility.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xlf.utility.mapper.TableMapper;
import com.xlf.utility.models.entity.sql.PostgresBaseTableDO;
import org.springframework.stereotype.Repository;

/**
 * PostgreSQL 表元数据处理 DAO
 * <p>
 * 专门用于处理 PostgreSQL 数据库的表结构元数据查询，对应 {@link PostgresBaseTableDO}。
 * 继承自 MyBatis-Plus 的 {@code ServiceImpl}，提供对 {@code information_schema.views} 的查询能力。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Repository
public class PostgresTableHandlerDAO extends ServiceImpl<TableMapper<PostgresBaseTableDO>, PostgresBaseTableDO>
        implements IService<PostgresBaseTableDO> {
}
