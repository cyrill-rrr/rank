package com.rank.domain.material.service;

import com.rank.domain.material.model.AbstractMaterialContent;
import com.rank.domain.material.vo.CurrentMaterialResult;
import com.rank.domain.material.vo.MaterialConfigVO;
import com.rank.domain.material.vo.UapAuditRequest;

/**
 * 材料场景策略接口：按材料场景解析、校验、构建UAP请求。
 */
public interface MaterialSceneStrategy {

    /**
     * 当前策略支持的材料场景
     */
    String supportScene();

    /**
     * 将JSON字符串解析为领域层材料内容对象
     *
     * @param jsonStr 材料内容JSON字符串
     * @return 材料内容领域对象
     */
    AbstractMaterialContent parse(String jsonStr);

    /**
     * 校验必填字段
     *
     * @param content 材料内容
     */
    void checkRequired(AbstractMaterialContent content);

    /**
     * 构建UAP审核请求
     *
     * @param content   材料内容
     * @param config    材料配置（含uapTemplate）
     * @return UAP审核请求
     */
    UapAuditRequest buildUapAuditRequest(AbstractMaterialContent content, MaterialConfigVO config);

    /**
     * 构建当前材料查询结果（含材料JSON序列化）
     *
     * @param entityContent 实体中的材料内容
     * @return CurrentMaterialResult中使用的材料JSON字符串
     */
    String toCurrentMaterialJson(AbstractMaterialContent entityContent);
}
