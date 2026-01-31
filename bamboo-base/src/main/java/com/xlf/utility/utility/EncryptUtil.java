package com.xlf.utility.utility;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.constant.StringConstant;
import com.xlf.utility.exception.library.BusinessException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 * 加密工具类
 * <p>
 * 用于提供加密工具方法。
 *
 * @author xiao_lfeng
 * @version v1.0.1
 * @since v1.0.1
 */
@SuppressWarnings("unused")
public class EncryptUtil {

    @Contract(value = " -> fail", pure = true)
    private EncryptUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * SHA-256加密
     * <hr/>
     * 使用SHA-256加密字符串；
     *
     * @param input 输入
     * @return 加密后的字符串
     */
    @NotNull
    public static String sha256Hash(@NotNull String input) {
        try {
            // 获取 SHA-256 实例
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * MD5加密
     * <hr/>
     *
     * @param input 输入
     * @return 加密后的字符串
     */
    @NotNull
    public static String md5(@NotNull String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 用户加密方法
     * <p>
     * 此方法用于对输入内容进行加密，通过 MD5 方式生成哈希值，并结合输入内容使用 AES 加密后
     * 返回 Base64 格式的加密字符串。
     *
     * <p>
     * 具体逻辑如下：
     * <ul>
     *   <li>首先对输入内容进行 MD5 哈希计算。</li>
     *   <li>将计算得到的哈希值与输入内容拼接。</li>
     *   <li>使用类内部定义的 AES 密钥将拼接后的字符串加密，生成 Base64 格式的最终结果。</li>
     * </ul>
     *
     * <p>
     * NOTICE: 输入内容不能为空，否则将抛出 {@code BusinessException} 异常。
     * 输入内容的有效性必须由调用方在传入方法前确保。
     *
     * <p>
     * NOTICE: 本方法使用内部静态 AES 密钥进行加密处理，若 AES 密钥发生更改或者使用其他方法
     * 加密的结果，则密钥和加解密方法之间须保持一致性，否则历史数据无法解密。
     *
     * @param input 待加密的字符串（不能为空）
     * @return 加密后的字符串（Base64 格式，包含 MD5 校验与原始数据）
     * @throws BusinessException 当输入内容为空时抛出异常。
     */
    public static String userEncryption(@NotNull String input) {
        if (StrUtil.isBlank(input)) {
            throw new BusinessException("输入内容为空", ErrorCode.PARAMETER_MISSING);
        }
        String getMd5 = md5(input);
        return SecureUtil.aes(StringConstant.AES_KEY.getBytes(StandardCharsets.UTF_8))
                .encryptBase64(getMd5 + input);
    }

    /**
     * 用户解密方法
     * <p>
     * 此方法用于解密由 AES 加密的字符串，并验证其完整性。通过 AES 算法解密得到
     * 原始数据后，将其分为两部分：前 32 字符为 MD5 校验和，后续字符为实际数据。
     * 方法会校验 MD5 哈希是否与后续数据匹配，确保解密后的数据完整性与正确性。
     * 若解密数据无效（例如格式不符或校验失败），则会抛出自定义业务异常。
     *
     * <p>
     * NOTICE: 加密内容不能为空。方法严格校验输入格式，若输入为不合法的加密字符串，
     * 方法将抛出 {@code BusinessException} 异常，请确保输入正确。
     *
     * <ul>
     *   <li>输入字符串必须是符合指定加密格式，并且经过有效加密的字符串。</li>
     *   <li>确保所引用的 AES 密钥与加密时使用的一致，否则解密将失败。</li>
     * </ul>
     *
     * <p>
     * NOTICE: AES 密钥强依赖类的内部字段，重构或修改时需保证密钥一致性。
     * 并且类的加密解密方法之间需保持兼容，错误修改可能导致无法解密历史数据。
     *
     * @param encryptedInput 加密后的字符串（必须为符合约定格式的 Base64 编码字符串）
     * @return 解密后的原始字符串（若校验通过）
     * @throws BusinessException 当输入内容为空、格式不匹配或校验失败时抛出。
     */
    public static @NotNull String userDecryption(@NotNull String encryptedInput) {
        if (StrUtil.isBlank(encryptedInput)) {
            throw new BusinessException("输入内容为空", ErrorCode.PARAMETER_MISSING);
        }
        String decrypted = SecureUtil.aes(StringConstant.AES_KEY.getBytes(StandardCharsets.UTF_8))
                .decryptStr(encryptedInput);
        return Optional.of(decrypted.substring(32))
                .filter(StrUtil::isNotBlank)
                .filter(str -> md5(str).equals(decrypted.substring(0, 32)))
                .orElseThrow(() -> new BusinessException("加密数据无效", ErrorCode.PARAMETER_INVALID));
    }
}
