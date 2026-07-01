package com.rank.api.review.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 分单响应DTO
 */
@Data
public class ImportReviewTasksResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 是否已接收分单请求 */
    private Boolean accepted;
}
