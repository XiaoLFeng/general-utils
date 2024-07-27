package com.xlf.utility;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 错误码
 * <hr/>
 * 用于定义错误码, 用于返回错误信息, 用于返回错误码
 *
 * @author xiao_lfeng
 * @version v1.0.1
 * @since v1.0.1
 */
@RequiredArgsConstructor
@Getter
@SuppressWarnings("unused")
public enum ErrorCode {
    SERVER_INTERNAL_ERROR("ServerInternalError", 50001, "服务器内部错误"),
    NOT_EXIST("NotExist", 40000, "内容不存在"),
    EXISTED("Existed", 40001, "内容已存在"),
    PARAMETER_ERROR("ParameterError", 40002, "参数错误"),
    PARAMETER_MISSING("ParameterMissing", 40003, "参数缺失"),
    PARAMETER_INVALID("ParameterInvalid", 40004, "参数无效"),
    PARAMETER_ILLEGAL("ParameterIllegal", 40005, "参数非法"),
    PARAMETER_TYPE_ERROR("ParameterTypeError", 40006, "参数类型错误"),
    BODY_ERROR("BodyError", 40007, "请求体错误"),
    BODY_MISSING("BodyMissing", 40008, "请求体缺失"),
    BODY_INVALID("BodyInvalid", 40009, "请求体无效"),
    BODY_ILLEGAL("BodyIllegal", 40010, "请求体非法"),
    BODY_TYPE_ERROR("BodyTypeError", 40011, "请求体类型错误"),
    HEADER_ERROR("HeaderError", 40012, "请求头错误"),
    HEADER_MISSING("HeaderMissing", 40013, "请求头缺失"),
    HEADER_INVALID("HeaderInvalid", 40014, "请求头无效"),
    HEADER_ILLEGAL("HeaderIllegal", 40015, "请求头非法"),
    HEADER_TYPE_ERROR("HeaderTypeError", 40016, "请求头类型错误"),
    OPERATION_ERROR("OperationError", 40017, "操作错误"),
    OPERATION_FAILED("OperationFailed", 40018, "操作失败"),
    OPERATION_INVALID("OperationInvalid", 40019, "操作无效"),
    OPERATION_ILLEGAL("OperationIllegal", 40020, "操作非法"),
    OPERATION_DENIED("OperationDenied", 40021, "操作被拒绝"),
    OPERATION_NOT_ALLOWED("OperationNotAllowed", 40022, "操作不允许"),
    OPERATION_NOT_SUPPORTED("OperationNotSupported", 40023, "操作不支持"),
    PAGE_NOT_FOUND("PageNotFound", 40401, "页面未找到"),
    METHOD_NOT_ALLOWED("MethodNotAllowed", 40501, "方法不允许"),
    UNAUTHORIZED("Unauthorized", 40101, "未授权"),
    FORBIDDEN("Forbidden", 40301, "禁止访问"),
    BAD_REQUEST("BadRequest", 40000, "错误请求"),
    NOT_ACCEPTABLE("NotAcceptable", 40601, "不可接受");

    final String output;
    final Integer code;
    final String message;
}
