package com.rank.infrastructure.sign.po;

import lombok.Data;

import java.util.Date;

/**
 * 报名记录PO，映射表t_sign
 */
@Data
public class SignPO {

    private Long id;                // 报名记录主键
    private String signScene;       // 报名场景：INSTITUTION/DOCTOR
    private Long subjectId;         // 报名主体ID：shopId(机构榜) / techId(医生榜)
    private Long indexShopId;       // 冗余机构ID：机构榜=shopId，医生榜=所属shopId
    private Integer projectCode;    // 提报项目code
    private String status;          // 报名状态：SIGNED/CANCELLED
    private Date createdTime;       // 创建时间
    private Date updatedTime;       // 更新时间
}
