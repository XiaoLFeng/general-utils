package com.xlf.utility.utility;

import cn.hutool.core.bean.BeanUtil;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 转换工具类
 * <p>
 * 用于将对象转换为 Map；将 Map 转换为对象；将对象转换为 Map，转换为 String 类型；将对象转换为 Map，转换为 Object 类型
 *
 * @author xiao_lfeng
 * @version v1.0.1
 * @since v1.0.1
 */
@SuppressWarnings("unused")
public class ConvertUtil {
    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(ConvertUtil.class);

    private ConvertUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 将对象转换为 Map
     * <br/>
     * 使用 Hutool 的 BeanUtil 将对象转换为 Map
     *
     * @param obj 对象
     * @return Map
     */
    @NotNull
    public static Map<String, Object> convertObjectToMap(@NotNull Object obj) {
        return BeanUtil.beanToMap(obj);
    }

    /**
     * 将对象转换为 Map
     * <br/>
     * 使用 Hutool 的 BeanUtil 将对象转换为 Map，并转换为 String 类型
     *
     * @param obj 对象
     * @return Map
     */
    @NotNull
    public static Map<String, String> convertObjectToMapString(@NotNull Object obj) {
        Map<String, Object> map = BeanUtil.beanToMap(obj);
        Map<String, String> stringMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            stringMap.put(entry.getKey(), entry.getValue() != null ? entry.getValue().toString() : "");
        }
        return stringMap;
    }
}
