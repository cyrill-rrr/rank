package com.rank.api.material.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 材料操作响应DTO
 */
@Data
public class OperateMaterialResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 材料记录主键ID
     */
    private Long materialId;
}
