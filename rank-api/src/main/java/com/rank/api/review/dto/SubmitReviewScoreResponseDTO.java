package com.rank.api.review.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 提交打分响应DTO
 */
@Data
public class SubmitReviewScoreResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 评审任务ID */
    private Long reviewTaskId;
    /** 提交后的状态 */
    private String status;
}
