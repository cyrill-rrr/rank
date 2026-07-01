package com.rank.application.material.factory;

import com.rank.domain.material.model.MaterialEntity;
import org.springframework.stereotype.Component;

/**
 * 材料工厂：用于创建材料实体
 */
@Component
public class MaterialFactory {

    /**
     * 创建初始状态的材料实体
     *
     * @param materialScene   材料场景
     * @param auditSubjectId  审计主体ID
     * @return 新建的MaterialEntity，状态为NO_DRAFT + PENDING_SUBMIT
     */
    public MaterialEntity createEntity(String materialScene, String auditSubjectId) {
        return new MaterialEntity(materialScene, auditSubjectId);
    }
}
