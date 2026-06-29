# PRD Knowledge Template

用于 `spec-prds/` 下的 PRD 知识版。这里不保存原始 PRD，只保存 AI 转换后的长期知识。

```md
---
title: TODO: PRD知识版标题
type: prd
keywords: [TODO]
related_docs: []
source_refs: []
---

# TODO: PRD知识版标题

## 来源

- TODO: 原始 PRD 链接。
- TODO: 评审记录或需求来源链接。

## 需求背景

TODO: 提炼长期有效的背景，不搬运原文。

## 目标

- TODO

## 影响领域

| 领域 | 影响 |
| --- | --- |
| TODO | TODO |

## 提炼出的业务规则

- TODO: 需求沉淀出的长期业务规则。

## 流程变化

1. TODO

## 验收标准

| 场景 | 预期 |
| --- | --- |
| TODO | TODO |

## 关联文档

- TODO

## 待补充

- TODO: 缺什么、谁确认、需要什么来源。
```

## AI 转换指引

- 不复制原始 PRD 全文，只提炼长期知识。
- 新业务规则应同步更新 `domains/`。
- 新流程或流程变化应同步更新 `workflows/`。
- 技术/架构取舍应关联或新增 `adr/`。
- 缺少来源链接、验收标准或影响领域时写 `TODO:`。
