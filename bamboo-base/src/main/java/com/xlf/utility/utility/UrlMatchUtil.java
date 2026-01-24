package com.xlf.utility.utility;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * URL 匹配工具类。
 * <p>
 * 提供基于 Ant 风格路径模式的 URL 匹配工具方法，用于判断请求 URL 是否匹配指定的排除模式列表。
 * 支持 Ant 风格的通配符规则：{@code ?}、{@code *}、{@code **}。
 *
 * <p>
 * <b>通配符说明：</b>
 * <ul>
 *     <li>{@code ?} - 匹配单个字符，例如 {@code /doc?.html} 可匹配 {@code /doc1.html}、{@code /docs.html}</li>
 *     <li>{@code *} - 匹配 0 或多个字符（单层路径），例如 {@code /api/*} 可匹配 {@code /api/users}，但不匹配 {@code /api/users/123}</li>
 *     <li>{@code **} - 匹配 0 或多个路径段，例如 {@code /api/**} 可匹配 {@code /api/users}、{@code /api/users/123}、{@code /api/v1/users/123}</li>
 * </ul>
 *
 * <p>
 * <b>使用示例：</b>
 * <pre>{@code
 * List<String> excludePatterns = List.of("/health", "/actuator/**", "/api/public/**");
 *
 * // 精确匹配
 * boolean result1 = UrlMatchUtil.isUrlExcluded("/health", excludePatterns);  // true
 *
 * // 通配符匹配
 * boolean result2 = UrlMatchUtil.isUrlExcluded("/actuator/metrics", excludePatterns);  // true
 * boolean result3 = UrlMatchUtil.isUrlExcluded("/api/public/users/123", excludePatterns);  // true
 *
 * // 不匹配
 * boolean result4 = UrlMatchUtil.isUrlExcluded("/api/private/users", excludePatterns);  // false
 * }</pre>
 *
 * <p>
 * NOTICE:
 * <ul>
 *     <li>此类为工具类，禁止实例化。</li>
 *     <li>所有方法均为静态方法。</li>
 *     <li>使用 Spring 内置的 {@link AntPathMatcher} 实现，线程安全且性能优良。</li>
 *     <li>当 {@code excludePatterns} 为 {@code null} 或空列表时，始终返回 {@code false}。</li>
 *     <li>当 {@code requestUrl} 为 {@code null} 或空字符串时，始终返回 {@code false}。</li>
 * </ul>
 *
 * @author xiao_lfeng
 * @version v1.1.8-SNAPSHOT
 * @see AntPathMatcher
 * @since v1.1.8-SNAPSHOT
 */
@SuppressWarnings("unused")
public final class UrlMatchUtil {

    /**
     * Ant 路径匹配器（线程安全）
     */
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Contract(value = " -> fail", pure = true)
    private UrlMatchUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 判断请求 URL 是否匹配排除模式列表中的任一模式。
     * <p>
     * 该方法遍历排除模式列表，使用 Ant 风格路径匹配器依次检查请求 URL 是否匹配每个模式。
     * 只要匹配任一模式，立即返回 {@code true}，实现提前返回优化。
     *
     * <p>
     * <b>边界情况处理：</b>
     * <ul>
     *     <li>如果 {@code requestUrl} 为 {@code null} 或空字符串，返回 {@code false}。</li>
     *     <li>如果 {@code excludePatterns} 为 {@code null} 或空列表，返回 {@code false}。</li>
     *     <li>如果排除模式列表中包含 {@code null} 元素，该元素将被自动跳过。</li>
     * </ul>
     *
     * <p>
     * <b>性能特性：</b>
     * <ul>
     *     <li>时间复杂度：O(n)，n 为排除模式列表的长度。</li>
     *     <li>使用提前返回优化，找到第一个匹配即停止遍历。</li>
     *     <li>建议保持排除模式列表长度 &lt; 20，以获得最佳性能。</li>
     *     <li>精确匹配（如 {@code /health}）性能优于通配符匹配（如 {@code /api/**}）。</li>
     * </ul>
     *
     * @param requestUrl      请求 URL，例如 {@code /api/users/123}。可以为 {@code null}（返回 {@code false}）。
     * @param excludePatterns 排除模式列表，支持 Ant 风格通配符。可以为 {@code null}（返回 {@code false}）。
     * @return 如果请求 URL 匹配排除模式列表中的任一模式，返回 {@code true}；
     * 否则返回 {@code false}。
     */
    public static boolean isUrlExcluded(@Nullable String requestUrl, @Nullable List<String> excludePatterns) {
        // 边界条件：请求 URL 为空
        if (requestUrl == null || requestUrl.trim().isEmpty()) {
            return false;
        }

        // 边界条件：排除模式列表为空
        if (excludePatterns == null || excludePatterns.isEmpty()) {
            return false;
        }

        // 遍历排除模式列表，检查是否匹配
        for (String pattern : excludePatterns) {
            // 跳过 null 模式
            if (pattern == null) {
                continue;
            }

            // 使用 AntPathMatcher 进行匹配
            if (PATH_MATCHER.match(pattern, requestUrl)) {
                return true;  // 提前返回：找到匹配即停止
            }
        }

        return false;
    }

    /**
     * 判断请求 URL 是否<b>不</b>匹配排除模式列表中的任一模式。
     * <p>
     * 该方法为 {@link #isUrlExcluded(String, List)} 的语义反转版本，
     * 提供更符合某些业务场景的命名方式。
     *
     * @param requestUrl      请求 URL，例如 {@code /api/users/123}。可以为 {@code null}（返回 {@code true}）。
     * @param excludePatterns 排除模式列表，支持 Ant 风格通配符。可以为 {@code null}（返回 {@code true}）。
     * @return 如果请求 URL <b>不</b>匹配排除模式列表中的任一模式，返回 {@code true}；
     * 否则返回 {@code false}。
     */
    public static boolean isUrlNotExcluded(@Nullable String requestUrl, @Nullable List<String> excludePatterns) {
        return !isUrlExcluded(requestUrl, excludePatterns);
    }

    /**
     * 判断请求 URL 是否匹配单个排除模式。
     * <p>
     * 该方法为单模式匹配的便捷方法，适用于只需检查一个模式的场景。
     *
     * @param requestUrl     请求 URL，例如 {@code /api/users/123}。可以为 {@code null}（返回 {@code false}）。
     * @param excludePattern 排除模式，支持 Ant 风格通配符。可以为 {@code null}（返回 {@code false}）。
     * @return 如果请求 URL 匹配排除模式，返回 {@code true}；
     * 否则返回 {@code false}。
     */
    public static boolean matchPattern(@Nullable String requestUrl, @Nullable String excludePattern) {
        // 边界条件检查
        if (requestUrl == null || requestUrl.trim().isEmpty() || excludePattern == null) {
            return false;
        }

        // 使用 AntPathMatcher 进行匹配
        return PATH_MATCHER.match(excludePattern, requestUrl);
    }

    /**
     * 获取 AntPathMatcher 实例。
     * <p>
     * 提供对内部 {@link AntPathMatcher} 实例的访问，用于高级自定义匹配场景。
     *
     * <p>
     * NOTICE:
     * <ul>
     *     <li>返回的实例为共享的静态实例，线程安全。</li>
     *     <li>请勿修改返回实例的配置（如 {@code setCaseSensitive}），以免影响其他调用者。</li>
     * </ul>
     *
     * @return {@link AntPathMatcher} 实例，永不为 {@code null}。
     */
    @Contract(pure = true)
    public static @NotNull AntPathMatcher getPathMatcher() {
        return PATH_MATCHER;
    }
}
