package com.xlf.utility.exception;

import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;

/**
 * MySQL异常处理接口
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public interface IMysqlException extends IDatabaseException {

    /**
     * 处理MySQL数据截断异常
     *
     * @param e MySQL数据截断异常对象
     * @return 标准化的异常响应
     */
    Object handlerMysqlDataTruncation(MysqlDataTruncation e);
}
