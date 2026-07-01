package com.rank.application.review.command;

import lombok.Data;

import java.util.List;

/**
 * 提交打分命令
 */
@Data
public class SubmitReviewScoreCommand {

    /** 评审任务ID */
    private Long reviewTaskId;
    /** 当前登录用户ID */
    private Long loginUserId;
    /** 每题作答 */
    private List<QuestionAnswerItem> answers;

    @Data
    public static class QuestionAnswerItem {

        /** 问题ID */
        private String questionId;
        /** 分数，范围1~10 */
        private Integer score;
    }
}
