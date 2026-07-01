package com.rank.domain.material.vo;

import lombok.Data;

/**
 * UAP回调中的审核信息值对象
 */
@Data
public class UapAuditInfoVO {

    /**
     * uapUniqueId：UAP侧的审核唯一标识
     */
    private String uapUniqueId;

    /**
     * 审核状态：APPROVED/REJECTED
     */
    private String auditStatus;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 透传字段内容（JSON字符串）
     */
    private String passageJson;
}
