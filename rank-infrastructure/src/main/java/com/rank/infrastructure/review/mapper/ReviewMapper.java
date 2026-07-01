package com.rank.infrastructure.review.mapper;

import com.rank.infrastructure.review.po.ReviewTaskPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评审任务 Mapper
 */
public interface ReviewMapper {

    /**
     * 按业务主键查询有效记录
     */
    ReviewTaskPO findByBizKey(@Param("materialId") Long materialId,
                              @Param("userId") Long userId,
                              @Param("scene") String scene);

    /**
     * 按主键查询有效记录
     */
    ReviewTaskPO findById(@Param("id") Long id);

    /**
     * 新增评审单
     */
    int insert(ReviewTaskPO po);

    /**
     * 更新评审单（状态、作答JSON、更新时间）
     */
    int update(ReviewTaskPO po);

    /**
     * 按用户ID逻辑删除全部有效评审单
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 分页查询有效评审单（管理员视角）
     */
    List<ReviewTaskPO> queryPage(@Param("materialId") Long materialId,
                                 @Param("userId") Long userId,
                                 @Param("scene") String scene,
                                 @Param("status") String status,
                                 @Param("offset") int offset,
                                 @Param("limit") int pageSize);

    /**
     * 统计有效评审单总数（管理员视角）
     */
    Long countPage(@Param("materialId") Long materialId,
                   @Param("userId") Long userId,
                   @Param("scene") String scene,
                   @Param("status") String status);

    /**
     * 分页查询有效评审单（专家视角：只能查自己的）
     */
    List<ReviewTaskPO> queryPageByUserId(@Param("userId") Long userId,
                                         @Param("status") String status,
                                         @Param("offset") int offset,
                                         @Param("limit") int pageSize);

    /**
     * 统计有效评审单总数（专家视角）
     */
    Long countPageByUserId(@Param("userId") Long userId,
                           @Param("status") String status);
}
