package com.xlf.utility.utility;

import com.xlf.utility.incrementer.SnowflakeIdGenerator;
import com.xlf.utility.models.entity.dto.SnowflakeInfoDTO;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 标准雪花ID工具类
 * <p>
 * {@code SnowflakeUtil} 提供静态方法用于生成和解析标准雪花ID。
 * 该工具类使用单例模式持有 {@link SnowflakeIdGenerator} 实例，确保全局唯一且线程安全。
 * </p>
 *
 * <h2>功能特性</h2>
 * <ul>
 *     <li><b>静态方法</b>：无需手动创建实例，直接调用静态方法</li>
 *     <li><b>单例模式</b>：内部持有单个生成器实例，保证全局一致</li>
 *     <li><b>配置化</b>：从配置文件读取数据中心ID、机器ID等参数</li>
 *     <li><b>解析功能</b>：支持解析ID的各个组成部分</li>
 *     <li><b>线程安全</b>：底层生成器使用同步机制保证并发安全</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 生成标准雪花ID
 * long id = SnowflakeUtil.generateId();
 * String idStr = SnowflakeUtil.generateIdString();
 *
 * // 解析ID各部分
 * long timestamp = SnowflakeUtil.parseTimestamp(id);
 * long datacenterId = SnowflakeUtil.parseDatacenterId(id);
 * long machineId = SnowflakeUtil.parseMachineId(id);
 * long sequence = SnowflakeUtil.parseSequence(id);
 *
 * // 解析完整信息
 * SnowflakeInfoDTO info = SnowflakeUtil.parseInfo(id);
 * System.out.println("生成时间: " + info.getGeneratedTime());
 * System.out.println("数据中心: " + info.getDatacenterId());
 * }</pre>
 *
 * <h2>配置说明</h2>
 * <p>
 * 工具类从配置文件 {@code application-utility-snowflake.yml} 读取以下配置：
 * <ul>
 *     <li>{@code utility.base.snowflake.datacenter-id}: 数据中心ID（默认1）</li>
 *     <li>{@code utility.base.snowflake.machine-id}: 机器ID（默认1）</li>
 *     <li>{@code utility.base.snowflake.epoch}: 起始时间戳（默认2024-01-01）</li>
 * </ul>
 * </p>
 *
 * @author xiao_lfeng
 * @version 1.1.8
 * @since 1.1.8
 * @see SnowflakeIdGenerator
 * @see SnowflakeInfoDTO
 */
public final class SnowflakeUtil {

    // ========================================
    // 位运算常量（与生成器保持一致）
    // ========================================

    /**
     * 序列号位数（12位）
     */
    static final long SEQUENCE_BITS = 12L;

    /**
     * 机器ID位数（5位）
     */
    static final long MACHINE_ID_BITS = 5L;

    /**
     * 数据中心ID位数（5位）
     */
    static final long DATACENTER_ID_BITS = 5L;

    /**
     * 序列号最大值（4095）
     */
    static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    /**
     * 机器ID最大值（31）
     */
    static final long MAX_MACHINE_ID = ~(-1L << MACHINE_ID_BITS);

    /**
     * 数据中心ID最大值（31）
     */
    static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);

    /**
     * 机器ID左移位数（12位）
     */
    static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 数据中心ID左移位数（17位）
     */
    static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;

    /**
     * 时间戳左移位数（22位）
     */
    static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS + DATACENTER_ID_BITS;

    // ========================================
    // 单例生成器
    // ========================================

    /**
     * 单例生成器实例
     * <p>
     * 通过 {@link com.xlf.utility.app.init.SnowflakeUtilInitializer} 在 Spring 容器启动时初始化。
     * </p>
     */
    private static volatile SnowflakeIdGenerator GENERATOR;

    /**
     * 私有构造函数，防止实例化
     */
    @Contract(pure = true)
    private SnowflakeUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 初始化雪花ID生成器
     * <p>
     * 由 {@link com.xlf.utility.app.init.SnowflakeUtilInitializer} 调用，
     * 从 Spring 配置文件读取配置后初始化生成器。
     * </p>
     *
     * @param generator 已配置的雪花ID生成器实例
     */
    public static void initialize(@NotNull SnowflakeIdGenerator generator) {
        if (GENERATOR == null) {
            synchronized (SnowflakeUtil.class) {
                if (GENERATOR == null) {
                    GENERATOR = generator;
                }
            }
        }
    }

    /**
     * 获取生成器实例
     * <p>
     * 如果生成器未初始化，则使用默认配置创建（兜底机制）。
     * </p>
     *
     * @return 雪花ID生成器实例
     */
    @NotNull
    private static SnowflakeIdGenerator getGenerator() {
        if (GENERATOR == null) {
            synchronized (SnowflakeUtil.class) {
                if (GENERATOR == null) {
                    // 兜底机制：使用默认配置
                    GENERATOR = new SnowflakeIdGenerator(1L, 1L, 1729440000000L);
                }
            }
        }
        return GENERATOR;
    }

    // ========================================
    // ID生成方法
    // ========================================

    /**
     * 生成标准雪花ID
     * <p>
     * 返回64位长整型的唯一ID，包含时间戳、数据中心ID、机器ID和序列号。
     * </p>
     *
     * @return 生成的雪花ID
     */
    @Contract(pure = false)
    public static long generateId() {
        return (Long) getGenerator().nextId(null);
    }

    /**
     * 生成字符串格式的雪花ID
     * <p>
     * 返回雪花ID的字符串表示形式。
     * </p>
     *
     * @return 雪花ID的字符串形式
     */
    @NotNull
    @Contract(pure = false)
    public static String generateIdString() {
        return String.valueOf(generateId());
    }

    // ========================================
    // ID解析方法
    // ========================================

    /**
     * 解析雪花ID中的时间戳
     * <p>
     * 提取ID中的41位时间戳部分，并加上起始时间戳（epoch）得到实际时间。
     * </p>
     *
     * @param id 雪花ID
     * @return 实际时间戳（毫秒）
     */
    @Contract(pure = true)
    public static long parseTimestamp(long id) {
        return (id >> TIMESTAMP_SHIFT) + getGenerator().getEpoch();
    }

    /**
     * 解析雪花ID中的数据中心ID
     * <p>
     * 提取ID中的5位数据中心ID部分。
     * </p>
     *
     * @param id 雪花ID
     * @return 数据中心ID（0-31）
     */
    @Contract(pure = true)
    public static long parseDatacenterId(long id) {
        return (id >> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID;
    }

    /**
     * 解析雪花ID中的机器ID
     * <p>
     * 提取ID中的5位机器ID部分。
     * </p>
     *
     * @param id 雪花ID
     * @return 机器ID（0-31）
     */
    @Contract(pure = true)
    public static long parseMachineId(long id) {
        return (id >> MACHINE_ID_SHIFT) & MAX_MACHINE_ID;
    }

    /**
     * 解析雪花ID中的序列号
     * <p>
     * 提取ID中的12位序列号部分。
     * </p>
     *
     * @param id 雪花ID
     * @return 序列号（0-4095）
     */
    @Contract(pure = true)
    public static long parseSequence(long id) {
        return id & MAX_SEQUENCE;
    }

    /**
     * 解析雪花ID的完整信息
     * <p>
     * 将雪花ID解析为 {@link SnowflakeInfoDTO} 对象，包含所有组成部分和生成时间。
     * </p>
     *
     * @param id 雪花ID
     * @return 包含完整信息的 SnowflakeInfoDTO 对象
     */
    @NotNull
    @Contract(pure = true)
    public static SnowflakeInfoDTO parseInfo(long id) {
        long timestamp = parseTimestamp(id);
        long datacenterId = parseDatacenterId(id);
        long machineId = parseMachineId(id);
        long sequence = parseSequence(id);

        // 将时间戳转换为 LocalDateTime
        LocalDateTime generatedTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault()
        );

        return SnowflakeInfoDTO.builder()
                .id(id)
                .timestamp(timestamp - getGenerator().getEpoch())
                .datacenterId(datacenterId)
                .machineId(machineId)
                .sequence(sequence)
                .generatedTime(generatedTime)
                .build();
    }

    // ========================================
    // ID验证方法
    // ========================================

    /**
     * 验证雪花ID是否有效
     * <p>
     * 检查雪花ID的有效性，包括：
     * <ul>
     *     <li>ID不为null且为正数</li>
     *     <li>时间戳在合理范围内（epoch到当前时间）</li>
     *     <li>各个组件在定义的最大值范围内</li>
     * </ul>
     * </p>
     *
     * <h3>使用示例</h3>
     * <pre>{@code
     * long id = SnowflakeUtil.generateId();
     * boolean valid = SnowflakeUtil.isValid(id);  // true
     *
     * boolean invalidId = SnowflakeUtil.isValid(null);  // false
     * boolean invalidId2 = SnowflakeUtil.isValid(-1L);  // false
     * }</pre>
     *
     * @param id 要验证的雪花ID
     * @return 如果ID有效返回 true，否则返回 false
     */
    @Contract(pure = true)
    public static boolean isValid(Long id) {
        // 验证ID非null且为正数
        if (id == null || id <= 0) {
            return false;
        }

        // 验证时间戳在合理范围内
        long timestamp = parseTimestamp(id);
        long epoch = getEpoch();
        long currentTime = System.currentTimeMillis();

        // 时间戳应该在epoch之后，当前时间之前（允许1秒的时钟偏移）
        if (timestamp < epoch || timestamp > currentTime + 1000) {
            return false;
        }

        // 验证各部分是否在有效范围内
        // 虽然通过位运算提取已经保证在范围内，但作为额外的安全检查
        long datacenterId = parseDatacenterId(id);
        long machineId = parseMachineId(id);
        long sequence = parseSequence(id);

        return datacenterId <= MAX_DATACENTER_ID
                && machineId <= MAX_MACHINE_ID
                && sequence <= MAX_SEQUENCE;
    }

    // ========================================
    // 配置相关方法
    // ========================================

    /**
     * 获取生成器的配置信息
     * <p>
     * 返回当前生成器使用的数据中心ID、机器ID和起始时间戳。
     * </p>
     *
     * @return 包含配置信息的字符串
     */
    @NotNull
    @Contract(pure = true)
    public static String getConfigInfo() {
        SnowflakeIdGenerator generator = getGenerator();
        return "SnowflakeUtil Config: " +
                "datacenterId=" + generator.getDatacenterId() +
                ", machineId=" + generator.getMachineId() +
                ", epoch=" + generator.getEpoch();
    }

    /**
     * 获取数据中心ID
     *
     * @return 数据中心ID
     */
    @Contract(pure = true)
    public static long getDatacenterId() {
        return getGenerator().getDatacenterId();
    }

    /**
     * 获取机器ID
     *
     * @return 机器ID
     */
    @Contract(pure = true)
    public static long getMachineId() {
        return getGenerator().getMachineId();
    }

    /**
     * 获取起始时间戳
     *
     * @return 起始时间戳（毫秒）
     */
    @Contract(pure = true)
    public static long getEpoch() {
        return getGenerator().getEpoch();
    }
}
