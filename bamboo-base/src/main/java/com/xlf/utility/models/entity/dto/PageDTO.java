package com.xlf.utility.models.entity.dto;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于分页数据传输的通用类
 *
 * @param <T> 分页数据记录类型
 * @author xiao_lfeng
 * @version v2.0.0-beta1
 * @since v2.0.0-beta1
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class PageDTO<T> {
    private List<T> records;
    private Long total;
    private Long size;
    private Long current;

    @Contract(pure = true)
    public PageDTO() {
        this.total = 0L;
        this.size = 0L;
        this.current = 0L;
        this.records = new ArrayList<>();
    }

    @Contract(pure = true)
    public PageDTO(long total, long size) {
        this.total = total;
        this.size = size;
        this.records = new ArrayList<>();
    }

    public PageDTO<T> setRecords(String jsonRecords, Class<T> t) {
        this.records = JSONUtil.toList(jsonRecords, t);
        return this;
    }

    public PageDTO<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }
}
