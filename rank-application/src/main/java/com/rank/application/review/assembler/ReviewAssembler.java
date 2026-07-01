package com.rank.application.review.assembler;

import com.rank.api.review.dto.DeleteReviewTasksRequestDTO;
import com.rank.api.review.dto.DeleteReviewTasksResponseDTO;
import com.rank.api.review.dto.ImportReviewTasksRequestDTO;
import com.rank.api.review.dto.ImportReviewTasksResponseDTO;
import com.rank.api.review.dto.QueryReviewTaskDetailRequestDTO;
import com.rank.api.review.dto.QueryReviewTaskDetailResponseDTO;
import com.rank.api.review.dto.QueryReviewTaskDetailResponseDTO.QuestionDetailDTO;
import com.rank.api.review.dto.QueryReviewTasksRequestDTO;
import com.rank.api.review.dto.QueryReviewTasksResponseDTO;
import com.rank.api.review.dto.QueryReviewTasksResponseDTO.ReviewTaskRecordDTO;
import com.rank.api.review.dto.SubmitReviewScoreRequestDTO;
import com.rank.api.review.dto.SubmitReviewScoreResponseDTO;
import com.rank.application.review.command.DeleteReviewTasksCommand;
import com.rank.application.review.command.ImportReviewTasksCommand;
import com.rank.application.review.command.ImportReviewTasksCommand.ImportReviewTaskItem;
import com.rank.application.review.command.SubmitReviewScoreCommand;
import com.rank.application.review.command.SubmitReviewScoreCommand.QuestionAnswerItem;
import com.rank.application.review.qry.ReviewTaskDetailQry;
import com.rank.application.review.qry.ReviewTaskPageQry;
import com.rank.domain.review.vo.QuestionAnswerVO;
import com.rank.domain.review.model.ReviewTaskEntity;
import com.rank.domain.review.vo.MaterialDetailVO;
import com.rank.domain.review.vo.QuestionConfigVO;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评审Assembler：DTO <-> Command/Qry 以及 Result <-> ResponseDTO 的双向转换
 */
@Component
public class ReviewAssembler {

    // ========== 分单 ==========

    /**
     * 分单请求DTO 转为 Command
     */
    public ImportReviewTasksCommand toImportCommand(ImportReviewTasksRequestDTO dto, Long operatorUserId) {
        ImportReviewTasksCommand command = new ImportReviewTasksCommand();
        command.setOperatorUserId(operatorUserId);                     // 操作人用户ID
        if (!CollectionUtils.isEmpty(dto.getRows())) {
            List<ImportReviewTaskItem> items = dto.getRows().stream().map(row -> {
                ImportReviewTaskItem item = new ImportReviewTaskItem();
                item.setMaterialId(row.getMaterialId());               // 外部材料ID
                item.setUserId(row.getUserId());                       // 专家用户ID
                item.setScene(row.getScene());                         // 评审场景
                return item;
            }).collect(Collectors.toList());
            command.setRows(items);
        } else {
            command.setRows(Collections.emptyList());
        }
        return command;
    }

    /**
     * 分单结果 转为 ResponseDTO
     */
    public ImportReviewTasksResponseDTO toImportResponse(boolean accepted) {
        ImportReviewTasksResponseDTO dto = new ImportReviewTasksResponseDTO();
        dto.setAccepted(accepted);
        return dto;
    }

    // ========== 删单 ==========

    /**
     * 删单请求DTO 转为 Command
     */
    public DeleteReviewTasksCommand toDeleteCommand(DeleteReviewTasksRequestDTO dto, Long operatorUserId) {
        DeleteReviewTasksCommand command = new DeleteReviewTasksCommand();
        command.setOperatorUserId(operatorUserId);                     // 操作人用户ID
        command.setUserId(dto.getUserId());                            // 被删除评审单所属用户ID
        return command;
    }

    /**
     * 删单结果 转为 ResponseDTO
     */
    public DeleteReviewTasksResponseDTO toDeleteResponse(int affectedRows) {
        DeleteReviewTasksResponseDTO dto = new DeleteReviewTasksResponseDTO();
        dto.setAffectedRows(affectedRows);
        return dto;
    }

    // ========== 列表 ==========

    /**
     * 列表请求DTO 转为 Qry
     */
    public ReviewTaskPageQry toPageQry(QueryReviewTasksRequestDTO dto, Long loginUserId, boolean admin) {
        ReviewTaskPageQry qry = new ReviewTaskPageQry();
        qry.setLoginUserId(loginUserId);                               // 当前登录用户ID
        qry.setAdmin(admin);                                           // 是否管理员
        qry.setMaterialId(dto.getMaterialId());                        // 材料ID筛选（管理员）
        qry.setUserId(dto.getUserId());                                // 用户ID筛选（管理员）
        qry.setScene(dto.getScene());                                  // 评审场景
        qry.setStatus(dto.getStatus());                                // 状态筛选：UNSCORED/SCORED
        qry.setPageNo(dto.getPageNo() != null ? dto.getPageNo() : 1);  // 页码，默认第1页
        qry.setPageSize(dto.getPageSize() != null ? dto.getPageSize() : 20); // 每页大小，默认20
        return qry;
    }

    /**
     * 列表结果 转为 ResponseDTO
     */
    public QueryReviewTasksResponseDTO toQueryPageResponse(long total, int pageNo, int pageSize,
                                                            List<ReviewTaskEntity> records) {
        QueryReviewTasksResponseDTO dto = new QueryReviewTasksResponseDTO();
        dto.setTotal(total);
        dto.setPageNo(pageNo);
        dto.setPageSize(pageSize);
        if (!CollectionUtils.isEmpty(records)) {
            List<ReviewTaskRecordDTO> recordDTOs = records.stream()
                    .map(this::toReviewTaskRecordDTO)
                    .collect(Collectors.toList());
            dto.setRecords(recordDTOs);
        } else {
            dto.setRecords(Collections.emptyList());
        }
        return dto;
    }

    /**
     * ReviewTaskEntity 转 ReviewTaskRecordDTO
     */
    private ReviewTaskRecordDTO toReviewTaskRecordDTO(ReviewTaskEntity entity) {
        ReviewTaskRecordDTO dto = new ReviewTaskRecordDTO();
        dto.setReviewTaskId(entity.getId());         // 评审任务ID
        dto.setMaterialId(entity.getMaterialId());   // 材料ID
        dto.setUserId(entity.getUserId());           // 专家用户ID
        dto.setScene(entity.getScene());             // 评审场景
        dto.setStatus(entity.getStatus().name());    // 评审状态
        return dto;
    }

    // ========== 详情 ==========

    /**
     * 详情请求DTO 转为 Qry
     */
    public ReviewTaskDetailQry toDetailQry(QueryReviewTaskDetailRequestDTO dto, Long loginUserId, boolean admin) {
        ReviewTaskDetailQry qry = new ReviewTaskDetailQry();
        qry.setLoginUserId(loginUserId);                               // 当前登录用户ID
        qry.setAdmin(admin);                                           // 是否管理员
        qry.setReviewTaskId(dto.getReviewTaskId());                    // 评审任务ID
        return qry;
    }

    /**
     * 详情结果 转为 ResponseDTO
     */
    public QueryReviewTaskDetailResponseDTO toDetailResponse(ReviewTaskEntity entity, MaterialDetailVO materialDetail,
                                                              List<QuestionConfigVO> questionConfigs) {
        QueryReviewTaskDetailResponseDTO dto = new QueryReviewTaskDetailResponseDTO();
        dto.setReviewTaskId(entity.getId());             // 评审任务ID
        dto.setMaterialId(entity.getMaterialId());       // 材料ID

        // 材料详情
        if (materialDetail != null) {
            QueryReviewTaskDetailResponseDTO.MaterialDetailResponseDTO materialDTO =
                    new QueryReviewTaskDetailResponseDTO.MaterialDetailResponseDTO();
            materialDTO.setMaterialId(materialDetail.getMaterialId());           // 材料ID
            materialDTO.setMaterialTitle(materialDetail.getMaterialTitle());     // 材料标题
            materialDTO.setMaterialType(materialDetail.getMaterialType());       // 材料类型
            materialDTO.setMaterialJsonStr(materialDetail.getMaterialJsonStr()); // 材料详情JSON
            dto.setMaterialDetail(materialDTO);
        }

        // 问题配置 + 已有作答
        // 构建 questionId -> 已有作答的映射
        Map<String, Integer> scoreMap = new HashMap<String, Integer>();
        if (!CollectionUtils.isEmpty(entity.getQuestionAnswerList())) {
            for (QuestionAnswerVO answer : entity.getQuestionAnswerList()) {
                scoreMap.put(answer.getQuestionId(), answer.getScore());
            }
        }

        if (!CollectionUtils.isEmpty(questionConfigs)) {
            List<QuestionDetailDTO> questions = questionConfigs.stream()
                    .map(qc -> {
                        QuestionDetailDTO qd = new QuestionDetailDTO();
                        qd.setQuestionId(qc.getQuestionId());   // 问题ID
                        qd.setTitle(qc.getTitle());             // 题干
                        qd.setRequired(qc.isRequired());         // 是否必答
                        qd.setScore(scoreMap.get(qc.getQuestionId())); // 已填分数，未填为null
                        return qd;
                    })
                    .collect(Collectors.toList());
            dto.setQuestions(questions);
        } else {
            dto.setQuestions(Collections.emptyList());
        }

        dto.setStatus(entity.getStatus().name());  // 评审状态
        return dto;
    }

    // ========== 提交打分 ==========

    /**
     * 提交打分请求DTO 转为 Command
     */
    public SubmitReviewScoreCommand toSubmitCommand(SubmitReviewScoreRequestDTO dto, Long loginUserId) {
        SubmitReviewScoreCommand command = new SubmitReviewScoreCommand();
        command.setReviewTaskId(dto.getReviewTaskId());                // 评审任务ID
        command.setLoginUserId(loginUserId);                           // 当前登录用户ID
        if (!CollectionUtils.isEmpty(dto.getAnswers())) {
            List<QuestionAnswerItem> items = dto.getAnswers().stream().map(a -> {
                QuestionAnswerItem item = new QuestionAnswerItem();
                item.setQuestionId(a.getQuestionId());                 // 问题ID
                item.setScore(a.getScore());                           // 分数1~10
                return item;
            }).collect(Collectors.toList());
            command.setAnswers(items);
        } else {
            command.setAnswers(Collections.emptyList());
        }
        return command;
    }

    /**
     * 提交打分结果 转为 ResponseDTO
     */
    public SubmitReviewScoreResponseDTO toSubmitResponse(Long reviewTaskId, String status) {
        SubmitReviewScoreResponseDTO dto = new SubmitReviewScoreResponseDTO();
        dto.setReviewTaskId(reviewTaskId);
        dto.setStatus(status);
        return dto;
    }

    /**
     * Command中的作答项 转为 领域层 QuestionAnswerVO 列表
     */
    public List<QuestionAnswerVO> toQuestionAnswerVOList(List<QuestionAnswerItem> items) {
        if (CollectionUtils.isEmpty(items)) {
            return Collections.emptyList();
        }
        List<QuestionAnswerVO> result = new ArrayList<QuestionAnswerVO>();
        for (QuestionAnswerItem item : items) {
            if (item.getScore() != null) {
                result.add(QuestionAnswerVO.answered(item.getQuestionId(), item.getScore()));
            } else {
                result.add(QuestionAnswerVO.unanswered(item.getQuestionId()));
            }
        }
        return result;
    }
}
