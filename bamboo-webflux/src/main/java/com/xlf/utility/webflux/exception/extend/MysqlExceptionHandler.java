package com.xlf.utility.webflux.exception.extend;

import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.exception.IMysqlException;
import com.xlf.utility.webflux.ResultUtil;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;

/**
 * MySQL异常处理类
 * <p>
 * {@code MysqlExceptionHandler} 是一个用于规范化处理 MySQL 相关异常的实现类。
 * 它实现了 {@code IMysqlException} 接口，用于捕获 MySQL 执行过程中可能出现的
 * 特定异常（如 SQL 语法错误、SQL 执行异常及数据截断异常等），并返回标准化的
 * 响应结构，从而提升异常处理的一致性和易用性。
 * <p>
 * 实现类通过日志记录详细的异常信息，便于开发人员追踪和排查问题。同时，该类会向客户端
 * 返回简洁且标准化的错误信息，确保前后端信息清晰传递。
 * <p>
 * NOTICE: 在使用本类时请确保相关方法的异常参数类型为其定义的 MySQL 异常，
 * 否则会导致未定义行为或异常。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public class MysqlExceptionHandler implements IMysqlException {
    private static final Logger log = LoggerFactory.getLogger(MysqlExceptionHandler.class);

    @Override
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public Mono<ResponseEntity<BaseResponse<Void>>> handleSQLSyntaxErrorException(@NotNull SQLSyntaxErrorException e) {
        log.error("SQL语法错误 | {}", e.getMessage(), e);
        return ResultUtil.error(ErrorCode.DATABASE_ERROR, e.getMessage(), null);
    }

    @Override
    @ExceptionHandler(SQLException.class)
    public Mono<ResponseEntity<BaseResponse<Void>>> handleSQLException(@NotNull SQLException e) {
        log.error("SQL错误 | {}", e.getMessage(), e);
        return ResultUtil.error(ErrorCode.DATABASE_ERROR, e.getMessage(), null);
    }

    @Override
    @ExceptionHandler(MysqlDataTruncation.class)
    public Mono<ResponseEntity<BaseResponse<HashMap<String, Object>>>> handlerMysqlDataTruncation(MysqlDataTruncation e) {
        log.error("MySQL数据截断异常 | {}", e.getMessage(), e);
        HashMap<String, Object> data = new HashMap<>();
        data.put("error_code", e.getErrorCode());
        data.put("data_size", e.getDataSize());
        data.put("error_message", e.getMessage());
        return ResultUtil.error(ErrorCode.DATABASE_ERROR, e.getCause().getMessage(), data);
    }
}
