package com.rank.application.review.facade;

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
import com.rank.api.review.facade.ReviewFacade;
import com.rank.application.review.assembler.ReviewAssembler;
import com.rank.application.review.command.DeleteReviewTasksCommand;
import com.rank.application.review.command.ImportReviewTasksCommand;
import com.rank.application.review.command.SubmitReviewScoreCommand;
import com.rank.application.review.qry.ReviewTaskDetailQry;
import com.rank.application.review.qry.ReviewTaskPageQry;
import com.rank.application.review.service.ReviewCommandAppService;
import com.rank.application.review.service.ReviewCommandAppService.SubmitReviewScoreResult;
import com.rank.application.review.service.ReviewQueryAppService;
import com.rank.application.review.service.ReviewQueryAppService.ReviewTaskDetailResult;
import com.rank.domain.common.PageResult;
import com.rank.domain.common.exception.BizException;
import com.rank.domain.review.model.ReviewTaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 评审Facade实现
 */
@Slf4j
@Service
public class ReviewFacadeImpl implements ReviewFacade {

    /**
     * 管理员用户ID白名单（硬编码，本期不接入鉴权系统）
     */
    private static final Set<Long> ADMIN_USER_IDS = new HashSet<Long>(Arrays.asList(90001L));

    private final ReviewAssembler reviewAssembler;
    private final ReviewCommandAppService reviewCommandAppService;
    private final ReviewQueryAppService reviewQueryAppService;

    public ReviewFacadeImpl(ReviewAssembler reviewAssembler,
                            ReviewCommandAppService reviewCommandAppService,
                            ReviewQueryAppService reviewQueryAppService) {
        this.reviewAssembler = reviewAssembler;
        this.reviewCommandAppService = reviewCommandAppService;
        this.reviewQueryAppService = reviewQueryAppService;
    }

    @Override
    public Response<ImportReviewTasksResponseDTO> importReviewTasks(ImportReviewTasksRequestDTO request) {
        long start = System.currentTimeMillis();
        try {
            // 1. 参数校验（快速失败）
            validateImportReviewTasks(request);

            // 2. 获取operatorUserId（建议由登录态注入，此处从DTO读取）
            Long operatorUserId = request.getOperatorUserId();

            // 3. DTO转Command
            ImportReviewTasksCommand command = reviewAssembler.toImportCommand(request, operatorUserId);

            // 4. 执行分单（发布Spring Event）
            reviewCommandAppService.importReviewTasks(command);

            // 5. 结果转换
            ImportReviewTasksResponseDTO response = reviewAssembler.toImportResponse(true);
            return Response.success(response);
        } catch (IllegalArgumentException e) {
            log.error("[ReviewFacadeImpl importReviewTasks] 参数校验失败, request={}", request, e);
            return Response.fail(400101, e.getMessage());
        } catch (BizException e) {
            log.error("[ReviewFacadeImpl importReviewTasks] 业务异常, request={}", request, e);
            return Response.fail(400101, e.getMessage());
        } catch (Exception e) {
            log.error("[ReviewFacadeImpl importReviewTasks] 分单异常, request={}", request, e);
            return Response.fail(400101, "分单操作失败");
        } finally {
            log.info("[ReviewFacadeImpl importReviewTasks] 耗时:{}ms", System.currentTimeMillis() - start);
        }
    }

    @Override
    public Response<DeleteReviewTasksResponseDTO> deleteReviewTasks(DeleteReviewTasksRequestDTO request) {
        long start = System.currentTimeMillis();
        try {
            // 1. 参数校验
            validateDeleteReviewTasks(request);

            // 2. 获取操作人用户ID
            Long operatorUserId = request.getOperatorUserId();

            // 3. DTO转Command
            DeleteReviewTasksCommand command = reviewAssembler.toDeleteCommand(request, operatorUserId);

            // 4. 执行删单
            int affectedRows = reviewCommandAppService.deleteReviewTasks(command);

            // 5. 结果转换
            DeleteReviewTasksResponseDTO response = reviewAssembler.toDeleteResponse(affectedRows);
            return Response.success(response);
        } catch (IllegalArgumentException e) {
            log.error("[ReviewFacadeImpl deleteReviewTasks] 参数校验失败, request={}", request, e);
            return Response.fail(400102, e.getMessage());
        } catch (BizException e) {
            log.error("[ReviewFacadeImpl deleteReviewTasks] 业务异常, request={}", request, e);
            return Response.fail(400102, e.getMessage());
        } catch (Exception e) {
            log.error("[ReviewFacadeImpl deleteReviewTasks] 删单异常, request={}", request, e);
            return Response.fail(400102, "删单操作失败");
        } finally {
            log.info("[ReviewFacadeImpl deleteReviewTasks] 耗时:{}ms", System.currentTimeMillis() - start);
        }
    }

    @Override
    public Response<QueryReviewTasksResponseDTO> queryReviewTasks(QueryReviewTasksRequestDTO request) {
        long start = System.currentTimeMillis();
        try {
            // 1. 参数校验
            validateQueryReviewTasks(request);

            // 2. 获取登录上下文（建议由登录态注入）
            Long loginUserId = request.getLoginUserId();
            boolean admin = isAdmin(loginUserId);

            // 3. DTO转Qry
            ReviewTaskPageQry qry = reviewAssembler.toPageQry(request, loginUserId, admin);

            // 4. 查询
            PageResult<ReviewTaskEntity> pageResult = reviewQueryAppService.queryReviewTasks(qry);

            // 5. 结果转换
            QueryReviewTasksResponseDTO response = reviewAssembler.toQueryPageResponse(
                    pageResult.getTotal(), pageResult.getPageNo(),
                    pageResult.getPageSize(), pageResult.getRecords());
            return Response.success(response);
        } catch (IllegalArgumentException e) {
            log.error("[ReviewFacadeImpl queryReviewTasks] 参数校验失败, request={}", request, e);
            return Response.fail(400103, e.getMessage());
        } catch (BizException e) {
            log.error("[ReviewFacadeImpl queryReviewTasks] 业务异常, request={}", request, e);
            return Response.fail(400103, e.getMessage());
        } catch (Exception e) {
            log.error("[ReviewFacadeImpl queryReviewTasks] 查询列表异常, request={}", request, e);
            return Response.fail(400103, "查询列表失败");
        } finally {
            log.info("[ReviewFacadeImpl queryReviewTasks] 耗时:{}ms", System.currentTimeMillis() - start);
        }
    }

    @Override
    public Response<QueryReviewTaskDetailResponseDTO> queryReviewTaskDetail(QueryReviewTaskDetailRequestDTO request) {
        long start = System.currentTimeMillis();
        try {
            // 1. 参数校验
            validateQueryReviewTaskDetail(request);

            // 2. 获取登录上下文
            Long loginUserId = request.getLoginUserId();
            boolean admin = isAdmin(loginUserId);

            // 3. DTO转Qry
            ReviewTaskDetailQry qry = reviewAssembler.toDetailQry(request, loginUserId, admin);

            // 4. 查询详情
            ReviewTaskDetailResult result = reviewQueryAppService.queryReviewTaskDetail(qry);

            // 5. 结果转换
            QueryReviewTaskDetailResponseDTO response = reviewAssembler.toDetailResponse(
                    result.getEntity(), result.getMaterialDetail(), result.getQuestionConfigs());
            return Response.success(response);
        } catch (IllegalArgumentException e) {
            log.error("[ReviewFacadeImpl queryReviewTaskDetail] 参数校验失败, request={}", request, e);
            return Response.fail(400104, e.getMessage());
        } catch (BizException e) {
            log.error("[ReviewFacadeImpl queryReviewTaskDetail] 业务异常, request={}", request, e);
            return Response.fail(400104, e.getMessage());
        } catch (Exception e) {
            log.error("[ReviewFacadeImpl queryReviewTaskDetail] 查询详情异常, request={}", request, e);
            return Response.fail(400104, "查询详情失败");
        } finally {
            log.info("[ReviewFacadeImpl queryReviewTaskDetail] 耗时:{}ms", System.currentTimeMillis() - start);
        }
    }

    @Override
    public Response<SubmitReviewScoreResponseDTO> submitReviewScore(SubmitReviewScoreRequestDTO request) {
        long start = System.currentTimeMillis();
        try {
            // 1. 参数校验
            validateSubmitReviewScore(request);

            // 2. 获取登录用户
            Long loginUserId = request.getLoginUserId();

            // 3. DTO转Command
            SubmitReviewScoreCommand command = reviewAssembler.toSubmitCommand(request, loginUserId);

            // 4. 执行提交打分
            SubmitReviewScoreResult result = reviewCommandAppService.submitReviewScore(command);

            // 5. 结果转换
            SubmitReviewScoreResponseDTO response = reviewAssembler.toSubmitResponse(
                    result.getReviewTaskId(), result.getStatus());
            return Response.success(response);
        } catch (IllegalArgumentException e) {
            log.error("[ReviewFacadeImpl submitReviewScore] 参数校验失败, request={}", request, e);
            return Response.fail(400105, e.getMessage());
        } catch (BizException e) {
            log.error("[ReviewFacadeImpl submitReviewScore] 业务异常, request={}", request, e);
            return Response.fail(400105, e.getMessage());
        } catch (Exception e) {
            log.error("[ReviewFacadeImpl submitReviewScore] 提交打分异常, request={}", request, e);
            return Response.fail(400105, "提交打分失败");
        } finally {
            log.info("[ReviewFacadeImpl submitReviewScore] 耗时:{}ms", System.currentTimeMillis() - start);
        }
    }

    /**
     * 判断用户是否为管理员
     */
    private boolean isAdmin(Long userId) {
        return userId != null && ADMIN_USER_IDS.contains(userId);
    }

    /**
     * 校验分单参数
     */
    private void validateImportReviewTasks(ImportReviewTasksRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        if (request.getOperatorUserId() == null) {
            throw new IllegalArgumentException("操作人用户ID不能为空");
        }
        if (request.getRows() == null || request.getRows().isEmpty()) {
            throw new IllegalArgumentException("分单行列表不能为空");
        }
        for (int i = 0; i < request.getRows().size(); i++) {
            ImportReviewTasksRequestDTO.ImportReviewTaskItemDTO row = request.getRows().get(i);
            if (row.getMaterialId() == null) {
                throw new IllegalArgumentException("第" + (i + 1) + "行的材料ID不能为空");
            }
            if (row.getUserId() == null) {
                throw new IllegalArgumentException("第" + (i + 1) + "行的用户ID不能为空");
            }
            if (row.getScene() == null || row.getScene().trim().isEmpty()) {
                throw new IllegalArgumentException("第" + (i + 1) + "行的场景不能为空");
            }
        }
    }

    /**
     * 校验删单参数
     */
    private void validateDeleteReviewTasks(DeleteReviewTasksRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        if (request.getOperatorUserId() == null) {
            throw new IllegalArgumentException("操作人用户ID不能为空");
        }
        if (!isAdmin(request.getOperatorUserId())) {
            throw BizException.forbidden("仅管理员可执行删单操作");
        }
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
    }

    /**
     * 校验列表查询参数
     */
    private void validateQueryReviewTasks(QueryReviewTasksRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        if (request.getLoginUserId() == null) {
            throw new IllegalArgumentException("登录用户ID不能为空");
        }
        if (request.getPageNo() == null || request.getPageNo() < 1) {
            throw new IllegalArgumentException("页码不合法");
        }
        if (request.getPageSize() == null || request.getPageSize() < 1) {
            throw new IllegalArgumentException("每页大小不合法");
        }
    }

    /**
     * 校验详情查询参数
     */
    private void validateQueryReviewTaskDetail(QueryReviewTaskDetailRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        if (request.getLoginUserId() == null) {
            throw new IllegalArgumentException("登录用户ID不能为空");
        }
        if (request.getReviewTaskId() == null) {
            throw new IllegalArgumentException("评审任务ID不能为空");
        }
    }

    /**
     * 校验提交打分参数
     */
    private void validateSubmitReviewScore(SubmitReviewScoreRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        if (request.getLoginUserId() == null) {
            throw new IllegalArgumentException("登录用户ID不能为空");
        }
        if (request.getReviewTaskId() == null) {
            throw new IllegalArgumentException("评审任务ID不能为空");
        }
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new IllegalArgumentException("每题作答不能为空");
        }
        for (int i = 0; i < request.getAnswers().size(); i++) {
            SubmitReviewScoreRequestDTO.QuestionAnswerDTO answer = request.getAnswers().get(i);
            if (answer.getQuestionId() == null || answer.getQuestionId().trim().isEmpty()) {
                throw new IllegalArgumentException("第" + (i + 1) + "个问题ID不能为空");
            }
            if (answer.getScore() == null || answer.getScore() < 1 || answer.getScore() > 10) {
                throw new IllegalArgumentException("第" + (i + 1) + "个分数必须在1~10之间");
            }
        }
    }
}
