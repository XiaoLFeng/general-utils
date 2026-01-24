package com.xlf.utility.utility;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * 权限匹配工具类。
 * <p>
 * 提供权限校验工具方法，用于判断用户是否具备执行某操作的权限。
 * 支持多级权限结构匹配及通配符规则（例如 {@code "*"}）。
 *
 * <p>
 * NOTICE:
 * <ul>
 *     <li>此类为工具类，禁止实例化。</li>
 *     <li>所有方法均为静态方法。</li>
 * </ul>
 *
 * @author xiao_lfeng
 * @version v1.0.5-SNAPSHOT
 * @since v1.0.5-SNAPSHOT
 */
@SuppressWarnings("unused")
public final class PermissionMatchUtil {

    private static final String DELIMITER = ":";
    private static final String WILDCARD = "*";

    @Contract(value = " -> fail", pure = true)
    private PermissionMatchUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 检查用户是否拥有指定的权限。
     * <p>
     * 该方法通过匹配用户已有的权限集和所需权限来判断用户是否具备执行某操作的权限。
     * 如果用户权限与所需权限完全匹配，或支持通配符规则匹配，则返回 {@code true}。
     *
     * <p>
     * NOTICE:
     * <ul>
     *     <li>方法中使用通配符支持权限匹配，例如{@code "read:*"}可以匹配{@code "read:books"}。</li>
     *     <li>参数{@code userPermissions}和{@code requiredPermission}均不可为 {@code null}。</li>
     *     <li>空的权限集（即{@code userPermissions}为空集合或{@code requiredPermission}为空字符串）将返回 {@code false}。</li>
     * </ul>
     *
     * @param userPermissions    用户的权限集合。每一项为一个权限标识，例如{@code "read:books"}或
     *                           {@code "write:docs"}。不得为空，且每项需为非空有效字符串。
     * @param requiredPermission 所需的权限标识。指定用户操作所需的权限，可以包括通配符，例如
     *                           {@code "write:*"}。不得为 {@code null} 或空字符串。
     * @return 如果用户权限集中存在匹配所需权限的权限项，返回 {@code true}；否则返回 {@code false}。
     * 特别是当用户权限集合为 {@code null} 或所需权限为 {@code null} 时，也会返回 {@code false}。
     */
    public static boolean hasPermission(Set<String> userPermissions, String requiredPermission) {
        return Optional.ofNullable(requiredPermission)
                .flatMap(req -> Optional.ofNullable(userPermissions)
                        .flatMap(perms -> perms.stream()
                                .filter(user -> PermissionMatchUtil.isWildcardMatch(user, req))
                                .findFirst())
                ).isPresent();
    }

    /**
     * 判断两个权限标识符是否匹配。
     * <p>
     * 此方法用于对比用户权限标识符与所需权限标识符，支持通过通配符（例如 {@code "*"}）
     * 实现的模糊匹配。如果两者完全相同，或符合通配符规则，则返回 {@code true}，否则返回 {@code false}。
     *
     * <p>
     * NOTICE:
     * <ul>
     *     <li>形参 {@code userPerm} 和 {@code requiredPerm} 均不得为 {@code null}，否则可能引发空指针异常。</li>
     *     <li>两个权限标识符需以冒号 {@code ":"} 分隔不同的权限层级，例如 {@code "read:books"}。</li>
     *     <li>通配符 {@code "*"} 可位于权限标识符中任意层级，用于匹配任意内容。例如：
     *         {@code "read:*"} 可匹配 {@code "read:books"} 或 {@code "read:articles"}。</li>
     *     <li>权限层级的数量必须一致，否则始终返回 {@code false}。</li>
     * </ul>
     *
     * @param userPerm     用户权限标识符。例如 {@code "read:books"}。
     *                     此参数不得为 {@code null}，且必须是非空有效字符串。
     * @param requiredPerm 所需权限标识符。例如 {@code "read:*"}。
     *                     此参数不得为 {@code null}，且必须是非空有效字符串。
     * @return 如果用户权限标识符与所需权限标识符匹配，返回 {@code true}；
     * 否则返回 {@code false}。
     */
    private static boolean isWildcardMatch(@NotNull String userPerm, String requiredPerm) {
        if (userPerm.equals(requiredPerm)) {
            return true;
        }

        String[] userParts = userPerm.split(DELIMITER);
        String[] requiredParts = requiredPerm.split(DELIMITER);

        return userParts.length == requiredParts.length &&
                IntStream.range(0, userParts.length)
                        .allMatch(i -> WILDCARD.equals(requiredParts[i]) || requiredParts[i].equals(userParts[i]));
    }
}
