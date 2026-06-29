package com.rank.application.sign.qry;

import lombok.Data;

/**
 * 报名分页查询Qry
 */
@Data
public class SignPageQry {

    private String signScene;    // 报名场景
    private Long indexShopId;    // 冗余机构ID
    private Integer status;      // 筛选状态，为空查全部
    private int pageNo;          // 页码
    private int pageSize;        // 每页大小
}
