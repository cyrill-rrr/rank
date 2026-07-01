package com.rank.domain.material.repository;

import com.rank.domain.material.vo.MaterialConfigVO;

/**
 * 材料配置仓储接口：读取Lion配置（窗口期 + UAP模板）
 */
public interface MaterialConfigRepository {

    /**
     * 按材料场景查询配置
     *
     * @param materialScene 材料场景
     * @return MaterialConfigVO 包含窗口期和UAP模板
     */
    MaterialConfigVO findByScene(String materialScene);
}
