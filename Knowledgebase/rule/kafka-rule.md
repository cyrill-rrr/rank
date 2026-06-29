---
title: Kafka标准接入
type: rule
keywords: [Kafka, 消息, 生产者, 消费者, topic, 重试]
related_docs: [rule/logging-rule.md, rule/exception-rule.md]
source_refs: []
---

# Kafka 标准接入

## 适用场景

适用于使用 Kafka 进行异步解耦、事件通知、任务驱动和跨系统数据同步的场景。

## 接入方式

TODO: 补充公司内部 Kafka SDK、topic 申请流程、权限申请流程和配置位置。

## 配置规范

| 配置 | 要求 |
| --- | --- |
| topic | MUST 使用能表达业务事件的命名 |
| consumer group | MUST 按消费方业务语义命名 |
| retry | MUST 明确失败重试和死信策略 |
| payload | MUST 保持兼容，新增字段优先向后兼容 |

## MUST

- MUST 明确消息是否允许重复消费。
- MUST 在消费者侧保证幂等。
- MUST 记录消息 key、topic、partition、offset 和业务主键。
- MUST 对消费失败定义重试、跳过或死信策略。

## SHOULD

- SHOULD 使用业务事件命名消息，而不是使用技术动作命名。
- SHOULD 在领域文档中标记关键事件的上游和下游。
- SHOULD 对消息 schema 的变更写明兼容性影响。

## 示例

```text
topic: rank-report-generated
key: signId
payload: reportId, signId, generatedAt, version
```

## 反例

```text
topic: data-sync
payload: 任意 JSON，无版本，无业务主键
```

## 常见坑

- 消费者没有幂等，重试导致重复写入。
- 消息字段删除或改名导致旧消费者失败。
- 只记录业务失败，不记录 offset，无法定位消息。

## 监控/排查

- 消费堆积。
- 消费失败率。
- 死信数量。
- 单条消息业务主键、offset 和 traceId。
