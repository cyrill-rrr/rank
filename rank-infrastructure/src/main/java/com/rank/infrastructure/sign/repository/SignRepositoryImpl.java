package com.rank.infrastructure.sign.repository;

import com.rank.domain.common.PageResult;
import com.rank.domain.common.CccWorkflowMockSwitch;
import com.rank.domain.sign.model.SignEntity;
import com.rank.domain.sign.repository.SignRepository;
import com.rank.infrastructure.sign.converter.SignConverter;
import com.rank.infrastructure.sign.mapper.SignMapper;
import com.rank.infrastructure.sign.po.SignPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 报名记录仓储实现
 */
@Slf4j
@Repository
public class SignRepositoryImpl implements SignRepository {

    private final SignMapper signMapper;
    private final SignConverter signConverter;

    public SignRepositoryImpl(SignMapper signMapper, SignConverter signConverter) {
        this.signMapper = signMapper;
        this.signConverter = signConverter;
    }

    @Override
    public List<SignEntity> queryBySceneSubject(String signScene, Long subjectId) {
        if (CccWorkflowMockSwitch.isEnabled()) {
            log.info("ccc-workflow-mock-subagent [SignRepositoryImpl queryBySceneSubject] mock return empty list, signScene={}, subjectId={}", signScene, subjectId);
            return Collections.emptyList();
        }
        try {
            List<SignPO> poList = signMapper.queryBySceneSubject(signScene, subjectId);
            if (CollectionUtils.isEmpty(poList)) {
                return Collections.emptyList();
            }
            return poList.stream()
                    .map(signConverter::toEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("[SignRepositoryImpl queryBySceneSubject] 查询报名记录异常, signScene={}, subjectId={}",
                    signScene, subjectId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public int countSignedDoctorsByIndexShopId(String signScene, Long indexShopId) {
        try {
            Integer count = signMapper.countSignedDoctorsByIndexShopId(signScene, indexShopId);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("[SignRepositoryImpl countSignedDoctorsByIndexShopId] 统计已报名医生数异常, signScene={}, indexShopId={}",
                    signScene, indexShopId, e);
            return 0;
        }
    }

    @Override
    public void saveOrUpdate(SignEntity signEntity) {
        try {
            SignPO po = signConverter.toPO(signEntity);
            if (po == null) {
                // Entity到PO转换失败，可能是Entity字段异常或Converter未正确处理
                log.info("[SignRepositoryImpl saveOrUpdate] 转换PO为空, signScene={}, subjectId={}, projectCode={}, status={}",
                        signEntity.getSignScene(), signEntity.getSubjectId(), signEntity.getProjectCode(), signEntity.getStatus());
                return;
            }
            if (po.getId() == null) {
                // 主键为空时走新增路径，新记录需回填自增主键以支持后续状态更新
                signMapper.insert(po);
                signEntity.setId(po.getId());
                signEntity.setCreatedTime(po.getCreatedTime());
                signEntity.setUpdatedTime(po.getUpdatedTime());
            } else {
                // 已有主键时走更新路径，只更新状态和时间戳
                signMapper.updateStatus(po);
                signEntity.setUpdatedTime(po.getUpdatedTime());
            }
        } catch (Exception e) {
            log.error("[SignRepositoryImpl saveOrUpdate] 保存报名记录异常, signScene={}, subjectId={}, projectCode={}, status={}",
                    signEntity.getSignScene(), signEntity.getSubjectId(), signEntity.getProjectCode(), signEntity.getStatus(), e);
            throw e;
        }
    }

    @Override
    public PageResult<SignEntity> queryShopSignPage(String signScene, Long indexShopId, String status,
                                                    int pageNo, int pageSize) {
        try {
            // 先查总条数，为0则直接返回空分页避免执行二次查询
            Long total = signMapper.countShopSignPage(signScene, indexShopId, status);
            if (total == null || total == 0) {
                return PageResult.empty(pageNo, pageSize);
            }

            // 非空时才查具体分页数据
            int offset = (pageNo - 1) * pageSize;
            List<SignPO> poList = signMapper.queryShopSignPage(signScene, indexShopId, status, offset, pageSize);
            List<SignEntity> records = CollectionUtils.isEmpty(poList)
                    ? Collections.emptyList()
                    : poList.stream().map(signConverter::toEntity).collect(Collectors.toList());

            return new PageResult<>(total, pageNo, pageSize, records);
        } catch (Exception e) {
            log.error("[SignRepositoryImpl queryShopSignPage] 分页查询报名记录异常, signScene={}, indexShopId={}",
                    signScene, indexShopId, e);
            return PageResult.empty(pageNo, pageSize);
        }
    }
}
