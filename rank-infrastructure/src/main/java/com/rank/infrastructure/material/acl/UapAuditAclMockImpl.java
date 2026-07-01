package com.rank.infrastructure.material.acl;

import com.rank.domain.material.repository.UapAuditAcl;
import com.rank.domain.material.vo.UapAuditRequest;
import com.rank.domain.material.vo.UapAuditResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * UAP审核ACL Mock实现
 * 真实接入UAP RPC时替换为UapAuditAclImpl
 */
@Slf4j
@Repository
public class UapAuditAclMockImpl implements UapAuditAcl {

    @Override
    public UapAuditResult submitAudit(UapAuditRequest request) {
        log.info("[UapAuditAclMockImpl submitAudit] 模拟UAP审核提交, template={}", request.getTemplate());
        String uuid = UUID.randomUUID().toString();
        log.info("[UapAuditAclMockImpl submitAudit] 模拟UAP审核成功, uapUniqueId={}", uuid);
        return UapAuditResult.success(uuid);
    }
}
