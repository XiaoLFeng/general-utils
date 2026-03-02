package com.xlf.utility.constant;

import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 字符串常量类
 * <p>
 * 提供系统的固定字符串常量，例如作者信息或系统版本号。
 *
 * @author xiao_lfeng
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public class StringConstant {

    private static final Logger log = LoggerFactory.getLogger(StringConstant.class);

    /**
     * 系统作者信息
     */
    public static final String SYSTEM_COMPANY = "GeneralUtils";

    /**
     * 系统版本信息（从 Maven 构建时自动获取）
     */
    public static final String SYSTEM_VERSION;

    static {
        String version = "vUNKNOWN";
        try (InputStream is = StringConstant.class.getClassLoader()
                .getResourceAsStream("version.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                version = "v" + props.getProperty("version", "UNKNOWN");
            }
        } catch (IOException e) {
            log.warn("无法读取版本信息: {}", e.getMessage());
        }
        SYSTEM_VERSION = version;
    }

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
