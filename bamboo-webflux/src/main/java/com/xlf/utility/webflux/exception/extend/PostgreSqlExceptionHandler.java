package com.xlf.utility.webflux.exception.extend;

import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.exception.IPostgreSqlException;
import com.xlf.utility.webflux.ResultUtil;
import org.jetbrains.annotations.NotNull;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

import java.sql.DataTruncation;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;

/**
 * PostgreSQL 异常处理类（WebFlux 响应式版本）
 * <p>
 * {@code PostgreSqlExceptionHandler} 是一个用于规范化处理 PostgreSQL 相关异常的实现类。
 * 它实现了 {@code IPostgreSqlException} 接口，用于捕获 PostgreSQL 执行过程中可能出现的
 * 特定异常（如 SQL 语法错误、SQL 执行异常、PSQL 异常及数据截断异常等），并返回标准化的
 * 响应结构，从而提升异常处理的一致性和易用性。
 * <p>
 * 本类专为 Spring WebFlux 响应式编程模型设计，所有处理方法均返回 {@code Mono} 包装的类型，
 * 以支持非阻塞异步处理。
 * <p>
 * 实现类通过日志记录详细的异常信息，便于开发人员追踪和排查问题。同时，该类会向客户端
 * 返回简洁且标准化的错误信息，确保前后端信息清晰传递。
 * <p>
 * NOTICE: 在使用本类时请确保相关方法的异常参数类型为其定义的 PostgreSQL 异常，
 * 否则会导致未定义行为或异常。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public class PostgreSqlExceptionHandler implements IPostgreSqlException {
    private static final Logger log = LoggerFactory.getLogger(PostgreSqlExceptionHandler.class);

    /**
     * 处理 SQL 语法错误异常
     * <p>
     * 当执行 SQL 语句时出现语法错误时触发，记录错误日志并返回标准化的错误响应。
     *
     * @param e SQL 语法错误异常对象
     * @return 包含错误信息的响应实体（包装在 Mono 中）
     */
    @Override
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public Mono<ResponseEntity<BaseResponse<Void>>> handleSQLSyntaxErrorException(@NotNull SQLSyntaxErrorException e) {
        log.error("SQL语法错误 | {}", e.getMessage(), e);
        return ResultUtil.error(ErrorCode.DATABASE_ERROR, e.getMessage(), null);
    }

    /**
     * 处理通用 SQL 异常
     * <p>
     * 当执行 SQL 语句时出现通用 SQL 异常时触发，记录错误日志并返回标准化的错误响应。
     *
     * @param e SQL 异常对象
     * @return 包含错误信息的响应实体（包装在 Mono 中）
     */
    @Override
    @ExceptionHandler(SQLException.class)
    public Mono<ResponseEntity<BaseResponse<Void>>> handleSQLException(@NotNull SQLException e) {
        log.error("SQL错误 | {}", e.getMessage(), e);
        return ResultUtil.error(ErrorCode.DATABASE_ERROR, e.getMessage(), null);
    }

    /**
     * 处理 PostgreSQL PSQL 异常
     * <p>
     * 当 PostgreSQL 数据库操作出现特定错误时触发，记录错误日志并返回包含
     * 错误码、SQL 状态和错误信息的标准化响应。
     *
     * @param e PSQL 异常对象
     * @return 包含详细错误信息的响应实体（包装在 Mono 中）
     */
    @Override
    @ExceptionHandler(PSQLException.class)
    public Mono<ResponseEntity<BaseResponse<HashMap<String, Object>>>> handlePSQLException(@NotNull PSQLException e) {
        log.error("PostgreSQL异常 | {}", e.getMessage(), e);
        HashMap<String, Object> data = new HashMap<>();
        data.put("error_code", e.getErrorCode());
        data.put("sql_state", e.getSQLState());
        data.put("error_message", e.getMessage());
        return ResultUtil.error(ErrorCode.DATABASE_ERROR, e.getMessage(), data);
    }

    /**
     * 处理 PostgreSQL 数据截断异常
     * <p>
     * 当插入或更新的数据长度超过数据库字段定义的最大长度时触发，记录错误日志并返回
     * 包含错误码、SQL 状态、数据大小和传输大小等详细信息的标准化响应。
     *
     * @param e 数据截断异常对象
     * @return 包含详细错误信息的响应实体（包装在 Mono 中）
     */
    @Override
    @ExceptionHandler(DataTruncation.class)
    public Mono<ResponseEntity<BaseResponse<HashMap<String, Object>>>> handleDataTruncation(@NotNull DataTruncation e) {
        log.error("PostgreSQL数据截断异常 | {}", e.getMessage(), e);
        HashMap<String, Object> data = new HashMap<>();
        data.put("error_code", e.getErrorCode());
        data.put("sql_state", e.getSQLState());
        data.put("data_size", e.getDataSize());
        data.put("transfer_size", e.getTransferSize());
        data.put("error_message", e.getMessage());
        return ResultUtil.error(ErrorCode.DATABASE_ERROR, e.getMessage(), data);
    }
}
