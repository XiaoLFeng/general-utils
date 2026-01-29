package com.xlf.utility.app.properties;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置类：UtilityBaseProperties
 * <p>
 * 该配置类用于加载通用基础属性，主要用于定义数据库类型以及分页相关的配置信息。
 * 配置通过读取以 "bamboo.base" 为前缀的属性来加载，并为开发者提供可配置选项。
 *
 * <p>
 * 属性结构为：
 * <ul>
 *   <li>{@code datasource.db-type} - 数据库类型（默认为 MySQL）。</li>
 *   <li>{@code datasource.page.max-limit} - 最大分页限制（默认为 500 条）。</li>
 *   <li>{@code datasource.page.default-limit} - 默认分页限制（默认为 20 条）。</li>
 *   <li>{@code snowflake} - 雪花ID配置。</li>
 * </ul>
 *
 * <p>NOTICE: 在配置时，请确保分页上限值不要设置为过小或过大，以避免系统性能瓶颈。</p>
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Data
@ConfigurationProperties(prefix = "bamboo.base")
public class UtilityBaseProperties {
    /**
     * 数据源配置
     */
    private Datasource datasource;

    /**
     * 系统名称
     */
    private String systemName;

    /**
     * 雪花ID配置
     */
    private Snowflake snowflake;

    @Data
    public static class Datasource {
        /**
         * 数据库类型
         */
        private DbType dbType = DbType.MYSQL;

        private Page page;

        @Data
        public static class Page {
            /**
             * 最大分页限制
             */
            private Long maxLimit = 500L;
            /**
             * 默认分页限制
             */
            private Long defaultLimit = 20L;
        }
    }

    /**
     * 雪花ID配置类
     * <p>
     * 用于配置雪花ID生成器的相关参数，包括数据中心ID、机器ID、起始时间戳等。
     * </p>
     */
    @Data
    public static class Snowflake {
        /**
         * 数据中心ID
         * <p>
         * 标准模式: 取值范围 0-31 (5位)<br>
         * 基因模式: 取值范围 0-7 (3位)
         * </p>
         */
        private Long datacenterId = 1L;

        /**
         * 机器ID
         * <p>
         * 标准模式: 取值范围 0-31 (5位)<br>
         * 基因模式: 取值范围 0-15 (4位)
         * </p>
         */
        private Long machineId = 1L;

        /**
         * 起始时间戳（Epoch）
         * <p>
         * 默认值: 1690214400000 (2023-07-25 00:00:00 UTC)
         * </p>
         */
        private Long epoch = 1690214400000L;
    }
}
