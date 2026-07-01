package com.rank.application.review.factory;

import com.rank.domain.review.model.ReviewTaskEntity;
import org.springframework.stereotype.Component;

/**
 * 评审任务工厂
 */
@Component
public class ReviewFactory {

    /**
     * 根据分单消息创建初始未打分评审任务
     *
     * @param materialId 外部材料ID
     * @param userId     专家用户ID
     * @param scene      评审场景
     * @return 初始未打分评审单
     */
    public ReviewTaskEntity createNewTask(Long materialId, Long userId, String scene) {
        return new ReviewTaskEntity(materialId, userId, scene);
    }
}
