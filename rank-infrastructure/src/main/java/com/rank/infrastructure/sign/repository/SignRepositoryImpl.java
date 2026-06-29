package com.rank.infrastructure.sign.repository;

import com.rank.domain.common.PageResult;
import com.rank.domain.sign.model.SignEntity;
import com.rank.domain.sign.repository.SignRepository;
import com.rank.infrastructure.sign.converter.SignConverter;
import com.rank.infrastructure.sign.mapper.SignMapper;
import com.rank.infrastructure.sign.po.SignPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

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
        try {
            List<SignPO> poList = signMapper.queryBySceneSubject(signScene, subjectId);
            if (poList == null || poList.isEmpty()) {
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
                log.error("[SignRepositoryImpl saveOrUpdate] 转换PO为空, signEntity={}", signEntity);
                return;
            }
            if (po.getId() == null) {
                // 新增
                signMapper.insert(po);
                // 回写ID
                signEntity.setId(po.getId());
                signEntity.setCreatedTime(po.getCreatedTime());
                signEntity.setUpdatedTime(po.getUpdatedTime());
            } else {
                // 更新
                signMapper.updateStatus(po);
                signEntity.setUpdatedTime(po.getUpdatedTime());
            }
        } catch (Exception e) {
            log.error("[SignRepositoryImpl saveOrUpdate] 保存报名记录异常, signEntity={}", signEntity, e);
            throw e;
        }
    }

    @Override
    public PageResult<SignEntity> queryShopSignPage(String signScene, Long indexShopId, String status,
                                                    int pageNo, int pageSize) {
        try {
            // 查询总数
            Long total = signMapper.countShopSignPage(signScene, indexShopId, status);
            if (total == null || total == 0) {
                return PageResult.empty(pageNo, pageSize);
            }

            // 分页查询
            int offset = (pageNo - 1) * pageSize;
            List<SignPO> poList = signMapper.queryShopSignPage(signScene, indexShopId, status, offset, pageSize);
            List<SignEntity> records = poList != null
                    ? poList.stream().map(signConverter::toEntity).collect(Collectors.toList())
                    : Collections.emptyList();

            return new PageResult<>(total, pageNo, pageSize, records);
        } catch (Exception e) {
            log.error("[SignRepositoryImpl queryShopSignPage] 分页查询报名记录异常, signScene={}, indexShopId={}",
                    signScene, indexShopId, e);
            return PageResult.empty(pageNo, pageSize);
        }
    }
}
