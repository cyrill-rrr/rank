package com.rank.api.sign.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 报名操作请求入参
 */
@Data
public class OperateSignRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer signScene;               // 报名场景：1 机构榜，2 医生榜
    private String operationType;            // 操作类型：SUBMIT 提报，CANCEL 退报
    private Long subjectId;                  // 报名主体ID：机构榜为shopId，医生榜为techId
    private List<Integer> projectCodeList;   // 本次操作的提报项目code列表
    private Long shopId;                     // 冗余机构ID：机构榜等于subjectId，医生榜为医生所属机构ID
}
