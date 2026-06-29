package com.rank.domain.sign.service;

import com.rank.domain.sign.model.SignEntity;
import com.rank.domain.sign.shared.SignStatusEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 报名领域服务：承载跨聚合的业务规则校验
 * - 提报/退报项目code解析
 * - 项目数量限制校验
 * - 医生数量限制校验
 * - 场景类型判断
 */
public class SignDomainService {

    private static final int MAX_PROJECTS_PER_SUBJECT = 2;
    private static final int MAX_DOCTORS_PER_SHOP = 10;

    /**
     * 确定需要提报的项目code列表：入参项目中，不存在或已退报的才需要提报
     *
     * @param projectSignMap 当前主体已有报名记录（按projectCode索引）
     * @param requestedCodes 入参请求的项目code列表
     * @return 需要进行提报操作的项目code列表
     */
    public List<Integer> resolveToSignProjectCodes(Map<Integer, SignEntity> projectSignMap,
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
     *
     * @param projectSignMap 当前主体已有报名记录（按projectCode索引）
     * @param requestedCodes 入参请求的项目code列表
     * @return 需要进行退报操作的项目code列表
     */
    public List<Integer> resolveToCancelProjectCodes(Map<Integer, SignEntity> projectSignMap,
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
     *
     * @param projectSignMap 当前主体已有报名记录（按projectCode索引）
     * @param toSignCodes    本次需要提报的项目code列表
     */
    public void checkProjectLimit(Map<Integer, SignEntity> projectSignMap, List<Integer> toSignCodes) {
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
     *
     * @param signedDoctorCount 当前机构已报名医生数
     */
    public void checkDoctorLimit(int signedDoctorCount) {
        if (signedDoctorCount >= MAX_DOCTORS_PER_SHOP) {
            throw new IllegalArgumentException("该机构最多报名" + MAX_DOCTORS_PER_SHOP + "个医生");
        }
    }

    /**
     * 判断当前主体是否医生榜场景
     *
     * @param signScene 报名场景字符串
     * @return true-医生榜
     */
    public boolean isDoctorScene(String signScene) {
        return "DOCTOR".equals(signScene);
    }

    /**
     * 判断是否是新增医生报名：当前主体下没有任何报名记录，或全部为退报状态
     *
     * @param allSubjectSigns 当前主体已有报名记录
     * @return true-本次是新医生报名
     */
    public boolean isNewDoctorSign(List<SignEntity> allSubjectSigns) {
        if (allSubjectSigns == null || allSubjectSigns.isEmpty()) {
            return true;
        }
        // 所有记录均为退报状态，则视为新报名
        for (SignEntity sign : allSubjectSigns) {
            if (SignStatusEnum.SIGNED.equals(sign.getStatus())) {
                return false;
            }
        }
        return true;
    }
}
