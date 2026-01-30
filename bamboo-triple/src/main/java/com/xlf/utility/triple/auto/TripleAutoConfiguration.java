package com.xlf.utility.triple.auto;

import com.xlf.utility.triple.aspect.DubboContextAspect;
import com.xlf.utility.triple.exception.DubboException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Triple 自动配置
 * <p>
 * 用于自动扫描 bamboo-triple 模块的组件与切面，
 * 使 TripleRequest 校验与 TripleResult 能够在引入模块后即刻生效。
 * </p>
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Configuration
@EnableAspectJAutoProxy
@ComponentScan("com.xlf.utility.triple")
@SuppressWarnings("unused")
public class TripleAutoConfiguration {

    /**
     * 注册 Dubbo 上下文切面
     * <p>
     * 用于处理带有 @DubboPersistentContext 注解的方法，
     * 自动提取并设置请求上下文。
     *
     * @return DubboContextAspect 实例
     */
    @Bean
    @ConditionalOnClass(name = "org.apache.dubbo.rpc.RpcContext")
    @ConditionalOnMissingBean
    public DubboContextAspect dubboContextAspect() {
        return new DubboContextAspect();
    }

    /**
     * 注册 Dubbo 异常处理器
     * <p>
     * 用于统一处理 Dubbo RPC 调用中的异常。
     *
     * @return DubboException 实例
     */
    @Bean
    @ConditionalOnClass(name = "org.apache.dubbo.rpc.RpcException")
    @ConditionalOnMissingBean
    public DubboException dubboException() {
        return new DubboException();
    }

    /**
     * 注册自定义执行异常处理器
     * <p>
     * 用于处理 ExecutionException，特别针对 Dubbo RPC 异常进行特殊处理。
     *
     * @return CustomExecutionException 实例
     */
    @Bean
    @ConditionalOnClass(name = "java.util.concurrent.ExecutionException")
    @ConditionalOnMissingBean
    public com.xlf.utility.triple.exception.CustomExecutionException customExecutionException() {
        return new com.xlf.utility.triple.exception.CustomExecutionException();
    }
}
