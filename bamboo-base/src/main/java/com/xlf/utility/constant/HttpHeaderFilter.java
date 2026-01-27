package com.xlf.utility.constant;

import org.jetbrains.annotations.Contract;

import java.util.Set;

/**
 * HTTP Header 过滤常量类
 * <p>
 * 提供HTTP头过滤相关的常量集合，用于在日志输出和请求处理时区分标准HTTP头与自定义HTTP头。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public final class HttpHeaderFilter {

    /**
     * 需要排除的标准HTTP头列表（小写）
     */
    public static final Set<String> STANDARD_HTTP_HEADERS = Set.of(
            "accept", "accept-charset", "accept-encoding", "accept-language",
            "accept-datetime", "authorization", "cache-control", "connection",
            "cookie", "content-length", "content-md5", "content-type",
            "date", "expect", "from", "host", "if-match", "if-modified-since",
            "if-none-match", "if-range", "if-unmodified-since", "max-forwards",
            "origin", "pragma", "proxy-authorization", "range", "referer",
            "te", "user-agent", "upgrade", "via", "warning"
    );

    /**
     * 常见自定义Header前缀列表（小写）
     */
    public static final Set<String> CUSTOM_HEADER_PREFIXES = Set.of(
            "x-", "custom-", "ext-"
    );

    @Contract(value = " -> fail", pure = true)
    private HttpHeaderFilter() {
        throw new UnsupportedOperationException("HttpHeaderFilter 是常量类，不允许实例化");
    }
}
