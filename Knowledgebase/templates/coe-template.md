# COE Template

用于 `coe/` 下的问题复盘文档。

```md
---
title: TODO: 问题复盘标题
type: coe
keywords: [TODO]
related_docs: []
source_refs: []
---

# TODO: 问题复盘标题

## 摘要

TODO: 一句话说明发生了什么、影响了什么。

## 影响范围

| 维度 | 内容 |
| --- | --- |
| 影响用户 | TODO |
| 影响功能 | TODO |
| 影响时间 | TODO |
| 业务影响 | TODO |

## 时间线

| 时间 | 事件 |
| --- | --- |
| TODO | TODO |

## 根因

TODO: 写经过确认的根因；未确认时标 TODO。

## 处置过程

1. TODO

## 改进动作

| 动作 | 负责人 | 截止时间 |
| --- | --- | --- |
| TODO | TODO | TODO |

## 知识库更新

- TODO: 需要同步更新哪些 domain、workflow、rule、ADR 或 PRD 知识版。

## 相关文档

- TODO
```

## AI 转换指引

- COE 必须包含“知识库更新”，否则复盘不能反哺长期知识。
- 从复盘中提炼出的长期规则，应同步更新对应 domain/workflow/rule。
- 不要复制完整聊天记录或报警流水。
- 不确定的根因、影响范围和动作 owner 必须写 `TODO:`。
