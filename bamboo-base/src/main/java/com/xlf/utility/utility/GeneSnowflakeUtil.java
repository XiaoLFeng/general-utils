package com.xlf.utility.utility;

import com.xlf.utility.models.dto.GeneSnowflakeInfoDTO;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 业务基因雪花ID工具类
 * <p>
 * {@code GeneSnowflakeUtil} 在标准雪花ID的基础上，嵌入8位业务基因标识。
 * 业务基因通过 CRC8 哈希算法计算业务标识字符串得到，可用于快速识别ID所属的业务类型。
 * </p>
 *
 * <h2>实现原理</h2>
 * <ol>
 *     <li>使用 {@link SnowflakeUtil} 生成标准64位雪花ID</li>
 *     <li>使用 {@link CRC8Util} 计算业务标识的8位基因值（0-255）</li>
 *     <li>通过位运算重组ID结构，嵌入业务基因</li>
 * </ol>
 *
 * <h2>ID结构转换</h2>
 * <pre>
 * 标准雪花ID (64位):
 * [1位符号][41位时间戳][5位数据中心][5位机器ID][12位序列号]
 *
 * 转换为业务基因雪花ID (64位):
 * [1位符号][41位时间戳][4位机器ID][8位业务基因][3位数据中心][8位序列号]
 * </pre>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 生成带业务基因的雪花ID
 * long userId = GeneSnowflakeUtil.generateId("user");
 * long orderId = GeneSnowflakeUtil.generateId("order");
 * long merchantId = GeneSnowflakeUtil.generateId("merchant");
 *
 * // 验证ID是否属于某个业务
 * boolean isUser = GeneSnowflakeUtil.verifyGene(userId, "user");     // true
 * boolean isOrder = GeneSnowflakeUtil.verifyGene(userId, "order");   // false
 *
 * // 解析完整信息
 * GeneSnowflakeInfoDTO info = GeneSnowflakeUtil.parseInfo(userId);
 * System.out.println("业务基因: " + info.getGene());
 * System.out.println("生成时间: " + info.getGeneratedTime());
 *
 * // 计算业务基因（不生成ID）
 * int gene = GeneSnowflakeUtil.calculateGene("user");
 * }</pre>
 *
 * <h2>注意事项</h2>
 * <ul>
 *     <li>业务基因使用 CRC8 算法，存在碰撞可能（约0.4%概率）</li>
 *     <li>相同业务标识总是生成相同基因值</li>
 *     <li>每毫秒可生成256个ID（序列号缩减为8位）</li>
 *     <li>机器ID缩减为4位（0-15），数据中心ID缩减为3位（0-7）</li>
 * </ul>
 *
 * @author xiao_lfeng
 * @version 1.1.8
 * @see SnowflakeUtil
 * @see CRC8Util
 * @see GeneSnowflakeInfoDTO
 * @since 1.1.8
 */
public final class GeneSnowflakeUtil {

    // ========================================
    // 位运算常量定义
    // ========================================

    private static final long SEQUENCE_BITS = 8L;
    private static final long DATACENTER_ID_BITS = 3L;
    private static final long GENE_BITS = 8L;
    private static final long MACHINE_ID_BITS = 4L;

    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    private static final long MAX_GENE = ~(-1L << GENE_BITS);
    private static final long MAX_MACHINE_ID = ~(-1L << MACHINE_ID_BITS);

    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS;
    private static final long GENE_SHIFT = SEQUENCE_BITS + DATACENTER_ID_BITS;
    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS + DATACENTER_ID_BITS + GENE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + DATACENTER_ID_BITS + GENE_BITS + MACHINE_ID_BITS;

    /**
     * 私有构造函数，防止实例化
     */
    @Contract(pure = true)
    private GeneSnowflakeUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ========================================
    // ID生成方法
    // ========================================

    /**
     * 生成带业务基因的雪花ID
     * <p>
     * 根据业务标识字符串生成带基因的雪花ID。相同的业务标识总是产生相同的基因值。
     * </p>
     *
     * <h3>生成步骤</h3>
     * <ol>
     *     <li>使用 {@link SnowflakeUtil} 生成标准雪花ID</li>
     *     <li>使用 {@link CRC8Util} 计算业务标识的基因值</li>
     *     <li>重组ID结构，嵌入业务基因</li>
     * </ol>
     *
     * @param businessIdentifier 业务标识字符串（如 "user", "order", "merchant"）
     * @return 带业务基因的雪花ID
     * @throws NullPointerException 如果 businessIdentifier 为 null
     */
    @Contract(pure = false)
    public static long generateId(@NotNull String businessIdentifier) {
        // 1. 生成标准雪花ID
        long standardId = SnowflakeUtil.generateId();

        // 2. 计算业务基因
        int gene = CRC8Util.calculate(businessIdentifier);

        // 3. 从标准ID中提取各部分
        long timestamp = (standardId >> SnowflakeUtil.TIMESTAMP_SHIFT);
        long standardDatacenterId = (standardId >> SnowflakeUtil.DATACENTER_ID_SHIFT) & SnowflakeUtil.MAX_DATACENTER_ID;
        long standardMachineId = (standardId >> SnowflakeUtil.MACHINE_ID_SHIFT) & SnowflakeUtil.MAX_MACHINE_ID;
        long standardSequence = standardId & SnowflakeUtil.MAX_SEQUENCE;

        // 4. 调整各部分以适应新结构
        // 机器ID从5位缩减为4位（取低4位）
        long machineId = standardMachineId & MAX_MACHINE_ID;

        // 数据中心ID从5位缩减为3位（取低3位）
        long datacenterId = standardDatacenterId & MAX_DATACENTER_ID;

        // 序列号从12位缩减为8位（取低8位）
        long sequence = standardSequence & MAX_SEQUENCE;

        // 5. 重组ID：时间戳 | 机器ID | 业务基因 | 数据中心ID | 序列号
        return (timestamp << TIMESTAMP_SHIFT)
                | (machineId << MACHINE_ID_SHIFT)
                | ((long) gene << GENE_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | sequence;
    }

    /**
     * 生成字符串格式的业务基因雪花ID
     *
     * @param businessIdentifier 业务标识字符串
     * @return 雪花ID的字符串形式
     */
    @NotNull
    @Contract(pure = false)
    public static String generateIdString(@NotNull String businessIdentifier) {
        return String.valueOf(generateId(businessIdentifier));
    }

    // ========================================
    // ID解析方法
    // ========================================

    /**
     * 从ID中解析业务基因
     * <p>
     * 提取ID中的8位业务基因部分。
     * </p>
     *
     * @param id 业务基因雪花ID
     * @return 业务基因值（0-255）
     */
    @Contract(pure = true)
    public static int parseGene(long id) {
        return (int) ((id >> GENE_SHIFT) & MAX_GENE);
    }

    /**
     * 从ID中解析时间戳
     * <p>
     * 提取ID中的时间戳部分，并加上起始时间戳得到实际时间。
     * </p>
     *
     * @param id 业务基因雪花ID
     * @return 实际时间戳（毫秒）
     */
    @Contract(pure = true)
    public static long parseTimestamp(long id) {
        return (id >> TIMESTAMP_SHIFT) + SnowflakeUtil.getEpoch();
    }

    /**
     * 从ID中解析机器ID
     * <p>
     * 提取ID中的4位机器ID部分。
     * </p>
     *
     * @param id 业务基因雪花ID
     * @return 机器ID（0-15）
     */
    @Contract(pure = true)
    public static long parseMachineId(long id) {
        return (id >> MACHINE_ID_SHIFT) & MAX_MACHINE_ID;
    }

    /**
     * 从ID中解析数据中心ID
     * <p>
     * 提取ID中的3位数据中心ID部分。
     * </p>
     *
     * @param id 业务基因雪花ID
     * @return 数据中心ID（0-7）
     */
    @Contract(pure = true)
    public static long parseDatacenterId(long id) {
        return (id >> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID;
    }

    /**
     * 从ID中解析序列号
     * <p>
     * 提取ID中的8位序列号部分。
     * </p>
     *
     * @param id 业务基因雪花ID
     * @return 序列号（0-255）
     */
    @Contract(pure = true)
    public static long parseSequence(long id) {
        return id & MAX_SEQUENCE;
    }

    /**
     * 解析业务基因雪花ID的完整信息
     * <p>
     * 将业务基因雪花ID解析为 {@link GeneSnowflakeInfoDTO} 对象，包含所有组成部分和生成时间。
     * </p>
     *
     * @param id 业务基因雪花ID
     * @return 包含完整信息的 GeneSnowflakeInfoDTO 对象
     */
    @NotNull
    @Contract(pure = true)
    public static GeneSnowflakeInfoDTO parseInfo(long id) {
        long timestamp = parseTimestamp(id);
        int gene = parseGene(id);
        long machineId = parseMachineId(id);
        long datacenterId = parseDatacenterId(id);
        long sequence = parseSequence(id);

        // 将时间戳转换为 LocalDateTime
        LocalDateTime generatedTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault()
        );

        return GeneSnowflakeInfoDTO.builder()
                .id(id)
                .timestamp(timestamp - SnowflakeUtil.getEpoch())  // 相对时间戳
                .gene(gene)
                .machineId(machineId)
                .datacenterId(datacenterId)
                .sequence(sequence)
                .generatedTime(generatedTime)
                .build();
    }

    // ========================================
    // 基因相关方法
    // ========================================

    /**
     * 计算业务标识的基因值
     * <p>
     * 使用 CRC8 算法计算业务标识字符串的基因值，不生成ID。
     * 可用于预先计算基因值或验证基因计算逻辑。
     * </p>
     *
     * @param businessIdentifier 业务标识字符串
     * @return 基因值（0-255）
     */
    @Contract(pure = true)
    public static int calculateGene(@NotNull String businessIdentifier) {
        return CRC8Util.calculate(businessIdentifier);
    }

    /**
     * 验证ID是否匹配业务标识
     * <p>
     * 从ID中提取业务基因，并与给定业务标识计算的基因值进行比较。
     * 如果匹配，说明该ID属于该业务类型（存在约0.4%的误判概率）。
     * </p>
     *
     * <h3>使用示例</h3>
     * <pre>{@code
     * long userId = GeneSnowflakeUtil.generateId("user");
     *
     * boolean isUser = GeneSnowflakeUtil.verifyGene(userId, "user");     // true
     * boolean isOrder = GeneSnowflakeUtil.verifyGene(userId, "order");   // false
     * }</pre>
     *
     * @param id                 业务基因雪花ID
     * @param businessIdentifier 业务标识字符串
     * @return 如果基因匹配返回 true，否则返回 false
     */
    @Contract(pure = true)
    public static boolean verifyGene(long id, @NotNull String businessIdentifier) {
        int idGene = parseGene(id);
        int calculatedGene = calculateGene(businessIdentifier);
        return idGene == calculatedGene;
    }

    /**
     * 验证业务基因雪花ID是否有效
     * <p>
     * 检查业务基因雪花ID的有效性，包括：
     * <ul>
     *     <li>ID不为null且为正数</li>
     *     <li>时间戳在合理范围内（epoch到当前时间）</li>
     *     <li>业务基因在0-255范围内</li>
     *     <li>各个组件（机器ID、数据中心ID、序列号）在定义的最大值范围内</li>
     * </ul>
     * </p>
     *
     * <h3>使用示例</h3>
     * <pre>{@code
     * long userId = GeneSnowflakeUtil.generateId("user");
     * boolean valid = GeneSnowflakeUtil.isValid(userId);  // true
     *
     * boolean invalidId = GeneSnowflakeUtil.isValid(null);  // false
     * boolean invalidId2 = GeneSnowflakeUtil.isValid(-1L);  // false
     * }</pre>
     *
     * @param id 要验证的业务基因雪花ID
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
        long epoch = SnowflakeUtil.getEpoch();
        long currentTime = System.currentTimeMillis();

        // 时间戳应该在epoch之后，当前时间之前（允许1秒的时钟偏移）
        if (timestamp < epoch || timestamp > currentTime + 1000) {
            return false;
        }

        // 验证各部分是否在有效范围内
        int gene = parseGene(id);
        long machineId = parseMachineId(id);
        long datacenterId = parseDatacenterId(id);
        long sequence = parseSequence(id);

        return gene <= MAX_GENE
                && machineId <= MAX_MACHINE_ID
                && datacenterId <= MAX_DATACENTER_ID
                && sequence <= MAX_SEQUENCE;
    }

    // ========================================
    // 配置相关方法
    // ========================================

    /**
     * 获取基因雪花ID的配置信息
     * <p>
     * 返回当前使用的数据中心ID、机器ID和起始时间戳（继承自标准雪花ID配置）。
     * </p>
     *
     * @return 包含配置信息的字符串
     */
    @NotNull
    @Contract(pure = true)
    public static String getConfigInfo() {
        return "GeneSnowflakeUtil Config (inherited from SnowflakeUtil): " +
                "datacenterId=" + SnowflakeUtil.getDatacenterId() +
                " (adjusted to 3-bit: " + (SnowflakeUtil.getDatacenterId() & MAX_DATACENTER_ID) + ")" +
                ", machineId=" + SnowflakeUtil.getMachineId() +
                " (adjusted to 4-bit: " + (SnowflakeUtil.getMachineId() & MAX_MACHINE_ID) + ")" +
                ", epoch=" + SnowflakeUtil.getEpoch();
    }

    /**
     * 获取业务基因雪花ID的位结构说明
     *
     * @return 位结构说明字符串
     */
    @NotNull
    @Contract(pure = true)
    public static String getBitStructure() {
        return "GeneSnowflakeId Bit Structure (64-bit):\n" +
                "  [1-bit sign][41-bit timestamp][4-bit machineId][8-bit gene][3-bit datacenterId][8-bit sequence]\n" +
                "  - Timestamp: 41 bits (supports ~69 years)\n" +
                "  - Machine ID: 4 bits (0-15)\n" +
                "  - Business Gene: 8 bits (0-255, calculated by CRC8)\n" +
                "  - Datacenter ID: 3 bits (0-7)\n" +
                "  - Sequence: 8 bits (0-255, max 256 IDs per millisecond)";
    }
}
