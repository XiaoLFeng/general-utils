package com.xlf.utility.utility;

import org.jetbrains.annotations.Contract;

import java.util.Optional;

/**
 * IpUtil - IP地址工具类
 * <p>
 * 提供与IP地址相关的工具方法，包括验证IP地址是否匹配、IP地址格式转换等。
 * 旨在帮助开发者高效地处理与IP地址操作相关的需求。
 * <p>
 * NOTICE: 本工具类中的所有方法均为静态方法，无法实例化。尝试创建实例将
 * 抛出 {@code UnsupportedOperationException} 异常。
 *
 * @author xiao_lfeng
 * @version v1.0.5-SNAPSHOT
 * @since v1.0.5-SNAPSHOT
 */
public class IpUtil {

    @Contract(value = " -> fail", pure = true)
    private IpUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 检查客户端IP地址是否与允许的目标IP规则匹配。
     * <p>
     * 此方法通过检查客户端IP地址是否符合特定的允许规则来进行匹配判断。规则包括：
     * <ul>
     *     <li>单个IP地址匹配（例如：{@code "192.168.1.1"}）。</li>
     *     <li>IP范围匹配（例如：{@code "192.168.1.1-192.168.1.100"}）。</li>
     *     <li>CIDR格式匹配（例如：{@code "192.168.1.0/24"}）。</li>
     * </ul>
     * 方法会在匹配过程中验证传入的IP格式是否符合IPv4标准，并判断客户端IP是否落在
     * 允许的IP范围内。
     *
     * <p>
     * NOTICE: 传入的IP地址和规则必须合法且符合IPv4标准，例如{@code "192.168.1.1"}。若规则
     * 或IP格式不正确（如含有无效字符），会记录日志并返回{@code false}。
     *
     * @param clientIp  客户端的IP地址，作为待验证的目标。必须是正确的IPv4格式，例如
     *                  {@code "192.168.1.10"}，否则方法将直接返回{@code false}。
     * @param allowedIp 允许的IP规则，可为单个IP、IP范围或CIDR格式。
     *                  例如：
     *                  <ul>
     *                      <li>{@code "192.168.1.1"}</li>
     *                      <li>{@code "192.168.1.1-192.168.1.100"}</li>
     *                      <li>{@code "192.168.1.0/24"}</li>
     *                  </ul>
     *                  输入不能为空，否则返回{@code false}。
     * @return 如果客户端IP符合允许的规则，返回{@code true}；否则返回{@code false}。
     * 如果输入格式无效或解析过程中出现异常，也会返回{@code false}。
     */
    public static boolean isIpMatch(String clientIp, String allowedIp) {
        return Optional.ofNullable(clientIp)
                .filter(ip -> ip.matches("\\d{1,3}(\\.\\d{1,3}){3}"))
                .flatMap(ip -> Optional.ofNullable(allowedIp)
                        .map(allowed -> {
                            try {
                                long ipLong = IpUtil.parseIpToLong(ip);
                                if (allowed.contains("-")) {
                                    String[] range = allowed.split("-");
                                    return ipLong >= IpUtil.parseIpToLong(range[0].trim()) &&
                                            ipLong <= IpUtil.parseIpToLong(range[1].trim());
                                }
                                if (allowed.contains("/")) {
                                    String[] parts = allowed.split("/");
                                    long networkLong = IpUtil.parseIpToLong(parts[0].trim());
                                    int maskBits = Integer.parseInt(parts[1].trim());
                                    long mask = -1L << (32 - maskBits);
                                    return (ipLong & mask) == (networkLong & mask);
                                }
                                return ip.equals(allowed.trim());
                            } catch (Exception ex) {
                                return false;
                            }
                        })).orElse(false);
    }

    /**
     * 将IP地址字符串转换为对应的长整型表示。
     * <p>
     * 此方法用于将IPv4格式的IP地址（如 {@code "192.168.1.1"}）转为长整型数值，
     * 便于后续计算和存储。转换过程中要求输入的IP地址符合IPv4格式。
     * <p>
     * NOTICE: 若输入的IP地址格式不正确（如为空或不符合IPv4标准），
     * 方法将返回 {@code 0L}。
     *
     * @param ipAddress 待转换的IPv4字符串，如 {@code "192.168.1.1"}。
     *                  如果输入为 {@code null} 或不符合IPv4格式，
     *                  则方法直接返回0。
     * @return 对应IP地址的长整型值。如果转换失败（如输入格式有误），返回 {@code 0L}。
     * 例如：传入 {@code "192.168.1.1"} 将返回对应的数值 {@code 3232235777L}。
     */
    public static long parseIpToLong(String ipAddress) {
        return Optional.ofNullable(ipAddress)
                .filter(ip -> ip.matches("\\d{1,3}(\\.\\d{1,3}){3}"))
                .map(ip -> ip.split("\\."))
                .filter(parts -> parts.length == 4)
                .map(parts -> ((Long.parseLong(parts[0]) & 0xFF) << 24) |
                        ((Long.parseLong(parts[1]) & 0xFF) << 16) |
                        ((Long.parseLong(parts[2]) & 0xFF) << 8) |
                        (Long.parseLong(parts[3]) & 0xFF))
                .orElse(0L);
    }
}
