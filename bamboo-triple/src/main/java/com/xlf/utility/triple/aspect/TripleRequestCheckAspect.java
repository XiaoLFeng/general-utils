package com.xlf.utility.triple.aspect;

import com.xlf.utility.ErrorCode;
import com.xlf.utility.exception.library.BusinessException;
import com.xlf.utility.triple.TripleRequest;
import com.xlf.utility.triple.annotations.TripleRequestCheck;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Triple Request 参数校验切面处理器
 * <p>
 * 该切面专门用于处理 TripleRequest 参数的自动校验功能。
 * 通过拦截标记了 {@link TripleRequestCheck} 注解的方法或类，
 * 自动对 TripleRequest 参数执行校验逻辑，确保只有合法的请求才能执行业务逻辑。
 * <p>
 * 主要功能：
 * <ul>
 *   <li>自动校验 TripleRequest 参数的完整性</li>
 *   <li>支持类级别和方法级别的注解控制</li>
 *   <li>校验失败时抛出标准的 BusinessException 异常</li>
 *   <li>支持自定义错误消息和错误码</li>
 *   <li>详细的调试信息用于问题定位</li>
 * </ul>
 * <p>
 * 处理流程：
 * <ol>
 *   <li>检查方法参数结构是否符合要求</li>
 *   <li>提取第一个 TripleRequest 参数</li>
 *   <li>调用 {@code request.validate()} 执行校验</li>
 *   <li>校验失败时抛出 BusinessException</li>
 *   <li>校验通过则继续执行目标方法</li>
 * </ol>
 * <p>
 * 使用示例：
 * <pre>
 * // 方法级别注解
 * &#64;TripleRequestCheck
 * public TripleResponse&lt;UserInfo&gt; getUser(UserRequest request) {
 *     // request 会自动校验，只有通过才会执行到这里
 *     return TripleResult.success(userInfo);
 * }
 *
 * // 类级别注解
 * &#64;TripleRequestCheck
 * &#64;Service
 * public class UserServiceImpl implements UserService {
 *     // 所有方法都会自动校验 TripleRequest 参数
 * }
 * </pre>
 * <p>
 * <strong>重要约定：</strong>
 * <ul>
 *   <li>方法的任意一个参数必须是 {@link TripleRequest} 或其子类</li>
 *   <li>TrippleRequest 子类必须实现 {@code validate()} 方法</li>
 *   <li>校验失败时会抛出 BusinessException，上层需要正确处理</li>
 * </ul>
 * <p>
 * NOTICE: 此切面优先级较高，确保在校验完成后再执行其他切面逻辑。
 * 与 {@link DubboContextAspect} 配合使用时，校验切面会在上下文传递之前执行。
 *
 * @author xiao_lfeng
 * @version v1.1.6-SNAPSHOT
 * @since v1.1.6-SNAPSHOT
 */
@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@SuppressWarnings("unused")
public class TripleRequestCheckAspect {

    /**
     * 拦截标记了 @TripleRequestCheck 注解的方法调用
     * <p>
     * 该切面方法会在 TripleRequest 参数校验通过后执行目标方法。
     * 支持类级别和方法级别的注解拦截。
     * <p>
     * 处理流程：
     * <ol>
     *   <li>检查方法参数结构</li>
     *   <li>提取 TripleRequest 参数</li>
     *   <li>执行参数校验</li>
     *   <li>校验失败抛出 BusinessException</li>
     *   <li>校验通过执行目标方法</li>
     * </ol>
     *
     * @param joinPoint          切点连接点，包含目标方法信息
     * @param tripleRequestCheck 注解实例，包含自定义配置
     * @return 目标方法的执行结果
     * @throws Throwable 目标方法执行时可能抛出的任何异常
     */
    @Around("@within(tripleRequestCheck) || @annotation(tripleRequestCheck)")
    public Object aroundTripleRequestMethod(
            @NotNull ProceedingJoinPoint joinPoint,
            TripleRequestCheck tripleRequestCheck
    ) throws Throwable {
        TripleRequest tripleRequest = this.extractTripleRequestFromMethod(joinPoint);
        if (tripleRequest == null) {
            log.debug("TripleRequest校验跳过 - 方法: {}, 参数不符合要求", this.getMethodName(joinPoint));
            return joinPoint.proceed();
        }
        this.validateTripleRequest(tripleRequest, joinPoint, tripleRequestCheck);
        log.debug("TripleRequest校验通过 - 方法: {}, 请求摘要: {}", this.getMethodName(joinPoint), tripleRequest.getSummary());
        return joinPoint.proceed();
    }

    /**
     * 从方法参数中提取 TripleRequest 对象
     * <p>
     * 检查方法参数结构，提取任意一个 TripleRequest 参数。
     * 如果参数结构不符合要求，会记录日志并返回 null。
     *
     * @param joinPoint 切点连接点
     * @return 提取到的 TripleRequest 对象，如果参数结构不符合要求则返回 null
     */
    private @Nullable TripleRequest extractTripleRequestFromMethod(@NotNull ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        // 检查参数数量
        if (args.length == 0) {
            log.debug("TripleRequest校验跳过 - 方法: {}, 无参数", this.getMethodName(joinPoint));
            return null;
        }

        // 遍历所有参数，查找第一个 TripleRequest 类型的参数
        for (Object arg : args) {
            if (arg instanceof TripleRequest tripleRequest) {
                return tripleRequest;
            }
        }

        // 未找到 TripleRequest 参数
        log.debug("TripleRequest校验跳过 - 方法: {}, 未找到TripleRequest类型参数", this.getMethodName(joinPoint));
        return null;
    }

    /**
     * 执行 TripleRequest 参数校验
     * <p>
     * 调用 TripleRequest 的 validate() 方法进行校验，
     * 如果校验失败，会抛出 BusinessException 异常。
     *
     * @param tripleRequest      TripleRequest 对象
     * @param joinPoint          切点连接点
     * @param tripleRequestCheck 注解实例，包含自定义配置
     * @throws BusinessException 校验失败时抛出
     */
    private void validateTripleRequest(
            @NotNull TripleRequest tripleRequest,
            @NotNull ProceedingJoinPoint joinPoint,
            @NotNull TripleRequestCheck tripleRequestCheck
    ) throws BusinessException {
        try {
            // 调用 TripleRequest 的校验方法
            boolean isValid = tripleRequest.validate();
            if (!isValid) {
                // 校验失败，抛出 BusinessException
                throw this.createValidationException(tripleRequest, joinPoint, tripleRequestCheck);
            }
        } catch (BusinessException e) {
            // 重新抛出 BusinessException
            throw e;
        } catch (Exception e) {
            // 校验过程中发生异常，创建 BusinessException
            log.error("TripleRequest校验过程中发生异常 - 方法: {}, 异常: {}",
                    this.getMethodName(joinPoint), e.getMessage(), e);
            throw this.createValidationException(tripleRequest, joinPoint, tripleRequestCheck);
        }
    }

    /**
     * 创建校验失败的 BusinessException
     * <p>
     * 根据注解配置和当前上下文创建合适的 BusinessException 异常对象。
     *
     * @param tripleRequest      TripleRequest 对象
     * @param joinPoint          切点连接点
     * @param tripleRequestCheck 注解实例
     * @return BusinessException 异常对象
     */
    @NotNull
    @Contract("_, _, _ -> new")
    private BusinessException createValidationException(
            @NotNull TripleRequest tripleRequest,
            @NotNull ProceedingJoinPoint joinPoint,
            @NotNull TripleRequestCheck tripleRequestCheck
    ) {
        // 构建错误消息
        String errorMessage = this.buildErrorMessage(tripleRequest, joinPoint, tripleRequestCheck);

        // 获取错误码
        ErrorCode errorCode = tripleRequestCheck.errorCode();

        // 创建 BusinessException
        if (tripleRequestCheck.message().isEmpty()) {
            // 使用默认错误消息
            return new BusinessException(errorMessage, errorCode, tripleRequest.getSummary());
        } else {
            // 使用自定义错误消息
            return new BusinessException(tripleRequestCheck.message(), errorCode, tripleRequest.getSummary());
        }
    }

    /**
     * 构建校验失败的错误消息
     * <p>
     * 根据配置和上下文信息构建详细的错误消息，便于问题定位。
     *
     * @param tripleRequest      TripleRequest 对象
     * @param joinPoint          切点连接点
     * @param tripleRequestCheck 注解实例
     * @return 构建的错误消息
     */
    @NotNull
    private String buildErrorMessage(
            @NotNull TripleRequest tripleRequest,
            @NotNull ProceedingJoinPoint joinPoint,
            @NotNull TripleRequestCheck tripleRequestCheck
    ) {
        String methodInfo = this.getMethodName(joinPoint);
        String requestSummary = tripleRequest.getSummary();

        if (!tripleRequestCheck.message().isEmpty()) {
            // 使用自定义错误消息
            return String.format("%s (方法: %s, 请求: %s)",
                    tripleRequestCheck.message(), methodInfo, requestSummary);
        } else {
            // 使用默认错误消息
            return String.format("TripleRequest参数校验失败 (方法: %s, 请求: %s)",
                    methodInfo, requestSummary);
        }
    }

    /**
     * 获取方法名称用于日志输出
     * <p>
     * 格式化为 "类名.方法名" 的形式，便于日志查看和调试。
     *
     * @param joinPoint 切点连接点
     * @return 格式化的方法名称
     */
    @NotNull
    private String getMethodName(@NotNull ProceedingJoinPoint joinPoint) {
        org.aspectj.lang.reflect.MethodSignature signature = (org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        return targetClass.getSimpleName() + "." + signature.getName();
    }
}
