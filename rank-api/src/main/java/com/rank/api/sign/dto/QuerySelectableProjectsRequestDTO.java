package com.rank.api.sign.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询可选提报项目请求入参
 */
@Data
public class QuerySelectableProjectsRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer signScene;    // 报名场景
}
