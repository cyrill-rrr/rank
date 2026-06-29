package com.rank.application.sign.service;

import com.rank.application.sign.qry.SelectableProjectQry;
import com.rank.application.sign.qry.ShopDoctorQry;
import com.rank.application.sign.qry.SignPageQry;
import com.rank.domain.common.PageResult;
import com.rank.domain.sign.model.SignEntity;
import com.rank.domain.sign.repository.DoctorAcl;
import com.rank.domain.sign.repository.SignConfigRepository;
import com.rank.domain.sign.repository.SignRepository;
import com.rank.domain.sign.vo.SignConfigVO;
import com.rank.domain.sign.vo.SignProjectVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 报名读操作编排服务
 */
@Slf4j
@Service
public class SignQueryAppService {

    private final SignRepository signRepository;
    private final SignConfigRepository signConfigRepository;
    private final DoctorAcl doctorAcl;

    public SignQueryAppService(SignRepository signRepository,
                               SignConfigRepository signConfigRepository,
                               DoctorAcl doctorAcl) {
        this.signRepository = signRepository;
        this.signConfigRepository = signConfigRepository;
        this.doctorAcl = doctorAcl;
    }

    /**
     * 分页查询机构维度报名记录
     */
    public PageResult<SignEntity> querySignPage(SignPageQry qry) {
        // 1. 查数据
        log.info("[SignQueryAppService querySignPage] 分页查询报名记录, signScene={}, indexShopId={}, pageNo={}, pageSize={}",
                qry.getSignScene(), qry.getIndexShopId(), qry.getPageNo(), qry.getPageSize());
        return signRepository.queryShopSignPage(
                qry.getSignScene(), qry.getIndexShopId(),
                convertStatusToDbStr(qry.getStatus()),
                qry.getPageNo(), qry.getPageSize());
    }

    /**
     * 查询可选提报项目
     */
    public List<SignProjectVO> querySelectableProjects(SelectableProjectQry qry) {
        // 1. 读配置
        log.info("[SignQueryAppService querySelectableProjects] 查询可选提报项目, signScene={}", qry.getSignScene());
        SignConfigVO config = signConfigRepository.findByScene(qry.getSignScene());
        if (config == null || config.getProjectList() == null) {
            return Collections.emptyList();
        }
        return config.getProjectList();
    }

    /**
     * 查询机构下医生列表
     */
    public List<com.rank.domain.sign.repository.DoctorAcl.DoctorDTO> queryShopDoctors(ShopDoctorQry qry) {
        // 1. 调ACL
        log.info("[SignQueryAppService queryShopDoctors] 查询机构医生列表, shopId={}", qry.getShopId());
        List<com.rank.domain.sign.repository.DoctorAcl.DoctorDTO> result = doctorAcl.queryDoctorsByShopId(
                qry.getShopId(), qry.getIncludeOffline());
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    /**
     * 将Integer状态码转为数据库存储的字符串
     */
    private String convertStatusToDbStr(Integer status) {
        if (status == null) {
            return null;
        }
        if (status == 1) {
            return "SIGNED";
        }
        if (status == 2) {
            return "CANCELLED";
        }
        return null;
    }
}
