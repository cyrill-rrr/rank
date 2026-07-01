package com.rank.domain.review.repository;

import com.rank.domain.review.vo.QuestionConfigVO;

import java.util.List;

/**
 * 评审问题配置仓储（海马/Appkit）。
 * 按问题ID列表批量查询问题配置。
 */
public interface ReviewQuestionConfigRepository {

    /**
     * 批量查询问题配置
     *
     * @param questionIdList 问题ID列表
     * @return 问题配置列表；查询失败返回空列表
     */
    List<QuestionConfigVO> queryQuestionConfig(List<String> questionIdList);
}
