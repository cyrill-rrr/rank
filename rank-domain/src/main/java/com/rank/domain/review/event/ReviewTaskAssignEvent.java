package com.rank.domain.review.event;

import lombok.Getter;

/**
 * 分单事件：一条消息对应一条评审单创建请求。
 * 在 importReviewTasks 中通过 ApplicationEventPublisher 发布，
 * 由 ReviewTaskAssignConsumer 通过 @EventListener 异步消费。
 */
@Getter
public class ReviewTaskAssignEvent {

    /** 外部材料ID */
    private final Long materialId;
    /** 专家用户ID */
    private final Long userId;
    /** 评审场景 */
    private final String scene;
    /** 操作人用户ID */
    private final Long operatorUserId;

    public ReviewTaskAssignEvent(Long materialId, Long userId, String scene, Long operatorUserId) {
        this.materialId = materialId;
        this.userId = userId;
        this.scene = scene;
        this.operatorUserId = operatorUserId;
    }
}
