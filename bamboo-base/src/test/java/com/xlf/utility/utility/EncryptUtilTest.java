package com.xlf.utility.utility;

import com.xlf.utility.ErrorCode;
import com.xlf.utility.exception.BusinessException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@code EncryptUtilTest} 是对 {@code EncryptUtil} 类中加密和解密功能的单元测试类。
 * 目标是通过标准化的测试用例确保加密解密功能逻辑正确、稳定且符合预期。该类覆盖了多种输入场景，
 * 并使用了断言来验证每个功能点的正确性。以下为具体方法的详细说明：
 *
 * <p>
 * 测试内容包括：
 * <ul>
 *   <li>加密是否正确处理非空输入并生成结果。</li>
 *   <li>加密是否能够处理多次调用的输入一致性。</li>
 *   <li>对特殊字符、边界条件（如空值、非法值）的适配和异常捕获。</li>
 *   <li>解密的结果是否能够还原正确的原始输入。</li>
 * </ul>
 *
 * <p>
 * NOTICE: 本类的测试用例需依赖 {@code EncryptUtil} 提供的加密算法逻辑。修改加密算法或逻辑时，需
 * 结合新的业务场景调整相关测试用例以确保适配。
 *
 * @author xiao_lfeng
 * @version v1.0.12-SNAPSHOT
 * @since v1.0.12-SNAPSHOT
 */
class EncryptUtilTest {

    /**
     * 测试用户加密功能是否能正确处理非空输入。
     * <p>
     * 该测试用例验证了 {@code EncryptUtil.userEncryption} 方法在接收到有效输入时，是否能够
     * 返回一个非空且与原始输入不同的加密结果。
     */
    @Test
    void testUserEncryption_Success() {
        String input1 = "4891";
        String encrypted1 = EncryptUtil.userEncryption(input1);
        System.out.println("Encrypted: " + encrypted1);

        String input2 = "4890";
        String encrypted2 = EncryptUtil.userEncryption(input2);
        System.out.println("Encrypted: " + encrypted2);

        assertNotNull(encrypted1);
        assertNotEquals(input1, encrypted1);
        assertFalse(encrypted1.isEmpty());
    }

    /**
     * 测试用户加密功能在接收到空字符串时是否抛出预期的异常。
     * <p>
     * 该测试用例验证了 {@code EncryptUtil.userEncryption} 方法在接收到空字符串时，是否会
     * 抛出 {@code BusinessException} 异常，并且异常信息和错误码符合预期。
     */
    @Test
    void testUserEncryption_BlankInput() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            EncryptUtil.userEncryption("");
        });

        assertEquals("输入内容为空", exception.getMessage());
        assertEquals(ErrorCode.PARAMETER_MISSING, exception.getErrorCode());
    }



    /**
     * 测试用户解密功能是否能正确还原加密后的字符串。
     * <p>
     * 该测试用例验证了 {@code EncryptUtil.userDecryption} 方法在接收到有效的加密字符串时，
     * 是否能够返回与原始输入相同的字符串，并且确保解密结果不为 null。
     */
    @Test
    void testUserDecryption_Success() {
        String originalInput = "testUser123";
        String encrypted = EncryptUtil.userEncryption(originalInput);
        String decrypted = EncryptUtil.userDecryption(encrypted);

        assertNotNull(decrypted);
        assertEquals(originalInput, decrypted);
    }

    /**
     * 测试用户解密功能在接收到空字符串时是否抛出预期的异常。
     * <p>
     * 该测试用例验证了 {@code EncryptUtil.userDecryption} 方法在接收到空字符串时，是否会
     * 抛出 {@code BusinessException} 异常，并且异常信息和错误码符合预期。
     */
    @Test
    void testUserDecryption_BlankInput() {
        BusinessException exception = assertThrows(BusinessException.class, () -> EncryptUtil.userDecryption(""));

        assertEquals("输入内容为空", exception.getMessage());
        assertEquals(ErrorCode.PARAMETER_MISSING, exception.getErrorCode());
    }

    /**
     * 测试用户加密功能是否是确定性的，即相同输入总是产生相同的加密结果。
     * <p>
     * 该测试用例验证了多次对同一输入调用 {@code EncryptUtil.userEncryption} 方法时，返回的
     * 加密结果是否一致。
     */
    @Test
    void testEncryption_IsDeterministic() {
        String input = "testUser123";
        String encrypted1 = EncryptUtil.userEncryption(input);
        String encrypted2 = EncryptUtil.userEncryption(input);

        assertEquals(encrypted1, encrypted2);
    }

    /**
     * 测试用户加密和解密功能对多种输入的适配性。
     * <p>
     * 该测试用例验证了对多种不同的输入字符串进行加密和解密操作，确保每个输入都能正确还原。
     */
    @Test
    void testEncryptionDecryption_MultipleInputs() {
        String[] testInputs = {
            "user123",
            "admin",
            "testuser@domain.com",
            "12345678",
            "special!@#$% characters"
        };

        for (String input : testInputs) {
            String encrypted = EncryptUtil.userEncryption(input);
            String decrypted = EncryptUtil.userDecryption(encrypted);
            assertEquals(input, decrypted, "Failed for input: " + input);
        }
    }

    /**
     * 测试用户加密功能对不同输入的加密结果是否不同。
     * <p>
     * 该测试用例验证了对两个不同的输入字符串进行加密时，返回的加密结果是否不同，以确保
     * 加密算法的随机性和安全性。
     */
    @Test
    void testEncryption_ProducesDifferentResultsForDifferentInputs() {
        String input1 = "user1";
        String input2 = "user2";

        String encrypted1 = EncryptUtil.userEncryption(input1);
        String encrypted2 = EncryptUtil.userEncryption(input2);

        assertNotEquals(encrypted1, encrypted2);
    }
}
