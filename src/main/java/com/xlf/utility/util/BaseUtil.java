package com.xlf.utility.util;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * BaseUtil
 * <hr/>
 * 基础工具类
 * <p>
 * 用于提供基础工具方法
 *
 * @since v1.0.1
 * @version v1.0.1
 * @author xiao_lfeng
 */
@SuppressWarnings("unused")
public class BaseUtil {
    /**
     * <h1>创建随机字符串</h1>
     * <hr/>
     * 创建指定长度的随机字符串
     *
     * @param size 长度
     * @return 随机字符串
     */
    @NotNull
    public static String createRandomString(int size) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            String code;
            switch (random.nextInt(3)) {
                case 0 -> code = String.valueOf(random.nextInt(10));
                case 1 -> code = String.valueOf((char) (random.nextInt(26) + 65));
                default -> code = String.valueOf((char) (random.nextInt(26) + 97));
            }
            stringBuilder.append(code);
        }
        return stringBuilder.toString();
    }

    /**
     * <h1>SHA-256加密</h1>
     * <hr/>
     * 使用SHA-256加密字符串
     *
     * @param input 输入
     * @return 加密后的字符串
     */
    @NotNull
    protected static String sha256Hash(@NotNull String input) {
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
}
