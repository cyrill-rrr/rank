package com.rank.application.review.service;

import com.rank.application.review.assembler.ReviewAssembler;
import com.rank.application.review.command.DeleteReviewTasksCommand;
import com.rank.application.review.command.ImportReviewTasksCommand;
import com.rank.application.review.command.SubmitReviewScoreCommand;
import com.rank.application.review.factory.ReviewFactory;
import com.rank.domain.common.exception.BizException;
import com.rank.domain.review.event.ReviewTaskAssignEvent;
import com.rank.domain.review.vo.QuestionAnswerVO;
import com.rank.domain.review.model.ReviewTaskEntity;
import com.rank.domain.review.repository.ReviewQuestionConfigRepository;
import com.rank.domain.review.repository.ReviewRepository;
import com.rank.domain.review.repository.ReviewTemplateConfigRepository;
import com.rank.domain.review.vo.QuestionConfigVO;
import com.rank.domain.review.vo.ReviewTemplateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 评审写操作编排服务
 */
@Slf4j
@Service
public class ReviewCommandAppService {

    private final ApplicationEventPublisher eventPublisher;
    private final ReviewRepository reviewRepository;
    private final ReviewTemplateConfigRepository reviewTemplateConfigRepository;
    private final ReviewQuestionConfigRepository reviewQuestionConfigRepository;
    private final ReviewFactory reviewFactory;
    private final ReviewAssembler reviewAssembler;

    public ReviewCommandAppService(ApplicationEventPublisher eventPublisher,
                                   ReviewRepository reviewRepository,
                                   ReviewTemplateConfigRepository reviewTemplateConfigRepository,
                                   ReviewQuestionConfigRepository reviewQuestionConfigRepository,
                                   ReviewFactory reviewFactory,
                                   ReviewAssembler reviewAssembler) {
        this.eventPublisher = eventPublisher;
        this.reviewRepository = reviewRepository;
        this.reviewTemplateConfigRepository = reviewTemplateConfigRepository;
        this.reviewQuestionConfigRepository = reviewQuestionConfigRepository;
        this.reviewFactory = reviewFactory;
        this.reviewAssembler = reviewAssembler;
    }

    /**
     * 分单：逐行发布 Spring Event
     *
     * @param command 分单命令
     */
    public void importReviewTasks(ImportReviewTasksCommand command) {
        // 1. 遍历每行分单数据，发布事件
        if (CollectionUtils.isEmpty(command.getRows())) {
            log.info("[ReviewCommandAppService importReviewTasks] 分单行为空, operatorUserId={}", command.getOperatorUserId());
            return;
        }
        for (ImportReviewTasksCommand.ImportReviewTaskItem row : command.getRows()) {
            // 2. 发布 ReviewTaskAssignEvent
            eventPublisher.publishEvent(new ReviewTaskAssignEvent(
                    row.getMaterialId(), row.getUserId(), row.getScene(), command.getOperatorUserId()));
        }
        log.info("[ReviewCommandAppService importReviewTasks] 发布分单事件完成, rows={}, operatorUserId={}",
                command.getRows().size(), command.getOperatorUserId());
    }

    /**
     * 删单：按用户ID逻辑删除全部评审单
     *
     * @param command 删单命令
     * @return 影响行数
     */
    public int deleteReviewTasks(DeleteReviewTasksCommand command) {
        // 1. 逻辑删除
        int affectedRows = reviewRepository.deleteByUserId(command.getUserId());
        log.info("[ReviewCommandAppService deleteReviewTasks] 删除评审单完成, userId={}, affectedRows={}",
                command.getUserId(), affectedRows);
        return affectedRows;
    }

    /**
     * 提交打分
     *
     * @param command 提交打分命令
     * @return 提交后的评审单ID和状态
     */
    public SubmitReviewScoreResult submitReviewScore(SubmitReviewScoreCommand command) {
        // 1. 查询评审单
        ReviewTaskEntity entity = reviewRepository.findById(command.getReviewTaskId());
        if (entity == null) {
            throw BizException.notFound("评审单不存在");
        }
        log.info("[ReviewCommandAppService submitReviewScore] 查询评审单完成, reviewTaskId={}, status={}",
                command.getReviewTaskId(), entity.getStatus());

        // 2. 读取 Lion 模板
        ReviewTemplateVO template = reviewTemplateConfigRepository.findByScene(entity.getScene());
        if (template == null || CollectionUtils.isEmpty(template.getQuestionIdList())) {
            log.error("[ReviewCommandAppService submitReviewScore] 模板配置缺失, scene={}", entity.getScene());
            throw BizException.invalidParam("模板配置缺失");
        }

        // 3. 读取海马问题配置
        List<QuestionConfigVO> questionConfigs = reviewQuestionConfigRepository.queryQuestionConfig(
                template.getQuestionIdList());
        if (CollectionUtils.isEmpty(questionConfigs)) {
            log.error("[ReviewCommandAppService submitReviewScore] 海马配置缺失, scene={}", entity.getScene());
            throw BizException.invalidParam("问题配置缺失");
        }

        // 4. 组装参数，调用领域方法
        Set<String> allowedIds = new HashSet<String>(template.getQuestionIdList());
        Set<String> requiredIds = new HashSet<String>();
        for (QuestionConfigVO qc : questionConfigs) {
            if (qc.isRequired()) {
                requiredIds.add(qc.getQuestionId());
            }
        }

        List<QuestionAnswerVO> answers = reviewAssembler.toQuestionAnswerVOList(command.getAnswers());
        entity.submitScores(command.getLoginUserId(), answers, allowedIds, requiredIds);

        // 5. 持久化
        reviewRepository.save(entity);
        log.info("[ReviewCommandAppService submitReviewScore] 提交打分成功, reviewTaskId={}, status={}",
                entity.getId(), entity.getStatus());

        return new SubmitReviewScoreResult(entity.getId(), entity.getStatus().name());
    }

    /**
     * 提交打分结果
     */
    public static class SubmitReviewScoreResult {
        private final Long reviewTaskId;
        private final String status;

        public SubmitReviewScoreResult(Long reviewTaskId, String status) {
            this.reviewTaskId = reviewTaskId;
            this.status = status;
        }

        public Long getReviewTaskId() {
            return reviewTaskId;
        }

        public String getStatus() {
            return status;
        }
    }
}
