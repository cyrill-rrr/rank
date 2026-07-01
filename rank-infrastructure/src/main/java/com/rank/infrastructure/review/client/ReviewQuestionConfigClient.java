package com.rank.infrastructure.review.client;

import com.rank.domain.review.repository.ReviewQuestionConfigRepository;
import com.rank.domain.review.vo.QuestionConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评审问题配置客户端（模拟海马/Appkit）。
 * 实现 ReviewQuestionConfigRepository 接口。
 * 本期不接入真实海马，返回硬编码示例数据。
 */
@Slf4j
@Component
public class ReviewQuestionConfigClient implements ReviewQuestionConfigRepository {

    /**
     * 模拟海马配置数据：问题ID -> QuestionConfigVO
     */
    private static final Map<String, QuestionConfigVO> MOCK_CONFIG = new HashMap<String, QuestionConfigVO>();

    static {
        // Q001: 案例完整度评分，必答
        MOCK_CONFIG.put("Q001", QuestionConfigVO.of("Q001", "案例完整度评分", true, "[1,2,3,4,5,6,7,8,9,10]"));
        // Q002: 技术难度评分，非必答
        MOCK_CONFIG.put("Q002", QuestionConfigVO.of("Q002", "技术难度评分", false, "[1,2,3,4,5,6,7,8,9,10]"));
    }

    @Override
    public List<QuestionConfigVO> queryQuestionConfig(List<String> questionIdList) {
        log.info("[ReviewQuestionConfigClient queryQuestionConfig] 查询海马配置, questionIdList={}", questionIdList);
        if (CollectionUtils.isEmpty(questionIdList)) {
            return Collections.emptyList();
        }

        List<QuestionConfigVO> result = new ArrayList<QuestionConfigVO>();
        for (String qid : questionIdList) {
            QuestionConfigVO config = MOCK_CONFIG.get(qid);
            if (config != null) {
                result.add(config);
            } else {
                log.error("[ReviewQuestionConfigClient queryQuestionConfig] 问题ID配置缺失, questionId={}",
                        qid, new IllegalStateException("问题ID配置缺失"));
            }
        }

        // 如果任何问题ID缺失，整体返回失败
        if (result.size() != questionIdList.size()) {
            log.error("[ReviewQuestionConfigClient queryQuestionConfig] 部分问题配置缺失, requested={}, found={}",
                    questionIdList.size(), result.size(), new IllegalStateException("海马配置不完整"));
            return Collections.emptyList();
        }

        return result;
    }
}
