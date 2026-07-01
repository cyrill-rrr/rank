package com.rank.application.review.command;

import lombok.Data;

import java.util.List;

/**
 * 分单命令
 */
@Data
public class ImportReviewTasksCommand {

    /** 分单行列表 */
    private List<ImportReviewTaskItem> rows;
    /** 操作人用户ID */
    private Long operatorUserId;

    @Data
    public static class ImportReviewTaskItem {

        /** 外部材料ID */
        private Long materialId;
        /** 专家用户ID */
        private Long userId;
        /** 评审场景 */
        private String scene;
    }
}
