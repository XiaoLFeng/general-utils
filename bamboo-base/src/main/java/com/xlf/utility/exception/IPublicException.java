package com.xlf.utility.exception;

import com.xlf.utility.exception.library.BusinessException;
import com.xlf.utility.exception.library.RequestException;
import org.jetbrains.annotations.NotNull;

/**
 * Spring Boot 扩展异常处理接口
 * <p>
 * 定义了应用层异常的统一处理方法规范。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public interface IPublicException {

    Object handleBusinessException(@NotNull BusinessException e);

    Object handlePageNotFoundException(@NotNull Object e);

    Object handleRequestHeaderNotMatchException(@NotNull Object e);

    Object handleMailTemplateNotFoundException(@NotNull Object e);

    Object handleUserAuthenticationException(@NotNull Object e);

    Object handleBaseUserAuthenticationException(@NotNull Object e);

    Object handleServerInternalErrorException(@NotNull Object e);

    Object handleCheckFailureException(@NotNull Object e);

    Object handleNoPermissionException(@NotNull Object e);

    Object handleSystemParameterErrorException(@NotNull Object e);

    Object handleDeveloperErrorException(@NotNull Object e);

    Object handleRequestException(@NotNull RequestException e);

    Object handleUnexpectedTypeException(@NotNull Object e);
}
