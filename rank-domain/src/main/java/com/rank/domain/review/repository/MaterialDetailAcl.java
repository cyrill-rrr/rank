package com.rank.domain.review.repository;

import com.rank.domain.review.vo.MaterialDetailVO;

/**
 * 材料详情查询接口（防腐层）。
 * 按 materialId 实时查询材料详情。
 * 实现类在 infrastructure 层。
 */
public interface MaterialDetailAcl {

    /**
     * 按材料ID查询材料详情
     *
     * @param materialId 外部材料ID
     * @return 材料详情值对象；失败返回null
     */
    MaterialDetailVO queryByMaterialId(Long materialId);
}
