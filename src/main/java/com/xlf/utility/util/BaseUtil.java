package com.xlf.utility.util;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * BaseUtil
 * <hr/>
 * 基础工具类
 * <p>
 * 用于提供基础工具方法
 *
 * @author xiao_lfeng
 * @version v1.0.1
 * @since v1.0.1
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
}
