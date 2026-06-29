package com.rank.application.sign.command;

import java.util.List;

/**
 * 报名操作Command
 */
public class OperateSignCommand {

    private String signScene;              // 报名场景
    private String operationType;          // 操作类型：SUBMIT/CANCEL
    private Long subjectId;                // 报名主体ID
    private List<Integer> projectCodeList; // 项目code列表
    private Long shopId;                   // 冗余机构ID

    public String getSignScene() {
        return signScene;
    }

    public void setSignScene(String signScene) {
        this.signScene = signScene;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public List<Integer> getProjectCodeList() {
        return projectCodeList;
    }

    public void setProjectCodeList(List<Integer> projectCodeList) {
        this.projectCodeList = projectCodeList;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
}
