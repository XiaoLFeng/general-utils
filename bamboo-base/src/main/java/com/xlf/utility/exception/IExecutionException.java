package com.xlf.utility.exception;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

/**
 * 执行异常处理接口
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public interface IExecutionException {

    /**
     * 处理 ExecutionException 异常
     *
     * @param exception ExecutionException 异常对象
     * @return 标准化异常响应
     */
    Object handleRpcException(@NotNull ExecutionException exception);
}
