# Domain Template

用于 `domains/` 下的业务领域文档。

```md
---
title: TODO: 中文领域名
type: domain
keywords: [TODO]
related_docs: []
source_refs: []
---

# TODO: 中文领域名

## 领域边界

### 负责

- TODO: 这个领域负责什么。

### 不负责

- TODO: 哪些内容应归到其他领域或目录。

## 领域模型

TODO: 用业务语言描述核心对象和关系。复杂关系使用 Mermaid。

| 对象 | 含义 | 关键规则 |
| --- | --- | --- |
| TODO | TODO | TODO |

## 持久化模型

TODO: 描述领域对象如何存储，source of truth 是哪里。

| 数据 | Source of truth | 关键字段 | 说明 |
| --- | --- | --- | --- |
| TODO | TODO | TODO | TODO |

## 状态机

TODO: 用 Mermaid stateDiagram 描述核心对象状态流转。如果没有独立状态机，写明“无独立状态机，状态跟随 XX”。

## 领域隐形知识

- TODO: 口头规则、历史坑、例外口径、默认假设。

## 依赖关系

| 类型 | 对象 | 说明 |
| --- | --- | --- |
| 上游 | TODO | TODO |
| 下游 | TODO | TODO |

## 相关文档

- TODO: 链接 PRD、ADR、COE、workflow 或规范。

## 待补充

- TODO: 缺什么、谁确认、需要什么来源。
```

## AI 转换指引

- 从自然语言中提取领域边界、模型、状态、持久化和隐形知识。
- 不要把一次性流程步骤写成领域知识；流程应放到 `workflows/`。
- 不要编造表名、状态枚举、接口名；缺失时写 `TODO:`。
- 更新领域关键词时同步更新根目录 `index.md`。
