package com.rank.infrastructure.review.po;

import lombok.Data;

import java.util.Date;

/**
 * 表用途：持久化一条材料分配给一个用户在一个场景下的评审任务。
 * 关键约束：同一个 materialId + userId + scene 只能有一条有效记录。
 * 表名：t_review_task
 */
@Data
public class ReviewTaskPO {

    /** 评审任务主键 */
    private Long id;
    /** 外部材料ID */
    private Long materialId;
    /** 专家用户ID */
    private Long userId;
    /** 评审场景 */
    private String scene;
    /** 评审任务状态：UNSCORED / SCORED */
    private String status;
    /** 问题ID与每题作答结果JSON字符串 */
    private String questionAnswerJsonStr;
    /** 是否已删除：0=否，1=是 */
    private Integer deleted;
    /** 创建时间 */
    private Date ctime;
    /** 更新时间 */
    private Date mtime;
}
