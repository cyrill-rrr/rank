package com.rank.infrastructure.sign.acl;

import com.rank.domain.sign.repository.DoctorAcl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 医生列表查询ACL占位实现
 * 硬编码返回假数据，真实接入外部服务时替换
 */
@Slf4j
@Repository
public class DoctorAclImpl implements DoctorAcl {

    @Override
    public List<DoctorDTO> queryDoctorsByShopId(Long shopId, Boolean includeOffline) {
        log.info("[DoctorAclImpl queryDoctorsByShopId] 查询机构医生列表, shopId={}, includeOffline={}",
                shopId, includeOffline);

        // hardcoded返回假数据
        List<DoctorDTO> doctors = new ArrayList<>();
        doctors.add(new DoctorDTO(900001L, "张医生", 1, shopId));
        doctors.add(new DoctorDTO(900002L, "李医生", 1, shopId));
        doctors.add(new DoctorDTO(900003L, "王医生", 0, shopId));

        if (includeOffline != null && !includeOffline) {
            // 过滤掉不在线医生
            doctors.removeIf(d -> d.getOnlineStatus() == null || d.getOnlineStatus() == 0);
        }

        return doctors;
    }
}
