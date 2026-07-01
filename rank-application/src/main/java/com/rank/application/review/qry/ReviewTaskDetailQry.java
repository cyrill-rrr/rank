package com.rank.application.review.qry;

import lombok.Data;

/**
 * 评审单详情 Qry
 */
@Data
public class ReviewTaskDetailQry {

    /** 当前登录用户ID */
    private Long loginUserId;
    /** 是否管理员 */
    private boolean admin;
    /** 评审任务ID */
    private Long reviewTaskId;
}
