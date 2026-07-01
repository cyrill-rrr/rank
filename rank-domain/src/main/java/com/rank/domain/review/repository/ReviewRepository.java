package com.rank.domain.review.repository;

import com.rank.domain.common.PageResult;
import com.rank.domain.review.model.ReviewTaskEntity;

import java.util.List;

/**
 * 评审任务仓储接口。
 */
public interface ReviewRepository {

    /**
     * 按业务键查询有效评审单
     *
     * @param materialId 材料ID
     * @param userId     用户ID
     * @param scene      场景
     * @return 评审单实体或null
     */
    ReviewTaskEntity findByBizKey(Long materialId, Long userId, String scene);

    /**
     * 按主键查询有效评审单
     *
     * @param id 评审单主键
     * @return 评审单实体或null
     */
    ReviewTaskEntity findById(Long id);

    /**
     * 保存评审单（新增或更新）
     *
     * @param entity 评审单实体
     */
    void save(ReviewTaskEntity entity);

    /**
     * 按用户ID逻辑删除全部有效评审单
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(Long userId);

    /**
     * 分页查询有效评审单
     *
     * @param materialId 材料ID（可选）
     * @param userId     用户ID（可选）
     * @param scene      场景（可选）
     * @param status     状态（可选）
     * @param pageNo     页码
     * @param pageSize   每页大小
     * @return 分页结果
     */
    PageResult<ReviewTaskEntity> queryPage(Long materialId, Long userId, String scene, String status,
                                           int pageNo, int pageSize);

    /**
     * 按用户ID分页查询有效评审单（专家视角）
     *
     * @param userId   用户ID
     * @param status   状态（可选）
     * @param pageNo   页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<ReviewTaskEntity> queryPageByUserId(Long userId, String status, int pageNo, int pageSize);
}
