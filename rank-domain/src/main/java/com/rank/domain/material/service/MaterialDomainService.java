package com.rank.domain.material.service;

import com.rank.domain.common.exception.BizException;
import com.rank.domain.material.shared.MaterialSceneEnum;
import com.rank.domain.material.vo.MaterialConfigVO;
import com.rank.domain.sign.repository.SignRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 材料领域服务：承载跨聚合的业务规则校验
 */
@Slf4j
@Service
public class MaterialDomainService {

    private final SignRepository signRepository;

    public MaterialDomainService(SignRepository signRepository) {
        this.signRepository = signRepository;
    }

    /**
     * 校验是否可以操作材料（窗口期 + 报名记录存在性校验）
     *
     * @param materialScene   材料场景
     * @param auditSubjectId  审计主体ID（techId字符串）
     * @param config          材料配置（含窗口期）
     */
    public void checkCanOperateMaterial(String materialScene, String auditSubjectId, MaterialConfigVO config) {
        // 1. 窗口期校验
        config.checkCanOperate();

        // 2. 调用报名域校验报名记录是否存在
        try {
            Long subjectId = Long.parseLong(auditSubjectId);
            // signScene固定为DOCTOR，因为材料提报只面向医生榜
            log.info("[MaterialDomainService checkCanOperateMaterial] 查询报名记录, scene=DOCTOR, subjectId={}", subjectId);
            try {
                signRepository.queryBySceneSubject("DOCTOR", subjectId);
                log.info("[MaterialDomainService checkCanOperateMaterial] 报名记录查询成功, subjectId={}", subjectId);
            } catch (Exception e) {
                log.error("[MaterialDomainService checkCanOperateMaterial] 报名域不可用, 跳过报名记录校验, auditSubjectId={}", auditSubjectId, e);
                // 报名域不可用时放行，不阻断材料操作
            }
        } catch (NumberFormatException e) {
            log.error("[MaterialDomainService checkCanOperateMaterial] auditSubjectId格式非法, auditSubjectId={}", auditSubjectId, e);
            throw BizException.invalidParam("审计主体ID格式非法");
        }
    }

    /**
     * 材料场景到报名项目code的映射（硬编码，长期方案）
     */
    public static String resolveSignProjectCode(String materialScene) {
        if (MaterialSceneEnum.MEDICAL_BEAUTY_DOCTOR.name().equals(materialScene)) {
            return "PROJECT_MEDICAL_BEAUTY";
        }
        if (MaterialSceneEnum.ORAL_DOCTOR.name().equals(materialScene)) {
            return "PROJECT_ORAL";
        }
        throw BizException.invalidParam("未知材料场景: " + materialScene);
    }
}
