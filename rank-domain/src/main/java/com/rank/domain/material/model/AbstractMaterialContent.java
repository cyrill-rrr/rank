package com.rank.domain.material.model;

import java.util.List;

import com.rank.domain.material.vo.MaterialCaseVO;

/**
 * 抽象材料基类：不同材料场景共享的通用材料内容结构。
 */
public abstract class AbstractMaterialContent {

    /**
     * 获取案例/病例列表
     */
    public abstract List<MaterialCaseVO> getCases();

    /**
     * 校验必填字段是否完整（送审时调用）
     */
    public abstract void checkRequired();
}
