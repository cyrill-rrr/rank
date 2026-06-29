package com.rank.application.sign.service;

import com.rank.application.sign.command.OperateSignCommand;
import com.rank.domain.sign.factory.SignFactory;
import com.rank.domain.sign.model.SignEntity;
import com.rank.domain.sign.repository.SignConfigRepository;
import com.rank.domain.sign.repository.SignRepository;
import com.rank.domain.sign.service.SignDomainService;
import com.rank.domain.sign.shared.SignOperationEnum;
import com.rank.domain.sign.shared.SignStatusEnum;
import com.rank.domain.sign.vo.SignConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报名写操作编排服务
 */
@Slf4j
@Service
public class SignCommandAppService {

    private final SignConfigRepository signConfigRepository;
    private final SignRepository signRepository;
    private final SignFactory signFactory;
    private final SignDomainService signDomainService;

    public SignCommandAppService(SignConfigRepository signConfigRepository,
                                 SignRepository signRepository,
                                 SignFactory signFactory,
                                 SignDomainService signDomainService) {
        this.signConfigRepository = signConfigRepository;
        this.signRepository = signRepository;
        this.signFactory = signFactory;
        this.signDomainService = signDomainService;
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
        StringBuilder logBuilder = new StringBuilder();

        if (SignOperationEnum.SUBMIT.name().equals(command.getOperationType())) {
            // 4a. 提报分支：确定需要提报的项目code
            List<Integer> toSignCodes = signDomainService.resolveToSignProjectCodes(
                    projectSignMap, command.getProjectCodeList());

            // 5a. 校验项目数不超过2
            signDomainService.checkProjectLimit(projectSignMap, toSignCodes);

            // 6a. 医生榜校验医生数（仅当有新增报名时）
            if (signDomainService.isDoctorScene(command.getSignScene()) && !toSignCodes.isEmpty()) {
                boolean isNewDoctor = signDomainService.isNewDoctorSign(allSubjectSigns);
                if (isNewDoctor) {
                    int signedDoctorCount = signRepository.countSignedDoctorsByIndexShopId(
                            command.getSignScene(), command.getShopId());
                    signDomainService.checkDoctorLimit(signedDoctorCount);
                }
            }

            // 7a. 执行提报
            for (Integer code : toSignCodes) {
                SignEntity existing = projectSignMap.get(code);
                if (existing == null) {
                    SignEntity newEntity = signFactory.createSignedEntity(
                            command.getSignScene(), command.getSubjectId(),
                            command.getShopId(), code);
                    signRepository.saveOrUpdate(newEntity);
                    affectedIds.add(newEntity.getId());
                    logBuilder.append("新增=").append(code).append(";");
                } else if (SignStatusEnum.CANCELLED.equals(existing.getStatus())) {
                    existing.submit();
                    signRepository.saveOrUpdate(existing);
                    affectedIds.add(existing.getId());
                    logBuilder.append("恢复=").append(code).append(";");
                } else {
                    logBuilder.append("跳过已提报=").append(code).append(";");
                }
            }
        } else {
            // 4b. 退报分支：确定需要退报的项目code
            List<Integer> toCancelCodes = signDomainService.resolveToCancelProjectCodes(
                    projectSignMap, command.getProjectCodeList());

            // 5b. 执行退报
            for (Integer code : toCancelCodes) {
                SignEntity existing = projectSignMap.get(code);
                if (existing != null && SignStatusEnum.SIGNED.equals(existing.getStatus())) {
                    existing.cancel();
                    signRepository.saveOrUpdate(existing);
                    affectedIds.add(existing.getId());
                    logBuilder.append("退报=").append(code).append(";");
                } else {
                    logBuilder.append("跳过退报=").append(code).append(";");
                }
            }
        }

        log.info("[SignCommandAppService operateSign] 批量操作结果: {}", logBuilder.toString());
        return affectedIds;
    }
}
