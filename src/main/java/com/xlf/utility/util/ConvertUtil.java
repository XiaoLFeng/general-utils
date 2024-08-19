package com.xlf.utility.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
 * @author xiaofeng
 */
@SuppressWarnings("unused")
public class ConvertUtil {

    /**
     * 将对象转换为 Map
     * <br/>
     * 将对象转换为 Map，使用 Gson 进行转换，对象中的属性名作为 Map 的 key，属性值作为 Map 的 value
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
     * 将对象转换为 Map，使用 Gson 进行转换，转换为 String 类型
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
     * 将对象转换为 Map，使用 Gson 进行转换，转换为 Object 类型
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
     * 将 Map 转换为对象，使用 Gson 进行转换，转换为指定的类
     *
     * @param map   Map
     * @param clazz 类
     * @param <T>   泛型
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
            obj = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                Object value = map.get(field.getName());

                if (value != null) {
                    String fieldTypeName = field.getType().getName();
                    boolean none = "".equalsIgnoreCase(value.toString()) || "null".equalsIgnoreCase(value.toString());
                    if ("java.util.Date".equalsIgnoreCase(fieldTypeName)) {
                        if (none) {
                            field.set(obj, null);
                        } else {
                            field.set(obj, sdf.parse(value.toString()));
                        }
                    } else if ("java.lang.Boolean".equalsIgnoreCase(fieldTypeName)) {
                        if ("1".equalsIgnoreCase(value.toString())) {
                            field.set(obj, true);
                        } else if ("0".equalsIgnoreCase(value.toString())) {
                            field.set(obj, false);
                        } else if (none) {
                            field.set(obj, null);
                        } else {
                            field.set(obj, Boolean.parseBoolean(value.toString()));
                        }
                    } else if ("java.lang.Integer".equalsIgnoreCase(fieldTypeName)) {
                        if (none) {
                            field.set(obj, null);
                        } else {
                            field.set(obj, Integer.parseInt(value.toString()));
                        }
                    } else if ("java.lang.Long".equalsIgnoreCase(fieldTypeName)) {
                        if (none) {
                            field.set(obj, null);
                        } else {
                            field.set(obj, Long.parseLong(value.toString()));
                        }
                    } else if ("java.lang.Float".equalsIgnoreCase(fieldTypeName)) {
                        if (none) {
                            field.set(obj, null);
                        } else {
                            field.set(obj, Float.parseFloat(value.toString()));
                        }
                    } else if ("java.lang.Double".equalsIgnoreCase(fieldTypeName)) {
                        if (none) {
                            field.set(obj, null);
                        } else {
                            field.set(obj, Double.parseDouble(value.toString()));
                        }
                    } else if ("java.sql.Timestamp".equalsIgnoreCase(fieldTypeName)) {
                        if (none) {
                            field.set(obj, null);
                        } else {
                            // 处理标准时间字符串 "yyyy-MM-dd HH:mm:ss"
                            if (value.toString().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                                field.set(obj, Timestamp.valueOf(value.toString()));
                            } else {
                                // 处理自定义格式 "MMM d, yyyy, h:mm:ss a"
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
                                LocalDateTime localDateTime = LocalDateTime.parse(value.toString(), formatter);
                                field.set(obj, Timestamp.valueOf(localDateTime));
                            }
                        }
                    } else {
                        if (none) {
                            field.set(obj, null);
                        } else {
                            field.set(obj, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}