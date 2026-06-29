package com.rank.infrastructure.sign.po;

import lombok.Data;

import java.util.Date;

/**
 * 报名记录PO，映射表t_sign。
 */
@Data
public class SignPO {

    /**
     * 报名记录主键。
     */
    private Long id;

    /**
     * 报名场景：INSTITUTION/DOCTOR。
     */
    private String signScene;

    /**
     * 报名主体ID：shopId(机构榜) / techId(医生榜)。
     */
    private Long subjectId;

    /**
     * 冗余机构ID：机构榜=shopId，医生榜=所属shopId。
     */
    private Long indexShopId;

    /**
     * 提报项目code。
     */
    private Integer projectCode;

    /**
     * 报名状态：SIGNED/CANCELLED。
     */
    private String status;

    /**
     * 创建时间。
     */
    private Date createdTime;

    /**
     * 更新时间。
     */
    private Date updatedTime;
}
