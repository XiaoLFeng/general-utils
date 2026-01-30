package com.xlf.utility.triple.exception;

import com.xlf.utility.ErrorCode;
import com.xlf.utility.exception.IDubboException;
import com.xlf.utility.triple.TripleResponse;
import com.xlf.utility.triple.TripleResult;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dubbo异常处理类
 * <p>
 * 该类用于处理 Dubbo RPC 框架中的异常，特别是 {@code RpcException} 类型的异常。
 * 它通过实现 {@code IDubboException} 接口，定义了统一的异常处理逻辑，增强系统在分布式
 * 环境下的异常处理能力及容错设计。
 * 其提供了一套规范的日志记录与错误响应输出机制，能够有效提升异常处理的一致性。
 *
 * <p>
 * 使用场景包括但不限于在基于 Dubbo 的分布式服务架构中处理因 RPC 调用导致的异常。
 * 实现中包括将异常转化为标准化响应对象，并记录关联日志，未成功调用 RPC 的情况下尤为适用。
 *
 * <p>
 * NOTICE:
 * <ul>
 * <li>仔细处理异常时的返回逻辑，尤其是基于 {@code ErrorCode} 映射结果的状态码，确保
 * 响应符合项目设计的规范。</li>
 * <li>务必避免对未明确处理需求的异常进行全局捕获，这可能导致逻辑链断裂。</li>
 * <li>当捕获到特定的 RPC 异常（如超时，请求失败等）时，务必对日志记录堆栈进行完整分析和正确的分类处理。</li>
 * </ul>
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public class DubboException implements IDubboException {
    private static final Logger log = LoggerFactory.getLogger(DubboException.class);

    @Override
    public TripleResponse<Void> handleRpcException(RpcException exception) {
        log.error("RPC 异常 | [{}]{} ", exception.getCode(), exception.getMessage(), exception);
        return TripleResult.error(ErrorCode.SERVER_INTERNAL_ERROR, exception.getMessage(), null);
    }
}
