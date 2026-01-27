package com.xlf.utility.webflux.controller;

import com.xlf.utility.BaseResponse;
import com.xlf.utility.controller.IErrorController;
import com.xlf.utility.exception.library.ServerInternalErrorException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

/**
 * 错误控制器
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
public class ErrorController implements IErrorController {

    @Override
    @RequestMapping("/error")
    public Mono<ResponseEntity<BaseResponse<?>>> error() {
        throw new ServerInternalErrorException("未定义任何报错信息");
    }
}
