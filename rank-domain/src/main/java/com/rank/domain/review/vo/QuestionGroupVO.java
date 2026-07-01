package com.rank.domain.review.vo;

import lombok.Getter;

import java.util.List;

/**
 * 值对象职责：表示问题分组信息。
 */
@Getter
public class QuestionGroupVO {

    /** 分组名称 */
    private final String groupName;
    /** 分组内问题ID列表 */
    private final List<String> questionIds;

    public QuestionGroupVO(String groupName, List<String> questionIds) {
        this.groupName = groupName;
        this.questionIds = questionIds;
    }
}
