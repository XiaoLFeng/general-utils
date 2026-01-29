package com.xlf.utility.models.entity.sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 通用表数据对象接口
 * <p>
 * 该接口定义了数据库表元数据的通用标准。它作为一个顶层抽象，规定了所有
 * 具体数据库表数据对象（如 MySQL、PostgreSQL 等）必须具备的核心行为，
 * 用于屏蔽不同数据库方言之间的差异。
 * <p>
 * 该接口主要用于描述表的基本身份信息，通常用于数据库迁移脚本的生成、
 * 代码生成器的元数据读取或数据库结构同步等底层操作。
 * <p>
 * <ul>
 * <li>所有实现类应尽量遵循 Java Bean 规范，提供标准的 Getter/Setter。</li>
 * <li>实现类通常对应于数据库系统中的 {@code information_schema} 表。</li>
 * </ul>
 * <p>
 * NOTICE: 该接口仅定义元数据规范，不包含具体的数据库连接或查询逻辑。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class BaseTableDO {
    private String tableCatalog;
    private String tableSchema;
    private String tableName;
}
