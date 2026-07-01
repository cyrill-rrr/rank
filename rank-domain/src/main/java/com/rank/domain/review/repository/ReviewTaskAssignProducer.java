package com.rank.domain.review.repository;

/**
 * 分单消息生产者接口。
 * 定义分单消息的发送契约，由 infrastructure 层实现。
 * 当前使用 Spring Event 桥接异步消费（因 Mafka 环境未就绪），
 * 后续切换真实 Mafka 时只需修改 infrastructure 层的实现类，调用方无需变更。
 */
public interface ReviewTaskAssignProducer {

    /**
     * 发送分单消息
     *
     * @param materialId     外部材料ID
     * @param userId         专家用户ID
     * @param scene          评审场景
     * @param operatorUserId 操作人用户ID
     */
    void send(Long materialId, Long userId, String scene, Long operatorUserId);
}
