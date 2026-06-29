package com.rank.api.sign.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询机构下医生列表请求入参
 */
@Data
public class QueryShopDoctorsRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long shopId;               // 机构ID
    private Boolean includeOffline;    // 是否包含不在线医生，默认true
}
