package com.rank.domain.sign.vo;

/**
 * 值对象职责：由Lion配置生成，用于校验项目是否可选。
 */
public class SignProjectVO {

    private final Integer projectCode;
    private final Integer signScene;

    public SignProjectVO(Integer projectCode, Integer signScene) {
        this.projectCode = projectCode;
        this.signScene = signScene;
    }

    public Integer getProjectCode() {
        return projectCode;
    }

    public Integer getSignScene() {
        return signScene;
    }
}
