package com.rank.domain.review.repository;

import com.rank.domain.review.vo.ReviewTemplateVO;

/**
 * 评审模板配置仓储（Lion/配置中心）。
 * 按 scene 读取当前问题编排模板，包含问题ID顺序、分组、展示位置。
 */
public interface ReviewTemplateConfigRepository {

    /**
     * 按场景查询问题模板
     *
     * @param scene 评审场景
     * @return 模板值对象，包含 questionIdList 和 groups；缺失返回null
     */
    ReviewTemplateVO findByScene(String scene);
}
