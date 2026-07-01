package com.rank.api.review.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 详情查询请求DTO。
 * 注意：管理员身份不在 DTO 中传递，由服务端根据 loginUserId 通过硬编码白名单判断。
 * 鉴权层注入的 admin 字段不在本 DTO 中，避免前端自传角色绕过鉴权。
 */
@Data
public class QueryReviewTaskDetailRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前登录用户ID，建议由登录态注入 */
    private Long loginUserId;
    /** 评审任务ID */
    private Long reviewTaskId;
}
