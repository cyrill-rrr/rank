package com.rank.application.sign.qry;

/**
 * 机构医生列表查询Qry
 */
public class ShopDoctorQry {

    private Long shopId;               // 机构ID
    private Boolean includeOffline;    // 是否包含不在线医生

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Boolean getIncludeOffline() {
        return includeOffline;
    }

    public void setIncludeOffline(Boolean includeOffline) {
        this.includeOffline = includeOffline;
    }
}
