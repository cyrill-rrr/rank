package com.rank.api.material.facade;

import com.rank.api.common.Response;
import com.rank.api.material.dto.OperateMaterialRequestDTO;
import com.rank.api.material.dto.OperateMaterialResponseDTO;
import com.rank.api.material.dto.QueryCurrentMaterialRequestDTO;
import com.rank.api.material.dto.QueryCurrentMaterialResponseDTO;

/**
 * 司南榜材料提报Facade接口
 */
public interface MaterialFacade {

    /**
     * 材料操作接口，保存草稿和送审共用。
     * 接口文档：按materialScene/auditSubjectId/operationType/materialJsonStr入参
     * 网关Path：本期暂无网关接入，BFF方接口拼接后调用
     *
     * @param request 材料操作请求
     * @return 材料操作响应
     */
    Response<OperateMaterialResponseDTO> operateMaterial(OperateMaterialRequestDTO request);

    /**
     * 查询当前材料信息。
     * 接口文档：按materialScene/auditSubjectId入参
     * 网关Path：本期暂无网关接入，BFF方接口拼接后调用
     *
     * @param request 当前材料查询请求
     * @return 当前材料查询响应
     */
    Response<QueryCurrentMaterialResponseDTO> queryCurrentMaterial(QueryCurrentMaterialRequestDTO request);
}
