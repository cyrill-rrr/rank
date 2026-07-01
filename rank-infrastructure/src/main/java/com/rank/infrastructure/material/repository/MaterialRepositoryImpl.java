package com.rank.infrastructure.material.repository;

import com.rank.domain.material.model.AbstractMaterialContent;
import com.rank.domain.material.model.MaterialEntity;
import com.rank.domain.material.repository.MaterialRepository;
import com.rank.domain.material.service.MaterialSceneStrategy;
import com.rank.domain.material.shared.MaterialAuditStatusEnum;
import com.rank.domain.material.shared.MaterialDraftStatusEnum;
import com.rank.infrastructure.material.converter.MaterialConverter;
import com.rank.infrastructure.material.mapper.MaterialMapper;
import com.rank.infrastructure.material.po.MaterialPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 材料提报仓储实现
 */
@Slf4j
@Repository
public class MaterialRepositoryImpl implements MaterialRepository {

    private final MaterialMapper materialMapper;
    private final MaterialConverter materialConverter;
    private final List<MaterialSceneStrategy> strategies;

    public MaterialRepositoryImpl(MaterialMapper materialMapper,
                                   MaterialConverter materialConverter,
                                   List<MaterialSceneStrategy> strategies) {
        this.materialMapper = materialMapper;
        this.materialConverter = materialConverter;
        this.strategies = strategies;
    }

    @Override
    public MaterialEntity findByBizKey(String materialScene, String auditSubjectId) {
        try {
            MaterialPO po = materialMapper.selectByBizKey(materialScene, auditSubjectId);
            if (po == null) {
                return null;
            }
            // Converter只做字段映射
            MaterialEntity entity = materialConverter.toEntity(po);
            if (entity == null) {
                return null;
            }
            // RepositoryImpl负责将JSON解析为领域对象
            AbstractMaterialContent draftContent = parseJson(materialScene, po.getDraftMaterialJsonStr(), strategies);
            AbstractMaterialContent materialContent = parseJson(materialScene, po.getMaterialJsonStr(), strategies);

            entity.setMaterialScene(po.getMaterialScene());
            entity.setAuditSubjectId(po.getAuditSubjectId());
            entity.setHasDraft(mapDbStrToDraftStatus(po.getHasDraft()));
            entity.setAuditStatus(mapDbStrToAuditStatus(po.getAuditStatus()));
            entity.setDraftMaterialContent(draftContent);
            entity.setMaterialContent(materialContent);
            entity.setUapUniqueId(po.getUapUniqueId());
            entity.setRejectReason(po.getRejectReason());
            entity.setCreatedTime(po.getCreatedTime());
            entity.setUpdatedTime(po.getUpdatedTime());
            return entity;
        } catch (Exception e) {
            log.error("[MaterialRepositoryImpl findByBizKey] 查询材料记录异常, materialScene={}, auditSubjectId={}",
                    materialScene, auditSubjectId, e);
            return null;
        }
    }

    @Override
    public MaterialEntity findByUapUniqueId(String uapUniqueId) {
        try {
            MaterialPO po = materialMapper.selectByUapUniqueId(uapUniqueId);
            if (po == null) {
                return null;
            }
            // Converter只做字段映射
            MaterialEntity entity = materialConverter.toEntity(po);
            if (entity == null) {
                return null;
            }
            // RepositoryImpl负责将JSON解析为领域对象
            AbstractMaterialContent draftContent = parseJson(po.getMaterialScene(), po.getDraftMaterialJsonStr(), strategies);
            AbstractMaterialContent materialContent = parseJson(po.getMaterialScene(), po.getMaterialJsonStr(), strategies);

            entity.setMaterialScene(po.getMaterialScene());
            entity.setAuditSubjectId(po.getAuditSubjectId());
            entity.setHasDraft(mapDbStrToDraftStatus(po.getHasDraft()));
            entity.setAuditStatus(mapDbStrToAuditStatus(po.getAuditStatus()));
            entity.setDraftMaterialContent(draftContent);
            entity.setMaterialContent(materialContent);
            entity.setUapUniqueId(po.getUapUniqueId());
            entity.setRejectReason(po.getRejectReason());
            entity.setCreatedTime(po.getCreatedTime());
            entity.setUpdatedTime(po.getUpdatedTime());
            return entity;
        } catch (Exception e) {
            log.error("[MaterialRepositoryImpl findByUapUniqueId] 按uapUniqueId查询异常, uapUniqueId={}",
                    uapUniqueId, e);
            return null;
        }
    }

    @Override
    public void saveOrUpdate(MaterialEntity materialEntity) {
        try {
            // 序列化材料内容为JSON
            MaterialSceneStrategy strategy = findStrategy(materialEntity.getMaterialScene(), strategies);
            String draftJson = serializeContent(strategy, materialEntity.getDraftMaterialContent());
            String materialJson = serializeContent(strategy, materialEntity.getMaterialContent());

            MaterialPO po = materialConverter.toPO(materialEntity, draftJson, materialJson);
            if (po == null) {
                log.info("[MaterialRepositoryImpl saveOrUpdate] 转换PO为空, materialScene={}, auditSubjectId={}",
                        materialEntity.getMaterialScene(), materialEntity.getAuditSubjectId());
                return;
            }
            if (po.getId() == null) {
                materialMapper.insert(po);
                materialEntity.setId(po.getId());
                materialEntity.setCreatedTime(po.getCreatedTime());
                materialEntity.setUpdatedTime(po.getUpdatedTime());
            } else {
                materialMapper.updateByBizKey(po);
                materialEntity.setUpdatedTime(po.getUpdatedTime());
            }
        } catch (Exception e) {
            log.error("[MaterialRepositoryImpl saveOrUpdate] 保存材料记录异常, materialScene={}, auditSubjectId={}",
                    materialEntity.getMaterialScene(), materialEntity.getAuditSubjectId(), e);
            throw e;
        }
    }

    /**
     * 根据材料场景解析JSON字符串
     */
    private static AbstractMaterialContent parseJson(String materialScene, String jsonStr,
                                                      List<MaterialSceneStrategy> strategies) {
        if (jsonStr == null) {
            return null;
        }
        for (MaterialSceneStrategy strategy : strategies) {
            if (strategy.supportScene().equals(materialScene)) {
                return strategy.parse(jsonStr);
            }
        }
        log.error("[MaterialRepositoryImpl parseJson] 未找到材料场景策略, materialScene={}", materialScene);
        return null;
    }

    /**
     * 序列化材料内容为JSON字符串
     */
    private static String serializeContent(MaterialSceneStrategy strategy, AbstractMaterialContent content) {
        if (content == null || strategy == null) {
            return null;
        }
        return strategy.toCurrentMaterialJson(content);
    }

    /**
     * 查找材料场景对应的策略
     */
    private static MaterialSceneStrategy findStrategy(String materialScene, List<MaterialSceneStrategy> strategies) {
        if (strategies != null) {
            for (MaterialSceneStrategy strategy : strategies) {
                if (strategy.supportScene().equals(materialScene)) {
                    return strategy;
                }
            }
        }
        return null;
    }

    /**
     * 数据库字符串转草稿状态枚举
     */
    private static MaterialDraftStatusEnum mapDbStrToDraftStatus(String dbStr) {
        if (dbStr == null) {
            return MaterialDraftStatusEnum.NO_DRAFT;
        }
        try {
            return MaterialDraftStatusEnum.valueOf(dbStr);
        } catch (IllegalArgumentException e) {
            return MaterialDraftStatusEnum.NO_DRAFT;
        }
    }

    /**
     * 数据库字符串转审核状态枚举
     */
    private static MaterialAuditStatusEnum mapDbStrToAuditStatus(String dbStr) {
        if (dbStr == null) {
            return null;
        }
        try {
            return MaterialAuditStatusEnum.valueOf(dbStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
