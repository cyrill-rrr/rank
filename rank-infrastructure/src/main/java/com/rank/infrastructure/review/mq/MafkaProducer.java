package com.rank.infrastructure.review.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Mafka Producer 骨架。
 * 本期使用 Spring Event 替代真实 Mafka，真正的发送逻辑在
 * ReviewCommandAppService 中通过 ApplicationEventPublisher 实现。
 * 本类保留骨架，不进行实际 MQ 调用。
 */
@Slf4j
@Component
public class MafkaProducer {

    /**
     * 发送分单消息（骨架方法，本期不使用）
     */
    public void send(ReviewTaskAssignMafkaEvent event) {
        log.info("[MafkaProducer send] Mafka骨架方法，实际未发送, event={}", event);
    }
}
