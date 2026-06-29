---
title: MySQL标准接入
type: rule
keywords: [MySQL, mysql, 表设计, 索引, 事务, 数据库]
related_docs: [rule/logging-rule.md, rule/exception-rule.md]
source_refs: []
---

# MySQL 标准接入

## 适用场景

适用于结构化业务数据、事务一致性要求较高的数据和可查询的持久化模型。

## 接入方式

TODO: 补充公司内部数据库申请、账号权限、迁移发布和变更审批流程。

## 配置规范

| 配置 | 要求 |
| --- | --- |
| 表名 | MUST 使用业务语义命名 |
| 主键 | MUST 明确生成方式和业务含义 |
| 索引 | MUST 根据查询场景设计 |
| 时间字段 | SHOULD 统一创建和更新时间字段 |

## MUST

- MUST 明确 source of truth，不允许多个表都声称自己是准数据。
- MUST 为高频查询设计索引，并说明索引用途。
- MUST 对状态字段记录枚举含义。
- MUST 对数据变更保持可追溯。

## SHOULD

- SHOULD 将领域模型和持久化模型的差异写入领域文档。
- SHOULD 避免把 JSON 大字段作为长期不可解释的核心数据。
- SHOULD 对重要状态变更记录操作人和操作时间。

## 示例

```text
rank_report
sign_material
review_task
```

## 反例

```text
data_table
info_record
status = 1/2/3 且无枚举说明
```

## 常见坑

- 表字段命名和领域语言不一致。
- 状态字段没有枚举解释。
- 冗余数据没有说明同步来源。
- 索引只按当前查询写，忽略后续影响。

## 监控/排查

- 慢 SQL。
- 锁等待。
- 连接池耗尽。
- 主从延迟。
- 数据变更审计。
