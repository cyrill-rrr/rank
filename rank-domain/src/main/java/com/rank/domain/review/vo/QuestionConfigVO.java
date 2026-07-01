package com.rank.domain.review.vo;

import lombok.Getter;

/**
 * 值对象职责：表示从海马配置读取的一道问题的完整配置。
 * 包含题干、必填、分数选项等。
 */
@Getter
public class QuestionConfigVO {

    /** 问题ID */
    private final String questionId;
    /** 题干 */
    private final String title;
    /** 是否必答 */
    private final boolean required;
    /** 单选分值选项，JSON数组 */
    private final String scoreOptions;

    private QuestionConfigVO(String questionId, String title, boolean required, String scoreOptions) {
        this.questionId = questionId;
        this.title = title;
        this.required = required;
        this.scoreOptions = scoreOptions;
    }

    public static QuestionConfigVO of(String questionId, String title, boolean required, String scoreOptions) {
        return new QuestionConfigVO(questionId, title, required, scoreOptions);
    }
}
