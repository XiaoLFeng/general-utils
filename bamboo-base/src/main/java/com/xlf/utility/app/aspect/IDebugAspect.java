package com.xlf.utility.app.aspect;

/**
 * Debug切面接口
 * <p>
 * 用于处理和检查Debug环境中运行的逻辑。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public interface IDebugAspect {

    /**
     * 检查并验证是否允许调用DebugController
     */
    void checkDebugController();
}
