package com.xlf.utility.models.entity.sql;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * MySQL 表信息数据对象
 * <p>
 * 该类用于映射 MySQL 数据库中的表结构元数据。通过 MyBatis-Plus 框架，
 * 该实体对应于 {@code information_schema.TABLES} 系统表，用于获取数据库中
 * 表的目录、Schema 以及表名等基础信息。
 * <p>
 * 该类主要用于系统启动时的数据库检查、迁移前奏或元数据同步等场景。
 * <p>
 * <ul>
 * <li>{@code tableCatalog}: 表所在的目录（通常是数据库名）。</li>
 * <li>{@code tableSchema}: 表所在的 Schema（数据库名）。</li>
 * <li>{@code tableName}: 表的名称。</li>
 * </ul>
 * <p>
 * NOTICE: 该类仅作为数据传输对象（DTO）使用，不包含复杂的业务逻辑。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v1.1.0-SNAPSHOT
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName(schema = "information_schema", value = "TABLES")
public final class MysqlBaseTableDO extends BaseTableDO {
}
