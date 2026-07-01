package com.rank.domain.review.vo;

import lombok.Getter;

/**
 * 值对象职责：表示从外部材料服务查询到的材料详情。
 * 材料是外部聚合根，评审系统只持有 materialId。
 */
@Getter
public class MaterialDetailVO {

    /** 外部材料ID */
    private final Long materialId;
    /** 材料标题 */
    private final String materialTitle;
    /** 材料类型 */
    private final String materialType;
    /** 材料详情JSON字符串 */
    private final String materialJsonStr;

    private MaterialDetailVO(Long materialId, String materialTitle, String materialType, String materialJsonStr) {
        this.materialId = materialId;
        this.materialTitle = materialTitle;
        this.materialType = materialType;
        this.materialJsonStr = materialJsonStr;
    }

    /**
     * 创建材料详情值对象
     */
    public static MaterialDetailVO of(Long materialId, String materialTitle, String materialType, String materialJsonStr) {
        return new MaterialDetailVO(materialId, materialTitle, materialType, materialJsonStr);
    }
}
