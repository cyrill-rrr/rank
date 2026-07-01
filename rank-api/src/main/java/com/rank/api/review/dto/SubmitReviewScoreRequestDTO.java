package com.rank.api.review.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 提交打分请求DTO
 */
@Data
public class SubmitReviewScoreRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前登录用户ID */
    private Long loginUserId;
    /** 评审任务ID */
    private Long reviewTaskId;
    /** 每题作答 */
    private List<QuestionAnswerDTO> answers;

    @Data
    public static class QuestionAnswerDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 问题ID */
        private String questionId;
        /** 分数，范围1~10 */
        private Integer score;
    }
}
