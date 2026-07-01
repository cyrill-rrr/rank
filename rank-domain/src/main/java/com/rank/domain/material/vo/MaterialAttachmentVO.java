package com.rank.domain.material.vo;

import lombok.Data;

/**
 * 附件值对象
 */
@Data
public class MaterialAttachmentVO {

    /**
     * 附件名称
     */
    private String name;

    /**
     * 附件URL
     */
    private String url;

    /**
     * 附件类型
     */
    private String type;
}
