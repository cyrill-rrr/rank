package com.rank.infrastructure.material.mq;

import lombok.Data;

/**
 * UAP审核回调MQ消息体
 * MQ注解待接入时补充
 */
@Data
public class UapAuditCallbackMafkaEvent {

    /**
     * uapUniqueId：UAP侧的审核唯一标识
     */
    private String uapUniqueId;

    /**
     * 审核状态：APPROVED / REJECTED
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
