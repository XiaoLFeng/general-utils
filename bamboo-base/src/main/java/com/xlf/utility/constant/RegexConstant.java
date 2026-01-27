package com.xlf.utility.constant;

import org.jetbrains.annotations.Contract;

/**
 * 正则表达式常量类
 * <p>
 * 提供系统中常用的正则表达式常量，适用于字符串匹配、校验等各种场景。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public class RegexConstant {

    /**
     * UUID 正则表达式常量
     */
    public static final String UUID = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";
    /**
     * 无破折号的 UUID 正则表达式常量
     */
    public static final String UUID_NO_DASH = "^[0-9a-f]{8}[0-9a-f]{4}[1-5][0-9a-f]{3}[89ab][0-9a-f]{3}[0-9a-f]{12}$";

    @Contract(value = " -> fail", pure = true)
    private RegexConstant() {
        throw new UnsupportedOperationException("RegexConstant 是常量类，不允许实例化");
    }
}
