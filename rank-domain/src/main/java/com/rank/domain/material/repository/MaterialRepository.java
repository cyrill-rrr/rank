package com.rank.domain.material.repository;

import com.rank.domain.material.model.MaterialEntity;

/**
 * 材料提报仓储接口
 */
public interface MaterialRepository {

    /**
     * 按业务键（materialScene + auditSubjectId）查询材料记录
     *
     * @param materialScene   材料场景
     * @param auditSubjectId  审计主体ID
     * @return 材料实体，不存在返回null
     */
    MaterialEntity findByBizKey(String materialScene, String auditSubjectId);

    /**
     * 按uapUniqueId查询材料记录
     *
     * @param uapUniqueId UAP审核唯一标识
     * @return 材料实体，不存在返回null
     */
    MaterialEntity findByUapUniqueId(String uapUniqueId);

    /**
     * 新增或更新材料记录
     *
     * @param materialEntity 材料实体
     */
    void saveOrUpdate(MaterialEntity materialEntity);
}
