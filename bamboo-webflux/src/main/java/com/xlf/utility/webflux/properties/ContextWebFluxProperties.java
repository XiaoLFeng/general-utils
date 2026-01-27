package com.xlf.utility.webflux.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * WebFlux 上下文配置属性类
 * <p>
 * 该类用于配置 WebFlux 响应式环境下的上下文行为，
 * 通过 {@code utility.context.webflux} 前缀的配置项进行控制。
 * <p>
 * 主要配置项：
 * - enableInput: 是否从请求头接收上下文ID（微服务间传递）
 * - enableOutput: 是否向下游服务传递上下文ID（Gateway模式）
 * - gatewayMode: 是否启用Gateway模式（影响日志输出级别）
 * - excludeUrls: 排除的URL列表（不生成上下文）
 * <p>
 * 配置示例：
 * <pre>
 * utility:
 *   context:
 *     webflux:
 *       enable-input: true
 *       enable-output: true
 *       gateway-mode: false
 *       exclude-urls:
 *         - /actuator/**
 *         - /health
 * </pre>
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Data
@ConfigurationProperties(prefix = "utility.context.webflux")
@SuppressWarnings("unused")
public class ContextWebFluxProperties {

    /**
     * 是否从请求头接收外部上下文ID
     * <p>
     * 当设置为 true 时，ContextFilter 会从请求头 {@code X-Context-UUID} 中提取上下文ID。
     * 适用于微服务间传递上下文的场景。
     * <p>
     * 默认值: true
     */
    private boolean enableInput = true;

    /**
     * 是否向下游服务传递上下文ID
     * <p>
     * 当设置为 true 时，ContextFilter 会将上下文ID写入请求头，传递给下游微服务。
     * 适用于Gateway网关场景。
     * <p>
     * 默认值: true
     */
    private boolean enableOutput = true;

    /**
     * 是否启用Gateway模式
     * <p>
     * 当设置为 true 时，ContextFilter 会使用 INFO 级别日志记录上下文创建信息。
     * 当设置为 false 时，使用 DEBUG 级别。
     * <p>
     * 默认值: false
     */
    private boolean gatewayMode = false;

    /**
     * 排除的URL列表
     * <p>
     * 配置在此列表中的URL不会生成上下文，直接放行。
     * 支持Ant风格路径匹配（*, **）。
     * <p>
     * 示例：
     * <ul>
     *   <li>/actuator/** - 排除所有actuator端点</li>
     *   <li>/health - 排除健康检查接口</li>
     *   <li>/public/* - 排除public目录下所有接口</li>
     * </ul>
     * <p>
     * 默认值: 空列表（不排除任何URL）
     */
    private List<String> excludeUrls = new ArrayList<>();
}
