package com.rank.domain.review.vo;

import lombok.Getter;

/**
 * 值对象职责：表示问题分组信息。
 */
@Getter
public class QuestionGroupVO {

    /** 分组名称 */
    private final String groupName;
    /** 分组内问题ID列表 */
    private final java.util.List<String> questionIds;

    public QuestionGroupVO(String groupName, java.util.List<String> questionIds) {
        this.groupName = groupName;
        this.questionIds = questionIds;
    }
}
