package com.xlf.utility.app.init;

import com.xlf.utility.app.properties.UtilityBaseProperties;
import com.xlf.utility.incrementer.SnowflakeIdGenerator;
import com.xlf.utility.utility.SnowflakeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 雪花ID工具类初始化器
 * <p>
 * 该组件在 Spring 容器启动时自动初始化雪花ID工具类。
 * </p>
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SnowflakeUtilInitializer implements ApplicationContextAware {

    private final UtilityBaseProperties properties;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        UtilityBaseProperties.Snowflake snowflakeConfig = properties.getSnowflake();

        long datacenterId = snowflakeConfig != null && snowflakeConfig.getDatacenterId() != null
                ? snowflakeConfig.getDatacenterId()
                : 1L;

        long machineId = snowflakeConfig != null && snowflakeConfig.getMachineId() != null
                ? snowflakeConfig.getMachineId()
                : 1L;

        long epoch = snowflakeConfig != null && snowflakeConfig.getEpoch() != null
                ? snowflakeConfig.getEpoch()
                : 1729440000000L;

        log.info("初始化 SnowflakeUtil: datacenterId={}, machineId={}, epoch={}",
                datacenterId, machineId, epoch);

        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(datacenterId, machineId, epoch);
        SnowflakeUtil.initialize(generator);

        log.info("SnowflakeUtil 初始化完成");
    }
}
