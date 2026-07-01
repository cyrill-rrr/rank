package com.rank.application.material.service;

import com.rank.application.material.command.MaterialCommand;
import com.rank.application.material.factory.MaterialFactory;
import com.rank.domain.common.exception.BizException;
import com.rank.domain.material.model.AbstractMaterialContent;
import com.rank.domain.material.model.MaterialEntity;
import com.rank.domain.material.repository.MaterialConfigRepository;
import com.rank.domain.material.repository.MaterialRepository;
import com.rank.domain.material.repository.UapAuditRepository;
import com.rank.domain.material.service.MaterialDomainService;
import com.rank.domain.material.service.MaterialSceneStrategy;
import com.rank.domain.material.shared.MaterialAuditStatusEnum;
import com.rank.domain.material.shared.MaterialOperationEnum;
import com.rank.domain.material.vo.MaterialConfigVO;
import com.rank.domain.material.vo.UapAuditInfoVO;
import com.rank.domain.material.vo.UapAuditRequest;
import com.rank.domain.material.vo.UapAuditResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 材料写操作编排服务
 */
@Slf4j
@Service
public class MaterialCommandAppService {

    private final MaterialConfigRepository materialConfigRepository;
    private final MaterialRepository materialRepository;
    private final MaterialDomainService materialDomainService;
    private final MaterialFactory materialFactory;
    private final List<MaterialSceneStrategy> strategies;
    private final UapAuditRepository uapAuditRepository;

    public MaterialCommandAppService(MaterialConfigRepository materialConfigRepository,
                                      MaterialRepository materialRepository,
                                      MaterialDomainService materialDomainService,
                                      MaterialFactory materialFactory,
                                      List<MaterialSceneStrategy> strategies,
                                      UapAuditRepository uapAuditRepository) {
        this.materialConfigRepository = materialConfigRepository;
        this.materialRepository = materialRepository;
        this.materialDomainService = materialDomainService;
        this.materialFactory = materialFactory;
        this.strategies = strategies;
        this.uapAuditRepository = uapAuditRepository;
    }

    /**
     * 材料操作编排（保存草稿或送审）
     *
     * @param command 操作指令
     * @return 材料记录主键ID
     */
    public Long operateMaterial(MaterialCommand command) {
        // 1. 读取材料配置（窗口期 + uapTemplate）
        log.info("[MaterialCommandAppService operateMaterial] 读取材料配置, materialScene={}", command.getMaterialScene());
        MaterialConfigVO config = materialConfigRepository.findByScene(command.getMaterialScene());
        if (config == null) {
            throw BizException.notFound("材料场景配置不存在");
        }

        // 2. 领域服务校验（窗口期 + 报名记录存在性）
        materialDomainService.checkCanOperateMaterial(
                command.getMaterialScene(), command.getAuditSubjectId(), config);

        // 3. 查找材料场景策略
        MaterialSceneStrategy strategy = findStrategy(command.getMaterialScene());

        // 4. 解析材料内容JSON为领域对象
        AbstractMaterialContent content = strategy.parse(command.getMaterialJsonStr());

        // 5. 查询或创建材料实体
        MaterialEntity entity = materialRepository.findByBizKey(command.getMaterialScene(), command.getAuditSubjectId());
        boolean isNew = (entity == null);
        if (isNew) {
            entity = materialFactory.createEntity(command.getMaterialScene(), command.getAuditSubjectId());
        }

        // 6. 按操作类型执行不同路径
        if (MaterialOperationEnum.SAVE_DRAFT.name().equals(command.getOperationType())) {
            // 保存草稿：不校验必填，允许空材料
            return handleSaveDraft(entity, content, strategy, isNew);
        } else if (MaterialOperationEnum.SUBMIT_AUDIT.name().equals(command.getOperationType())) {
            // 送审：必须先校验非空，再调UAP
            return handleSubmitAudit(entity, content, config, strategy, isNew);
        } else {
            throw BizException.invalidParam("不支持的材质操作类型: " + command.getOperationType());
        }
    }

    /**
     * 处理保存草稿
     */
    private Long handleSaveDraft(MaterialEntity entity, AbstractMaterialContent content,
                                  MaterialSceneStrategy strategy, boolean isNew) {
        log.info("[MaterialCommandAppService handleSaveDraft] 开始保存草稿, materialScene={}, auditSubjectId={}",
                entity.getMaterialScene(), entity.getAuditSubjectId());

        // 保存草稿：设置草稿内容和状态
        entity.saveDraft(content);
        try {
            materialRepository.saveOrUpdate(entity);
        } catch (Exception e) {
            log.error("[MaterialCommandAppService handleSaveDraft] 草稿保存失败, materialScene={}, auditSubjectId={}",
                    entity.getMaterialScene(), entity.getAuditSubjectId(), e);
            // DB不可用时返回materialId保持部分已完成状态
            return entity.getId();
        }

        log.info("[MaterialCommandAppService handleSaveDraft] 草稿保存成功, materialId={}", entity.getId());
        return entity.getId();
    }

    /**
     * 处理送审
     */
    private Long handleSubmitAudit(MaterialEntity entity, AbstractMaterialContent content,
                                    MaterialConfigVO config, MaterialSceneStrategy strategy,
                                    boolean isNew) {
        log.info("[MaterialCommandAppService handleSubmitAudit] 开始送审, materialScene={}, auditSubjectId={}",
                entity.getMaterialScene(), entity.getAuditSubjectId());

        // 如果不是新记录，已有的草稿内容优先
        if (!isNew && entity.getDraftMaterialContent() != null && content == null) {
            content = entity.getDraftMaterialContent();
        }

        // 校验非空：送审时必须不为空
        if (content == null) {
            throw BizException.invalidParam("送审材料内容不能为空");
        }
        strategy.checkRequired(content);

        // 保存草稿（先把内容存为草稿）
        entity.saveDraft(content);
        try {
            materialRepository.saveOrUpdate(entity);
        } catch (Exception e) {
            log.error("[MaterialCommandAppService handleSubmitAudit] 草稿保存失败, materialScene={}, auditSubjectId={}",
                    entity.getMaterialScene(), entity.getAuditSubjectId(), e);
            return entity.getId();
        }

        // 调UAP审核
        UapAuditRequest auditRequest = strategy.buildUapAuditRequest(content, config);
        UapAuditResult auditResult;
        try {
            auditResult = uapAuditRepository.submitAudit(auditRequest);
        } catch (Exception e) {
            log.error("[MaterialCommandAppService handleSubmitAudit] UAP审核调用异常, materialId={}", entity.getId(), e);
            // UAP失败时DB保持HAS_DRAFT+PENDING_SUBMIT，返回materialId
            return entity.getId();
        }

        if (!auditResult.isSuccess()) {
            log.error("[MaterialCommandAppService handleSubmitAudit] UAP审核返回失败, materialId={}, errorMsg={}",
                    entity.getId(), auditResult.getErrorMsg());
            return entity.getId();
        }

        // UAP成功：标记审核中
        entity.markUnderReview(auditResult.getUapUniqueId());
        try {
            materialRepository.saveOrUpdate(entity);
        } catch (Exception e) {
            log.error("[MaterialCommandAppService handleSubmitAudit] 更新审核状态失败, materialId={}", entity.getId(), e);
            // DB不可用，返回materialId
            return entity.getId();
        }

        log.info("[MaterialCommandAppService handleSubmitAudit] 送审成功, materialId={}, uapUniqueId={}",
                entity.getId(), auditResult.getUapUniqueId());
        return entity.getId();
    }

    /**
     * 处理UAP审核回调
     *
     * @param auditInfo UAP审核回调信息
     */
    public void handleAuditCallback(UapAuditInfoVO auditInfo) {
        // 1. 按uapUniqueId查找材料记录
        MaterialEntity entity = materialRepository.findByUapUniqueId(auditInfo.getUapUniqueId());
        if (entity == null) {
            log.error("[MaterialCommandAppService handleAuditCallback] 未找到材料记录, uapUniqueId={}",
                    auditInfo.getUapUniqueId());
            return;
        }

        // 2. 校验透传字段（记录日志但不阻断）
        try {
            String passageJson = auditInfo.getPassageJson();
            if (passageJson != null && !passageJson.trim().isEmpty()) {
                com.alibaba.fastjson.JSONObject passage = com.alibaba.fastjson.JSON.parseObject(passageJson);
                String passageMaterialScene = passage.getString("materialScene");
                String passageAuditSubjectId = passage.getString("auditSubjectId");
                if (passageMaterialScene != null && !passageMaterialScene.equals(entity.getMaterialScene())) {
                    log.info("[MaterialCommandAppService handleAuditCallback] 透传materialScene不匹配, passage={}, db={}",
                            passageMaterialScene, entity.getMaterialScene());
                }
                if (passageAuditSubjectId != null && !passageAuditSubjectId.equals(entity.getAuditSubjectId())) {
                    log.info("[MaterialCommandAppService handleAuditCallback] 透传auditSubjectId不匹配, passage={}, db={}",
                            passageAuditSubjectId, entity.getAuditSubjectId());
                }
            }
        } catch (Exception e) {
            log.info("[MaterialCommandAppService handleAuditCallback] 透传字段解析异常, uapUniqueId={}", auditInfo.getUapUniqueId(), e);
        }

        // 3. 映射审核状态
        MaterialAuditStatusEnum resultStatus;
        if ("APPROVED".equals(auditInfo.getAuditStatus())) {
            resultStatus = MaterialAuditStatusEnum.APPROVED;
        } else if ("REJECTED".equals(auditInfo.getAuditStatus())) {
            resultStatus = MaterialAuditStatusEnum.REJECTED;
        } else {
            log.info("[MaterialCommandAppService handleAuditCallback] 未知审核状态, status={}", auditInfo.getAuditStatus());
            return;
        }

        // 4. 应用审核结果（非审核中状态幂等忽略）
        boolean updated = entity.applyAuditResult(resultStatus, auditInfo.getRejectReason());
        if (updated) {
            materialRepository.saveOrUpdate(entity);
            log.info("[MaterialCommandAppService handleAuditCallback] 审核回调处理成功, uapUniqueId={}, resultStatus={}",
                    auditInfo.getUapUniqueId(), resultStatus);
        } else {
            log.info("[MaterialCommandAppService handleAuditCallback] 非审核中状态忽略回调, uapUniqueId={}, currentStatus={}",
                    auditInfo.getUapUniqueId(), entity.getAuditStatus());
        }
    }

    /**
     * 查找材料场景对应的策略
     */
    private MaterialSceneStrategy findStrategy(String materialScene) {
        if (strategies != null) {
            for (MaterialSceneStrategy strategy : strategies) {
                if (strategy.supportScene().equals(materialScene)) {
                    return strategy;
                }
            }
        }
        throw BizException.invalidParam("未找到材料场景策略: " + materialScene);
    }
}
