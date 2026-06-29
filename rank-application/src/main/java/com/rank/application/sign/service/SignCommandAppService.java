package com.rank.application.sign.service;

import com.rank.application.sign.command.OperateSignCommand;
import com.rank.application.sign.factory.SignFactory;
import com.rank.domain.sign.model.SignEntity;
import com.rank.domain.sign.repository.SignConfigRepository;
import com.rank.domain.sign.repository.SignRepository;
import com.rank.domain.sign.shared.SignOperationEnum;
import com.rank.domain.sign.shared.SignStatusEnum;
import com.rank.domain.sign.vo.SignConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 报名写操作编排服务
 */
@Slf4j
@Service
public class SignCommandAppService {

    private static final int MAX_PROJECTS_PER_SUBJECT = 2;
    private static final int MAX_DOCTORS_PER_SHOP = 10;

    private final SignConfigRepository signConfigRepository;
    private final SignRepository signRepository;
    private final SignFactory signFactory;

    public SignCommandAppService(SignConfigRepository signConfigRepository,
                                 SignRepository signRepository,
                                 SignFactory signFactory) {
        this.signConfigRepository = signConfigRepository;
        this.signRepository = signRepository;
        this.signFactory = signFactory;
    }

    /**
     * 报名操作编排（提报或退报）
     *
     * @param command 操作指令
     * @return 受影响的主键列表
     */
    public List<Long> operateSign(OperateSignCommand command) {
        // 1. 读取Lion配置并校验窗口期和项目可选性
        log.info("[SignCommandAppService operateSign] 读取报名配置, signScene={}", command.getSignScene());
        SignConfigVO config = signConfigRepository.findByScene(command.getSignScene());
        if (config == null) {
            throw new IllegalArgumentException("报名配置缺失，signScene=" + command.getSignScene());
        }
        config.checkCanOperate(command.getOperationType());
        config.checkProjectsSelectable(command.getProjectCodeList());

        // 2. 查询当前主体已有报名记录
        List<SignEntity> allSubjectSigns = signRepository.queryBySceneSubject(
                command.getSignScene(), command.getSubjectId());

        // 3. 按projectCode建立映射，用于内存比对
        Map<Integer, SignEntity> projectSignMap = allSubjectSigns.stream()
                .collect(Collectors.toMap(SignEntity::getProjectCode, e -> e, (e1, e2) -> e1));

        List<Long> affectedIds = new ArrayList<>();

        if (SignOperationEnum.SUBMIT.name().equals(command.getOperationType())) {
            // 4a. 提报分支：确定需要提报的项目code
            List<Integer> toSignCodes = resolveToSignProjectCodes(projectSignMap, command.getProjectCodeList());

            // 5a. 校验项目数不超过2
            checkProjectLimit(projectSignMap, toSignCodes);

            // 6a. 医生榜校验医生数（仅当有新增报名时）
            if (isDoctorScene(command.getSignScene()) && !toSignCodes.isEmpty()) {
                boolean isNewDoctor = isNewDoctorSign(allSubjectSigns);
                if (isNewDoctor) {
                    int signedDoctorCount = signRepository.countSignedDoctorsByIndexShopId(
                            command.getSignScene(), command.getShopId());
                    checkDoctorLimit(signedDoctorCount);
                }
            }

            // 7a. 执行提报
            for (Integer code : toSignCodes) {
                SignEntity existing = projectSignMap.get(code);
                if (existing == null) {
                    // 历史不存在，创建新记录
                    SignEntity newEntity = signFactory.createSignedEntity(
                            command.getSignScene(), command.getSubjectId(),
                            command.getShopId(), code);
                    signRepository.saveOrUpdate(newEntity);
                    affectedIds.add(newEntity.getId());
                    log.info("[SignCommandAppService operateSign] 新增提报记录, subjectId={}, projectCode={}, id={}",
                            command.getSubjectId(), code, newEntity.getId());
                } else if (SignStatusEnum.CANCELLED.equals(existing.getStatus())) {
                    // 已退报，恢复提报
                    existing.submit();
                    signRepository.saveOrUpdate(existing);
                    affectedIds.add(existing.getId());
                    log.info("[SignCommandAppService operateSign] 恢复提报, id={}, projectCode={}",
                            existing.getId(), code);
                } else {
                    // 已提报，跳过
                    log.info("[SignCommandAppService operateSign] 跳过已提报项目, projectCode={}", code);
                }
            }
        } else {
            // 4b. 退报分支：确定需要退报的项目code
            List<Integer> toCancelCodes = resolveToCancelProjectCodes(projectSignMap, command.getProjectCodeList());

            // 5b. 执行退报
            for (Integer code : toCancelCodes) {
                SignEntity existing = projectSignMap.get(code);
                if (existing != null && SignStatusEnum.SIGNED.equals(existing.getStatus())) {
                    existing.cancel();
                    signRepository.saveOrUpdate(existing);
                    affectedIds.add(existing.getId());
                    log.info("[SignCommandAppService operateSign] 退报成功, id={}, projectCode={}",
                            existing.getId(), code);
                } else {
                    log.info("[SignCommandAppService operateSign] 跳过退报（不存在或已退报）, projectCode={}", code);
                }
            }
        }

        return affectedIds;
    }

    /**
     * 确定需要提报的项目code列表：入参项目中，不存在或已退报的才需要提报
     */
    private List<Integer> resolveToSignProjectCodes(Map<Integer, SignEntity> projectSignMap,
                                                    List<Integer> requestedCodes) {
        List<Integer> result = new ArrayList<>();
        for (Integer code : requestedCodes) {
            SignEntity existing = projectSignMap.get(code);
            if (existing == null || SignStatusEnum.CANCELLED.equals(existing.getStatus())) {
                result.add(code);
            }
        }
        return result;
    }

    /**
     * 确定需要退报的项目code列表：入参项目中，已提报的才需要退报
     */
    private List<Integer> resolveToCancelProjectCodes(Map<Integer, SignEntity> projectSignMap,
                                                      List<Integer> requestedCodes) {
        List<Integer> result = new ArrayList<>();
        for (Integer code : requestedCodes) {
            SignEntity existing = projectSignMap.get(code);
            if (existing != null && SignStatusEnum.SIGNED.equals(existing.getStatus())) {
                result.add(code);
            }
        }
        return result;
    }

    /**
     * 校验提报项目数不超过限制
     */
    private void checkProjectLimit(Map<Integer, SignEntity> projectSignMap, List<Integer> toSignCodes) {
        long alreadySignedCount = projectSignMap.values().stream()
                .filter(e -> SignStatusEnum.SIGNED.equals(e.getStatus()))
                .count();
        long totalAfterSubmit = alreadySignedCount + toSignCodes.size();
        if (totalAfterSubmit > MAX_PROJECTS_PER_SUBJECT) {
            throw new IllegalArgumentException("每个报名主体最多提报" + MAX_PROJECTS_PER_SUBJECT + "个项目");
        }
    }

    /**
     * 校验医生数不超过限制
     */
    private void checkDoctorLimit(int signedDoctorCount) {
        if (signedDoctorCount >= MAX_DOCTORS_PER_SHOP) {
            throw new IllegalArgumentException("该机构最多报名" + MAX_DOCTORS_PER_SHOP + "个医生");
        }
    }

    /**
     * 判断当前主体是否医生榜场景
     */
    private boolean isDoctorScene(String signScene) {
        return "DOCTOR".equals(signScene);
    }

    /**
     * 判断是否是新增医生报名：当前主体下没有任何报名记录
     */
    private boolean isNewDoctorSign(List<SignEntity> allSubjectSigns) {
        if (allSubjectSigns == null || allSubjectSigns.isEmpty()) {
            return true;
        }
        // 检查是否全部为退报状态
        return allSubjectSigns.stream()
                .allMatch(e -> SignStatusEnum.CANCELLED.equals(e.getStatus()));
    }
}
