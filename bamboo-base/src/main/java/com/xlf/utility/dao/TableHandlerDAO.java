package com.xlf.utility.dao;

import com.xlf.utility.mapper.TableMapper;
import com.xlf.utility.models.entity.sql.BaseTableDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 通用数据库表处理数据访问对象
 * <p>
 * 该服务类继承自 MyBatis-Plus 的 {@code ServiceImpl}，作为通用表数据对象
 * （{@code BaseTableDO}）的数据访问层（DAO）实现。
 * <p>
 * 它通过泛型 {@code <T extends BaseTableDO>} 支持动态的实体类型，提供了对
 * 数据库表元数据的通用增删改查（CRUD）操作能力。
 * 该类主要用于处理数据库表结构的元数据信息，通常配合 {@code TableMapper}
 * 使用，利用 MyBatis-Plus 的自动化机制，无需编写 SQL 即可完成对表结构定义
 * 数据的维护，常用于数据库同步、结构迁移或元数据管理等场景。
 * <p>
 * NOTICE: 请确保传入的泛型 {@code <T>} 已正确配置 MyBatis-Plus 注解
 * （如 {@code @TableName} 和 {@code @TableId}），否则可能导致操作失败。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
public class TableHandlerDAO<T extends BaseTableDO> extends ServiceImpl<TableMapper<T>, T> implements IService<T> {
}
