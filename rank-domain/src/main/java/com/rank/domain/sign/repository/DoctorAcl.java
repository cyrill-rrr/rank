package com.rank.domain.sign.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 医生列表查询ACL防腐层接口
 * 本期为占位实现，外部服务细节待后续补齐
 */
public interface DoctorAcl {

    /**
     * 查询机构下医生列表
     *
     * @param shopId         机构ID
     * @param includeOffline 是否包含不在线医生
     * @return 医生DTO列表
     */
    List<DoctorDTO> queryDoctorsByShopId(Long shopId, Boolean includeOffline);

    /**
     * 医生信息DTO，供应用层转换
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class DoctorDTO {
        private Long techId;           // 医生ID
        private String doctorName;     // 医生名称
        private Integer onlineStatus;  // 在线状态
        private Long shopId;           // 所属机构ID
    }
}
