package com.xlf.utility.exception;

import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;

/**
 * MySQL 异常处理接口
 * <p>
 * {@code IMysqlException} 是一个用于规范化处理 MySQL 数据库特有异常的接口。
 * 它继承自 {@code ISqlException}，在通用 SQL 异常处理的基础上，
 * 定义了 MySQL 特有的异常处理方法，如数据截断异常等。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public interface IMysqlException extends ISqlException {

    /**
     * 处理MySQL数据截断异常
     *
     * @param e MySQL数据截断异常对象
     * @return 标准化的异常响应
     */
    Object handlerMysqlDataTruncation(MysqlDataTruncation e);
}
