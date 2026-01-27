package com.xlf.utility.constant;

import org.jetbrains.annotations.Contract;

/**
 * 字符串常量类
 * <p>
 * 提供系统的固定字符串常量，例如作者信息或系统版本号。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public class StringConstant {

    /**
     * 系统作者信息
     */
    public static final String SYSTEM_COMPANY = "FengUtil";
    /**
     * 系统版本信息
     */
    public static final String SYSTEM_VERSION = "v2.0.0-beta1";

    /**
     * AES 加密密钥字节数组
     * <p>
     * 用于 AES 加密算法的密钥，长度为 32 字符。
     */
    public static final String AES_KEY = "Kq7pS2xJvT8aF5mYd9hGc4bN6rLpW0zR";

    @Contract(value = " -> fail", pure = true)
    private StringConstant() {
        throw new UnsupportedOperationException("StringConstant 是常量类，不允许实例化");
    }
}
