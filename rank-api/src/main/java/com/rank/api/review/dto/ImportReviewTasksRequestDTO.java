package com.rank.api.review.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分单请求DTO
 */
@Data
public class ImportReviewTasksRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 操作人用户ID，建议由登录态注入 */
    private Long operatorUserId;
    /** 分单行列表 */
    private List<ImportReviewTaskItemDTO> rows;

    @Data
    public static class ImportReviewTaskItemDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 外部材料ID */
        private Long materialId;
        /** 专家用户ID */
        private Long userId;
        /** 评审场景 */
        private String scene;
    }
}
