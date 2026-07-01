package com.rank.domain.review.model;

import com.rank.domain.common.exception.BizException;
import com.rank.domain.review.shared.ReviewTaskStatusEnum;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 聚合职责：维护一条材料评审任务的分配、作答和删除状态。
 * 核心不变量：
 * 1. 一条有效评审任务只对应一个 materialId、一个 userId、一个 scene。
 * 2. 未打分状态才能提交打分；已打分后不允许重新提交或修改。
 * 3. 专家提交的 questionId 必须来自提交时当前 scene 的问题模板。
 */
@Getter
public class ReviewTaskEntity {

    /** 评审任务主键 */
    private Long id;
    /** 外部材料ID */
    private Long materialId;
    /** 专家用户ID */
    private Long userId;
    /** 评审场景 */
    private String scene;
    /** 评审任务状态 */
    private ReviewTaskStatusEnum status;
    /** 问题ID与专家作答结果 */
    private List<QuestionAnswerVO> questionAnswerList;
    /** 是否已删除 */
    private Boolean deleted;
    /** 创建时间 */
    private Date createdTime;
    /** 更新时间 */
    private Date updatedTime;

    public ReviewTaskEntity() {
    }

    /**
     * 创建初始未打分评审任务
     *
     * @param materialId 外部材料ID
     * @param userId     专家用户ID
     * @param scene      评审场景
     */
    public ReviewTaskEntity(Long materialId, Long userId, String scene) {
        this.materialId = materialId;
        this.userId = userId;
        this.scene = scene;
        this.status = ReviewTaskStatusEnum.UNSCORED;
        this.questionAnswerList = new ArrayList<QuestionAnswerVO>();
        this.deleted = Boolean.FALSE;
        this.createdTime = new Date();
        this.updatedTime = new Date();
    }

    /**
     * 提交打分：校验归属用户、未打分状态、问题范围、必答完整性，然后写入分数并变更状态。
     *
     * @param submitUserId 提交打分的用户ID
     * @param answers      本次提交的每题分数
     * @param allowedIds   当前scene模板中的问题ID集合
     * @param requiredIds  海马配置中必答的问题ID集合
     */
    public void submitScores(Long submitUserId, List<QuestionAnswerVO> answers,
                             Set<String> allowedIds, Set<String> requiredIds) {
        if (!this.userId.equals(submitUserId)) {
            throw BizException.forbidden("无权提交该评审任务");
        }
        if (!ReviewTaskStatusEnum.UNSCORED.equals(this.status)) {
            throw BizException.illegalState("评审任务已打分，不能重复提交");
        }
        validateQuestionScope(answers, allowedIds);
        validateRequiredAnswered(answers, requiredIds);
        this.questionAnswerList = answers;
        this.status = ReviewTaskStatusEnum.SCORED;
        this.updatedTime = new Date();
    }

    /**
     * 逻辑删除评审任务
     */
    public void markDeleted() {
        this.deleted = Boolean.TRUE;
        this.updatedTime = new Date();
    }

    /**
     * 校验提交的问题是否都在当前scene模板的问题ID集合中
     */
    private void validateQuestionScope(List<QuestionAnswerVO> answers, Set<String> allowedIds) {
        for (QuestionAnswerVO answer : answers) {
            if (!allowedIds.contains(answer.getQuestionId())) {
                throw BizException.invalidParam("提交了当前场景模板外的问题");
            }
        }
    }

    /**
     * 校验必答问题是否都已作答
     */
    private void validateRequiredAnswered(List<QuestionAnswerVO> answers, Set<String> requiredIds) {
        Set<String> answeredIds = new HashSet<String>();
        for (QuestionAnswerVO answer : answers) {
            if (answer.hasScore()) {
                answeredIds.add(answer.getQuestionId());
            }
        }
        for (String requiredId : requiredIds) {
            if (!answeredIds.contains(requiredId)) {
                throw BizException.invalidParam("必答问题未完成");
            }
        }
    }

    // ====== MyBatis / Converter 使用 setter ======

    public void setId(Long id) {
        this.id = id;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public void setStatus(ReviewTaskStatusEnum status) {
        this.status = status;
    }

    public void setQuestionAnswerList(List<QuestionAnswerVO> questionAnswerList) {
        this.questionAnswerList = questionAnswerList;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }
}
