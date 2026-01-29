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
                    migrate_id     BIGSERIAL PRIMARY KEY,
                    migrate_name   VARCHAR(255) NOT NULL UNIQUE,
                    migrate_hash   VARCHAR(64)  NOT NULL,
                    migrate_status VARCHAR(20)  NOT NULL DEFAULT 'SUCCESS',
                    error_message  TEXT,
                    applied_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                );
                COMMENT ON TABLE awaken_migrate IS '迁移记录表';
                COMMENT ON COLUMN awaken_migrate.migrate_id IS '主键';
                COMMENT ON COLUMN awaken_migrate.migrate_name IS '迁移文件名';
                COMMENT ON COLUMN awaken_migrate.migrate_hash IS '文件 SHA-256 哈希值';
                COMMENT ON COLUMN awaken_migrate.migrate_status IS '迁移状态：SUCCESS/FAILED/ROLLBACK';
                COMMENT ON COLUMN awaken_migrate.error_message IS '错误信息';
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
}
