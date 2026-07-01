package com.rank.api.review.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 删单响应DTO
 */
@Data
public class DeleteReviewTasksResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 删除影响行数 */
    private Integer affectedRows;
}
