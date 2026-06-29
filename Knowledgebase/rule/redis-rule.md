---
title: Redis标准接入
type: rule
keywords: [Redis, 缓存, 分布式锁, TTL, key]
related_docs: [rule/logging-rule.md, rule/exception-rule.md]
source_refs: []
---

# Redis 标准接入

## 适用场景

适用于缓存、限流、短期状态、分布式锁等场景。

## 接入方式

TODO: 补充公司内部 Redis SDK、实例申请流程、权限申请流程和配置位置。

## 配置规范

| 配置 | 要求 |
| --- | --- |
| key | MUST 包含业务前缀和关键业务标识 |
| TTL | MUST 明确过期时间，禁止无界缓存 |
| value | MUST 明确序列化格式和版本 |
| lock | MUST 明确锁超时和释放策略 |

## MUST

- MUST 为缓存 key 设计稳定命名。
- MUST 设置合理 TTL。
- MUST 处理缓存穿透、击穿和雪崩风险。
- MUST 避免把 Redis 当作长期 source of truth，除非架构明确允许。

## SHOULD

- SHOULD 在 key 中体现业务域，例如 `report:{reportId}`。
- SHOULD 对缓存命中率和延迟做监控。
- SHOULD 对热点 key 设计降级或限流策略。

## 示例

```text
report:visible:{reportId}
material:status:{materialId}
```

## 反例

```text
data:{id}
cache:{id}
无 TTL 的临时状态
```

## 常见坑

- key 命名过于泛化，排查时无法知道业务含义。
- TTL 过长导致用户看到旧结果。
- 分布式锁没有超时或误删其他请求的锁。

## 监控/排查

- 命中率。
- 慢查询。
- 大 key。
- 热 key。
- 连接数和错误率。
