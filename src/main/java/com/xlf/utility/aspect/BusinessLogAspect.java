package com.xlf.utility.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 业务日志切面
 * <p>
 * 用于记录业务日志，该切面用于记录业务日志。
 *
 * @author xiao_lfeng
 * @version 1.0.9-beta.1.1
 * @since 1.0.9-beta.1.1
 */
@SuppressWarnings("unused")
public class BusinessLogAspect {

    /**
     * 日志记录器
     */
    private static final Logger log = LoggerFactory.getLogger(BusinessLogAspect.class);

    /**
     * 控制器日志
     * <p>
     * 用于记录控制器日志，该方法用于记录控制器日志
     *
     * @param joinPoint 切入点
     */
    public void beforeControllerLog(@NotNull JoinPoint joinPoint) {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            // 获取方法签名
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Class<?> declaringType = methodSignature.getDeclaringType();
            String methodName = methodSignature.getName();

            log.info("[CONT] 执行 {}:{} 接口", declaringType.getName(), methodName);
            log.info("\t\t地址: [{}]{}", request.getMethod(), request.getServletPath());
        } else {
            throw new RuntimeException("无法获取信息");
        }
    }

    /**
     * 服务日志
     * <p>
     * 用于记录服务日志，该方法用于记录服务日志
     *
     * @param joinPoint 切入点
     */
    public void beforeServiceLog(@NotNull JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class<?> declaringType = methodSignature.getDeclaringType();
        String methodName = methodSignature.getName();

        log.info("[SERV] 执行 {}:{} 业务", declaringType.getName(), methodName);
    }

    /**
     * DAO 日志
     * <p>
     * 用于记录 DAO 日志，该方法用于记录 DAO 日志
     *
     * @param pjp 切入点
     * @throws Throwable 异常
     */
    public void beforeDaoLog(@NotNull ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Class<?> declaringType = methodSignature.getDeclaringType();
        String methodName = methodSignature.getName();
        Object[] args = pjp.getArgs();

        log.info("==>[DAO] 操作 {}:{} 记录", declaringType.getName(), methodName);

        if (args.length > 0) {
            log.debug("\t> 传入信息:");
            for (Object arg : args) {
                log.debug("\t\t> 参数: {}", arg);
            }
        }

        Object result = pjp.proceed();
        log.info("<==[DAO] 返回数据类型 {}", declaringType.getName());
        if (result != null) {
            log.debug("\t> 传出信息：{}", result);
        }
    }
}
