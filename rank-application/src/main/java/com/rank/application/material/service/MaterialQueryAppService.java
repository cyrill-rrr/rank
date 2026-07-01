package com.rank.application.material.service;

import com.rank.application.material.qry.MaterialQry;
import com.rank.domain.material.model.AbstractMaterialContent;
import com.rank.domain.material.model.MaterialEntity;
import com.rank.domain.material.repository.MaterialRepository;
import com.rank.domain.material.service.MaterialSceneStrategy;
import com.rank.domain.material.shared.MaterialAuditStatusEnum;
import com.rank.domain.material.shared.MaterialDraftStatusEnum;
import com.rank.domain.material.vo.CurrentMaterialResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 材料读操作编排服务
 */
@Slf4j
@Service
public class MaterialQueryAppService {

    private final MaterialRepository materialRepository;
    private final List<MaterialSceneStrategy> strategies;

    public MaterialQueryAppService(MaterialRepository materialRepository,
                                    List<MaterialSceneStrategy> strategies) {
        this.materialRepository = materialRepository;
        this.strategies = strategies;
    }

    /**
     * 查询当前材料信息
     *
     * @param qry 查询条件
     * @return 当前材料查询结果，无材料时返回null
     */
    public CurrentMaterialResult queryCurrentMaterial(MaterialQry qry) {
        log.info("ccc-workflow-mock-subagent [MaterialQueryAppService queryCurrentMaterial] entry, materialScene={}, auditSubjectId={}",
                qry.getMaterialScene(), qry.getAuditSubjectId());
        // 1. 查数据
        log.info("[MaterialQueryAppService queryCurrentMaterial] 查询材料记录, materialScene={}, auditSubjectId={}",
                qry.getMaterialScene(), qry.getAuditSubjectId());
        MaterialEntity entity = materialRepository.findByBizKey(qry.getMaterialScene(), qry.getAuditSubjectId());

        // 2. 无材料返回null
        if (entity == null || entity.getId() == null) {
            log.info("[MaterialQueryAppService queryCurrentMaterial] 未找到材料记录, materialScene={}, auditSubjectId={}",
                    qry.getMaterialScene(), qry.getAuditSubjectId());
            log.info("ccc-workflow-mock-subagent [MaterialQueryAppService queryCurrentMaterial] return null (no material), materialScene={}, auditSubjectId={}",
                    qry.getMaterialScene(), qry.getAuditSubjectId());
            return null;
        }

        // 3. 确定展示的材料内容和状态
        AbstractMaterialContent displayContent;
        MaterialDraftStatusEnum hasDraft;
        MaterialAuditStatusEnum auditStatus = entity.getAuditStatus();

        if (MaterialDraftStatusEnum.HAS_DRAFT.equals(entity.getHasDraft())) {
            // 有草稿：显示草稿内容
            displayContent = entity.getDraftMaterialContent();
            hasDraft = MaterialDraftStatusEnum.HAS_DRAFT;
        } else {
            // 无草稿：显示正式材料内容
            displayContent = entity.getMaterialContent();
            hasDraft = MaterialDraftStatusEnum.NO_DRAFT;
        }

        // 4. 序列化为JSON
        String materialJsonStr = serializeContent(qry.getMaterialScene(), displayContent, strategies);

        // 5. 组装结果
        CurrentMaterialResult result = new CurrentMaterialResult(
                entity.getId(),
                hasDraft,
                auditStatus,
                materialJsonStr,
                entity.getRejectReason()
        );
        log.info("ccc-workflow-mock-subagent [MaterialQueryAppService queryCurrentMaterial] success return, materialId={}", entity.getId());
        return result;
    }

    /**
     * 序列化材料内容为JSON字符串
     */
    private static String serializeContent(String materialScene, AbstractMaterialContent content,
                                            List<MaterialSceneStrategy> strategies) {
        if (content == null) {
            return null;
        }
        for (MaterialSceneStrategy strategy : strategies) {
            if (strategy.supportScene().equals(materialScene)) {
                return strategy.toCurrentMaterialJson(content);
            }
        }
        return null;
    }
}
