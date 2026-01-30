package com.xlf.utility.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xlf.utility.mapper.TableMapper;
import com.xlf.utility.models.entity.sql.MysqlBaseTableDO;
import org.springframework.stereotype.Repository;

/**
 * MySQL 表元数据处理 DAO
 * <p>
 * 专门用于处理 MySQL 数据库的表结构元数据查询，对应 {@link MysqlBaseTableDO}。
 * 继承自 MyBatis-Plus 的 {@code ServiceImpl}，提供对 {@code information_schema.TABLES} 的查询能力。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Repository
public class MysqlTableHandlerDAO extends ServiceImpl<TableMapper<MysqlBaseTableDO>, MysqlBaseTableDO>
        implements IService<MysqlBaseTableDO> {
}
