package com.xlf.utility.notify.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 通知消息模型
 * <p>
 * 用于封装通知消息的基本信息，包括接收者、主题、内容等。
 * </p>
 *
 * @author xiao_lfeng
 * @since 2.0.0-beta1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyMessage {

    /**
     * 接收者地址
     * <p>
     * 根据通知类型的不同，该字段可以是：
     * - 邮箱地址
     * - Webhook URL
     * - 手机号码
     * </p>
     */
    private String to;

    /**
     * 消息主题
     * <p>
     * 消息的标题或主题行，用于快速描述消息内容。
     * </p>
     */
    private String subject;

    /**
     * 消息内容
     * <p>
     * 消息的详细内容，支持纯文本或 HTML 格式。
     * </p>
     */
    private String content;

    /**
     * 消息类型
     * <p>
     * 指定通知的类型，如 EMAIL、WEBHOOK、SMS 等。
     * </p>
     */
    private NotifyType type;

    /**
     * 模板变量
     * <p>
     * 如果使用模板引擎，该字段包含模板变量的键值对。
     * </p>
     */
    private Map<String, Object> variables;

    /**
     * 优先级
     * <p>
     * 消息的优先级，范围从 1（低）到 5（高），默认为 3。
     * </p>
     */
    @Builder.Default
    private int priority = 3;

    /**
     * 通知类型枚举
     */
    public enum NotifyType {
        /**
         * 邮件通知
         */
        EMAIL,
        /**
         * Webhook 通知
         */
        WEBHOOK,
        /**
         * 短信通知
         */
        SMS
    }
}
