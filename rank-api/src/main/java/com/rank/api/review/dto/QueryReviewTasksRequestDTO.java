package com.rank.api.review.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 列表查询请求DTO
 */
@Data
public class QueryReviewTasksRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前登录用户ID，建议由登录态注入 */
    private Long loginUserId;
    /** 管理员筛选条件：材料ID */
    private Long materialId;
    /** 管理员筛选条件：用户ID */
    private Long userId;
    /** 评审场景 */
    private String scene;
    /** 状态：UNSCORED / SCORED */
    private String status;
    /** 页码 */
    private Integer pageNo;
    /** 每页大小 */
    private Integer pageSize;
}
