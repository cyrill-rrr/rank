package com.rank.domain.sign.repository;

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
    class DoctorDTO {
        private Long techId;
        private String doctorName;
        private Integer onlineStatus;
        private Long shopId;

        public DoctorDTO() {
        }

        public DoctorDTO(Long techId, String doctorName, Integer onlineStatus, Long shopId) {
            this.techId = techId;
            this.doctorName = doctorName;
            this.onlineStatus = onlineStatus;
            this.shopId = shopId;
        }

        public Long getTechId() {
            return techId;
        }

        public void setTechId(Long techId) {
            this.techId = techId;
        }

        public String getDoctorName() {
            return doctorName;
        }

        public void setDoctorName(String doctorName) {
            this.doctorName = doctorName;
        }

        public Integer getOnlineStatus() {
            return onlineStatus;
        }

        public void setOnlineStatus(Integer onlineStatus) {
            this.onlineStatus = onlineStatus;
        }

        public Long getShopId() {
            return shopId;
        }

        public void setShopId(Long shopId) {
            this.shopId = shopId;
        }
    }
}
