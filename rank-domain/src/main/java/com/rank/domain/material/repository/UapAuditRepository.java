package com.rank.domain.material.repository;

import com.rank.domain.material.vo.UapAuditRequest;
import com.rank.domain.material.vo.UapAuditResult;

/**
 * UAP审核仓储接口：屏蔽外部RPC调用细节
 */
public interface UapAuditRepository {

    /**
     * 提交UAP审核
     *
     * @param request UAP审核请求
     * @return UAP审核结果
     */
    UapAuditResult submitAudit(UapAuditRequest request);
}
