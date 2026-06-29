package com.rank.api.sign.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 查询可选提报项目响应
 */
@Data
public class QuerySelectableProjectsResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ProjectDTO> projects;   // 可选提报项目列表

    @Data
    public static class ProjectDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        private Integer projectCode;   // 提报项目code
    }
}
