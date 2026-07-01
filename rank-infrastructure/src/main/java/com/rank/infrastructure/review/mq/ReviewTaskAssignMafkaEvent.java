package com.rank.infrastructure.review.mq;

import lombok.Data;

import java.io.Serializable;

/**
 * 分单消息骨架（Mafka 消息体）。
 * 保留 Mafka 框架的骨架代码。本期使用 Spring Event 替代真实 MQ，
 * 不直接使用此消息体发送消息。
 */
@Data
public class ReviewTaskAssignMafkaEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 外部材料ID */
    private Long materialId;
    /** 专家用户ID */
    private Long userId;
    /** 评审场景 */
    private String scene;
    /** 操作人用户ID */
    private Long operatorUserId;
    /** 消息发送时间 */
    private Long occurredAt;
}
