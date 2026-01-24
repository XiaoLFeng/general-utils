package com.xlf.utility.notify.service;

import com.xlf.utility.notify.model.NotifyMessage;
import com.xlf.utility.notify.model.NotifyResult;

/**
 * 通知服务接口
 * <p>
 * 定义了发送通知的基本方法，支持多种通知类型（邮件、Webhook、短信等）。
 * </p>
 *
 * @author xiao_lfeng
 * @since 2.0.0-beta1
 */
public interface NotifyService {

    /**
     * 发送通知
     * <p>
     * 根据消息类型自动选择对应的通知方式发送通知。
     * </p>
     *
     * @param message 通知消息
     * @return 发送结果
     */
    NotifyResult send(NotifyMessage message);

    /**
     * 发送异步通知
     * <p>
     * 异步发送通知，立即返回，不阻塞调用线程。
     * </p>
     *
     * @param message 通知消息
     */
    void sendAsync(NotifyMessage message);
}
