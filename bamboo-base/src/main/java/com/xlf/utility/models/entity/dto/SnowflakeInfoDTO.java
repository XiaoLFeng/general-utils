package com.xlf.utility.models.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 雪花ID信息数据传输对象
 * <p>
 * 用于封装标准雪花ID的各个组成部分，便于解析和展示雪花ID的详细信息。
 * 标准雪花ID采用64位结构，包含时间戳、数据中心ID、机器ID和序列号。
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
 * <h2>字段说明</h2>
 * <ul>
 *     <li><b>id</b>: 完整的64位雪花ID</li>
 *     <li><b>timestamp</b>: 41位时间戳（相对于起始时间的毫秒数）</li>
 *     <li><b>datacenterId</b>: 5位数据中心ID（0-31）</li>
 *     <li><b>machineId</b>: 5位机器ID（0-31）</li>
 *     <li><b>sequence</b>: 12位序列号（0-4095）</li>
 *     <li><b>generatedTime</b>: ID生成的实际时间（已转换为LocalDateTime）</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 解析雪花ID
 * long snowflakeId = 1234567890123456789L;
 * SnowflakeInfoDTO info = SnowflakeInfoDTO.builder()
 *     .id(snowflakeId)
 *     .timestamp(extractedTimestamp)
 *     .datacenterId(extractedDatacenterId)
 *     .machineId(extractedMachineId)
 *     .sequence(extractedSequence)
 *     .generatedTime(convertedDateTime)
 *     .build();
 *
 * System.out.println("数据中心: " + info.getDatacenterId());
 * System.out.println("机器ID: " + info.getMachineId());
 * System.out.println("生成时间: " + info.getGeneratedTime());
 * }</pre>
 *
 * @author xiao_lfeng
 * @version 1.1.8
 * @since 1.1.8
 * @see com.xlf.utility.incrementer.SnowflakeIdGenerator
 * @see com.xlf.utility.utility.SnowflakeUtil
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnowflakeInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7764276964933736573L;

    /**
     * 完整的雪花ID
     * <p>
     * 64位长整型，包含时间戳、数据中心ID、机器ID和序列号的完整信息。
     * </p>
     */
    @NotNull
    private Long id;

    /**
     * 时间戳部分
     * <p>
     * 41位时间戳，表示相对于起始时间（epoch）的毫秒数。
     * 最大值为 2^41 - 1，约可使用69年。
     * </p>
     * <p>
     * 实际时间 = epoch + timestamp
     * </p>
     */
    @NotNull
    private Long timestamp;

    /**
     * 数据中心ID
     * <p>
     * 5位数据中心标识，取值范围 0-31。
     * 用于区分不同的数据中心或地理位置。
     * </p>
     */
    @NotNull
    private Long datacenterId;

    /**
     * 机器ID
     * <p>
     * 5位机器标识，取值范围 0-31。
     * 用于区分同一数据中心内的不同机器节点。
     * </p>
     */
    @NotNull
    private Long machineId;

    /**
     * 序列号
     * <p>
     * 12位序列号，取值范围 0-4095。
     * 在同一毫秒内生成多个ID时，序列号递增。
     * 每毫秒最多可生成4096个不重复的ID。
     * </p>
     */
    @NotNull
    private Long sequence;

    /**
     * ID生成的实际时间
     * <p>
     * 将时间戳转换为实际的日期时间对象，便于人类阅读。
     * 如果未设置，则可能为null。
     * </p>
     */
    @Nullable
    private LocalDateTime generatedTime;

    /**
     * 获取格式化的信息字符串
     * <p>
     * 返回包含所有字段信息的可读字符串，便于日志输出和调试。
     * </p>
     *
     * @return 格式化的信息字符串
     */
    @Override
    public String toString() {
        return "SnowflakeInfoDTO{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", datacenterId=" + datacenterId +
                ", machineId=" + machineId +
                ", sequence=" + sequence +
                ", generatedTime=" + generatedTime +
                '}';
    }
}
