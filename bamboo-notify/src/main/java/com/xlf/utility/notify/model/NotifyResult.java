package com.xlf.utility.notify.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知结果模型
 * <p>
 * 用于封装通知发送的结果信息，包括是否成功、错误信息等。
 * </p>
 *
 * @author xiao_lfeng
 * @since 2.0.0-beta1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyResult {

    /**
     * 是否发送成功
     */
    private boolean success;

    /**
     * 结果消息
     * <p>
     * 如果发送成功，该字段包含成功消息；
     * 如果发送失败，该字段包含错误信息。
     * </p>
     */
    private String message;

    /**
     * 发送时间
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * 错误码
     * <p>
     * 如果发送失败，该字段包含错误码；
     * 如果发送成功，该字段为 null。
     * </p>
     */
    private String errorCode;

    /**
     * 创建成功结果
     *
     * @param message 成功消息
     * @return 成功结果
     */
    public static NotifyResult success(String message) {
        return NotifyResult.builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败结果
     *
     * @param message 失败消息
     * @return 失败结果
     */
    public static NotifyResult failure(String message) {
        return NotifyResult.builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败结果（带错误码）
     *
     * @param message   失败消息
     * @param errorCode 错误码
     * @return 失败结果
     */
    public static NotifyResult failure(String message, String errorCode) {
        return NotifyResult.builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
