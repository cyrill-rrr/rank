package com.rank.application.sign.command;

import lombok.Data;

import java.util.List;

/**
 * 报名操作Command
 */
@Data
public class OperateSignCommand {

    private String signScene;              // 报名场景
    private String operationType;          // 操作类型：SUBMIT/CANCEL
    private Long subjectId;                // 报名主体ID
    private List<Integer> projectCodeList; // 项目code列表
    private Long shopId;                   // 冗余机构ID
}
