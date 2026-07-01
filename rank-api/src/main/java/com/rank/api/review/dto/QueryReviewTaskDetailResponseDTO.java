package com.rank.api.review.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 详情查询响应DTO
 */
@Data
public class QueryReviewTaskDetailResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 评审任务ID */
    private Long reviewTaskId;
    /** 材料ID */
    private Long materialId;
    /** 材料详情 */
    private MaterialDetailResponseDTO materialDetail;
    /** 问题列表（含配置和当前作答） */
    private List<QuestionDetailDTO> questions;
    /** 评审状态 */
    private String status;

    @Data
    public static class MaterialDetailResponseDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 材料ID */
        private Long materialId;
        /** 材料标题 */
        private String materialTitle;
        /** 材料类型 */
        private String materialType;
        /** 材料详情JSON字符串 */
        private String materialJsonStr;
    }

    @Data
    public static class QuestionDetailDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 问题ID */
        private String questionId;
        /** 题干 */
        private String title;
        /** 是否必答 */
        private Boolean required;
        /** 已填分数，未填为null */
        private Integer score;
    }
}
