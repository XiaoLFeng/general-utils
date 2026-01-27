package com.xlf.utility.exception;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

/**
 * 数据库异常处理接口
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public interface IDatabaseException {
    Object handleSQLSyntaxErrorException(@NotNull SQLSyntaxErrorException e);

    Object handleSQLException(@NotNull SQLException e);
}
