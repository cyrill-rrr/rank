# Workflow Template

用于 `workflows/` 下的问题/任务流程文档。

```md
---
title: TODO: 流程标题
type: workflow
keywords: [TODO]
related_docs: []
source_refs: []
---

# TODO: 流程标题

## 适用场景

TODO: 什么问题或任务使用这个流程。

## 需要输入

| 输入 | 用途 |
| --- | --- |
| TODO | TODO |

## 执行步骤

| 步骤 | 动作 | 规则 |
| --- | --- | --- |
| 1 | TODO | TODO |

## 可能结果

| 结果 | 说明 |
| --- | --- |
| TODO | TODO |

## 相关知识

- TODO: 按需链接 domain、PRD、ADR、COE、rule。

## 待补充

- TODO: 缺什么、谁确认、需要什么来源。
```

## AI 转换指引

- workflow 只写“怎么做”和“怎么判断”。
- 事实依据应链接到 `domains/`、`spec-prds/`、`adr/`、`coe/` 或 `rule/`。
- 如果一个流程跨多个领域，在步骤中标出每步对应领域。
- 相关知识按需添加，不强制为空也不强制每篇都有。
