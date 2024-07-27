package com.xlf.utility.util;

import org.jetbrains.annotations.NotNull;
import org.mindrot.jbcrypt.BCrypt;

/**
 * PasswordUtil
 * <hr/>
 * 密码工具类
 * <p>
 * 用于加密密码, 验证密码
 *
 * @author xiao_lfeng
 * @version v1.0.1
 * @since v1.0.1
 */
@SuppressWarnings("unused")
public class PasswordUtil {

    /**
     * <h1>加密密码</h1>
     * <hr/>
     * 加密密码
     * <p>
     * 1. 使用SHA-256加密密码
     * 2. 使用BCrypt加密
     * 3. 返回加密后的密码
     *
     * @param password 密码
     * @return 加密后的密码
     */
    @NotNull
    public static String encrypt(String password) {
        String sha256Hash = BaseUtil.sha256Hash(password);
        return BCrypt.hashpw(sha256Hash, BCrypt.gensalt());
    }

    /**
     * <h1>验证密码</h1>
     * <hr/>
     * 验证密码
     * <p>
     * 1. 使用SHA-256加密密码
     * 2. 使用BCrypt验证密码
     *
     * @param password          密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean verify(String password, String encryptedPassword) {
        String sha256Hash = BaseUtil.sha256Hash(password);
        return BCrypt.checkpw(sha256Hash, encryptedPassword);
    }
}
