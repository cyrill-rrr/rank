package com.rank.api.material.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 当前材料查询响应DTO
 * materialId=null时表示无材料，前端根据materialId判空，其他字段也为null
 */
@Data
public class QueryCurrentMaterialResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 材料记录主键ID，null表示无材料
     */
    private Long materialId;

    /**
     * 是否有草稿：NO_DRAFT / HAS_DRAFT
     */
    private String hasDraft;

    /**
     * 审核状态：PENDING_SUBMIT / UNDER_REVIEW / APPROVED / REJECTED
     */
    private String auditStatus;

    /**
     * 材料内容JSON字符串
     */
    private String materialJsonStr;

    /**
     * 驳回原因
     */
    private String rejectReason;
}
