package com.xlf.utility.mvc.aspect;

import com.xlf.utility.mvc.annotations.DubboPersistentContext;
import com.xlf.utility.mvc.holder.ContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Dubbo 上下文持久化切面处理器
 * <p>
 * 通过反射从参数中提取 getCtx() 返回值作为上下文ID，避免对具体请求类型产生硬依赖。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@SuppressWarnings("unused")
public class DubboContextAspect {

    @Around("@within(com.xlf.utility.mvc.annotations.DubboPersistentContext) " +
            "|| @annotation(com.xlf.utility.mvc.annotations.DubboPersistentContext)")
    public Object aroundDubboMethod(@NotNull ProceedingJoinPoint joinPoint) throws Throwable {
        String contextId = extractContextIdFromArguments(joinPoint);
        if (contextId == null) {
            log.debug("Dubbo方法未找到上下文参数，直接执行 - 方法: {}", getMethodName(joinPoint));
            return joinPoint.proceed();
        }
        try {
            ContextHolder.initContext(contextId);
            MDC.put("CONTEXT_ID", contextId);
            if (log.isDebugEnabled()) {
                log.debug("Dubbo调用上下文已设置 - 方法: {}, 上下文: {}", getMethodName(joinPoint), contextId);
            }
            return joinPoint.proceed();
        } finally {
            ContextHolder.clear();
            MDC.remove("CONTEXT_ID");
        }
    }

    private @Nullable String extractContextIdFromArguments(@NotNull ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length == 0) {
            log.warn("Dubbo方法缺少参数，无法提取上下文ID - 方法: {}", getMethodName(joinPoint));
            return null;
        }

        for (Object arg : args) {
            if (arg == null) {
                continue;
            }
            try {
                Method method = arg.getClass().getMethod("getCtx");
                Object value = method.invoke(arg);
                if (value instanceof String ctx && !ctx.trim().isEmpty()) {
                    return ctx.trim();
                }
            } catch (NoSuchMethodException ignored) {
                // not a context carrier
            } catch (Exception e) {
                log.debug("提取上下文ID异常: {}", e.getMessage());
            }
        }

        log.warn("Dubbo方法未找到可提取上下文ID的参数 - 方法: {}", getMethodName(joinPoint));
        return null;
    }

    @NotNull
    private String getMethodName(@NotNull ProceedingJoinPoint joinPoint) {
        org.aspectj.lang.reflect.MethodSignature signature = (org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        return targetClass.getSimpleName() + "." + signature.getName();
    }
}
