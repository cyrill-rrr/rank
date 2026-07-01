package com.rank.api.review.facade;

import com.rank.api.common.Response;
import com.rank.api.review.dto.DeleteReviewTasksRequestDTO;
import com.rank.api.review.dto.DeleteReviewTasksResponseDTO;
import com.rank.api.review.dto.ImportReviewTasksRequestDTO;
import com.rank.api.review.dto.ImportReviewTasksResponseDTO;
import com.rank.api.review.dto.QueryReviewTaskDetailRequestDTO;
import com.rank.api.review.dto.QueryReviewTaskDetailResponseDTO;
import com.rank.api.review.dto.QueryReviewTasksRequestDTO;
import com.rank.api.review.dto.QueryReviewTasksResponseDTO;
import com.rank.api.review.dto.SubmitReviewScoreRequestDTO;
import com.rank.api.review.dto.SubmitReviewScoreResponseDTO;

/**
 * 评审系统 Facade 接口。
 * 接口文档：
 * - 分单：管理员批量导入分单，逐行 Spring Event 异步消费
 * - 删单：管理员按用户ID逻辑删除全部评审单
 * - 列表：管理员/专家共用，按角色控制可见范围
 * - 详情：材料详情 + 题目配置 + 当前作答
 * - 打分：专家提交每题 1~10 单选分
 */
public interface ReviewFacade {

    /**
     * 分单：管理员批量分单，逐行发送 Spring Event 异步消费
     *
     * @param request 分单请求（含 operatorUserId 和行列表）
     * @return accepted=true 表示已接收分单请求
     * 网关 Path：待补充
     */
    Response<ImportReviewTasksResponseDTO> importReviewTasks(ImportReviewTasksRequestDTO request);

    /**
     * 删单：管理员按用户ID删除该用户全部评审单（含已打分单据）
     *
     * @param request 删单请求（含 operatorUserId 和 userId）
     * @return affectedRows 删除影响行数
     * 网关 Path：待补充
     */
    Response<DeleteReviewTasksResponseDTO> deleteReviewTasks(DeleteReviewTasksRequestDTO request);

    /**
     * 列表：查询已打分/未打分评审单，管理员和非管理员共用
     * 管理员可按 materialId/userId/scene/status 查询
     * 非管理员强制使用登录 userId 限定范围
     *
     * @param request 列表查询请求
     * @return 分页结果
     * 网关 Path：待补充
     */
    Response<QueryReviewTasksResponseDTO> queryReviewTasks(QueryReviewTasksRequestDTO request);

    /**
     * 详情：查询评审单详情，包含材料详情、问题配置和当前作答
     *
     * @param request 详情查询请求
     * @return 评审单详情（含材料详情和问题列表）
     * 网关 Path：待补充
     */
    Response<QueryReviewTaskDetailResponseDTO> queryReviewTaskDetail(QueryReviewTaskDetailRequestDTO request);

    /**
     * 提交打分：专家提交评审单内所有问题的 1~10 单选分
     *
     * @param request 提交打分请求
     * @return 提交后的评审单 ID 和状态
     * 网关 Path：待补充
     */
    Response<SubmitReviewScoreResponseDTO> submitReviewScore(SubmitReviewScoreRequestDTO request);
}
