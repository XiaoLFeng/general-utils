package com.xlf.utility.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xlf.utility.models.entity.sql.BaseTableDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通用数据库表操作映射接口
 <p>
 用于提供通用的数据库表操作的映射接口。该接口继承自 MyBatis-Plus 的
 {@code BaseMapper}，利用泛型 {@code <T>} 支持对不同实体类型的动态处理。
 <p>
 该接口主要用于构建动态的数据访问服务（如 {@code TableHandlerDAO}），
 允许在运行时指定具体的实体类型，从而实现对任意数据库表结构的通用 CRUD 操作。
 <p>
 该接口无需手动编写 SQL 方法，MyBatis-Plus 会自动根据实体类
 （如 {@code MysqlBaseTableDO}）的注解（如 {@code @TableName}）解析表结构。
 <p>
 <ul>
 <li>泛型 {@code <T>}: 指定该 Mapper 操作的实体对象类型，通常对应数据库中的表。</li>
 <li>继承方法: 包含插入、删除、更新、查询等标准的数据库操作方法。</li>
 </ul>
 <p>
 NOTICE: 该接口是一个基础空接口，所有功能均继承自父类，请确保传入的泛型
 实体类已正确配置 MyBatis-Plus 注解（如主键 {@code @TableId} 和表名
 {@code @TableName}）。
 *
 * @author xiao_lfeng
 * @version v1.1.0-SNAPSHOT
 * @since v1.1.0-SNAPSHOT
 */
@Mapper
public interface TableMapper<T extends BaseTableDO> extends BaseMapper<T> {
}
