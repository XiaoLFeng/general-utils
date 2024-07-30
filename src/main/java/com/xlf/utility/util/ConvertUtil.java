package com.xlf.utility.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * ConvertUtil
 * <br/>
 * 转换工具类
 * <p>
 *     用于将对象转换为 Map
 * </p>
 *
 * @since v1.0.0
 * @version v1.0.0
 * @author xiao_lfeng
 */
public class ConvertUtil {
    /**
     * 将对象转换为 Map
     * <br/>
     * 将对象转换为 Map；
     * 该方法将对象转换为 Map，该 Map 的 key 为对象的属性名，value 为对象的属性值
     *
     * @param obj 对象
     * @return Map
     */
    @NotNull
    public static Map<String, Object> convertObjectToMap(@NotNull Object obj) {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(obj));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to convert object to map", e);
            }
        }
        return map;
    }

    /**
     * 将对象转换为 Map
     * <br/>
     * 将对象转换为 Map；
     * 该方法将对象转换为 Map，该 Map 的 key 为对象的属性名，value 为对象的属性值
     *
     * @param obj 对象
     * @return Map
     */
    @NotNull
    public static Map<String, String> convertObjectToMapString(@NotNull Object obj) {
        Map<String, String> map = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(obj).toString());
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to convert object to map", e);
            }
        }
        return map;
    }

    /**
     * 将对象转换为 Map
     * <br/>
     * 将对象转换为 Map；
     * 该方法将对象转换为 Map，该 Map 的 key 为对象的属性名，value 为对象的属性值
     *
     * @param obj 对象
     * @return Map
     */
    @NotNull
    public static Map<Object, Object> convertObjectToMapObject(@NotNull Object obj) {
        Map<Object, Object> map = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(obj));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to convert object to map", e);
            }
        }
        return map;
    }

    /**
     * 将 Map 转换为对象
     * <br/>
     * 将 Map 转换为对象；
     * 该方法将 Map 转换为对象，该 Map 的 key 为对象的属性名，value 为对象的属性值
     *
     * @param map Map
     * @param clazz 对象的 Class
     * @param <T> 对象的类型
     * @return 对象
     */
    public static <T> T convertMapToObject(Map<Object, Object> map, @NotNull Class<T> clazz) {
        T obj = null;
        try {
            obj = clazz.newInstance();
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                field.set(obj, map.get(field.getName()));
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
