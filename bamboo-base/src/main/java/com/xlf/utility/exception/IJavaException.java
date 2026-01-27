package com.xlf.utility.exception;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Java基础异常处理接口
 * <p>
 * 定义了Java基础异常的统一处理方法规范。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@SuppressWarnings("unused")
public interface IJavaException {

    Object handleIoException(@NotNull IOException e);

    Object handleNullPointerException(@NotNull NullPointerException e);

    Object handleFileNotFoundException(@NotNull FileNotFoundException e);

    Object handleIllegalArgumentException(@NotNull IllegalArgumentException e);

    Object handleClassCastException(@NotNull ClassCastException e);

    Object handleArrayIndexOutOfBoundsException(@NotNull ArrayIndexOutOfBoundsException e);

    Object handleArithmeticException(@NotNull ArithmeticException e);

    Object handleUnsupportedOperationException(@NotNull UnsupportedOperationException e);

    Object handleSecurityException(@NotNull SecurityException e);

    Object handleConcurrentModificationException(@NotNull java.util.ConcurrentModificationException e);

    Object handleTimeoutException(@NotNull TimeoutException e);

    Object handleIllegalStateException(@NotNull IllegalStateException e);

    @NotNull Object handleException(@NotNull Exception e);
}
