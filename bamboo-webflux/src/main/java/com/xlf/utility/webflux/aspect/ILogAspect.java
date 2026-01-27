package com.xlf.utility.webflux.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.jetbrains.annotations.NotNull;

/**
 * 业务日志切面接口（WebFlux版本）
 * <p>
 * 定义系统中业务日志记录的标准行为，支持记录控制器、服务和 DAO 层操作日志。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public interface ILogAspect {

    /**
     * 控制器日志
     */
    Object beforeControllerLog(@NotNull ProceedingJoinPoint joinPoint) throws Throwable;

    /**
     * 服务日志
     */
    Object beforeServiceLog(@NotNull ProceedingJoinPoint joinPoint) throws Throwable;

    /**
     * DAO 日志
     */
    Object beforeDaoLog(@NotNull ProceedingJoinPoint pjp) throws Throwable;

    /**
     * 输出调试数据
     */
    void showDebugData(@NotNull JoinPoint joinPoint);
}
