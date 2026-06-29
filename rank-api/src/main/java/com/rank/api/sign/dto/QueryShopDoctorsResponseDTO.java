package com.rank.api.sign.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 查询机构下医生列表响应
 */
@Data
public class QueryShopDoctorsResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<DoctorDTO> doctors;   // 医生列表
}
