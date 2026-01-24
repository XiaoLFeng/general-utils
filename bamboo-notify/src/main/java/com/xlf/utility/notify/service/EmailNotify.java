package com.xlf.utility.notify.service;

import com.xlf.utility.notify.model.NotifyMessage;
import com.xlf.utility.notify.model.NotifyResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 邮件通知服务实现（占位）
 * <p>
 * 提供基于邮件的通知功能。
 * </p>
 *
 * @author xiao_lfeng
 * @since 2.0.0-beta1
 */
public class EmailNotify implements NotifyService {

    private final Logger logger = LoggerFactory.getLogger(EmailNotify.class);

    @Override
    public NotifyResult send(NotifyMessage message) {
        // TODO: 实现邮件发送逻辑
        this.logger.warn("Email notification not implemented yet. Message: {}", message);
        return NotifyResult.failure("Email notification not implemented yet");
    }

    @Override
    public void sendAsync(NotifyMessage message) {
        // TODO: 实现异步邮件发送逻辑
        this.logger.warn("Async email notification not implemented yet. Message: {}", message);
    }
}
