package com.xlf.utility.util;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * UuidUtil
 * <hr/>
 * 生成UUID, 用于生成唯一标识
 *
 * @author xiao_lfeng
 * @version v1.0.1
 * @since v1.0.1
 */
@SuppressWarnings("unused")
public interface UuidUtil {
    /**
     * 生成UUID
     * <p>
     * 生成UUID字符串，带横杠。
     *
     * @return UUID字符串
     */
    @NotNull
    static String generateStringUuid() {
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * 生成UUID
     * <p>
     * 生成UUID, 用于生成唯一标识，带横杠。
     *
     * @return UUID
     */
    @NotNull
    static UUID generateUuid() {
        return java.util.UUID.randomUUID();
    }

    /**
     * 转换为UUID
     * <p>
     * 将字符串转换为UUID
     * <p>
     * 例如: "550e8400-e29b-41d4-a716-446655440000" 字符串，转换为UUID的形式
     *
     * @param uuid UUID字符串
     * @return UUID
     */
    @NotNull
    static UUID convertToUuid(String uuid) {
        return UUID.fromString(uuid);
    }

    /**
     * 生成UUID
     * <p>
     * 生成UUID字符串, 不带横杠
     * <p>
     * 例如: "550e8400e29b41d4a716446655440000"
     *
     * @return UUID字符串
     */
    @NotNull
    static String generateUuidNoDash() {
        return generateStringUuid().replace("-", "");
    }

    /**
     * 生成UUID
     * <p>
     * 生成UUID，根据字符串生成UUID，并非字符串 UUID 生成 UUID；
     * <p>
     * 例如: "Hello" -> "550e8400-e29b-41d4-a716-446655440000(UUID)"
     *
     * @param getString 字符串
     * @return UUID
     */
    @NotNull
    static UUID makeUuidFromString(@NotNull String getString) {
        return UUID.nameUUIDFromBytes(getString.getBytes());
    }

    /**
     * 生成UUID
     * <p>
     * 生成UUID字符串，带横杠；根据字符串生成UUID，并非字符串 UUID 生成 UUID 字符串；
     * <p>
     * 例如: "Hello" -> "550e8400-e29b-41d4-a716-446655440000(String)"
     *
     * @param getString 字符串
     * @return UUID字符串
     */
    static String makeStringUuidFromString(@NotNull String getString) {
        return UUID.nameUUIDFromBytes(getString.getBytes()).toString();
    }

    /**
     * 生成UUID
     * <p>
     * 生成UUID字符串, 不带横杠
     * <p>
     * 例如:  "Hello" -> "550e8400e29b41d4a716446655440000"
     *
     * @param getString 字符串
     * @return UUID字符串
     */
    @NotNull
    static String makeUuidFromStringNoDash(@NotNull String getString) {
        return UUID.nameUUIDFromBytes(getString.getBytes()).toString().replace("-", "");
    }
}
