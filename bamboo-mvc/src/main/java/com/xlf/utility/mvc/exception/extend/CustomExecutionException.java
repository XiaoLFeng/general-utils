package com.xlf.utility.mvc.exception.extend;

import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.exception.IExecutionException;
import com.xlf.utility.mvc.ResultUtil;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.StatusRpcException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.concurrent.ExecutionException;

@SuppressWarnings("unused")
public class CustomExecutionException implements IExecutionException {
    private static final Logger log = LoggerFactory.getLogger(CustomExecutionException.class);


    @ExceptionHandler(ExecutionException.class)
    public ResponseEntity<BaseResponse<Void>> handleRpcException(@NotNull ExecutionException exception) {
        if (exception.getCause() != null) {
            if (exception.getCause() instanceof StatusRpcException rpcException) {
                log.error("RPC 异常 | [{}]{} ", rpcException.getCode(), rpcException.getMessage(), rpcException);
                return ResultUtil.error(ErrorCode.SERVER_INTERNAL_ERROR, rpcException.getMessage(), null);
            }
        }
        log.error("执行异常 | {} ", exception.getMessage(), exception);
        return ResultUtil.error(ErrorCode.SERVER_INTERNAL_ERROR, exception.getMessage(), null);
    }
}
