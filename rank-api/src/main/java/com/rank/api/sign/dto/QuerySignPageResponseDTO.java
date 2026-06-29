package com.rank.api.sign.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 查询报名结果分页列表响应
 */
@Data
public class QuerySignPageResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long total;            // 总条数
    private Integer pageNo;        // 当前页
    private Integer pageSize;      // 每页大小
    private List<SignRecordDTO> records;  // 报名记录列表

    @Data
    public static class SignRecordDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        private Long signId;              // 报名记录ID
        private Integer signScene;        // 报名场景
        private Long subjectId;           // 报名主体ID
        private Integer projectCode;      // 提报项目code
        private Integer status;           // 报名状态
    }
}
