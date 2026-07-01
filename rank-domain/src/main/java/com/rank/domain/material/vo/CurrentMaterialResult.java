package com.rank.domain.material.vo;

import com.rank.domain.material.shared.MaterialAuditStatusEnum;
import com.rank.domain.material.shared.MaterialDraftStatusEnum;

/**
 * 当前材料查询结果值对象
 */
public class CurrentMaterialResult {

    /**
     * 材料主键ID
     */
    private final Long materialId;

    /**
     * 草稿状态
     */
    private final MaterialDraftStatusEnum hasDraft;

    /**
     * 审核状态
     */
    private final MaterialAuditStatusEnum auditStatus;

    /**
     * 材料内容JSON字符串
     */
    private final String materialJsonStr;

    /**
     * 驳回原因
     */
    private final String rejectReason;

    public CurrentMaterialResult(Long materialId, MaterialDraftStatusEnum hasDraft,
                                  MaterialAuditStatusEnum auditStatus,
                                  String materialJsonStr, String rejectReason) {
        this.materialId = materialId;
        this.hasDraft = hasDraft;
        this.auditStatus = auditStatus;
        this.materialJsonStr = materialJsonStr;
        this.rejectReason = rejectReason;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public MaterialDraftStatusEnum getHasDraft() {
        return hasDraft;
    }

    public MaterialAuditStatusEnum getAuditStatus() {
        return auditStatus;
    }

    public String getMaterialJsonStr() {
        return materialJsonStr;
    }

    public String getRejectReason() {
        return rejectReason;
    }
}
