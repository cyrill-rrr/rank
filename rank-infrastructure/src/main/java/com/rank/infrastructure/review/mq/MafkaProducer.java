package com.rank.infrastructure.review.mq;

import com.rank.domain.review.event.ReviewTaskAssignEvent;
import com.rank.domain.review.repository.ReviewTaskAssignProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Mafka Producer 桥接实现，实现 ReviewTaskAssignProducer 接口。
 * 本期因 Mafka 环境未就绪，内部使用 Spring Event 桥接异步消费。
 * 后续 Mafka 环境就绪后，只需将 publishEvent 替换为真实 Mafka 发送调用，
 * 调用方 ReviewCommandAppService.importReviewTasks 无需任何变更。
 */
@Slf4j
@Component
public class MafkaProducer implements ReviewTaskAssignProducer {

    private final ApplicationEventPublisher eventPublisher;

    public MafkaProducer(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void send(Long materialId, Long userId, String scene, Long operatorUserId) {
        log.info("[MafkaProducer send] 发送分单消息, materialId={}, userId={}, scene={}",
                materialId, userId, scene);
        // 桥接：通过 Spring Event 发布，由 ReviewTaskAssignConsumer (@EventListener) 消费
        eventPublisher.publishEvent(new ReviewTaskAssignEvent(
                materialId, userId, scene, operatorUserId));
    }
}
