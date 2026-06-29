package com.rank.domain.common;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 通用分页结果
 *
 * @param <T> 分页记录类型
 */
@Data
public class PageResult<T> {

    private long total;
    private int pageNo;
    private int pageSize;
    private List<T> records;

    public PageResult() {
    }

    public PageResult(long total, int pageNo, int pageSize, List<T> records) {
        this.total = total;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.records = records;
    }

    /**
     * 返回空分页结果
     */
    public static <T> PageResult<T> empty(int pageNo, int pageSize) {
        return new PageResult<>(0, pageNo, pageSize, Collections.emptyList());
    }
}
