package com.xlf.utility.triple.exception;

import com.xlf.utility.ErrorCode;
import com.xlf.utility.exception.IExecutionException;
import com.xlf.utility.triple.TripleResponse;
import com.xlf.utility.triple.TripleResult;
import org.apache.dubbo.rpc.StatusRpcException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * 自定义执行异常处理器
 * <p>
 * 用于处理 {@link ExecutionException} 类型的异常，特别针对 Dubbo RPC 调用中的异常进行特殊处理。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public class CustomExecutionException implements IExecutionException {
    private static final Logger log = LoggerFactory.getLogger(CustomExecutionException.class);

    /**
     * 处理 ExecutionException 异常
     * <p>
     * 如果异常原因是 Dubbo 的 StatusRpcException，则提取 RPC 异常信息进行特殊处理。
     *
     * @param exception ExecutionException 实例
     * @return TripleResponse 响应对象
     */
    @Override
    public TripleResponse<Void> handleRpcException(@NotNull ExecutionException exception) {
        if (exception.getCause() != null) {
            if (exception.getCause() instanceof StatusRpcException rpcException) {
                log.error("RPC 异常 | [{}]{} ", rpcException.getCode(), rpcException.getMessage(), rpcException);
                return TripleResult.error(ErrorCode.SERVER_INTERNAL_ERROR, rpcException.getMessage(), null);
            }
        }
        log.error("执行异常 | {} ", exception.getMessage(), exception);
        return TripleResult.error(ErrorCode.SERVER_INTERNAL_ERROR, exception.getMessage(), null);
    }
}
