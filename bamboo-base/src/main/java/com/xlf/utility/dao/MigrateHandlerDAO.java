package com.xlf.utility.dao;

import com.xlf.utility.mapper.MigrateMapper;
import com.xlf.utility.models.entity.MigrateDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 数据库迁移记录的数据访问服务对象
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
public class MigrateHandlerDAO extends ServiceImpl<MigrateMapper, MigrateDO> implements IService<MigrateDO> {
}
