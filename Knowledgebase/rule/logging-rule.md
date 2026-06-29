---
title: 日志规范
type: rule
keywords: [日志, logging, 打日志, traceId, 排查]
related_docs: [rule/exception-rule.md]
source_refs: []
---

# 日志规范

## 适用范围

适用于服务端业务代码、任务代码、中间件调用和关键流程排查。

## MUST

- MUST 在跨领域流程的关键节点记录日志，例如报名提交、材料驳回、评审结论、打分完成、报告透出。
- MUST 带上可定位问题的业务标识，例如 `signId`、`materialId`、`reviewTaskId`、`scoreJobId`、`reportId`。
- MUST 记录失败原因和异常上下文，不只记录“失败了”。
- MUST 避免在日志中输出敏感信息或完整原始材料。

## SHOULD

- SHOULD 使用结构化日志字段，方便检索和聚合。
- SHOULD 在异步任务开始、结束、失败、重试时记录同一组核心字段。
- SHOULD 将用户可见问题和内部状态关联起来，方便答疑。

## 示例

```text
event=reject_sign_material signId=... materialId=... reasonCode=... operator=...
event=rank_report_generate_failed signId=... reportId=... reason=...
```

## 反例

```text
failed
error happened
material invalid: <完整原始材料内容>
```

## 例外情况

- 高频日志可降采样，但关键失败日志不得被采样丢弃。

## 相关文档

- [exception-rule.md](exception-rule.md)
- [../workflows/为什么我没有上榜-workflow.md](../workflows/为什么我没有上榜-workflow.md)
