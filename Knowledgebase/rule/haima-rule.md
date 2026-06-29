---
title: Haima标准接入
type: rule
keywords: [Haima, haima, 中间件, 标准接入, 排查]
related_docs: [rule/logging-rule.md, rule/exception-rule.md]
source_refs: []
---

# Haima 标准接入

## 适用场景

TODO: 补充 Haima 在公司内部的准确含义、适用场景和边界。

## 接入方式

TODO: 补充 Haima SDK、控制台、权限、配置和发布流程。

## 配置规范

| 配置 | 要求 |
| --- | --- |
| 服务标识 | TODO: 补充命名规则 |
| 权限 | TODO: 补充申请和审批流程 |
| 超时 | TODO: 补充默认值和例外 |
| 降级 | TODO: 补充失败兜底策略 |

## MUST

- MUST 在接入前明确 Haima 的业务用途和调用边界。
- MUST 记录调用失败时的业务影响。
- MUST 补充日志字段，至少能定位调用方、被调方和业务主键。

## SHOULD

- SHOULD 为关键调用配置监控和报警。
- SHOULD 在领域文档中标记依赖 Haima 的业务能力。

## 示例

TODO: 补充标准接入示例。

## 反例

TODO: 补充已知错误接入方式。

## 常见坑

TODO: 补充 Haima 常见坑。

## 监控/排查

TODO: 补充核心监控指标、控制台入口和排查步骤。
