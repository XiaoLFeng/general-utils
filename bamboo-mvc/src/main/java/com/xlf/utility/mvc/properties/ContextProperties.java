package com.xlf.utility.mvc.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * MVC 上下文管理配置属性
 * <p>
 * 配置前缀：bamboo.context
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Data
@ConfigurationProperties(prefix = "bamboo.context")
public class ContextProperties {

    /**
     * 是否允许从外部 Header 传入 UUID
     */
    private boolean enableInput = true;

    /**
     * 排除的 URL 列表（不生成上下文）
     */
    private List<String> excludeUrls = new ArrayList<>();
}
