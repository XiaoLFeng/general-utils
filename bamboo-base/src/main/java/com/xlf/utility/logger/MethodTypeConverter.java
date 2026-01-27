package com.xlf.utility.logger;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import cn.hutool.core.util.StrUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;

/**
 * MethodTypeConverter 类用于将日志事件的 Logger 名称进行分类转换。
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
public class MethodTypeConverter extends ClassicConverter {

    @Override
    public String convert(@NotNull ILoggingEvent event) {
        String loggerName = event.getLoggerName();

        String methodType = MDC.get("METHOD_TYPE");
        if (StrUtil.isNotBlank(methodType)) {
            return "\u001b[0;96m[" + methodType + "]\u001b[0m";
        } else if (loggerName.contains(".task") || loggerName.contains(".schedule") || loggerName.contains(".quartz") || loggerName.contains(".job")) {
            return "\u001b[0;33m[TASK]\u001b[0m";
        }

        if (loggerName.contains(".controller")) {
            return "\u001b[0;34m[CTRL]\u001b[0m";
        } else if (loggerName.contains(".service") || loggerName.contains(".logic")) {
            return "\u001b[0;34m[SERV]\u001b[0m";
        } else if (loggerName.contains(".dao") || loggerName.contains(".mapper")) {
            return "\u001b[0;34m[DATA]\u001b[0m";
        } else if (loggerName.contains(".cache")) {
            return "\u001b[0;34m[CCHE]\u001b[0m";
        } else if (loggerName.contains(".ResultUtil") || loggerName.contains(".Result")) {
            return "\u001b[0;34m[RESU]\u001b[0m";
        }

        else if (loggerName.contains(".aspect")) {
            return "\u001b[0;33m[ASPT]\u001b[0m";
        } else if (loggerName.contains(".util")) {
            return "\u001b[0;33m[UTIL]\u001b[0m";
        } else if (loggerName.contains(".filter")) {
            return "\u001b[0;33m[FILT]\u001b[0m";
        }

        else if (loggerName.contains(".init") || loggerName.contains(".setup")) {
            return "\u001b[0;90m[INIT]\u001b[0m";
        } else if (loggerName.contains(".config")) {
            return "\u001b[0;90m[CONF]\u001b[0m";
        } else if (loggerName.contains(".holder")) {
            return "\u001b[0;90m[HOLD]\u001b[0m";
        }

        else if (loggerName.contains(".exception")) {
            return "\u001b[0;31m[EXCP]\u001b[0m";
        } else if (loggerName.contains(".test")) {
            return "\u001b[0;31m[TEST]\u001b[0m";
        }

        return "      ";
    }
}
