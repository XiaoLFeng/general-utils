package com.xlf.utility.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
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
@SuppressWarnings("unused")
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
    @Nullable
    public static <T> T convertMapToObject(Map<Object, Object> map, @NotNull Class<T> clazz) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (map == null) {
            return null;
        }
        T obj = null;
        try {
            // 使用newInstance来创建对象
            obj = clazz.getDeclaredConstructor().newInstance();
            // 获取类中的所有字段
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                // 判断是拥有某个修饰符
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                // 当字段使用private修饰时，需要加上
                field.setAccessible(true);
                // 获取参数类型名字
                String filedTypeName = field.getType().getName();
                // 判断是否为日期类型
                if ("java.util.date".equalsIgnoreCase(filedTypeName)) {
                    String dateTimestamp = (String) map.get(field.getName());
                    if ("null".equalsIgnoreCase(dateTimestamp)) {
                        field.set(obj, null);
                    } else {
                        field.set(obj, sdf.parse(dateTimestamp));
                    }
                }
                // 是否是布尔类型
                if ("java.lang.Boolean".equalsIgnoreCase(filedTypeName)) {
                    String value = (String) map.get(field.getName());
                    if ("null".equalsIgnoreCase(value)) {
                        field.set(obj, null);
                    } else {
                        field.set(obj, Boolean.parseBoolean(value));
                    }
                }
                // 是否是整型
                if ("java.lang.Integer".equalsIgnoreCase(filedTypeName)) {
                    String value = (String) map.get(field.getName());
                    if ("null".equalsIgnoreCase(value)) {
                        field.set(obj, null);
                    } else {
                        field.set(obj, Integer.parseInt(value));
                    }
                }
                // 是否是长整型
                if ("java.lang.Long".equalsIgnoreCase(filedTypeName)) {
                    String value = (String) map.get(field.getName());
                    if ("null".equalsIgnoreCase(value)) {
                        field.set(obj, null);
                    } else {
                        field.set(obj, Long.parseLong(value));
                    }
                }
                // 是否是浮点型
                if ("java.lang.Float".equalsIgnoreCase(filedTypeName)) {
                    String value = (String) map.get(field.getName());
                    if ("null".equalsIgnoreCase(value)) {
                        field.set(obj, null);
                    } else {
                        field.set(obj, Float.parseFloat(value));
                    }
                }
                // 是否是双精度浮点型
                if ("java.lang.Double".equalsIgnoreCase(filedTypeName)) {
                    String value = (String) map.get(field.getName());
                    if ("null".equalsIgnoreCase(value)) {
                        field.set(obj, null);
                    } else {
                        field.set(obj, Double.parseDouble(value));
                    }
                }
                // 是否是字符串
                field.set(obj, map.get(field.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}
