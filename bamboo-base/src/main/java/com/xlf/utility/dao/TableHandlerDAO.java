package com.xlf.utility.dao;

import com.xlf.utility.mapper.TableMapper;
import com.xlf.utility.models.entity.TableDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 表操作的数据访问服务对象
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
public class TableHandlerDAO extends ServiceImpl<TableMapper, TableDO> implements IService<TableDO> {
}
