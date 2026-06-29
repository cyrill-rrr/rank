package com.rank.api.sign.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询报名结果分页列表请求入参
 */
@Data
public class QuerySignPageRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer signScene;    // 报名场景
    private Long shopId;          // 机构ID
    private Integer status;       // 报名状态，不传查全部
    private Integer pageNo;       // 页码，从1开始
    private Integer pageSize;     // 每页大小
}
