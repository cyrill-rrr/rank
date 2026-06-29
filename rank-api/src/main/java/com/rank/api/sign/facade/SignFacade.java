package com.rank.api.sign.facade;

import com.rank.api.common.Response;
import com.rank.api.sign.dto.OperateSignRequestDTO;
import com.rank.api.sign.dto.OperateSignResponseDTO;
import com.rank.api.sign.dto.QuerySelectableProjectsRequestDTO;
import com.rank.api.sign.dto.QuerySelectableProjectsResponseDTO;
import com.rank.api.sign.dto.QueryShopDoctorsRequestDTO;
import com.rank.api.sign.dto.QueryShopDoctorsResponseDTO;
import com.rank.api.sign.dto.QuerySignPageRequestDTO;
import com.rank.api.sign.dto.QuerySignPageResponseDTO;

/**
 * 司南榜报名Facade接口
 */
public interface SignFacade {

    /**
     * 报名操作接口，提报和退报共用。
     * 接口文档：按signScene/operationType/subjectId/projectCodeList/shopId入参
     * 网关Path：本期暂无网关接入，BFF方接口拼接后调用
     *
     * @param request 报名操作请求
     * @return 报名操作响应
     */
    Response<OperateSignResponseDTO> operateSign(OperateSignRequestDTO request);

    /**
     * 查询报名结果分页列表。
     * 按机构维度分页查询机构榜或医生榜报名记录。
     * 接口文档：按signScene/shopId/status/pageNo/pageSize入参
     * 网关Path：本期暂无网关接入，BFF方接口拼接后调用
     *
     * @param request 分页查询请求
     * @return 分页查询响应
     */
    Response<QuerySignPageResponseDTO> querySignPage(QuerySignPageRequestDTO request);

    /**
     * 查询当前报名场景可选择的提报项目。
     * 接口文档：只返回项目列表，不返回窗口期状态
     * 网关Path：本期暂无网关接入，BFF方接口拼接后调用
     *
     * @param request 可选项目查询请求
     * @return 可选项目查询响应
     */
    Response<QuerySelectableProjectsResponseDTO> querySelectableProjects(QuerySelectableProjectsRequestDTO request);

    /**
     * 查询当前机构下可用于医生榜报名的医生列表。
     * 接口文档：按shopId/includeOffline入参
     * 网关Path：本期暂无网关接入，BFF方接口拼接后调用
     *
     * @param request 机构医生查询请求
     * @return 机构医生查询响应
     */
    Response<QueryShopDoctorsResponseDTO> queryShopDoctors(QueryShopDoctorsRequestDTO request);
}
