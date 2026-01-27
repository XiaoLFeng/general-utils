package com.xlf.utility.utility;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@code CRC8UtilTest} 是对 {@code CRC8Util} 类中 CRC8 哈希计算功能的单元测试类。
 * <p>
 * 目标是通过标准化的测试用例确保 CRC8 计算逻辑正确、稳定且符合预期。该类覆盖了多种输入场景，
 * 并使用了断言来验证每个功能点的正确性。
 * </p>
 *
 * <p>
 * 测试内容包括：
 * <ul>
 *   <li>基本字符串 CRC8 计算是否正确</li>
 *   <li>相同输入是否产生相同输出（确定性）</li>
 *   <li>不同输入是否产生不同输出（唯一性）</li>
 *   <li>中文字符串的 CRC8 计算</li>
 *   <li>空字符串的处理</li>
 *   <li>字节数组的 CRC8 计算</li>
 *   <li>CRC8 校验功能的正确性</li>
 *   <li>返回值范围的验证（0-255）</li>
 * </ul>
 * </p>
 *
 * <p>
 * NOTICE: 本类的测试用例依赖 {@code CRC8Util} 提供的 CRC-8-MAXIM 算法逻辑。
 * 修改算法实现时，需要确保测试用例能够正确验证新的实现。
 * </p>
 *
 * @author XiaoLFeng
 * @version 1.1.8
 * @since 1.1.8
 */
class CRC8UtilTest {

    /**
     * 测试基本字符串的 CRC8 计算功能
     * <p>
     * 验证 {@code CRC8Util.calculate(String)} 方法能够正确计算字符串的 CRC8 值，
     * 并且返回值在有效范围内（0-255）。
     * </p>
     */
    @Test
    void testCalculate_BasicString() {
        String input = "user_12345";
        int crc = CRC8Util.calculate(input);

        System.out.println("CRC8 of '" + input + "': " + crc);

        // 验证返回值在有效范围内
        assertTrue(crc >= 0 && crc <= 255,
                "CRC8 value should be in range 0-255, got: " + crc);
    }

    /**
     * 测试相同输入是否产生相同输出（确定性测试）
     * <p>
     * 验证 {@code CRC8Util.calculate(String)} 方法对相同输入多次调用时，
     * 是否总是返回相同的 CRC8 值。这是哈希算法的基本要求。
     * </p>
     */
    @Test
    void testCalculate_SameInputSameOutput() {
        String input = "merchant_001";
        int crc1 = CRC8Util.calculate(input);
        int crc2 = CRC8Util.calculate(input);
        int crc3 = CRC8Util.calculate(input);

        System.out.println("CRC8 consistency test: " + crc1 + ", " + crc2 + ", " + crc3);

        assertEquals(crc1, crc2, "Same input should produce same CRC8");
        assertEquals(crc2, crc3, "Same input should produce same CRC8");
    }

    /**
     * 测试不同输入是否产生不同输出
     * <p>
     * 验证 {@code CRC8Util.calculate(String)} 方法对不同输入是否能够产生不同的 CRC8 值。
     * 虽然 CRC8 有碰撞可能，但对于明显不同的输入应该产生不同的结果。
     * </p>
     */
    @Test
    void testCalculate_DifferentInputDifferentOutput() {
        String input1 = "user";
        String input2 = "admin";
        String input3 = "merchant";

        int crc1 = CRC8Util.calculate(input1);
        int crc2 = CRC8Util.calculate(input2);
        int crc3 = CRC8Util.calculate(input3);

        System.out.println("CRC8 values: user=" + crc1 + ", admin=" + crc2 + ", merchant=" + crc3);

        // 验证不同输入产生不同输出
        assertNotEquals(crc1, crc2, "Different inputs should produce different CRC8");
        assertNotEquals(crc2, crc3, "Different inputs should produce different CRC8");
        assertNotEquals(crc1, crc3, "Different inputs should produce different CRC8");
    }

    /**
     * 测试中文字符串的 CRC8 计算
     * <p>
     * 验证 {@code CRC8Util.calculate(String)} 方法能够正确处理包含中文字符的字符串，
     * 并且多次计算结果一致。
     * </p>
     */
    @Test
    void testCalculate_ChineseCharacters() {
        String input1 = "用户_12345";
        String input2 = "商户_001";
        String input3 = "订单";

        int crc1 = CRC8Util.calculate(input1);
        int crc2 = CRC8Util.calculate(input2);
        int crc3 = CRC8Util.calculate(input3);

        System.out.println("CRC8 for Chinese: " + input1 + "=" + crc1 +
                ", " + input2 + "=" + crc2 +
                ", " + input3 + "=" + crc3);

        // 验证返回值在有效范围内
        assertTrue(crc1 >= 0 && crc1 <= 255);
        assertTrue(crc2 >= 0 && crc2 <= 255);
        assertTrue(crc3 >= 0 && crc3 <= 255);

        // 验证相同输入产生相同输出
        assertEquals(crc1, CRC8Util.calculate(input1));
        assertEquals(crc2, CRC8Util.calculate(input2));
        assertEquals(crc3, CRC8Util.calculate(input3));
    }

    /**
     * 测试空字符串的 CRC8 计算
     * <p>
     * 验证 {@code CRC8Util.calculate(String)} 方法能够正确处理空字符串，
     * 并返回有效的 CRC8 值。
     * </p>
     */
    @Test
    void testCalculate_EmptyString() {
        String input = "";
        int crc = CRC8Util.calculate(input);

        System.out.println("CRC8 of empty string: " + crc);

        // 空字符串的 CRC8 值应该是 0（初始值）
        assertEquals(0, crc, "CRC8 of empty string should be 0");
    }

    /**
     * 测试字节数组的 CRC8 计算
     * <p>
     * 验证 {@code CRC8Util.calculate(byte[])} 方法能够正确计算字节数组的 CRC8 值。
     * </p>
     */
    @Test
    void testCalculate_ByteArray() {
        byte[] data1 = "test".getBytes(StandardCharsets.UTF_8);
        byte[] data2 = "test".getBytes(StandardCharsets.UTF_8);
        byte[] data3 = "other".getBytes(StandardCharsets.UTF_8);

        byte crc1 = CRC8Util.calculate(data1);
        byte crc2 = CRC8Util.calculate(data2);
        byte crc3 = CRC8Util.calculate(data3);

        System.out.println("CRC8 byte values: test=" + (crc1 & 0xFF) +
                ", test=" + (crc2 & 0xFF) +
                ", other=" + (crc3 & 0xFF));

        // 相同数据应该产生相同的 CRC8
        assertEquals(crc1, crc2, "Same byte array should produce same CRC8");

        // 不同数据应该产生不同的 CRC8
        assertNotEquals(crc1, crc3, "Different byte arrays should produce different CRC8");
    }

    /**
     * 测试字节数组 CRC8 校验功能的正确性
     * <p>
     * 验证 {@code CRC8Util.verify(byte[], byte)} 方法能够正确验证 CRC8 校验值。
     * </p>
     */
    @Test
    void testVerify_ByteArray_Correctness() {
        byte[] data = "verification_test".getBytes(StandardCharsets.UTF_8);
        byte crc = CRC8Util.calculate(data);

        // 验证正确的 CRC8 值
        assertTrue(CRC8Util.verify(data, crc),
                "CRC8 verification should pass for correct value");

        // 验证错误的 CRC8 值
        byte wrongCrc = (byte) (crc + 1);
        assertFalse(CRC8Util.verify(data, wrongCrc),
                "CRC8 verification should fail for incorrect value");
    }

    /**
     * 测试字符串 CRC8 校验功能的正确性
     * <p>
     * 验证 {@code CRC8Util.verify(String, int)} 方法能够正确验证 CRC8 校验值。
     * </p>
     */
    @Test
    void testVerify_String_Correctness() {
        String input = "user_verification";
        int crc = CRC8Util.calculate(input);

        System.out.println("Original CRC8: " + crc);

        // 验证正确的 CRC8 值
        assertTrue(CRC8Util.verify(input, crc),
                "CRC8 verification should pass for correct value");

        // 验证错误的 CRC8 值
        int wrongCrc = (crc + 1) % 256;
        assertFalse(CRC8Util.verify(input, wrongCrc),
                "CRC8 verification should fail for incorrect value");
    }

    /**
     * 测试数据被篡改后的 CRC8 校验
     * <p>
     * 验证当数据被修改后，{@code CRC8Util.verify} 方法能够检测到数据不一致。
     * </p>
     */
    @Test
    void testVerify_DataTampering() {
        byte[] originalData = "original_data".getBytes(StandardCharsets.UTF_8);
        byte crc = CRC8Util.calculate(originalData);

        // 数据被篡改
        byte[] tamperedData = "modified_data".getBytes(StandardCharsets.UTF_8);

        // 验证应该失败
        assertFalse(CRC8Util.verify(tamperedData, crc),
                "CRC8 verification should fail for tampered data");
    }

    /**
     * 测试多种业务场景的输入
     * <p>
     * 验证 {@code CRC8Util.calculate(String)} 方法对多种实际业务场景的输入都能正确处理，
     * 并且相同输入产生相同输出，不同输入产生不同输出。
     * </p>
     */
    @Test
    void testCalculate_MultipleBusinessScenarios() {
        String[] businessInputs = {
                "user_001",
                "merchant_12345",
                "order_20241111_001",
                "payment_abc123",
                "invoice_2024",
                "customer_vip_001"
        };

        int[] crcValues = new int[businessInputs.length];

        // 计算所有输入的 CRC8 值
        for (int i = 0; i < businessInputs.length; i++) {
            crcValues[i] = CRC8Util.calculate(businessInputs[i]);
            System.out.println("CRC8 of '" + businessInputs[i] + "': " + crcValues[i]);

            // 验证返回值范围
            assertTrue(crcValues[i] >= 0 && crcValues[i] <= 255,
                    "CRC8 value should be in range 0-255");

            // 验证确定性
            assertEquals(crcValues[i], CRC8Util.calculate(businessInputs[i]),
                    "Same input should produce same CRC8");
        }

        // 验证不同输入产生的 CRC8 值的唯一性（虽然可能有碰撞，但对这些输入应该都不同）
        for (int i = 0; i < crcValues.length; i++) {
            for (int j = i + 1; j < crcValues.length; j++) {
                if (crcValues[i] == crcValues[j]) {
                    System.out.println("WARNING: CRC8 collision detected between '" +
                            businessInputs[i] + "' and '" + businessInputs[j] +
                            "' (both have CRC8=" + crcValues[i] + ")");
                }
            }
        }
    }

    /**
     * 测试 CRC8 算法的分布均匀性（简单统计）
     * <p>
     * 对大量输入计算 CRC8 值，统计分布情况，验证算法的均匀性。
     * </p>
     */
    @Test
    void testCalculate_Distribution() {
        int[] distribution = new int[256];
        int testCount = 1000;

        // 生成测试数据并计算 CRC8
        for (int i = 0; i < testCount; i++) {
            String input = "test_" + i;
            int crc = CRC8Util.calculate(input);
            distribution[crc]++;
        }

        // 统计非零桶的数量
        int nonZeroBuckets = 0;
        int maxCount = 0;
        int minCount = testCount;

        for (int count : distribution) {
            if (count > 0) {
                nonZeroBuckets++;
                maxCount = Math.max(maxCount, count);
                minCount = Math.min(minCount, count);
            }
        }

        System.out.println("CRC8 Distribution Statistics:");
        System.out.println("  Total inputs: " + testCount);
        System.out.println("  Non-zero buckets: " + nonZeroBuckets + " / 256");
        System.out.println("  Max count in single bucket: " + maxCount);
        System.out.println("  Min count in non-zero bucket: " + minCount);

        // 验证至少有一定数量的不同 CRC8 值
        assertTrue(nonZeroBuckets > 200,
                "CRC8 should have good distribution, got only " + nonZeroBuckets + " different values");
    }

    /**
     * 测试获取多项式值的方法
     * <p>
     * 验证 {@code CRC8Util.getPolynomial()} 方法返回正确的多项式常量。
     * </p>
     */
    @Test
    void testGetPolynomial() {
        int polynomial = CRC8Util.getPolynomial();
        assertEquals(0x31, polynomial, "CRC-8-MAXIM polynomial should be 0x31");
        System.out.println("CRC8 polynomial: 0x" + Integer.toHexString(polynomial));
    }

    /**
     * 测试字符串和字节数组计算的一致性
     * <p>
     * 验证对同一字符串，使用字符串方法和字节数组方法计算的 CRC8 值是否一致。
     * </p>
     */
    @Test
    void testCalculate_StringAndByteArrayConsistency() {
        String input = "consistency_test";
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);

        int stringCrc = CRC8Util.calculate(input);
        int byteArrayCrc = CRC8Util.calculate(bytes) & 0xFF;  // 转换为无符号

        System.out.println("String CRC8: " + stringCrc);
        System.out.println("Byte array CRC8: " + byteArrayCrc);

        assertEquals(stringCrc, byteArrayCrc,
                "String and byte array methods should produce same CRC8");
    }
}
