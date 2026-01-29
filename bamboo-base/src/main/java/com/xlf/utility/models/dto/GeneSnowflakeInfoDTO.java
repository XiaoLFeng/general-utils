package com.xlf.utility.models.dto;

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
 * 业务基因雪花ID信息数据传输对象
 * <p>
 * 用于封装带业务基因的雪花ID的各个组成部分，便于解析和展示雪花ID的详细信息。
 * 业务基因雪花ID在标准雪花ID的基础上，嵌入了8位业务基因标识，用于快速识别业务类型。
 * </p>
 *
 * <h2>业务基因雪花ID结构（64位）</h2>
 * <pre>
 * ┌─────────────┬──────────┬──────────┬──────────┬──────────┐
 * │  时间戳位   │ 机器ID位 │ 业务基因 │ 数据中心 │ 序列号位 │
 * │  (41位)     │  (4位)   │  (8位)   │  (3位)   │  (8位)   │
 * └─────────────┴──────────┴──────────┴──────────┴──────────┘
 *  63          22 21      18 17      10 9        7 6        0
 * </pre>
 *
 * <h2>字段说明</h2>
 * <ul>
 *     <li><b>id</b>: 完整的64位雪花ID</li>
 *     <li><b>timestamp</b>: 41位时间戳（相对于起始时间的毫秒数）</li>
 *     <li><b>machineId</b>: 4位机器ID（0-15）</li>
 *     <li><b>gene</b>: 8位业务基因（0-255，通过CRC8算法计算）</li>
 *     <li><b>datacenterId</b>: 3位数据中心ID（0-7）</li>
 *     <li><b>sequence</b>: 8位序列号（0-255）</li>
 *     <li><b>generatedTime</b>: ID生成的实际时间（已转换为LocalDateTime）</li>
 * </ul>
 *
 * <h2>业务基因说明</h2>
 * <p>
 * 业务基因通过 CRC8 哈希算法计算业务标识字符串得到，取值范围 0-255。
 * 相同的业务标识字符串总是产生相同的基因值，可用于：
 * <ul>
 *     <li>快速识别ID所属的业务类型（如用户、订单、商户等）</li>
 *     <li>在分布式系统中快速过滤和路由</li>
 *     <li>减少数据库查询，直接从ID判断业务归属</li>
 * </ul>
 * </p>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 解析业务基因雪花ID
 * long geneSnowflakeId = 1234567890123456789L;
 * GeneSnowflakeInfoDTO info = GeneSnowflakeInfoDTO.builder()
 *     .id(geneSnowflakeId)
 *     .timestamp(extractedTimestamp)
 *     .machineId(extractedMachineId)
 *     .gene(extractedGene)
 *     .datacenterId(extractedDatacenterId)
 *     .sequence(extractedSequence)
 *     .generatedTime(convertedDateTime)
 *     .build();
 *
 * System.out.println("业务基因: " + info.getGene());
 * System.out.println("机器ID: " + info.getMachineId());
 * System.out.println("生成时间: " + info.getGeneratedTime());
 *
 * // 验证ID是否属于某个业务
 * if (GeneSnowflakeUtil.verifyGene(geneSnowflakeId, "user")) {
 *     System.out.println("这是用户相关的ID");
 * }
 * }</pre>
 *
 * @author xiao_lfeng
 * @version 1.1.8
 * @since 1.1.8
 * @see com.xlf.utility.utility.GeneSnowflakeUtil
 * @see com.xlf.utility.utility.CRC8Util
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneSnowflakeInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1170553478864387473L;

    /**
     * 完整的雪花ID
     * <p>
     * 64位长整型，包含时间戳、机器ID、业务基因、数据中心ID和序列号的完整信息。
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
     * 机器ID
     * <p>
     * 4位机器标识，取值范围 0-15。
     * 用于区分同一数据中心内的不同机器节点。
     * </p>
     * <p>
     * 注意：相比标准雪花ID的5位机器ID，这里缩减为4位，为业务基因腾出空间。
     * </p>
     */
    @NotNull
    private Long machineId;

    /**
     * 业务基因
     * <p>
     * 8位业务基因标识，取值范围 0-255。
     * 通过 CRC8 哈希算法计算业务标识字符串得到。
     * </p>
     * <p>
     * 业务基因的作用：
     * <ul>
     *     <li>快速识别ID所属的业务类型</li>
     *     <li>支持按业务类型快速过滤</li>
     *     <li>减少数据库查询</li>
     * </ul>
     * </p>
     * <p>
     * 示例：
     * <ul>
     *     <li>"user" 的基因值可能是 123</li>
     *     <li>"order" 的基因值可能是 45</li>
     *     <li>"merchant" 的基因值可能是 201</li>
     * </ul>
     * </p>
     */
    @NotNull
    private Integer gene;

    /**
     * 数据中心ID
     * <p>
     * 3位数据中心标识，取值范围 0-7。
     * 用于区分不同的数据中心或地理位置。
     * </p>
     * <p>
     * 注意：相比标准雪花ID的5位数据中心ID，这里缩减为3位，为业务基因腾出空间。
     * </p>
     */
    @NotNull
    private Long datacenterId;

    /**
     * 序列号
     * <p>
     * 8位序列号，取值范围 0-255。
     * 在同一毫秒内生成多个ID时，序列号递增。
     * 每毫秒最多可生成256个不重复的ID。
     * </p>
     * <p>
     * 注意：相比标准雪花ID的12位序列号（4096个/毫秒），这里缩减为8位（256个/毫秒），
     * 适合中等并发场景。如果需要更高并发，建议使用标准雪花ID。
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
        return "GeneSnowflakeInfoDTO{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", machineId=" + machineId +
                ", gene=" + gene +
                ", datacenterId=" + datacenterId +
                ", sequence=" + sequence +
                ", generatedTime=" + generatedTime +
                '}';
    }
}
