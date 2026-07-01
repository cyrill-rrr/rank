package com.rank.api.review.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 详情查询请求DTO
 */
@Data
public class QueryReviewTaskDetailRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前登录用户ID，建议由登录态注入 */
    private Long loginUserId;
    /** 评审任务ID */
    private Long reviewTaskId;
}
