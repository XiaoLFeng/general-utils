package com.xlf.utility.notify.template;

/**
 * 模板引擎接口（占位）
 * <p>
 * 用于生成通知消息模板。
 * </p>
 *
 * @author xiao_lfeng
 * @since 2.0.0-beta1
 */
public interface TemplateEngine {

    /**
     * 渲染模板
     *
     * @param templateName 模板名称
     * @param variables    模板变量
     * @return 渲染后的内容
     */
    String render(String templateName, java.util.Map<String, Object> variables);
}
