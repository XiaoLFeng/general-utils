package com.xlf.utility.models.entity.sql;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * PostgreSQL 视图数据对象
 * <p>
 * 该类用于映射 PostgreSQL 数据库中视图的结构元数据。通过 MyBatis-Plus
 * 框架，该实体对应于 {@code information_schema.views} 系统表，用于获取
 * 数据库中视图的目录、Schema 以及名称等基础信息。
 * <p>
 * 该类实现了通用表数据接口 {@link BaseTableDO}，主要用于系统启动时的数据库
 * 检查、元数据同步或视图迁移等场景，用于屏蔽特定数据库方言的差异。
 * <p>
 * <ul>
 * <li>{@code tableCatalog}: 视图所在的目录（对应数据库名）。</li>
 * <li>{@code tableSchema}: 视图所在的 Schema（模式名）。</li>
 * <li>{@code tableName}: 视图的名称。</li>
 * </ul>
 * <p>
 * NOTICE: 虽然该类映射的是 {@code views} 表而非常规的 {@code tables} 表，
 * 但在通用接口体系中，它被视为一种特殊的表结构进行处理。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName(schema = "information_schema", value = "views")
public class PostgresBaseTableDO extends BaseTableDO {
    // 字段继承自 BaseTableDO，无需重复定义
}
