package com.xlf.utility.strategy;

import com.baomidou.mybatisplus.annotation.DbType;
import com.xlf.utility.models.entity.sql.BaseTableDO;
import com.xlf.utility.models.entity.sql.PostgresBaseTableDO;
import org.springframework.stereotype.Component;

/**
 * PostgreSQL 方言策略实现
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Component
public class PostgreSqlDialectStrategy implements SqlDialectStrategy {

    @Override
    public String getCreateMigrateTableSql() {
        return """
                CREATE TABLE IF NOT EXISTS awaken_migrate
                (
                    migrate_id         BIGSERIAL PRIMARY KEY,
                    migrate_name       VARCHAR(255) NOT NULL UNIQUE,
                    migrate_hash       VARCHAR(64)  NOT NULL,
                    migrate_status     VARCHAR(20)  NOT NULL DEFAULT 'SUCCESS',
                    error_message      TEXT,
                    last_executed_line INT,
                    total_lines        INT,
                    applied_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                );
                COMMENT ON TABLE awaken_migrate IS '迁移记录表';
                COMMENT ON COLUMN awaken_migrate.migrate_id IS '主键';
                COMMENT ON COLUMN awaken_migrate.migrate_name IS '迁移文件名';
                COMMENT ON COLUMN awaken_migrate.migrate_hash IS '文件 SHA-256 哈希值';
                COMMENT ON COLUMN awaken_migrate.migrate_status IS '迁移状态：SUCCESS/PARTIAL/FAILED';
                COMMENT ON COLUMN awaken_migrate.error_message IS '错误信息';
                COMMENT ON COLUMN awaken_migrate.last_executed_line IS '最后成功执行的语句序号';
                COMMENT ON COLUMN awaken_migrate.total_lines IS '总语句数';
                COMMENT ON COLUMN awaken_migrate.applied_at IS '应用时间';
                """;
    }

    @Override
    public boolean supports(DbType dbType) {
        return dbType == DbType.POSTGRE_SQL;
    }

    @Override
    public Class<? extends BaseTableDO> getTableClass() {
        return PostgresBaseTableDO.class;
    }

    @Override
    public String getCheckTableExistsSql(String schema, String tableName) {
        if (schema != null) {
            return "SELECT 1 FROM information_schema.tables " +
                   "WHERE table_schema = '" + schema + "' AND table_name = '" + tableName + "' LIMIT 1";
        } else {
            return "SELECT 1 FROM information_schema.tables " +
                   "WHERE table_schema = CURRENT_SCHEMA() AND table_name = '" + tableName + "' LIMIT 1";
        }
    }
}
