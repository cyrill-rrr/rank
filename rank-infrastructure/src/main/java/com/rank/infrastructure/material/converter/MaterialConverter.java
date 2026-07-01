package com.rank.infrastructure.material.converter;

import com.rank.domain.material.model.AbstractMaterialContent;
import com.rank.domain.material.model.MaterialEntity;
import com.rank.domain.material.shared.MaterialAuditStatusEnum;
import com.rank.domain.material.shared.MaterialDraftStatusEnum;
import com.rank.infrastructure.material.po.MaterialPO;
import org.springframework.stereotype.Component;

/**
 * MaterialEntity <-> MaterialPO 转换器
 * 处理枚举与数据库字符串的显式映射，不处理JSON解析
 */
@Component
public class MaterialConverter {

    /**
     * PO 转 Entity（不含材料内容反序列化，由调用方设置）
     */
    public MaterialEntity toEntity(MaterialPO po) {
        if (po == null) {
            return null;
        }
        MaterialEntity entity = new MaterialEntity();
        entity.setId(po.getId());
        entity.setCreatedTime(po.getCreatedTime());
        entity.setUpdatedTime(po.getUpdatedTime());
        return entity;
    }

    /**
     * Entity 转 PO
     *
     * @param entity             材料实体
     * @param draftJson          序列化后的草稿内容JSON（可为null）
     * @param materialJson       序列化后的正式材料内容JSON（可为null）
     * @return MaterialPO
     */
    public MaterialPO toPO(MaterialEntity entity, String draftJson, String materialJson) {
        if (entity == null) {
            return null;
        }
        MaterialPO po = new MaterialPO();
        po.setId(entity.getId());
        po.setMaterialScene(entity.getMaterialScene());
        po.setAuditSubjectId(entity.getAuditSubjectId());
        po.setHasDraft(mapDraftStatusToDbStr(entity.getHasDraft()));
        po.setAuditStatus(mapAuditStatusToDbStr(entity.getAuditStatus()));
        po.setDraftMaterialJsonStr(draftJson);
        po.setMaterialJsonStr(materialJson);
        po.setUapUniqueId(entity.getUapUniqueId());
        po.setRejectReason(entity.getRejectReason());
        po.setCreatedTime(entity.getCreatedTime());
        po.setUpdatedTime(entity.getUpdatedTime());
        return po;
    }

    /**
     * 草稿状态枚举转数据库字符串
     */
    private static String mapDraftStatusToDbStr(MaterialDraftStatusEnum status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }

    /**
     * 审核状态枚举转数据库字符串
     */
    private static String mapAuditStatusToDbStr(MaterialAuditStatusEnum status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }
}
