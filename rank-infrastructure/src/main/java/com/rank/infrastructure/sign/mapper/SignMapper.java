package com.rank.infrastructure.sign.mapper;

import com.rank.infrastructure.sign.po.SignPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 报名记录Mapper
 */
public interface SignMapper {

    /**
     * 按报名场景和主体ID查询记录
     */
    List<SignPO> queryBySceneSubject(@Param("signScene") String signScene,
                                     @Param("subjectId") Long subjectId);

    /**
     * 统计医生榜中某机构已报名医生数（去重subjectId）
     */
    Integer countSignedDoctorsByIndexShopId(@Param("signScene") String signScene,
                                            @Param("indexShopId") Long indexShopId);

    /**
     * 新增报名记录
     */
    int insert(SignPO po);

    /**
     * 更新报名记录状态
     */
    int updateStatus(SignPO po);

    /**
     * 分页查询机构维度报名记录
     */
    List<SignPO> queryShopSignPage(@Param("signScene") String signScene,
                                   @Param("indexShopId") Long indexShopId,
                                   @Param("status") String status,
                                   @Param("offset") int offset,
                                   @Param("limit") int pageSize);

    /**
     * 统计机构维度报名记录总数
     */
    Long countShopSignPage(@Param("signScene") String signScene,
                           @Param("indexShopId") Long indexShopId,
                           @Param("status") String status);
}
