package com.rank.application.review.qry;

import lombok.Data;

/**
 * 评审单分页查询 Qry
 */
@Data
public class ReviewTaskPageQry {

    /** 当前登录用户ID */
    private Long loginUserId;
    /** 是否管理员 */
    private boolean admin;
    /** 材料ID筛选（管理员） */
    private Long materialId;
    /** 用户ID筛选（管理员） */
    private Long userId;
    /** 场景筛选 */
    private String scene;
    /** 状态筛选：UNSCORED / SCORED */
    private String status;
    /** 页码 */
    private int pageNo;
    /** 每页大小 */
    private int pageSize;
}
