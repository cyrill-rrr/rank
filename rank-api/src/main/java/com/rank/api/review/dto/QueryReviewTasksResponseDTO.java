package com.rank.api.review.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 列表查询响应DTO
 */
@Data
public class QueryReviewTasksResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总数 */
    private Long total;
    /** 页码 */
    private Integer pageNo;
    /** 每页大小 */
    private Integer pageSize;
    /** 记录列表 */
    private List<ReviewTaskRecordDTO> records;

    @Data
    public static class ReviewTaskRecordDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 评审任务ID */
        private Long reviewTaskId;
        /** 材料ID */
        private Long materialId;
        /** 用户ID */
        private Long userId;
        /** 场景 */
        private String scene;
        /** 状态 */
        private String status;
    }
}
