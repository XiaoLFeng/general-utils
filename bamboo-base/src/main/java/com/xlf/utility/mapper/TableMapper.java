package com.xlf.utility.mapper;

import com.xlf.utility.models.entity.TableDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据库表元数据的映射接口
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Mapper
public interface TableMapper extends BaseMapper<TableDO> {
}
