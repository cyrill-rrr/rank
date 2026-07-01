package com.rank.infrastructure.material.mq;

import com.rank.application.material.service.MaterialCommandAppService;
import com.rank.domain.material.vo.UapAuditInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * UAP审核结果MQ回调消费
 * MQ注解（@MafkaListener）待接入时补充，当前为@Componet骨架
 */
@Slf4j
@Component
public class UapAuditCallbackConsumer {

    private final MaterialCommandAppService materialCommandAppService;

    public UapAuditCallbackConsumer(MaterialCommandAppService materialCommandAppService) {
        this.materialCommandAppService = materialCommandAppService;
    }

    /**
     * 处理UAP审核回调消息
     *
     * @param event MQ消息体
     */
    public void handleAuditCallback(UapAuditCallbackMafkaEvent event) {
        long start = System.currentTimeMillis();
        log.info("ccc-workflow-mock-subagent [UapAuditCallbackConsumer handleAuditCallback] entry, uapUniqueId={}, auditStatus={}",
                event.getUapUniqueId(), event.getAuditStatus());
        try {
            log.info("[UapAuditCallbackConsumer handleAuditCallback] 收到UAP审核回调, uapUniqueId={}, auditStatus={}",
                    event.getUapUniqueId(), event.getAuditStatus());

            // 委托AppService处理审核回调业务逻辑
            UapAuditInfoVO auditInfo = new UapAuditInfoVO();
            auditInfo.setUapUniqueId(event.getUapUniqueId());
            auditInfo.setAuditStatus(event.getAuditStatus());
            auditInfo.setRejectReason(event.getRejectReason());
            auditInfo.setPassageJson(event.getPassageJson());

            materialCommandAppService.handleAuditCallback(auditInfo);
        } catch (Exception e) {
            log.error("ccc-workflow-mock-subagent [UapAuditCallbackConsumer handleAuditCallback] catch Exception, uapUniqueId={}", event.getUapUniqueId(), e);
            log.error("[UapAuditCallbackConsumer handleAuditCallback] 处理UAP审核回调异常, uapUniqueId={}",
                    event.getUapUniqueId(), e);
        } finally {
            log.info("[UapAuditCallbackConsumer handleAuditCallback] 耗时:{}ms", System.currentTimeMillis() - start);
        }
    }
}
