package com.xlf.utility.exception;

import org.jetbrains.annotations.NotNull;
import org.postgresql.util.PSQLException;

import java.sql.DataTruncation;

/**
 * PostgreSQL 异常处理接口
 * <p>
 * {@code IPostgreSqlException} 是一个用于规范化处理 PostgreSQL 数据库特有异常的接口。
 * 它继承自 {@code ISqlException}，在通用 SQL 异常处理的基础上，
 * 定义了 PostgreSQL 特有的异常处理方法，如 PSQL 异常和数据截断异常等。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public interface IPostgreSqlException extends ISqlException {

    /**
     * 处理 PostgreSQL PSQL 异常
     * <p>
     * 当 PostgreSQL 数据库操作出现特定错误时触发。
     * 此方法用于捕获并处理 {@link PSQLException} 异常，返回包含详细错误信息的标准化响应。
     *
     * @param e PSQL 异常对象
     * @return 标准化的异常响应
     */
    Object handlePostgreSqlException(@NotNull PSQLException e);

    /**
     * 处理 PostgreSQL 数据截断异常
     * <p>
     * 当插入或更新的数据长度超过数据库字段定义的最大长度时触发。
     * 此方法用于捕获并处理 {@link DataTruncation} 异常，
     * 返回包含错误码、SQL 状态、数据大小等详细信息的响应。
     *
     * @param e 数据截断异常对象
     * @return 标准化的异常响应
     */
    Object handleDataTruncation(@NotNull DataTruncation e);
}
