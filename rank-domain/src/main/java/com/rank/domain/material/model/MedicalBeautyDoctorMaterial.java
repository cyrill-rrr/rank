package com.rank.domain.material.model;

import java.util.Collections;
import java.util.List;

import com.rank.domain.common.exception.BizException;
import com.rank.domain.material.vo.MaterialAttachmentVO;
import com.rank.domain.material.vo.MaterialCaseVO;

/**
 * 医美医生榜材料内容
 */
public class MedicalBeautyDoctorMaterial extends AbstractMaterialContent {

    /**
     * 案例列表
     */
    private List<MaterialCaseVO> cases;

    /**
     * 资质附件列表
     */
    private List<MaterialAttachmentVO> qualifications;

    /**
     * 个人简介
     */
    private String introduction;

    public MedicalBeautyDoctorMaterial() {
        this.cases = Collections.emptyList();
        this.qualifications = Collections.emptyList();
    }

    public MedicalBeautyDoctorMaterial(List<MaterialCaseVO> cases, List<MaterialAttachmentVO> qualifications,
                                        String introduction) {
        this.cases = cases != null ? cases : Collections.emptyList();
        this.qualifications = qualifications != null ? qualifications : Collections.emptyList();
        this.introduction = introduction;
    }

    @Override
    public List<MaterialCaseVO> getCases() {
        return cases;
    }

    @Override
    public void checkRequired() {
        if (cases == null || cases.isEmpty()) {
            throw BizException.invalidParam("医美医生榜材料必须包含至少一个案例");
        }
        if (qualifications == null || qualifications.isEmpty()) {
            throw BizException.invalidParam("医美医生榜材料必须包含资质附件");
        }
    }

    public List<MaterialAttachmentVO> getQualifications() {
        return qualifications;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setCases(List<MaterialCaseVO> cases) {
        this.cases = cases;
    }

    public void setQualifications(List<MaterialAttachmentVO> qualifications) {
        this.qualifications = qualifications;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}
