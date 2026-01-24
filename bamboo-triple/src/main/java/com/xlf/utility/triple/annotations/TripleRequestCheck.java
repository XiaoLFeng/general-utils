package com.xlf.utility.triple.annotations;

import com.xlf.utility.ErrorCode;

import java.lang.annotation.*;

/**
 * Triple Request 参数校验注解
 * <p>
 * 此注解用于标记需要进行 TripleRequest 参数校验的 Dubbo 服务方法或类。
 * 当方法被调用时，会自动对 TripleRequest 参数执行校验逻辑，只有校验通过才会执行目标方法，
 * 否则会抛出 BusinessException 异常。
 * <p>
 * 主要功能：
 * <ul>
 *   <li>自动校验 TripleRequest 参数的完整性</li>
 *   <li>支持方法级别和类级别的注解控制</li>
 *   <li>校验失败时抛出标准的 BusinessException 异常</li>
 *   <li>支持自定义错误消息和错误码</li>
 *   <li>提供详细的调试信息用于问题定位</li>
 * </ul>
 * <p>
 * 使用示例：
 * <pre>
 * // 方法级别注解 - 仅特定方法需要校验
 * &#64;Service
 * public class UserServiceImpl implements UserService {
 *
 *     &#64;TripleRequestCheck
 *     public TripleResponse&lt;UserInfo&gt; getUser(UserRequest request) {
 *         // request 会自动调用 request.validate() 进行校验
 *         // 只有校验通过才会执行到这里
 *         return TripleResult.success(userInfo);
 *     }
 * }
 *
 * // 类级别注解 - 所有方法都进行校验
 * &#64;TripleRequestCheck
 * &#64;Service
 * public class OrderServiceImpl implements OrderService {
 *
 *     public TripleResponse&lt;OrderInfo&gt; create(OrderRequest request) {
 *         // 自动校验 request
 *         return TripleResult.success(orderInfo);
 *     }
 *
 *     public TripleResponse&lt;OrderInfo&gt; update(OrderRequest request) {
 *         // 自动校验 request
 *         return TripleResult.success(orderInfo);
 *     }
 * }
 *
 * // 带自定义错误消息
 * &#64;TripleRequestCheck(message = "用户查询参数不完整")
 * public TripleResponse&lt;UserInfo&gt; getUser(UserRequest request) {
 *     // 校验失败时会显示自定义错误消息
 *     return TripleResult.success(userInfo);
 * }
 *
 * // 带自定义错误码
 * &#64;TripleRequestCheck(errorCode = ErrorCode.PARAMETER_MISSING)
 * public TripleResponse&lt;UserInfo&gt; getUser(UserRequest request) {
 *     // 使用指定的错误码
 *     return TripleResult.success(userInfo);
 * }
 * </pre>
 * <p>
 * <strong>重要约定：</strong>使用此注解的方法必须满足：
 * <ul>
 *   <li>第一个参数必须是 {@link com.xlf.utility.triple.TripleRequest} 或其子类</li>
 *   <li>TrippleRequest 子类必须实现 {@code validate()} 方法</li>
 *   <li>确保调用方正确构造 TripleRequest 对象</li>
 * </ul>
 * <p>
 * NOTICE: 此注解仅在有 Dubbo 相关依赖时生效，与现有的 @DubboPersistentContext 注解
 * 可以同时使用，实现上下文传递和参数校验的完整功能。
 *
 * @author xiao_lfeng
 * @version v1.1.6-SNAPSHOT
 * @since v1.1.6-SNAPSHOT
 */
@Documented
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface TripleRequestCheck {

    /**
     * 自定义错误消息
     * <p>
     * 当 TripleRequest 校验失败时，会使用此消息作为 BusinessException 的错误信息。
     * 如果为空字符串，则会使用默认的错误消息格式。
     *
     * @return 自定义错误消息，默认为空字符串（使用默认消息）
     */
    String message() default "";

    /**
     * 指定校验失败时的错误码
     * <p>
     * 当 TripleRequest 校验失败时，会使用此错误码创建 BusinessException。
     * 默认使用 PARAMETER_INVALID 错误码，表示参数无效。
     *
     * @return 错误码，默认为 ErrorCode.PARAMETER_INVALID
     */
    ErrorCode errorCode() default ErrorCode.PARAMETER_INVALID;
}
