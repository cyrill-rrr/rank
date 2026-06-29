package com.rank.application.sign.qry;

import lombok.Data;

/**
 * 机构医生列表查询Qry
 */
@Data
public class ShopDoctorQry {

    private Long shopId;               // 机构ID
    private Boolean includeOffline;    // 是否包含不在线医生
}
