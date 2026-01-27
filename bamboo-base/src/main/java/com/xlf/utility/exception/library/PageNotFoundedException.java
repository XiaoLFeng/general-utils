package com.xlf.utility.exception.library;

/**
 * 页面未找到异常类，用于定义页面未找到异常。
 * <p>
 * 该异常类继承自 {@link RuntimeException}，并包含了异常信息和页面路由字段。
 * 可以通过构造函数初始化，以适应特定的页面未找到异常处理需求。
 *
 * @author xiao_lfeng
 * @version v1.0.9-beta.1.0
 * @since v1.0.9-beta.1.0
 */
@SuppressWarnings("unused")
public class PageNotFoundedException extends RuntimeException {

    /**
     * 页面路由，指示未找到页面的具体路径。
     */
    private final String route;

    /**
     * 异常信息，描述页面未找到的详细信息。
     */
    private final String message;

    /**
     * 是否包含堆栈跟踪信息
     */
    private final boolean hasTrace;

    /**
     * 构造函数，初始化页面未找到异常的所有属性。
     *
     * @param message  异常的错误消息
     * @param route    页面路由
     * @param hasTrace 是否包含堆栈跟踪信息
     */
    public PageNotFoundedException(String message, String route, boolean hasTrace) {
        super(message);
        this.message = message;
        this.route = route;
        this.hasTrace = hasTrace;
    }

    /**
     * 构造函数，初始化页面未找到异常的所有属性。
     *
     * @param message 异常的错误消息
     * @param route   页面路由
     */
    public PageNotFoundedException(String message, String route) {
        this(message, route, false);
    }

    /**
     * 构造函数，使用默认的错误消息 "页面未找到" 并指定页面路由。
     *
     * @param route 页面路由
     */
    public PageNotFoundedException(String route) {
        this("页面未找到", route);
    }

    /**
     * 获取异常信息。
     *
     * @return 返回异常的错误消息
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 获取页面路由。
     *
     * @return 返回未找到页面的路由信息
     */
    public String getRoute() {
        return route;
    }

    /**
     * 判断是否包含堆栈跟踪信息。
     *
     * @return 如果包含堆栈跟踪信息则返回 true，否则返回 false
     */
    public boolean isHasTrace() {
        return hasTrace;
    }
}