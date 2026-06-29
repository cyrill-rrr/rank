package com.rank.domain.sign.repository;

import com.rank.domain.common.PageResult;
import com.rank.domain.sign.model.SignEntity;

import java.util.List;

/**
 * 报名记录仓储接口
 */
public interface SignRepository {

    /**
     * 按报名场景和主体ID查询报名记录列表
     */
    List<SignEntity> queryBySceneSubject(String signScene, Long subjectId);

    /**
     * 统计医生榜中某机构已报名医生数
     */
    int countSignedDoctorsByIndexShopId(String signScene, Long indexShopId);

    /**
     * 新增或更新报名记录
     */
    void saveOrUpdate(SignEntity signEntity);

    /**
     * 分页查询机构维度报名记录
     */
    PageResult<SignEntity> queryShopSignPage(String signScene, Long indexShopId, String status,
                                             int pageNo, int pageSize);
}
