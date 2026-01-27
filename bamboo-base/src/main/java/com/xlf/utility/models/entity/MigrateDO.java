package com.xlf.utility.models.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 数据库迁移记录实体类
 *
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("awaken_migrate")
public final class MigrateDO {
    @TableId(type = IdType.AUTO)
    private Long migrateId;
    private String migrateName;
    private String migrateHash;
    private String migrateStatus;
    private String errorMessage;
    private Date appliedAt;
}
