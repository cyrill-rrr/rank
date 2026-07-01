package com.rank.application.review.command;

import lombok.Data;

/**
 * 删单命令
 */
@Data
public class DeleteReviewTasksCommand {

    /** 管理员用户ID */
    private Long operatorUserId;
    /** 被删除评审单所属用户ID */
    private Long userId;
}
