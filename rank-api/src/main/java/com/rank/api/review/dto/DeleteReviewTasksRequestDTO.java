package com.rank.api.review.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 删单请求DTO
 */
@Data
public class DeleteReviewTasksRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 管理员用户ID */
    private Long operatorUserId;
    /** 被删除评审单所属用户ID */
    private Long userId;
}
