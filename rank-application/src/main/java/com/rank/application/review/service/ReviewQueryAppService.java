package com.rank.application.review.service;

import com.rank.application.review.qry.ReviewTaskDetailQry;
import com.rank.application.review.qry.ReviewTaskPageQry;
import com.rank.domain.common.PageResult;
import com.rank.domain.common.exception.BizException;
import com.rank.domain.review.model.ReviewTaskEntity;
import com.rank.domain.review.repository.ReviewQuestionConfigRepository;
import com.rank.domain.review.repository.MaterialDetailAcl;
import com.rank.domain.review.repository.ReviewRepository;
import com.rank.domain.review.repository.ReviewTemplateConfigRepository;
import com.rank.domain.review.vo.MaterialDetailVO;
import com.rank.domain.review.vo.QuestionConfigVO;
import com.rank.domain.review.vo.ReviewTemplateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 评审读操作编排服务
 */
@Slf4j
@Service
public class ReviewQueryAppService {

    private final ReviewRepository reviewRepository;
    private final MaterialDetailAcl materialDetailAcl;
    private final ReviewTemplateConfigRepository reviewTemplateConfigRepository;
    private final ReviewQuestionConfigRepository reviewQuestionConfigRepository;

    public ReviewQueryAppService(ReviewRepository reviewRepository,
                                 MaterialDetailAcl materialDetailAcl,
                                 ReviewTemplateConfigRepository reviewTemplateConfigRepository,
                                 ReviewQuestionConfigRepository reviewQuestionConfigRepository) {
        this.reviewRepository = reviewRepository;
        this.materialDetailAcl = materialDetailAcl;
        this.reviewTemplateConfigRepository = reviewTemplateConfigRepository;
        this.reviewQuestionConfigRepository = reviewQuestionConfigRepository;
    }

    /**
     * 分页查询评审单（管理员/专家共用）
     *
     * @param qry 查询条件
     * @return 分页结果
     */
    public PageResult<ReviewTaskEntity> queryReviewTasks(ReviewTaskPageQry qry) {
        // 1. 管理员 vs 专家不同查询路径
        if (qry.isAdmin()) {
            // 管理员：按筛选条件查全部
            return reviewRepository.queryPage(
                    qry.getMaterialId(), qry.getUserId(), qry.getScene(),
                    qry.getStatus(), qry.getPageNo(), qry.getPageSize());
        } else {
            // 非管理员（专家）：强制使用登录 userId 限定范围
            return reviewRepository.queryPageByUserId(
                    qry.getLoginUserId(), qry.getStatus(), qry.getPageNo(), qry.getPageSize());
        }
    }

    /**
     * 查询评审单详情（含材料详情和问题配置）
     *
     * @param qry 查询条件
     * @return 详情结果
     */
    public ReviewTaskDetailResult queryReviewTaskDetail(ReviewTaskDetailQry qry) {
        // 1. 查询评审单
        ReviewTaskEntity entity = reviewRepository.findById(qry.getReviewTaskId());
        if (entity == null) {
            throw BizException.notFound("评审单不存在");
        }

        // 2. 权限校验：非管理员只能查看自己的评审单
        if (!qry.isAdmin() && !qry.getLoginUserId().equals(entity.getUserId())) {
            throw BizException.forbidden("无权查看该评审单");
        }

        // 3. 查询材料详情（实时）
        MaterialDetailVO materialDetail = materialDetailAcl.queryByMaterialId(entity.getMaterialId());
        if (materialDetail == null) {
            log.error("[ReviewQueryAppService queryReviewTaskDetail] 材料详情查询失败, materialId={}", entity.getMaterialId());
            throw BizException.notFound("材料详情查询失败");
        }

        // 4. 读取 Lion 模板
        ReviewTemplateVO template = reviewTemplateConfigRepository.findByScene(entity.getScene());
        if (template == null || CollectionUtils.isEmpty(template.getQuestionIdList())) {
            log.error("[ReviewQueryAppService queryReviewTaskDetail] 模板配置缺失, scene={}", entity.getScene());
            throw BizException.notFound("模板配置缺失");
        }

        // 5. 读取海马问题配置
        List<QuestionConfigVO> questionConfigs = reviewQuestionConfigRepository.queryQuestionConfig(
                template.getQuestionIdList());
        if (CollectionUtils.isEmpty(questionConfigs)) {
            log.error("[ReviewQueryAppService queryReviewTaskDetail] 海马配置缺失, scene={}", entity.getScene());
            throw BizException.notFound("问题配置缺失");
        }

        return new ReviewTaskDetailResult(entity, materialDetail, questionConfigs);
    }

    /**
     * 详情结果
     */
    public static class ReviewTaskDetailResult {
        private final ReviewTaskEntity entity;
        private final MaterialDetailVO materialDetail;
        private final List<QuestionConfigVO> questionConfigs;

        public ReviewTaskDetailResult(ReviewTaskEntity entity, MaterialDetailVO materialDetail,
                                       List<QuestionConfigVO> questionConfigs) {
            this.entity = entity;
            this.materialDetail = materialDetail;
            this.questionConfigs = questionConfigs;
        }

        public ReviewTaskEntity getEntity() {
            return entity;
        }

        public MaterialDetailVO getMaterialDetail() {
            return materialDetail;
        }

        public List<QuestionConfigVO> getQuestionConfigs() {
            return questionConfigs;
        }
    }
}
