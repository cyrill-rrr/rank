package com.rank.api.material.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 材料操作请求DTO
 */
@Data
public class OperateMaterialRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 材料场景：MEDICAL_BEAUTY_DOCTOR / ORAL_DOCTOR
     */
    private String materialScene;

    /**
     * 审计主体ID（techId字符串）
     */
    private String auditSubjectId;

    /**
     * 操作类型：SAVE_DRAFT / SUBMIT_AUDIT
     */
    private String operationType;

    /**
     * 材料内容JSON字符串（草稿可为空/空JSON，送审时必填）
     */
    private String materialJsonStr;
}
