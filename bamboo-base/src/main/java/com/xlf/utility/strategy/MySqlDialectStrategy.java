package com.xlf.utility.strategy;

import com.baomidou.mybatisplus.annotation.DbType;
import com.xlf.utility.models.entity.sql.BaseTableDO;
import com.xlf.utility.models.entity.sql.MysqlBaseTableDO;
import org.springframework.stereotype.Component;

/**
 * MySQL 方言策略实现
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Component
public class MySqlDialectStrategy implements SqlDialectStrategy {

    @Override
    public String getCreateMigrateTableSql() {
        return """
                CREATE TABLE IF NOT EXISTS `awaken_migrate`
                (
                    migrate_id        BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
                    migrate_name      VARCHAR(255)    NOT NULL UNIQUE COMMENT '迁移文件名',
                    migrate_hash      VARCHAR(64)     NOT NULL COMMENT '文件 SHA-256 哈希值',
                    migrate_status    VARCHAR(20)     NOT NULL DEFAULT 'SUCCESS' COMMENT '迁移状态：SUCCESS/PARTIAL/FAILED',
                    error_message     TEXT                     DEFAULT NULL COMMENT '错误信息',
                    last_executed_line INT                     DEFAULT NULL COMMENT '最后成功执行的语句序号',
                    total_lines       INT                      DEFAULT NULL COMMENT '总语句数',
                    applied_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '应用时间'
                ) ENGINE = InnoDB
                  DEFAULT CHARSET = utf8mb4 COMMENT ='迁移记录表';
                """;
    }

    @Override
    public boolean supports(DbType dbType) {
        return dbType == DbType.MYSQL || dbType == DbType.MARIADB;
    }

    @Override
    public Class<? extends BaseTableDO> getTableClass() {
        return MysqlBaseTableDO.class;
    }

    @Override
    public String getCheckTableExistsSql(String schema, String tableName) {
        if (schema != null) {
            return "SELECT 1 FROM information_schema.TABLES " +
                   "WHERE TABLE_SCHEMA = '" + schema + "' AND TABLE_NAME = '" + tableName + "' LIMIT 1";
        } else {
            return "SELECT 1 FROM information_schema.TABLES " +
                   "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = '" + tableName + "' LIMIT 1";
        }
    }
}
