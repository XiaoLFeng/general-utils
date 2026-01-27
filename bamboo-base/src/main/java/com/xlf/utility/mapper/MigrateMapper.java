package com.xlf.utility.mapper;

import com.xlf.utility.models.entity.MigrateDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据库迁移记录映射接口
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Mapper
public interface MigrateMapper extends BaseMapper<MigrateDO> {
}
