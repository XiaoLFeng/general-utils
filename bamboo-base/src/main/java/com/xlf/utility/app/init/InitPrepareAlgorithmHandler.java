package com.xlf.utility.app.init;

import com.baomidou.mybatisplus.annotation.DbType;
import com.xlf.utility.app.properties.UtilityBaseProperties;
import com.xlf.utility.dao.MigrateHandlerDAO;
import com.xlf.utility.models.entity.MigrateDO;
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
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 初始化准备算法处理器
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public abstract class InitPrepareAlgorithmHandler {
    private static final Logger log = LoggerFactory.getLogger(InitPrepareAlgorithmHandler.class);

    /**
     * 迁移文件名正则表达式
     */
    private static final Pattern MIGRATE_FILE_PATTERN = Pattern.compile("^\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\w+\\.sql$");

    private final JdbcTemplate jdbcTemplate;
    private final MigrateHandlerDAO migrateDAO;
    private final TransactionTemplate transactionTemplate;
    private final SqlDialectStrategyFactory strategyFactory;
    private final DbType dbType;

    @Contract(pure = true)
    public InitPrepareAlgorithmHandler(
            JdbcTemplate jdbcTemplate,
            MigrateHandlerDAO migrateDAO,
            TransactionTemplate transactionTemplate,
            SqlDialectStrategyFactory strategyFactory,
            UtilityBaseProperties properties
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.migrateDAO = migrateDAO;
        this.transactionTemplate = transactionTemplate;
        this.strategyFactory = strategyFactory;
        this.dbType = properties.getDatasource() != null
                ? properties.getDatasource().getDbType()
                : DbType.MYSQL;
    }

    public final void checkTable(String schema, String tableName) {
        boolean tableExists = this.checkTableExists(schema, tableName);

        if (!tableExists) {
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

        boolean tableExists = this.checkTableExists(schema, "awaken_migrate");

        if (!tableExists) {
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

        // 解析 SQL 语句
        String processedSql = sqlContent.replaceAll("(?s)/\\*.*?\\*/", "");
        processedSql = processedSql.replaceAll("--.*", "");
        String[] statements = processedSql.split(";");
        List<String> validStatements = java.util.Arrays.stream(statements)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        if (validStatements.isEmpty()) {
            log.warn("迁移文件为空 | {}", fileName);
            return;
        }

        // 检查是否存在部分执行的记录
        MigrateDO existingMigrate = migrateDAO.lambdaQuery()
                .eq(MigrateDO::getMigrateName, fileName)
                .one();

        int startLine = 0;
        if (existingMigrate != null) {
            if (!fileHash.equals(existingMigrate.getMigrateHash())) {
                log.error("迁移文件已被修改！文件：{}", fileName);
                log.error("原始哈希：{}", existingMigrate.getMigrateHash());
                log.error("当前哈希：{}", fileHash);
                log.error("已执行的迁移文件不应被修改，系统将终止启动！");
                System.exit(1);
            }
            if ("SUCCESS".equals(existingMigrate.getMigrateStatus())) {
                log.debug("迁移 {} 已成功执行，跳过。", fileName);
                return;
            }
            if ("PARTIAL".equals(existingMigrate.getMigrateStatus())) {
                startLine = existingMigrate.getLastExecutedLine() + 1;
                log.info("迁移 {} 部分执行，从第 {} 条语句继续", fileName, startLine + 1);
            }
        }

        // 执行迁移
        log.info("开始执行迁移 | {} | 共 {} 条语句", fileName, validStatements.size());
        MigrateDO migrate = existingMigrate != null ? existingMigrate : new MigrateDO()
                .setMigrateName(fileName)
                .setMigrateHash(fileHash)
                .setTotalLines(validStatements.size());

        for (int i = startLine; i < validStatements.size(); i++) {
            String sql = validStatements.get(i);
            try {
                jdbcTemplate.execute(sql);
                migrate.setLastExecutedLine(i)
                        .setMigrateStatus("PARTIAL")
                        .setAppliedAt(new Date());
                migrateDAO.saveOrUpdate(migrate);
            } catch (Exception e) {
                log.error("执行迁移失败 | {} | 第 {} 条语句 | {}", fileName, i + 1, e.getMessage(), e);
                throw new RuntimeException("执行迁移失败：" + fileName + "，第 " + (i + 1) + " 条语句", e);
            }
        }

        // 全部成功
        migrate.setMigrateStatus("SUCCESS");
        migrateDAO.saveOrUpdate(migrate);
        log.info("迁移执行成功 | {}", fileName);
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

    /**
     * 使用策略模式检查表是否存在
     *
     * @param schema    数据库 schema，为 null 时使用当前数据库
     * @param tableName 表名
     * @return 表是否存在
     */
    private boolean checkTableExists(String schema, String tableName) {
        SqlDialectStrategy strategy = strategyFactory.getStrategy(dbType);
        String sql = strategy.getCheckTableExistsSql(schema, tableName);
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return !result.isEmpty();
    }
}
