package com.xlf.utility.exception;

import org.apache.dubbo.rpc.RpcException;

/**
 * Dubbo异常处理接口
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public interface IDubboException {

    /**
     * 处理 RpcException 异常
     *
     * @param exception RpcException 实例
     * @return 处理结果对象
     */
    Object handleRpcException(RpcException exception);
}
