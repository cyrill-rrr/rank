package com.rank.domain.material.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rank.domain.common.exception.BizException;
import com.rank.domain.material.model.AbstractMaterialContent;
import com.rank.domain.material.model.MedicalBeautyDoctorMaterial;
import com.rank.domain.material.vo.CurrentMaterialResult;
import com.rank.domain.material.vo.MaterialConfigVO;
import com.rank.domain.material.vo.UapAuditRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 医美医生榜材料策略实现
 */
@Slf4j
@Service
public class MedicalBeautyDoctorMaterialStrategy implements MaterialSceneStrategy {

    @Override
    public String supportScene() {
        return "MEDICAL_BEAUTY_DOCTOR";
    }

    @Override
    public AbstractMaterialContent parse(String jsonStr) {
        try {
            if (jsonStr == null || jsonStr.trim().isEmpty() || "{}".equals(jsonStr.trim())) {
                return new MedicalBeautyDoctorMaterial();
            }
            return JSON.parseObject(jsonStr, MedicalBeautyDoctorMaterial.class);
        } catch (Exception e) {
            log.error("[MedicalBeautyDoctorMaterialStrategy parse] JSON解析失败, jsonStr={}", jsonStr, e);
            throw BizException.invalidParam("医美医生榜材料JSON解析失败");
        }
    }

    @Override
    public void checkRequired(AbstractMaterialContent content) {
        if (!(content instanceof MedicalBeautyDoctorMaterial)) {
            throw BizException.invalidParam("材料类型不匹配，期望医美医生榜材料");
        }
        ((MedicalBeautyDoctorMaterial) content).checkRequired();
    }

    @Override
    public UapAuditRequest buildUapAuditRequest(AbstractMaterialContent content, MaterialConfigVO config) {
        String dataJson = JSON.toJSONString(content);
        return new UapAuditRequest(config.getUapTemplate(), dataJson);
    }

    @Override
    public String toCurrentMaterialJson(AbstractMaterialContent entityContent) {
        if (entityContent == null) {
            return null;
        }
        return JSON.toJSONString(entityContent);
    }
}
