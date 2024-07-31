package com.xlf.utility.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * ConvertUtil
 * <br/>
 * 转换工具类
 * <p>
 * 用于将对象转换为 Map
 * </p>
 *
 * @author xiao_lfeng
 * @version v1.0.0
 * @since v1.0.0
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
        String getJson = new Gson().toJson(obj);
        return new Gson().fromJson(getJson, new TypeToken<HashMap<String, Object>>() {
        }.getType());
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
        String getJson = new Gson().toJson(obj);
        return new Gson().fromJson(getJson, new TypeToken<Map<String, String>>() {
        }.getType());
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
        String getJson = new Gson().toJson(obj);
        return new Gson().fromJson(getJson, new TypeToken<Map<Object, Object>>() {
        }.getType());
    }

    /**
     * 将 Map 转换为对象
     * <br/>
     * 将 Map 转换为对象；
     * 该方法将 Map 转换为对象，该 Map 的 key 为对象的属性名，value 为对象的属性值
     *
     * @param map   Map
     * @param clazz 对象的 Class
     * @param <T>   对象的类型
     * @return 对象
     */
    public static <T> T convertMapToObject(Map<Object, Object> map, @NotNull Class<T> clazz) {
        String getJson = new Gson().toJson(map);
        return new Gson().fromJson(getJson, clazz);
    }
}
