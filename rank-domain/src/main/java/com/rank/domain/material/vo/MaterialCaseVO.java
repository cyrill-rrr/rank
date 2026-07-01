package com.rank.domain.material.vo;

import lombok.Data;

import java.util.List;

/**
 * 案例/病例值对象
 */
@Data
public class MaterialCaseVO {

    /**
     * 案例标题
     */
    private String title;

    /**
     * 案例描述
     */
    private String description;

    /**
     * 案例附件列表
     */
    private List<MaterialAttachmentVO> attachments;
}
