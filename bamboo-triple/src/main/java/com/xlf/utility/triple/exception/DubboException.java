package com.xlf.utility.triple.exception;

import com.xlf.utility.ErrorCode;
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
public class DubboException {
    private static final Logger log = LoggerFactory.getLogger(DubboException.class);

    /**
     * 处理 Dubbo RPC 调用异常
     * <p>
     * 该方法用于捕获和处理 Dubbo 服务调用过程中产生的 {@code RpcException} 异常。
     * 它会记录异常的详细日志（包含错误码和消息），并将其转换为统一的 {@code TripleResponse}
     * 错误响应对象，以保证 API 返回格式的一致性。默认返回服务器内部错误状态码。
     * <p>
     * 内部处理流程：
     * <ul>
     * <li>记录 ERROR 级别日志，包含异常码、信息及堆栈。</li>
     * <li>调用 {@code TripleResult.error} 生成标准错误响应。</li>
     * </ul>
     * <p>
     * NOTICE:
     * 该方法直接使用异常消息作为返回消息，请注意确保不包含敏感信息。
     * <p>
     * 捕获的 RPC 异常对象，包含错误码和详细信息
     *
     * @return 返回包装后的错误响应对象 {@link TripleResponse}，状态码为
     * {@code ErrorCode.SERVER_INTERNAL_ERROR}，数据体为 {@code null}
     */
    public TripleResponse<Void> handleRpcException(RpcException exception) {
        log.error("RPC 异常 | [{}]{} ", exception.getCode(), exception.getMessage(), exception);
        return TripleResult.error(ErrorCode.SERVER_INTERNAL_ERROR, exception.getMessage(), null);
    }
}
