package com.xlf.utility.app.init;

import com.baomidou.mybatisplus.annotation.DbType;
import com.xlf.utility.app.properties.UtilityBaseProperties;
import com.xlf.utility.dao.MigrateHandlerDAO;
import com.xlf.utility.dao.TableHandlerDAO;
import com.xlf.utility.models.entity.MigrateDO;
import com.xlf.utility.models.entity.sql.BaseTableDO;
import com.xlf.utility.strategy.SqlDialectStrategy;
import com.xlf.utility.strategy.SqlDialectStrategyFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HexFormat;
import java.util.regex.Pattern;

/**
 * 初始化准备算法处理器
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public abstract class InitPrepareAlgorithmHandler<T extends BaseTableDO> {
    private static final Logger log = LoggerFactory.getLogger(InitPrepareAlgorithmHandler.class);

    /**
     * 迁移文件名正则表达式
     */
    private static final Pattern MIGRATE_FILE_PATTERN = Pattern.compile("^\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\w+\\.sql$");

    private final JdbcTemplate jdbcTemplate;
    private final TableHandlerDAO<T> tableDAO;
    private final MigrateHandlerDAO migrateDAO;
    private final TransactionTemplate transactionTemplate;
    private final SqlDialectStrategyFactory strategyFactory;
    private final DbType dbType;

    @Contract(pure = true)
    public InitPrepareAlgorithmHandler(
            JdbcTemplate jdbcTemplate,
            TableHandlerDAO<T> tableDAO,
            MigrateHandlerDAO migrateDAO,
            TransactionTemplate transactionTemplate,
            SqlDialectStrategyFactory strategyFactory,
            UtilityBaseProperties properties
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.tableDAO = tableDAO;
        this.migrateDAO = migrateDAO;
        this.transactionTemplate = transactionTemplate;
        this.strategyFactory = strategyFactory;
        this.dbType = properties.getDatasource() != null
                ? properties.getDatasource().getDbType()
                : DbType.MYSQL;
    }

    public final void checkTable(String schema, String tableName) {
        T tableDO = tableDAO.lambdaQuery()
                .eq(schema != null, BaseTableDO::getTableSchema, schema)
                .eq(BaseTableDO::getTableName, tableName)
                .one();

        if (tableDO == null) {
            ClassPathResource classPathResource = new ClassPathResource("/database/" + tableName + ".sql");
            try {
                String getSql = FileCopyUtils.copyToString(new InputStreamReader(classPathResource.getInputStream(), StandardCharsets.UTF_8));

                getSql = getSql.replaceAll("(?s)/\\*.*?\\*/", "");
                getSql = getSql.replaceAll("--.*", "");

                final String finalSql = getSql;
                Boolean success = transactionTemplate.execute(status -> {
                    try {
                        for (String sql : finalSql.split(";")) {
                            String trimmedSql = sql.trim();
                            if (!trimmedSql.isEmpty()) {
                                jdbcTemplate.execute(trimmedSql);
                            }
                        }
                        return true;
                    } catch (Exception e) {
                        log.error("执行SQL语句失败，正在回滚 | {}", e.getMessage(), e);
                        status.setRollbackOnly();
                        throw new RuntimeException("创建表失败：" + tableName, e);
                    }
                });

                if (success != null && success) {
                    log.debug("创建表成功 | {}", tableName);
                }
            } catch (Exception e) {
                log.error("执行SQL语句失败 | {}", e.getMessage(), e);
                System.exit(1);
            }
        } else {
            log.info("表 {} 已存在，跳过创建。", tableName);
        }
    }

    protected final void checkMigrateTable(String schema) {
        SqlDialectStrategy strategy = strategyFactory.getStrategy(dbType);

        T tableDO = tableDAO.lambdaQuery()
                .eq(schema != null, BaseTableDO::getTableSchema, schema)
                .eq(BaseTableDO::getTableName, "awaken_migrate")
                .one();

        if (tableDO == null) {
            String sqlString = strategy.getCreateMigrateTableSql();

            try {
                transactionTemplate.execute(status -> {
                    try {
                        jdbcTemplate.execute(sqlString);
                        return true;
                    } catch (Exception e) {
                        log.error("创建迁移记录表失败，正在回滚 | {}", e.getMessage(), e);
                        status.setRollbackOnly();
                        throw new RuntimeException("创建迁移记录表失败", e);
                    }
                });
                log.debug("创建表成功 | awaken_migrate");
            } catch (Exception e) {
                log.error("执行SQL语句失败 | {}", e.getMessage(), e);
                System.exit(1);
            }
        } else {
            log.info("表 awaken_migrate 已存在，跳过创建。");
        }
    }

    public final void migrateTable(@NotNull String fileName, @NotNull String sqlContent) {
        if (!MIGRATE_FILE_PATTERN.matcher(fileName).matches()) {
            log.warn("迁移文件名格式不正确，跳过执行 | {}", fileName);
            log.warn("正确格式：yyyy_MM_dd_HH_mm_functionName.sql，例如：2025_10_18_16_36_addForeignKey.sql");
            return;
        }

        String fileHash = this.calculateSha256(sqlContent);

        MigrateDO existingMigrate = migrateDAO.lambdaQuery()
                .eq(MigrateDO::getMigrateName, fileName)
                .one();

        if (existingMigrate != null) {
            if (!fileHash.equals(existingMigrate.getMigrateHash())) {
                log.error("迁移文件已被修改！文件：{}", fileName);
                log.error("原始哈希：{}", existingMigrate.getMigrateHash());
                log.error("当前哈希：{}", fileHash);
                log.error("已执行的迁移文件不应被修改，系统将终止启动！");
                System.exit(1);
            }
            log.debug("迁移 {} 已执行，跳过。", fileName);
            return;
        }

        log.info("开始执行迁移 | {}", fileName);
        try {
            Boolean success = transactionTemplate.execute(status -> {
                try {
                    String processedSql = sqlContent.replaceAll("(?s)/\\*.*?\\*/", "");
                    processedSql = processedSql.replaceAll("--.*", "");

                    for (String sql : processedSql.split(";")) {
                        String trimmedSql = sql.trim();
                        if (!trimmedSql.isEmpty()) {
                            jdbcTemplate.execute(trimmedSql);
                        }
                    }

                    MigrateDO migrate = new MigrateDO()
                            .setMigrateName(fileName)
                            .setMigrateHash(fileHash)
                            .setMigrateStatus("SUCCESS")
                            .setAppliedAt(new Date());
                    migrateDAO.save(migrate);

                    return true;
                } catch (Exception e) {
                    log.error("执行迁移失败，正在回滚 | {}", e.getMessage(), e);
                    status.setRollbackOnly();

                    MigrateDO migrate = new MigrateDO()
                            .setMigrateName(fileName)
                            .setMigrateHash(fileHash)
                            .setMigrateStatus("FAILED")
                            .setErrorMessage(e.getMessage())
                            .setAppliedAt(new Date());
                    migrateDAO.save(migrate);
                    throw new RuntimeException("执行迁移失败：" + fileName, e);
                }
            });

            if (success != null && success) {
                log.info("迁移执行成功 | {}", fileName);
            }
        } catch (Exception e) {
            log.error("迁移执行失败 | {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    private String calculateSha256(@NotNull String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            log.error("计算SHA-256失败 | {}", e.getMessage(), e);
            return "";
        }
    }
}
