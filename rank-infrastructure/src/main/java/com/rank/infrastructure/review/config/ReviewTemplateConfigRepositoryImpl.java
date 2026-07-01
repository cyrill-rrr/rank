package com.rank.infrastructure.review.config;

import com.rank.domain.review.repository.ReviewTemplateConfigRepository;
import com.rank.domain.review.vo.QuestionGroupVO;
import com.rank.domain.review.vo.ReviewTemplateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

/**
 * 评审模板配置仓储 Mock 实现（模拟 Lion 配置中心）。
 * 本期不做真实 Lion 接入，返回硬编码示例数据。
 */
@Slf4j
@Component
public class ReviewTemplateConfigRepositoryImpl implements ReviewTemplateConfigRepository {

    @Override
    public ReviewTemplateVO findByScene(String scene) {
        log.info("[ReviewTemplateConfigRepositoryImpl findByScene] 读取模板配置, scene={}", scene);
        if (scene == null || scene.trim().isEmpty()) {
            log.error("[ReviewTemplateConfigRepositoryImpl findByScene] scene为空", new IllegalArgumentException("scene为空"));
            return null;
        }

        // 硬编码示例数据：MEDICAL_BEAUTY_DOCTOR 场景
        // questionIds=[Q001, Q002]; groups=[]
        return ReviewTemplateVO.of(
                scene,
                Arrays.asList("Q001", "Q002"),
                Collections.<QuestionGroupVO>emptyList()
        );
    }
}
