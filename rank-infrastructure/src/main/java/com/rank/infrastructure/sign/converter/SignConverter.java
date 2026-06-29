package com.rank.infrastructure.sign.converter;

import com.rank.domain.sign.model.SignEntity;
import com.rank.domain.sign.shared.SignStatusEnum;
import com.rank.infrastructure.sign.po.SignPO;
import org.springframework.stereotype.Component;

/**
 * SignEntity <-> SignPO 转换器
 * 处理枚举与数据库字符串的显式映射
 */
@Component
public class SignConverter {

    /**
     * PO 转 Entity
     */
    public SignEntity toEntity(SignPO po) {
        if (po == null) {
            return null;
        }
        SignEntity entity = new SignEntity();
        entity.setId(po.getId());
        // signScene直接使用String，不转枚举
        entity.setSignScene(po.getSignScene());
        entity.setSubjectId(po.getSubjectId());
        entity.setIndexShopId(po.getIndexShopId());
        entity.setProjectCode(po.getProjectCode());
        // status从String转Enum
        entity.setStatus(mapStatusToEnum(po.getStatus()));
        entity.setCreatedTime(po.getCreatedTime());
        entity.setUpdatedTime(po.getUpdatedTime());
        return entity;
    }

    /**
     * Entity 转 PO
     */
    public SignPO toPO(SignEntity entity) {
        if (entity == null) {
            return null;
        }
        SignPO po = new SignPO();
        po.setId(entity.getId());
        po.setSignScene(entity.getSignScene());
        po.setSubjectId(entity.getSubjectId());
        po.setIndexShopId(entity.getIndexShopId());
        po.setProjectCode(entity.getProjectCode());
        // status从Enum转String
        po.setStatus(mapStatusToDbStr(entity.getStatus()));
        po.setCreatedTime(entity.getCreatedTime());
        po.setUpdatedTime(entity.getUpdatedTime());
        return po;
    }

    /**
     * 数据库字符串状态转枚举
     */
    private SignStatusEnum mapStatusToEnum(String status) {
        if (status == null) {
            return null;
        }
        try {
            return SignStatusEnum.valueOf(status);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 枚举转数据库字符串
     */
    private String mapStatusToDbStr(SignStatusEnum status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }
}
