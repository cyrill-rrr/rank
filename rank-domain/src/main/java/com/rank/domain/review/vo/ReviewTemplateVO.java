package com.rank.domain.review.vo;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * 值对象职责：表示一个评审场景的问题编排模板。
 * 包含问题ID列表和分组信息，从Lion/配置中心读取。
 */
@Getter
public class ReviewTemplateVO {

    /** 评审场景 */
    private final String scene;
    /** 按顺序排列的问题ID列表 */
    private final List<String> questionIdList;
    /** 问题分组和展示位置 */
    private final List<QuestionGroupVO> groups;

    private ReviewTemplateVO(String scene, List<String> questionIdList, List<QuestionGroupVO> groups) {
        this.scene = scene;
        this.questionIdList = questionIdList != null ? questionIdList : Collections.emptyList();
        this.groups = groups != null ? groups : Collections.emptyList();
    }

    public static ReviewTemplateVO of(String scene, List<String> questionIdList, List<QuestionGroupVO> groups) {
        return new ReviewTemplateVO(scene, questionIdList, groups);
    }

    /**
     * 获取问题ID集合（用于校验）
     */
    public List<String> getQuestionIdList() {
        return questionIdList;
    }
}
