package com.xlf.utility.app.init;

import com.xlf.utility.constant.StringConstant;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 系统启动初始化处理器
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public abstract class InitSystemStartupHandler {

    private static final Logger log = LoggerFactory.getLogger(InitSystemStartupHandler.class);

    /**
     * 初始化方法
     */
    public abstract void init();

    /**
     * 初始化结束
     */
    @Bean
    public CommandLineRunner initFinal() {
        return args -> {
            log.info("=========== End of Initialization ===========");
            System.out.print("""
                    \u001B[1;35m    ___                 __                \u001B[1;34m ____
                    \u001B[1;35m   /   |_      ______ _/ /_____  ____    \u001B[1;34m / __ )____ _________
                    \u001B[1;35m  / /| | | /| / / __ `/ //_/ _ \\/ __ \\\\ \u001B[1;34m  / __  / __ `/ ___/ _ \\\\
                    \u001B[1;35m / ___ | |/ |/ / /_/ / ,< /  __/ / / /\u001B[1;34m  / /_/ / /_/ (__  )  __/
                    \u001B[1;35m/_/  |_|__/|__/\\\\__,_/_/|_|\\\\___/_/ /_/\u001B[1;34m  /_____/\\\\__,_/____/\\\\___/ \u001B[0m
                    """);
            System.out.println("\t\t\u001B[33m::: " + StringConstant.SYSTEM_COMPANY + " :::\t\t\t::: " + StringConstant.SYSTEM_VERSION + " :::\u001B[0m");
        };
    }

    /**
     * 准备数据库
     */
    public void prepareDatabase() {
        log.debug("准备数据库「当前无数据库需要检查」");
    }

    /**
     * 执行数据库迁移
     */
    public void databaseMigrate(String schema, @NotNull InitPrepareAlgorithmHandler prepareAlgorithmHandler) {
        log.info("=========== 开始数据库迁移 ===========");

        prepareAlgorithmHandler.checkMigrateTable(schema);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources("classpath*:migrate/*.sql");

            if (resources.length == 0) {
                log.info("未找到迁移文件，跳过迁移。");
                return;
            }

            List<String> fileNames = new ArrayList<>();
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename != null && filename.toLowerCase().endsWith(".sql")) {
                    fileNames.add(filename);
                }
            }

            if (fileNames.isEmpty()) {
                log.info("未找到有效的迁移文件，跳过迁移。");
                return;
            }

            fileNames.sort(Comparator.naturalOrder());

            log.info("找到 {} 个迁移文件", fileNames.size());

            for (String fileName : fileNames) {
                try {
                    Resource resource = resolver.getResource("classpath:migrate/" + fileName);
                    String sqlContent = FileCopyUtils.copyToString(
                            new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
                    );
                    prepareAlgorithmHandler.migrateTable(fileName, sqlContent);
                    log.debug("成功执行迁移文件 | {}", fileName);
                } catch (IOException e) {
                    log.error("读取迁移文件失败 | {} | {}", fileName, e.getMessage(), e);
                    System.exit(1);
                }
            }

            log.info("=========== 数据库迁移完成 ===========");
        } catch (IOException e) {
            log.error("访问迁移目录失败 | {}", e.getMessage(), e);
            log.warn("如果没有迁移需求，可以忽略此错误。");
        }
    }
}
