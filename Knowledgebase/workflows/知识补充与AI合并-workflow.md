---
title: 知识补充与AI合并
type: workflow
keywords: [补知识, AI合并, knowledge分支, 自然语言, 模板转换, TODO]
related_docs: [README.md, index.md, templates/domain-template.md, templates/workflow-template.md, templates/rule-template.md, templates/adr-template.md, templates/coe-template.md, templates/prd-template.md]
source_refs: []
---

# 知识补充与 AI 合并

## 适用场景

人在 `knowledge/<topic>` 分支用自然语言补充新事实、规则、背景、复盘或需求材料，需要 AI 按知识库结构整理进正式文档。

## 需要输入

| 输入 | 要求 |
| --- | --- |
| 自然语言知识 | 可以是零散说明、规则、案例、背景或问题 |
| 来源链接 | 原始 PRD、工单、复盘、文档或聊天记录链接；不复制原文 |
| 期望用途 | 说明是为了写代码、查背景、影响面评估还是答疑 |
| 已知关联 | 如果知道关联领域、流程或规范，直接写出来 |

## 推荐输入格式

```md
# 背景
TODO: 用自然语言说明这段知识来自哪里、解决什么问题。

# 已知事实
TODO: 写已经确认的事实，不需要套模板。

# 来源
- TODO: 原始材料链接

# 我希望知识库以后能回答的问题
- TODO: 例如“为什么材料驳回后没有上榜？”
```

## 合并总流程

1. 先读 [../README.md](../README.md)，确认全库原则：正式知识只存转换后内容，原始材料只记录来源链接。
2. 再读 [../index.md](../index.md)，用关键词表定位唯一目标文档。
3. 如果 `index.md` 没有明确映射，按本文件的“按包合并规则”选择目标包和模板。
4. 优先更新已有文档；只有没有合适文档时才新建。
5. 将自然语言事实拆到正确章节。
6. 更新正式文档 frontmatter：`title`、`type`、`keywords`、`related_docs`、`source_refs`。
7. 新增关键词、别名或新文档时，同步更新 [../index.md](../index.md)，且每行只映射一个 Markdown 文件。
8. 对无法确认的信息写 `TODO:`，不要编造。
9. 输出合并说明，列出修改了哪些文档、为什么修改、还缺什么确认。

## 选择模板

| 输入内容特征 | 目标目录 | 模板 |
| --- | --- | --- |
| 领域边界、模型、状态、持久化、隐形规则 | `domains/` | [../templates/domain-template.md](../templates/domain-template.md) |
| 按步骤排查或完成一个任务 | `workflows/` | [../templates/workflow-template.md](../templates/workflow-template.md) |
| 编码规则、中间件规则、MUST/SHOULD | `rule/` | [../templates/rule-template.md](../templates/rule-template.md) |
| 为什么做某个技术/架构决策 | `adr/` | [../templates/adr-template.md](../templates/adr-template.md) |
| 问题复盘、根因、改进动作 | `coe/` | [../templates/coe-template.md](../templates/coe-template.md) |
| 原始 PRD 中可长期沉淀的知识 | `spec-prds/` | [../templates/prd-template.md](../templates/prd-template.md) |

## 按包合并规则

### domains/

放入 `domains/` 的内容：

- 业务域边界、职责和不负责范围。
- 业务对象、关系、规则和状态流转。
- 数据存储位置、source of truth、关键字段和同步关系。
- 领域隐形知识，例如口头规则、历史坑、例外口径和默认假设。

不要放入 `domains/` 的内容：

- 原始 PRD 全文。
- 临时讨论记录。
- 只属于一次事故的完整复盘。
- 可直接归入 workflow 的操作步骤。

合并要求：

- 使用 [../templates/domain-template.md](../templates/domain-template.md)。
- 优先更新已有领域包，例如 `domains/material/README.md`。
- 新知识涉及状态、数据、依赖或隐形规则时，必须放入对应章节。
- 缺少业务确认的信息写成 `TODO:`，不要推断。
- 新增领域或关键词后，同步维护 [../index.md](../index.md)。

### workflows/

放入 `workflows/` 的内容：

- 跨多个领域的问题排查流程。
- 明确输入、步骤和输出的任务流程。
- 知识补充、AI 合并这类维护流程。

不要放入 `workflows/` 的内容：

- 纯业务事实；应放到 `domains/`。
- 原始需求背景；应转换后放到 `spec-prds/`。
- 一次问题复盘；应放到 `coe/`。

合并要求：

- 使用 [../templates/workflow-template.md](../templates/workflow-template.md)。
- workflow 只写“怎么做”和“怎么判断”，事实依据链接到 domain、spec-prds、ADR、COE 或 rule。
- 如果流程依赖多个领域，应在步骤中标出每步对应领域。
- 相关知识链接按需维护，不强制每篇都有。

### rule/

放入 `rule/` 的内容：

- 命名、日志、异常处理等代码规则。
- Kafka、Redis、Haima、MySQL 等中间件或基础设施接入规范。
- DDD 概念说明。

不要放入 `rule/` 的内容：

- 具体业务领域事实。
- 一次性项目方案。
- 原始设计评审记录。

合并要求：

- 使用 [../templates/rule-template.md](../templates/rule-template.md)。
- 规则用 `MUST` 表示强制，用 `SHOULD` 表示推荐。
- 新增规范必须带正例或反例，除非只是 DDD 概念说明。
- 中间件文档需要包含接入方式、配置规范、常见坑和监控/排查。

### adr/

放入 `adr/` 的内容：

- 架构、模型、接口、存储、依赖、技术选型等长期影响决策。
- 决策时考虑过的备选方案。
- 决策带来的影响和后续动作。

不要放入 `adr/` 的内容：

- 只影响单次需求的小实现细节。
- 原始会议纪要。
- 纯产品决策。

合并要求：

- 使用 [../templates/adr-template.md](../templates/adr-template.md)。
- ADR 不维护状态字段。
- 如果自然语言输入包含“为什么这么设计”“当时为什么选 A 不选 B”，优先考虑是否需要更新或新增 ADR。
- 不要把完整 PRD 或 COE 塞进 ADR，只提炼决策相关内容。

### coe/

放入 `coe/` 的内容：

- 线上问题、重要异常、事故或业务影响问题的复盘。
- 根因、处置过程和改进动作。
- 需要同步更新的领域知识、流程或规范。

不要放入 `coe/` 的内容：

- 未经确认的聊天记录全文。
- 单纯报警流水。
- 与长期知识无关的临时记录。

合并要求：

- 使用 [../templates/coe-template.md](../templates/coe-template.md)。
- COE 文档应写明“知识库更新”。
- 如果复盘暴露领域隐形知识，应同步更新 `domains/`。
- 如果复盘暴露排查流程缺失，应同步更新 `workflows/`。

### spec-prds/

放入 `spec-prds/` 的内容：

- 需求背景和目标。
- 影响领域。
- 从 PRD 提炼出的业务规则。
- 流程变化和验收标准。
- 原始 PRD 链接。

不要放入 `spec-prds/` 的内容：

- 原始 PRD 全文。
- 大量交互稿截图。
- 一次性沟通记录。

合并要求：

- 使用 [../templates/prd-template.md](../templates/prd-template.md)。
- 只提炼长期知识，不搬运原文。
- 需求引入的新业务规则应同步更新领域文档。
- 需求改变流程时应同步更新 workflow。
- 需求解释技术取舍时可关联或新增 ADR。

## TODO 规则

| 情况 | 写法 |
| --- | --- |
| 缺负责人确认 | `TODO: 需要 XX 领域负责人确认 ...` |
| 缺来源链接 | `TODO: 补充原始 PRD/工单/复盘链接 ...` |
| 缺真实枚举 | `TODO: 补充真实状态/原因/类型枚举 ...` |
| 缺系统位置 | `TODO: 补充后台入口/API/表/任务位置 ...` |

## 人工 review

人工 review 时检查：

- 是否放进正确目录。
- 是否使用正确模板。
- 是否只写已确认事实。
- 是否更新了 `index.md`。
- TODO 是否具体、可追问、可补齐。

## 可能结果

| 结果 | 说明 |
| --- | --- |
| 合并到已有文档 | 最常见，AI 将新知识补进相关章节 |
| 新建文档 | 只有没有合适文档时使用 |
| 只标 TODO | 输入信息不足，AI 不能安全整理成事实 |
| 需要负责人确认 | 知识冲突或来源不足，需要先确认 |
