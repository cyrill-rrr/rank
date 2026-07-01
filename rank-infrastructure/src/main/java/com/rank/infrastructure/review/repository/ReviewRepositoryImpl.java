package com.rank.infrastructure.review.repository;

import com.rank.domain.common.PageResult;
import com.rank.domain.review.model.ReviewTaskEntity;
import com.rank.domain.review.repository.ReviewRepository;
import com.rank.infrastructure.review.converter.ReviewConverter;
import com.rank.infrastructure.review.mapper.ReviewMapper;
import com.rank.infrastructure.review.po.ReviewTaskPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评审任务仓储实现
 */
@Slf4j
@Repository
public class ReviewRepositoryImpl implements ReviewRepository {

    private final ReviewMapper reviewMapper;
    private final ReviewConverter reviewConverter;

    public ReviewRepositoryImpl(ReviewMapper reviewMapper, ReviewConverter reviewConverter) {
        this.reviewMapper = reviewMapper;
        this.reviewConverter = reviewConverter;
    }

    @Override
    public ReviewTaskEntity findByBizKey(Long materialId, Long userId, String scene) {
        log.info("[ReviewRepositoryImpl findByBizKey] materialId={}, userId={}, scene={}", materialId, userId, scene);
        ReviewTaskPO po = reviewMapper.findByBizKey(materialId, userId, scene);
        ReviewTaskEntity result = reviewConverter.toEntity(po);
        log.info("[ReviewRepositoryImpl findByBizKey] result={}", result);
        return result;
    }

    @Override
    public ReviewTaskEntity findById(Long id) {
        log.info("[ReviewRepositoryImpl findById] id={}", id);
        ReviewTaskPO po = reviewMapper.findById(id);
        ReviewTaskEntity result = reviewConverter.toEntity(po);
        log.info("[ReviewRepositoryImpl findById] result={}", result);
        return result;
    }

    @Override
    public void save(ReviewTaskEntity entity) {
        log.info("[ReviewRepositoryImpl save] entity materialId={}, userId={}, scene={}",
                entity != null ? entity.getMaterialId() : null,
                entity != null ? entity.getUserId() : null,
                entity != null ? entity.getScene() : null);
        try {
            ReviewTaskPO po = reviewConverter.toPO(entity);
            if (po == null) {
                log.error("[ReviewRepositoryImpl save] 转换PO为空, materialId={}, userId={}, scene={}",
                        entity.getMaterialId(), entity.getUserId(), entity.getScene());
                throw new IllegalArgumentException("实体转换失败");
            }
            if (po.getId() == null) {
                // 新增
                reviewMapper.insert(po);
                entity.setId(po.getId());
            } else {
                // 更新
                reviewMapper.update(po);
            }
        } catch (Exception e) {
            log.error("[ReviewRepositoryImpl save] 保存评审单异常, materialId={}, userId={}, scene={}",
                    entity.getMaterialId(), entity.getUserId(), entity.getScene(), e);
            throw e;
        }
    }

    @Override
    public int deleteByUserId(Long userId) {
        log.info("[ReviewRepositoryImpl deleteByUserId] userId={}", userId);
        try {
            int result = reviewMapper.deleteByUserId(userId);
            log.info("[ReviewRepositoryImpl deleteByUserId] affectedRows={}", result);
            return result;
        } catch (Exception e) {
            log.error("[ReviewRepositoryImpl deleteByUserId] 删除评审单异常, userId={}", userId, e);
            throw e;
        }
    }

    @Override
    public PageResult<ReviewTaskEntity> queryPage(Long materialId, Long userId, String scene, String status,
                                                   int pageNo, int pageSize) {
        log.info("[ReviewRepositoryImpl queryPage] materialId={}, userId={}, scene={}, status={}, pageNo={}, pageSize={}",
                materialId, userId, scene, status, pageNo, pageSize);
        try {
            Long total = reviewMapper.countPage(materialId, userId, scene, status);
            if (total == null || total == 0) {
                log.info("[ReviewRepositoryImpl queryPage] 查询结果为空, materialId={}, userId={}, scene={}, status={}",
                        materialId, userId, scene, status);
                return PageResult.empty(pageNo, pageSize);
            }
            int offset = (pageNo - 1) * pageSize;
            List<ReviewTaskPO> poList = reviewMapper.queryPage(materialId, userId, scene, status, offset, pageSize);
            List<ReviewTaskEntity> records = poListToEntities(poList);
            PageResult<ReviewTaskEntity> result = new PageResult<ReviewTaskEntity>(total, pageNo, pageSize, records);
            log.info("[ReviewRepositoryImpl queryPage] total={}, pageNo={}, pageSize={}", total, pageNo, pageSize);
            return result;
        } catch (Exception e) {
            log.error("[ReviewRepositoryImpl queryPage] 分页查询异常, materialId={}, userId={}, scene={}",
                    materialId, userId, scene, e);
            throw e;
        }
    }

    @Override
    public PageResult<ReviewTaskEntity> queryPageByUserId(Long userId, String status, int pageNo, int pageSize) {
        log.info("[ReviewRepositoryImpl queryPageByUserId] userId={}, status={}, pageNo={}, pageSize={}",
                userId, status, pageNo, pageSize);
        try {
            Long total = reviewMapper.countPageByUserId(userId, status);
            if (total == null || total == 0) {
                log.info("[ReviewRepositoryImpl queryPageByUserId] 查询结果为空, userId={}, status={}",
                        userId, status);
                return PageResult.empty(pageNo, pageSize);
            }
            int offset = (pageNo - 1) * pageSize;
            List<ReviewTaskPO> poList = reviewMapper.queryPageByUserId(userId, status, offset, pageSize);
            List<ReviewTaskEntity> records = poListToEntities(poList);
            PageResult<ReviewTaskEntity> result = new PageResult<ReviewTaskEntity>(total, pageNo, pageSize, records);
            log.info("[ReviewRepositoryImpl queryPageByUserId] total={}, pageNo={}, pageSize={}", total, pageNo, pageSize);
            return result;
        } catch (Exception e) {
            log.error("[ReviewRepositoryImpl queryPageByUserId] 按用户分页查询异常, userId={}", userId, e);
            throw e;
        }
    }

    private List<ReviewTaskEntity> poListToEntities(List<ReviewTaskPO> poList) {
        if (CollectionUtils.isEmpty(poList)) {
            return Collections.emptyList();
        }
        return poList.stream()
                .map(reviewConverter::toEntity)
                .collect(Collectors.toList());
    }
}
