package com.xlf.utility.incrementer;

import com.xlf.utility.ErrorCode;
import com.xlf.utility.exception.BusinessException;
import com.xlf.utility.models.entity.dto.SnowflakeInfoDTO;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 标准雪花ID生成器
 * <p>
 * {@code SnowflakeIdGenerator} 实现了 {@link IdentifierGenerator} 接口，
 * 基于 Twitter Snowflake 算法生成64位长整型的唯一ID。该算法可以在分布式环境下
 * 保证ID的唯一性、递增性和高性能。
 * </p>
 *
 * <h2>雪花ID结构（64位）</h2>
 * <pre>
 * ┌─────────────┬──────────┬──────────┬─────────────┐
 * │  时间戳位   │ 数据中心 │ 机器ID位 │  序列号位   │
 * │  (41位)     │  (5位)   │  (5位)   │   (12位)    │
 * └─────────────┴──────────┴──────────┴─────────────┘
 *  63          22 21      17 16      12 11          0
 * </pre>
 *
 * <h2>特性</h2>
 * <ul>
 *     <li><b>唯一性</b>：通过数据中心ID、机器ID和序列号保证全局唯一</li>
 *     <li><b>递增性</b>：基于时间戳，ID随时间递增</li>
 *     <li><b>高性能</b>：单机每毫秒可生成4096个不重复ID</li>
 *     <li><b>时间有序</b>：ID包含时间信息，可用于排序和时间范围查询</li>
 *     <li><b>线程安全</b>：使用同步机制保证并发环境下的正确性</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 创建生成器实例
 * SnowflakeIdGenerator generator = new SnowflakeIdGenerator();
 * generator.configure(1L, 1L);  // 配置数据中心ID和机器ID
 *
 * // 生成ID
 * long id = (Long) generator.nextId(null);
 * String uuidStr = generator.nextUUID(null);
 * }</pre>
 *
 * <h2>注意事项</h2>
 * <ul>
 *     <li>必须调用 {@link #configure(long, long)} 方法配置数据中心ID和机器ID</li>
 *     <li>数据中心ID和机器ID在集群中必须唯一</li>
 *     <li>系统时钟不能回拨，否则会抛出异常</li>
 *     <li>建议在生产环境使用NTP同步时钟</li>
 * </ul>
 *
 * @author xiao_lfeng
 * @version 1.1.8
 * @since 1.1.8
 * @see com.xlf.utility.utility.SnowflakeUtil
 * @see SnowflakeInfoDTO
 */
@SuppressWarnings("unused")
public class SnowflakeIdGenerator implements IdentifierGenerator {

    // ========================================
    // 位运算常量定义
    // ========================================

    /**
     * 数据中心ID位数（5位）
     */
    private static final long DATACENTER_ID_BITS = 5L;

    /**
     * 机器ID位数（5位）
     */
    private static final long MACHINE_ID_BITS = 5L;

    /**
     * 序列号位数（12位）
     */
    private static final long SEQUENCE_BITS = 12L;

    /**
     * 数据中心ID最大值（31）
     */
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);

    /**
     * 机器ID最大值（31）
     */
    private static final long MAX_MACHINE_ID = ~(-1L << MACHINE_ID_BITS);

    /**
     * 序列号最大值（4095）
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    /**
     * 机器ID左移位数（12位）
     */
    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 数据中心ID左移位数（17位）
     */
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;

    /**
     * 时间戳左移位数（22位）
     */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS + DATACENTER_ID_BITS;

    // ========================================
    // 实例字段
    // ========================================

    @Setter
    @Getter
    private long epoch = 1729440000000L;
    @Getter
    private long datacenterId;
    @Getter
    private long machineId;
    @Getter
    private long sequence = 0L;
    @Getter
    private long lastTimestamp = -1L;
    @Getter
    private boolean configured = false;

    private final Object lock = new Object();


    // ========================================
    // 构造函数
    // ========================================

    /**
     * 默认构造函数
     * <p>
     * 创建生成器实例后，必须调用 {@link #configure(long, long)} 方法进行配置。
     * </p>
     */
    public SnowflakeIdGenerator() {
        // 默认构造函数，需要手动配置
    }

    /**
     * 带参数的构造函数
     *
     * @param datacenterId 数据中心ID（0-31）
     * @param machineId    机器ID（0-31）
     * @throws BusinessException 如果参数超出有效范围
     */
    public SnowflakeIdGenerator(long datacenterId, long machineId) {
        configure(datacenterId, machineId);
    }

    /**
     * 带完整参数的构造函数
     *
     * @param datacenterId 数据中心ID（0-31）
     * @param machineId    机器ID（0-31）
     * @param epoch        起始时间戳（毫秒）
     * @throws BusinessException 如果参数超出有效范围
     */
    public SnowflakeIdGenerator(long datacenterId, long machineId, long epoch) {
        this.epoch = epoch;
        configure(datacenterId, machineId);
    }

    // ========================================
    // 配置方法
    // ========================================

    /**
     * 配置雪花ID生成器
     * <p>
     * 设置数据中心ID和机器ID。这两个参数在集群中必须唯一，以保证生成的ID全局唯一。
     * </p>
     *
     * @param datacenterId 数据中心ID，取值范围 0-31
     * @param machineId    机器ID，取值范围 0-31
     * @throws BusinessException 如果参数超出有效范围
     */
    public void configure(long datacenterId, long machineId) {
        if (datacenterId < 0 || datacenterId > MAX_DATACENTER_ID) {
            throw new BusinessException(
                    "数据中心ID超出范围，有效范围: 0-" + MAX_DATACENTER_ID + "，当前值: " + datacenterId,
                    ErrorCode.PARAMETER_INVALID
            );
        }
        if (machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw new BusinessException(
                    "机器ID超出范围，有效范围: 0-" + MAX_MACHINE_ID + "，当前值: " + machineId,
                    ErrorCode.PARAMETER_INVALID
            );
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
        this.configured = true;
    }

    // ========================================
    // ID生成方法
    // ========================================

    /**
     * 生成下一个雪花ID
     * <p>
     * 实现 {@link IdentifierGenerator#nextId(Object)} 方法。
     * 生成的ID为64位长整型，包含时间戳、数据中心ID、机器ID和序列号。
     * </p>
     *
     * @param entity 实体对象（本实现中不使用此参数）
     * @return 生成的雪花ID
     * @throws BusinessException 如果未配置或时钟回拨
     */
    @Override
    @NotNull
    @Contract(pure = false)
    public Number nextId(Object entity) {
        if (!configured) {
            throw new BusinessException(
                    "雪花ID生成器未配置，请先调用 configure(datacenterId, machineId) 方法",
                    ErrorCode.SERVER_INTERNAL_ERROR
            );
        }

        synchronized (lock) {
            long timestamp = getCurrentTimestamp();

            // 检测时钟回拨
            if (timestamp < lastTimestamp) {
                throw new BusinessException(
                        "系统时钟回拨，上次时间戳: " + lastTimestamp + "，当前时间戳: " + timestamp,
                        ErrorCode.SERVER_INTERNAL_ERROR
                );
            }

            // 同一毫秒内生成多个ID
            if (timestamp == lastTimestamp) {
                sequence = (sequence + 1) & MAX_SEQUENCE;
                // 序列号溢出，等待下一毫秒
                if (sequence == 0) {
                    timestamp = waitNextMillis(lastTimestamp);
                }
            } else {
                // 新的毫秒，重置序列号
                sequence = 0L;
            }

            // 更新上次时间戳
            lastTimestamp = timestamp;

            // 组装ID
            return ((timestamp - epoch) << TIMESTAMP_SHIFT)
                    | (datacenterId << DATACENTER_ID_SHIFT)
                    | (machineId << MACHINE_ID_SHIFT)
                    | sequence;
        }
    }

    /**
     * 生成UUID格式的雪花ID字符串
     * <p>
     * 实现 {@link IdentifierGenerator#nextUUID(Object)} 方法。
     * 返回的是雪花ID的字符串表示形式。
     * </p>
     *
     * @param entity 实体对象（本实现中不使用此参数）
     * @return 雪花ID的字符串形式
     */
    @NotNull
    @Override
    public String nextUUID(Object entity) {
        return String.valueOf(nextId(entity));
    }

    // ========================================
    // 辅助方法
    // ========================================

    /**
     * 获取当前时间戳（毫秒）
     *
     * @return 当前时间戳
     */
    @Contract(pure = true)
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 等待下一毫秒
     * <p>
     * 当同一毫秒内序列号用尽时，自旋等待下一毫秒。
     * </p>
     *
     * @param lastTimestamp 上次时间戳
     * @return 下一毫秒的时间戳
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }
}
