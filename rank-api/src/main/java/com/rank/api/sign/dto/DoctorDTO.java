package com.rank.api.sign.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 医生信息DTO
 */
@Data
public class DoctorDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long techId;           // 医生ID
    private String doctorName;     // 医生名称
    private Integer onlineStatus;  // 在线状态
    private Long shopId;           // 所属机构ID
}
