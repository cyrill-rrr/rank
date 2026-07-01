package com.rank.domain.material.model;

import java.util.Collections;
import java.util.List;

import com.rank.domain.common.exception.BizException;
import com.rank.domain.material.vo.MaterialCaseVO;

/**
 * 口腔医生榜材料内容
 */
public class OralDoctorMaterial extends AbstractMaterialContent {

    /**
     * 病例列表
     */
    private List<MaterialCaseVO> cases;

    /**
     * 执业证书编号
     */
    private String licenseNumber;

    public OralDoctorMaterial() {
        this.cases = Collections.emptyList();
    }

    public OralDoctorMaterial(List<MaterialCaseVO> cases, String licenseNumber) {
        this.cases = cases != null ? cases : Collections.emptyList();
        this.licenseNumber = licenseNumber;
    }

    @Override
    public List<MaterialCaseVO> getCases() {
        return cases;
    }

    @Override
    public void checkRequired() {
        if (cases == null || cases.isEmpty()) {
            throw BizException.invalidParam("口腔医生榜材料必须包含至少一个病例");
        }
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setCases(List<MaterialCaseVO> cases) {
        this.cases = cases;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
}
