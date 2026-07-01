package com.rank.domain.material.shared;

/**
 * 材料审核状态枚举
 */
public enum MaterialAuditStatusEnum {

    /**
     * 待送审
     */
    PENDING_SUBMIT,
    /**
     * 审核中
     */
    UNDER_REVIEW,
    /**
     * 审核通过
     */
    APPROVED,
    /**
     * 审核驳回
     */
    REJECTED
}
