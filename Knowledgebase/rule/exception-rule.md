---
title: 异常处理规范
type: rule
keywords: [异常, exception, 错误处理, 失败, 重试]
related_docs: [rule/logging-rule.md]
source_refs: []
---

# 异常处理规范

## 适用范围

适用于业务校验失败、外部依赖失败、中间件调用失败、异步任务失败和用户可见错误。

## MUST

- MUST 区分业务失败和系统异常。业务失败应有可解释原因，系统异常应可排查。
- MUST 保留原始异常上下文，禁止吞异常后只返回空结果。
- MUST 对可重试和不可重试失败做区分。
- MUST 对用户可见错误提供稳定错误码或原因码。

## SHOULD

- SHOULD 在领域文档中记录重要失败原因和状态映射。
- SHOULD 对异步任务失败提供重试或补偿策略。
- SHOULD 将异常处理和日志字段保持一致，方便排查。

## 示例

```text
业务失败：材料状态不允许驳回 -> 返回明确原因码
系统异常：报告生成任务依赖超时 -> 记录依赖、参数、traceId，进入重试或补偿
```

## 反例

```text
catch (Exception e) {
  return null;
}
```

## 例外情况

- 非关键旁路逻辑可以降级，但必须记录降级原因和影响范围。

## 相关文档

- [logging-rule.md](logging-rule.md)
