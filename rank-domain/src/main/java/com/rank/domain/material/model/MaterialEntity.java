package com.rank.domain.material.model;

import com.rank.domain.common.exception.BizException;
import com.rank.domain.material.shared.MaterialAuditStatusEnum;
import com.rank.domain.material.shared.MaterialDraftStatusEnum;

import java.util.Date;

/**
 * 聚合职责：维护单个材料提报记录的生命周期和状态流转。
 * 核心不变量：
 * 1. 一个材料记录只属于一个审计主体和一个场景。
 * 2. 状态流转严格遵从：create() -> saveDraft() -> markUnderReview() -> applyAuditResult()。
 * 3. 只有草稿状态才能送审，只有审核中状态才能应用审核结果。
 */
public class MaterialEntity {

    /** 材料记录主键 */
    private Long id;
    /** 材料场景 */
    private String materialScene;
    /** 审计主体ID（techId） */
    private String auditSubjectId;
    /** 是否有草稿 */
    private MaterialDraftStatusEnum hasDraft;
    /** 审核状态 */
    private MaterialAuditStatusEnum auditStatus;
    /** 草稿材料内容 */
    private AbstractMaterialContent draftMaterialContent;
    /** 正式材料内容 */
    private AbstractMaterialContent materialContent;
    /** UAP审核唯一标识 */
    private String uapUniqueId;
    /** 驳回原因 */
    private String rejectReason;
    /** 创建时间 */
    private Date createdTime;
    /** 更新时间 */
    private Date updatedTime;

    public MaterialEntity() {
    }

    /**
     * 创建初始状态的材料实体
     *
     * @param materialScene   材料场景
     * @param auditSubjectId  审计主体ID
     */
    public MaterialEntity(String materialScene, String auditSubjectId) {
        this.materialScene = materialScene;
        this.auditSubjectId = auditSubjectId;
        this.hasDraft = MaterialDraftStatusEnum.NO_DRAFT;
        this.auditStatus = MaterialAuditStatusEnum.PENDING_SUBMIT;
        this.createdTime = new Date();
        this.updatedTime = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaterialScene() {
        return materialScene;
    }

    public String getAuditSubjectId() {
        return auditSubjectId;
    }

    public MaterialDraftStatusEnum getHasDraft() {
        return hasDraft;
    }

    public MaterialAuditStatusEnum getAuditStatus() {
        return auditStatus;
    }

    public AbstractMaterialContent getDraftMaterialContent() {
        return draftMaterialContent;
    }

    public AbstractMaterialContent getMaterialContent() {
        return materialContent;
    }

    public String getUapUniqueId() {
        return uapUniqueId;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public void setMaterialScene(String materialScene) {
        this.materialScene = materialScene;
    }

    public void setAuditSubjectId(String auditSubjectId) {
        this.auditSubjectId = auditSubjectId;
    }

    public void setHasDraft(MaterialDraftStatusEnum hasDraft) {
        this.hasDraft = hasDraft;
    }

    public void setAuditStatus(MaterialAuditStatusEnum auditStatus) {
        this.auditStatus = auditStatus;
    }

    public void setDraftMaterialContent(AbstractMaterialContent draftMaterialContent) {
        this.draftMaterialContent = draftMaterialContent;
    }

    public void setMaterialContent(AbstractMaterialContent materialContent) {
        this.materialContent = materialContent;
    }

    public void setUapUniqueId(String uapUniqueId) {
        this.uapUniqueId = uapUniqueId;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    /**
     * 保存草稿：设置草稿状态和有草稿内容
     *
     * @param content 草稿材料内容
     */
    public void saveDraft(AbstractMaterialContent content) {
        this.hasDraft = MaterialDraftStatusEnum.HAS_DRAFT;
        this.draftMaterialContent = content;
        this.auditStatus = MaterialAuditStatusEnum.PENDING_SUBMIT;
        this.updatedTime = new Date();
    }

    /**
     * 标记为审核中：草稿材料覆盖为正式材料，清除草稿状态
     *
     * @param uapUniqueId UAP审核唯一标识
     */
    public void markUnderReview(String uapUniqueId) {
        if (!MaterialDraftStatusEnum.HAS_DRAFT.equals(this.hasDraft)) {
            throw BizException.illegalState("没有草稿不能送审");
        }
        this.materialContent = this.draftMaterialContent;
        this.draftMaterialContent = null;
        this.hasDraft = MaterialDraftStatusEnum.NO_DRAFT;
        this.auditStatus = MaterialAuditStatusEnum.UNDER_REVIEW;
        this.uapUniqueId = uapUniqueId;
        this.updatedTime = new Date();
    }

    /**
     * 应用审核结果：仅当审核中状态生效，其他状态返回false（幂等忽略）
     *
     * @param resultStatus 审核结果状态
     * @param rejectReason 驳回原因（审核通过时传null）
     * @return true=状态已更新，false=已忽略（非审核中状态）
     */
    public boolean applyAuditResult(MaterialAuditStatusEnum resultStatus, String rejectReason) {
        if (!MaterialAuditStatusEnum.UNDER_REVIEW.equals(this.auditStatus)) {
            return false;
        }
        this.auditStatus = resultStatus;
        this.rejectReason = rejectReason;
        this.updatedTime = new Date();
        return true;
    }
}
