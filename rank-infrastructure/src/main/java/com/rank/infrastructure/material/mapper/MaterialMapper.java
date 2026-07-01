package com.rank.infrastructure.material.mapper;

import com.rank.infrastructure.material.po.MaterialPO;
import org.apache.ibatis.annotations.Param;

/**
 * 材料提报Mapper
 */
public interface MaterialMapper {

    /**
     * 按业务键（materialScene + auditSubjectId）查询材料记录
     */
    MaterialPO selectByBizKey(@Param("materialScene") String materialScene,
                              @Param("auditSubjectId") String auditSubjectId);

    /**
     * 按uapUniqueId查询材料记录
     */
    MaterialPO selectByUapUniqueId(@Param("uapUniqueId") String uapUniqueId);

    /**
     * 新增材料记录
     */
    int insert(MaterialPO po);

    /**
     * 按主键更新材料记录
     */
    int updateByBizKey(MaterialPO po);
}
