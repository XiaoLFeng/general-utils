package com.xlf.utility.config.filter;

import com.xlf.utility.ErrorCode;
import com.xlf.utility.ResultUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 禁止空 referer 过滤器
 * <p>
 * 该类用于配置禁止空 referer 过滤器；
 * 该类使用 {@code OncePerRequestFilter} 类继承。
 *
 * @since v1.0.9-beta.1.0
 * @version v1.0.9-beta.1.0
 * @author xiao_lfeng
 */
@SuppressWarnings("unused")
public class BanEmptyReferer extends OncePerRequestFilter {
    private final boolean isJsonOutput;

    public BanEmptyReferer(boolean isJsonOutput) {
        this.isJsonOutput = isJsonOutput;
    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        // 禁止空 referer
        if (request.getHeader("Referer") == null || "".equals(request.getHeader("Referer"))) {
            if (isJsonOutput) {
                ResultUtil.error(ErrorCode.FORBIDDEN, "空 Referer 不被允许", request);
            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
