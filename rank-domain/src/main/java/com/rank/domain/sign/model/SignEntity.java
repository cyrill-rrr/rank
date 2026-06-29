package com.rank.domain.sign.model;

import com.rank.domain.sign.shared.SignStatusEnum;
import lombok.Getter;

import java.util.Date;

/**
 * 聚合职责：维护报名主体在某个报名场景下对单个提报项目的报名状态。
 * 核心不变量：
 * 1. 一个报名记录只对应一个场景、一个主体、一个提报项目。
 * 2. 提报和退报进入Entity状态流转前，必须先通过SignConfigVO的窗口期校验。
 */
@Getter
public class SignEntity {

    private Long id;
    private String signScene;
    private Long subjectId;
    private Long indexShopId;
    private Integer projectCode;
    private SignStatusEnum status;
    private Date createdTime;
    private Date updatedTime;

    public SignEntity() {
    }

    public SignEntity(String signScene, Long subjectId, Long indexShopId, Integer projectCode,
                      SignStatusEnum status) {
        this.signScene = signScene;
        this.subjectId = subjectId;
        this.indexShopId = indexShopId;
        this.projectCode = projectCode;
        this.status = status;
        this.createdTime = new Date();
        this.updatedTime = new Date();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSignScene(String signScene) {
        this.signScene = signScene;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public void setIndexShopId(Long indexShopId) {
        this.indexShopId = indexShopId;
    }

    public void setProjectCode(Integer projectCode) {
        this.projectCode = projectCode;
    }

    public void setStatus(SignStatusEnum status) {
        this.status = status;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    /**
     * 状态流转为已提报
     */
    public void submit() {
        if (SignStatusEnum.SIGNED.equals(this.status)) {
            return;
        }
        this.status = SignStatusEnum.SIGNED;
        this.updatedTime = new Date();
    }

    /**
     * 状态流转为已退报
     */
    public void cancel() {
        if (SignStatusEnum.CANCELLED.equals(this.status)) {
            return;
        }
        this.status = SignStatusEnum.CANCELLED;
        this.updatedTime = new Date();
    }
}
