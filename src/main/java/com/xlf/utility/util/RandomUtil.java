package com.xlf.utility.util;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * RandomUtil
 * <hr/>
 * 随机工具类
 * <p>
 * 用于生成随机字符串
 * </p>
 *
 * @since v1.0.0
 * @version v1.0.0
 * @author xiao_lfeng
 */
@SuppressWarnings("unused")
public class RandomUtil {

    /**
     * 创建随机字符串
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
     * 创建随机数字
     * <hr/>
     * 创建指定长度的随机数字
     *
     * @param size 长度
     * @return 随机数字
     */
    @NotNull
    public static String createRandomInt(int size) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            String code;
            code = String.valueOf(random.nextInt(10));
            stringBuilder.append(code);
        }
        return stringBuilder.toString();
    }

    /**
     * 创建随机小写字母
     * <hr/>
     * 创建指定长度的随机小写字母
     *
     * @param size 长度
     * @return 随机小写字母
     */
    @NotNull
    public static String createRandomLowerLetter(int size) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            String code;
            code = String.valueOf((char) (random.nextInt(26) + 97));
            stringBuilder.append(code);
        }
        return stringBuilder.toString();
    }

    /**
     * 创建随机大写字母
     * <hr/>
     * 创建指定长度的随机大写字母
     *
     * @param size 长度
     * @return 随机大写字母
     */
    @NotNull
    public static String createRandomUpperLetter(int size) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            String code;
            code = String.valueOf((char) (random.nextInt(26) + 65));
            stringBuilder.append(code);
        }
        return stringBuilder.toString();
    }

    /**
     * 创建随机字母
     * <hr/>
     * 创建指定长度的随机字母
     *
     * @param size 长度
     * @return 随机字母
     */
    @NotNull
    public static String createRandomLetter(int size) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            String code;
            if (random.nextInt(2) == 0) {
                code = String.valueOf((char) (random.nextInt(26) + 65));
            } else {
                code = String.valueOf((char) (random.nextInt(26) + 97));
            }
            stringBuilder.append(code);
        }
        return stringBuilder.toString();
    }
}
