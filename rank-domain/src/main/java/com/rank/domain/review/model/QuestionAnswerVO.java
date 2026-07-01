package com.rank.domain.review.model;

import com.rank.domain.common.exception.BizException;
import lombok.Getter;

/**
 * 值对象职责：表示一个问题ID及专家对该问题的单选打分结果。
 * 核心不变量：score 为空表示未作答；非空时必须在 1~10 范围内。
 */
@Getter
public class QuestionAnswerVO {

    /** 问题ID */
    private final String questionId;
    /** 单选分数，范围1~10 */
    private final Integer score;

    private QuestionAnswerVO(String questionId, Integer score) {
        if (questionId == null || questionId.trim().isEmpty()) {
            throw BizException.invalidParam("问题ID不能为空");
        }
        if (score != null && (score < 1 || score > 10)) {
            throw BizException.invalidParam("分数必须在1~10之间");
        }
        this.questionId = questionId;
        this.score = score;
    }

    /**
     * 创建未作答的问题答案
     */
    public static QuestionAnswerVO unanswered(String questionId) {
        return new QuestionAnswerVO(questionId, null);
    }

    /**
     * 创建已作答的问题答案
     */
    public static QuestionAnswerVO answered(String questionId, Integer score) {
        return new QuestionAnswerVO(questionId, score);
    }

    /**
     * 是否已打分
     */
    public boolean hasScore() {
        return score != null;
    }
}
