package com.rank.infrastructure.material.acl;

import com.alibaba.fastjson.JSON;
import com.rank.domain.common.CccWorkflowMockSwitch;
import com.rank.domain.material.repository.UapAuditRepository;
import com.rank.domain.material.vo.UapAuditRequest;
import com.rank.domain.material.vo.UapAuditResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * UAP审核仓储Mock实现
 * 真实接入UAP RPC时替换为UapAuditRepositoryImpl
 */
@Slf4j
@Repository
public class UapAuditAclMockImpl implements UapAuditRepository {

    @Override
    public UapAuditResult submitAudit(UapAuditRequest request) {
        if (CccWorkflowMockSwitch.isEnabled()) {
            String mockUuid = "mock-uap-" + UUID.randomUUID().toString();
            UapAuditResult mockResult = UapAuditResult.success(mockUuid);
            log.info("ccc-workflow-mock-subagent [UapAuditAclMockImpl submitAudit] mock return result={}", JSON.toJSONString(mockResult));
            return mockResult;
        }
        log.info("[UapAuditAclMockImpl submitAudit] 模拟UAP审核提交, template={}", request.getTemplate());
        String uuid = UUID.randomUUID().toString();
        log.info("[UapAuditAclMockImpl submitAudit] 模拟UAP审核成功, uapUniqueId={}", uuid);
        return UapAuditResult.success(uuid);
    }
}
