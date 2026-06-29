package com.rank.application.sign.service;

import com.rank.api.sign.dto.DoctorDTO;
import com.rank.application.sign.assembler.SignAssembler;
import com.rank.application.sign.qry.SelectableProjectQry;
import com.rank.application.sign.qry.ShopDoctorQry;
import com.rank.application.sign.qry.SignPageQry;
import com.rank.domain.common.PageResult;
import com.rank.domain.sign.model.SignEntity;
import com.rank.domain.sign.repository.DoctorAcl;
import com.rank.domain.sign.repository.SignConfigRepository;
import com.rank.domain.sign.repository.SignRepository;
import com.rank.domain.sign.shared.SignStatusEnum;
import com.rank.domain.sign.vo.SignConfigVO;
import com.rank.domain.sign.vo.SignProjectVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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
            throw new IllegalArgumentException("报名配置暂不可用");
        }
        return config.getProjectList();
    }

    /**
     * 查询机构下医生列表
     * 通过SignAssembler将DoctorAcl.DoctorDTO转换为API层DoctorDTO
     *
     * @param qry     查询条件
     * @param assembler 用于DTO转换的Assembler
     * @return API层医生DTO列表
     */
    public List<DoctorDTO> queryShopDoctors(ShopDoctorQry qry, SignAssembler assembler) {
        // 1. 调ACL
        log.info("[SignQueryAppService queryShopDoctors] 查询机构医生列表, shopId={}", qry.getShopId());
        List<DoctorAcl.DoctorDTO> result = doctorAcl.queryDoctorsByShopId(
                qry.getShopId(), qry.getIncludeOffline());
        if (result == null) {
            return Collections.emptyList();
        }
        return assembler.toApiDoctorDTOList(result);
    }

    /**
     * 将Integer状态码转为数据库存储的字符串
     */
    private static String convertStatusToDbStr(Integer status) {
        if (status == null) {
            return null;
        }
        if (status == 1) {
            return SignStatusEnum.SIGNED.name();
        }
        if (status == 2) {
            return SignStatusEnum.CANCELLED.name();
        }
        return null;
    }
}
