package com.rank.infrastructure.review.mq;

import com.rank.domain.review.event.ReviewTaskAssignEvent;
import com.rank.application.review.factory.ReviewFactory;
import com.rank.domain.review.model.ReviewTaskEntity;
import com.rank.domain.review.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 分单消费处理器。
 * 通过 @EventListener 监听 ReviewTaskAssignEvent，实现异步消费。
 * 使用 Spring Event 模拟 MQ 消费，保留 Mafka 骨架代码但不直接使用。
 */
@Slf4j
@Component
public class ReviewTaskAssignConsumer {

    private final ReviewRepository reviewRepository;
    private final ReviewFactory reviewFactory;

    public ReviewTaskAssignConsumer(ReviewRepository reviewRepository, ReviewFactory reviewFactory) {
        this.reviewRepository = reviewRepository;
        this.reviewFactory = reviewFactory;
    }

    /**
     * 处理分单事件
     *
     * @param event 分单事件
     */
    @EventListener
    public void handle(ReviewTaskAssignEvent event) {
        if (event == null || event.getMaterialId() == null || event.getUserId() == null
                || event.getScene() == null || event.getScene().trim().isEmpty()) {
            log.error("[ReviewTaskAssignConsumer handle] 分单事件参数缺失, event={}", event);
            return;
        }

        try {
            // 1. 幂等检查：同一 materialId + userId + scene 是否已存在有效记录
            ReviewTaskEntity existing = reviewRepository.findByBizKey(
                    event.getMaterialId(), event.getUserId(), event.getScene());
            if (existing != null) {
                log.info("[ReviewTaskAssignConsumer handle] 重复分单跳过, materialId={}, userId={}, scene={}",
                        event.getMaterialId(), event.getUserId(), event.getScene());
                return;
            }

            // 2. 通过 Factory 创建评审单
            ReviewTaskEntity entity = reviewFactory.createNewTask(
                    event.getMaterialId(), event.getUserId(), event.getScene());

            // 3. 持久化
            reviewRepository.save(entity);
            log.info("[ReviewTaskAssignConsumer handle] 评审单创建成功, id={}, materialId={}, userId={}, scene={}",
                    entity.getId(), event.getMaterialId(), event.getUserId(), event.getScene());
        } catch (Exception e) {
            log.error("[ReviewTaskAssignConsumer handle] 消费分单事件异常, event={}", event, e);
        }
    }
}
