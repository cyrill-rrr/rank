package com.rank.application.sign.factory;

import com.rank.domain.sign.model.SignEntity;
import com.rank.domain.sign.shared.SignStatusEnum;
import org.springframework.stereotype.Component;

/**
 * 报名工厂：用于创建报名实体
 */
@Component
public class SignFactory {

    /**
     * 创建已提报状态的报名实体
     *
     * @param signScene    报名场景
     * @param subjectId    报名主体ID
     * @param indexShopId  冗余机构ID
     * @param projectCode  提报项目code
     * @return 新建的SignEntity，状态为SIGNED
     */
    public SignEntity createSignedEntity(String signScene, Long subjectId, Long indexShopId,
                                         Integer projectCode) {
        return new SignEntity(signScene, subjectId, indexShopId, projectCode, SignStatusEnum.SIGNED);
    }
}
