package com.xlf.utility.controller;

/**
 * 错误控制器接口
 * <p>
 * 用于定义错误处理相关的方法，规范化错误处理流程。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
public interface IErrorController {

    /**
     * 返回错误信息处理结果
     *
     * @return 错误处理结果对象
     */
    Object error();
}
