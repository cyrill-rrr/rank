package com.rank.api.material.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 当前材料查询请求DTO
 */
@Data
public class QueryCurrentMaterialRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 材料场景：MEDICAL_BEAUTY_DOCTOR / ORAL_DOCTOR
     */
    private String materialScene;

    /**
     * 审计主体ID（techId字符串）
     */
    private String auditSubjectId;
}
