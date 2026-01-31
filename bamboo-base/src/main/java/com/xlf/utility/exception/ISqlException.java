package com.xlf.utility.exception;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

/**
 * SQL 数据库通用异常处理接口
 * <p>
 * {@code ISqlException} 是一个用于规范化处理 SQL 数据库通用异常的接口。
 * 它继承自 {@code IDatabaseException}，定义了所有 SQL 数据库共有的异常处理方法，
 * 如 SQL 语法错误和通用 SQL 异常等。
 * <p>
 * 具体的 SQL 数据库异常处理接口（如 MySQL、PostgreSQL）应继承此接口，
 * 并在此基础上定义各自特有的异常处理方法。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public interface ISqlException extends IDatabaseException {

    /**
     * 处理 SQL 语法错误异常
     * <p>
     * 当执行 SQL 语句时出现语法错误（如关键字拼写错误、缺少必要的关键字等）时触发。
     * 此方法用于捕获并处理 {@link SQLSyntaxErrorException} 异常，返回标准化的错误响应。
     *
     * @param e SQL 语法错误异常对象
     * @return 标准化的异常响应
     */
    @Override
    Object handleSQLSyntaxErrorException(@NotNull SQLSyntaxErrorException e);

    /**
     * 处理通用 SQL 异常
     * <p>
     * 当执行 SQL 语句时出现通用 SQL 异常时触发。
     * 此方法用于捕获并处理 {@link SQLException} 异常，返回标准化的错误响应。
     *
     * @param e SQL 异常对象
     * @return 标准化的异常响应
     */
    @Override
    Object handleSQLException(@NotNull SQLException e);
}
