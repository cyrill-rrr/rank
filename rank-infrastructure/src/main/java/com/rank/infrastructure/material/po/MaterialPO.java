package com.rank.infrastructure.material.po;

import lombok.Data;

import java.util.Date;

/**
 * 材料提报PO，映射材料提报表。
 */
@Data
public class MaterialPO {

    /**
     * 主键。
     */
    private Long id;

    /**
     * 材料场景：MEDICAL_BEAUTY_DOCTOR / ORAL_DOCTOR。
     */
    private String materialScene;

    /**
     * 审计主体ID（techId）。
     */
    private String auditSubjectId;

    /**
     * 是否有草稿：NO_DRAFT / HAS_DRAFT。
     */
    private String hasDraft;

    /**
     * 审核状态：PENDING_SUBMIT / UNDER_REVIEW / APPROVED / REJECTED。
     */
    private String auditStatus;

    /**
     * 草稿材料内容JSON字符串（有草稿时非空）。
     */
    private String draftMaterialJsonStr;

    /**
     * 正式材料内容JSON字符串（送审后非空）。
     */
    private String materialJsonStr;

    /**
     * UAP审核唯一标识。
     */
    private String uapUniqueId;

    /**
     * 驳回原因。
     */
    private String rejectReason;

    /**
     * 创建时间。
     */
    private Date createdTime;

    /**
     * 更新时间。
     */
    private Date updatedTime;
}
