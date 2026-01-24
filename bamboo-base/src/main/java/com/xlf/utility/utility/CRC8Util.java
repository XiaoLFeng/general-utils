package com.xlf.utility.utility;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

/**
 * CRC8 循环冗余校验工具类
 * <p>
 * 提供基于 CRC-8-MAXIM 多项式的哈希计算功能，主要用于生成雪花ID的业务基因标识。
 * CRC-8-MAXIM 使用多项式 0x31 (x^8 + x^5 + x^4 + 1)，适合短数据校验。
 * </p>
 *
 * <h2>使用场景</h2>
 * <ul>
 *     <li>生成业务基因雪花ID的8位基因值</li>
 *     <li>数据完整性校验</li>
 *     <li>快速哈希计算</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 计算字符串的 CRC8 值
 * int crc = CRC8Util.calculate("user_12345");  // 返回 0-255 之间的值
 *
 * // 计算字节数组的 CRC8 值
 * byte[] data = "merchant_001".getBytes(StandardCharsets.UTF_8);
 * byte crc8 = CRC8Util.calculate(data);
 *
 * // 验证 CRC8 校验值
 * boolean isValid = CRC8Util.verify(data, crc8);
 * }</pre>
 *
 * <h2>算法说明</h2>
 * <p>
 * CRC-8-MAXIM 算法流程：
 * <ol>
 *     <li>初始化 CRC 值为 0x00</li>
 *     <li>对每个字节进行异或运算</li>
 *     <li>执行 8 次位移和条件异或</li>
 *     <li>返回最终的 8 位 CRC 值</li>
 * </ol>
 * </p>
 *
 * @author xiao_lfeng
 * @version 1.1.8
 * @since 1.1.8
 */
public final class CRC8Util {

    /**
     * CRC-8-MAXIM 多项式常量
     * <p>
     * 多项式值为 0x31，对应二进制 0011 0001，表示 x^8 + x^5 + x^4 + 1
     * </p>
     */
    private static final int POLYNOMIAL = 0x31;

    /**
     * CRC 初始值
     */
    private static final int INITIAL_VALUE = 0x00;

    /**
     * 私有构造函数，防止实例化
     */
    @Contract(pure = true)
    private CRC8Util() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 计算字节数组的 CRC8 值
     * <p>
     * 使用 CRC-8-MAXIM 算法对输入的字节数组进行循环冗余校验计算。
     * </p>
     *
     * <h3>算法步骤</h3>
     * <ol>
     *     <li>初始化 CRC 为 0x00</li>
     *     <li>遍历每个字节：
     *         <ul>
     *             <li>将字节与当前 CRC 值异或</li>
     *             <li>执行 8 次位移操作</li>
     *             <li>如果最高位为 1，则与多项式异或</li>
     *         </ul>
     *     </li>
     *     <li>返回最终的 8 位 CRC 值</li>
     * </ol>
     *
     * @param data 待计算的字节数组，不能为 null
     * @return CRC8 校验值，范围 0-255（有符号字节 -128 到 127）
     * @throws NullPointerException 如果 data 为 null
     */
    @Contract(pure = true)
    public static byte calculate(@NotNull byte[] data) {
        int crc = INITIAL_VALUE;

        for (byte b : data) {
            // 将当前字节与 CRC 异或
            crc ^= (b & 0xFF);

            // 执行 8 次位移和条件异或
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x80) != 0) {
                    // 最高位为 1，左移并与多项式异或
                    crc = (crc << 1) ^ POLYNOMIAL;
                } else {
                    // 最高位为 0，直接左移
                    crc <<= 1;
                }
            }
        }

        // 返回 8 位 CRC 值
        return (byte) (crc & 0xFF);
    }

    /**
     * 计算字符串的 CRC8 值（无符号整数）
     * <p>
     * 将字符串转换为 UTF-8 字节数组后计算 CRC8 值，返回无符号整数形式（0-255）。
     * 这个方法主要用于生成业务基因雪花ID的基因值。
     * </p>
     *
     * <h3>使用示例</h3>
     * <pre>{@code
     * int gene = CRC8Util.calculate("user");      // 返回 0-255
     * int gene2 = CRC8Util.calculate("merchant"); // 不同字符串返回不同值
     * int gene3 = CRC8Util.calculate("user");     // 相同字符串返回相同值
     * }</pre>
     *
     * @param input 待计算的字符串，不能为 null
     * @return CRC8 校验值的无符号整数形式，范围 0-255
     * @throws NullPointerException 如果 input 为 null
     */
    @Contract(pure = true)
    public static int calculate(@NotNull String input) {
        byte[] data = input.getBytes(StandardCharsets.UTF_8);
        return calculate(data) & 0xFF;
    }

    /**
     * 验证数据的 CRC8 校验值是否正确
     * <p>
     * 重新计算给定数据的 CRC8 值，并与提供的校验值进行比较。
     * </p>
     *
     * <h3>使用示例</h3>
     * <pre>{@code
     * byte[] data = "test".getBytes(StandardCharsets.UTF_8);
     * byte crc = CRC8Util.calculate(data);
     * boolean isValid = CRC8Util.verify(data, crc);  // 返回 true
     *
     * // 数据被篡改
     * data[0] = 'X';
     * boolean isValid2 = CRC8Util.verify(data, crc);  // 返回 false
     * }</pre>
     *
     * @param data     原始数据，不能为 null
     * @param expected 期望的 CRC8 校验值
     * @return 如果计算的 CRC8 值与期望值相等返回 true，否则返回 false
     * @throws NullPointerException 如果 data 为 null
     */
    @Contract(pure = true)
    public static boolean verify(byte @NotNull [] data, byte expected) {
        byte calculated = calculate(data);
        return calculated == expected;
    }

    /**
     * 验证字符串的 CRC8 校验值是否正确
     * <p>
     * 重新计算给定字符串的 CRC8 值（无符号整数），并与提供的校验值进行比较。
     * </p>
     *
     * @param input    原始字符串，不能为 null
     * @param expected 期望的 CRC8 校验值（0-255）
     * @return 如果计算的 CRC8 值与期望值相等返回 true，否则返回 false
     * @throws NullPointerException 如果 input 为 null
     */
    @Contract(pure = true)
    public static boolean verify(@NotNull String input, int expected) {
        int calculated = calculate(input);
        return calculated == expected;
    }

    /**
     * 获取 CRC-8-MAXIM 多项式值
     * <p>
     * 返回当前使用的多项式常量，主要用于调试和文档目的。
     * </p>
     *
     * @return 多项式值 0x31
     */
    @Contract(pure = true)
    public static int getPolynomial() {
        return POLYNOMIAL;
    }
}
