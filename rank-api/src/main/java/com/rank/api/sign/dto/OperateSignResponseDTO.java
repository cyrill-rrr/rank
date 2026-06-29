package com.rank.api.sign.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 报名操作响应
 */
@Data
public class OperateSignResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Long> signIdList;   // 本次新增、恢复报名或退报更新的报名记录主键
}
