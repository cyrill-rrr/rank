---
title: 企业知识库新人手册
type: guide
keywords: [知识库, 新人手册, 使用说明, 补充知识, AI合并]
related_docs: [index.md, workflows/知识补充与AI合并-workflow.md]
source_refs: []
---

# 企业知识库 

这个知识库用于沉淀企业内部长期有效的工程、业务和产品知识。它不是原始资料仓库，不直接保存完整 PRD、聊天记录、事故记录或临时草稿；正式进入知识库的内容都应该是按模板转换后的知识文档。

## 项目介绍

这是一个面向人和 AI Agent 的 Markdown 知识库项目。它的目标不是“把所有资料都放进来”，而是把原始资料转换成可维护、可路由、可引用的正式知识。

项目里的文档主要服务四类工作：

- 写代码前理解业务边界、领域模型、状态机、持久化模型和规则。
- 查业务背景、历史决策、PRD 知识版和问题复盘。
- 做影响面评估，沿领域依赖、流程、规则、PRD、ADR、COE 找证据。
- 让 AI Agent 根据 `knowledge/<topic>` 分支里的自然语言输入，把新知识合并到正确位置。

## Agent 读取顺序

Agent 不应该只靠全文搜索猜答案，默认按下面顺序读：

1. 先读 [index.md](index.md)，用关键词映射定位唯一目标文档。
2. 如果 `index.md` 找不到明确入口，再读本 README，理解项目目标和包结构。
3. 如果是合并 `knowledge/<topic>` 分支，直接读 [workflows/知识补充与AI合并-workflow.md](workflows/知识补充与AI合并-workflow.md)；合并规则统一维护在那里。
4. 如果仍然不能定位答案，先问对应负责人/确认人，不要编造。

## 知识库能做什么

| 使用 case | 入口 | 预期结果 |
| --- | --- | --- |
| 写代码前查上下文 | [index.md](index.md) 的“写代码”场景 | 找到相关领域、状态机、持久化模型、规则和历史决策 |
| 查业务背景 | [index.md](index.md) 的“查背景”场景 | 找到 PRD 知识版、领域隐形知识、ADR 和 COE |
| 做影响面评估 | [index.md](index.md) 的“影响面评估”场景 | 输出可能受影响的领域、流程、规则、PRD、ADR、COE |
| 回答业务问题 | [index.md](index.md) 的“答疑”场景 | 路由到 workflow 或 domain 文档，并给出可追溯依据 |
| 补充新知识 | [workflows/知识补充与AI合并-workflow.md](workflows/知识补充与AI合并-workflow.md) | 人用自然语言补充，AI 按模板合并到正式知识库 |

## 包结构

```text
.
├── README.md                         # 项目介绍、包结构、维护规则
├── index.md                          # 知识库导航入口，不是普通文档清单
├── domains/                          # 业务领域知识，每个领域一个包
│   ├── sign/                         # 报名域
│   ├── material/                     # 材料域
│   ├── review/                       # 评审域
│   ├── score/                        # 模拟打分域
│   └── report/                       # 报告域
├── workflows/                        # 按问题或任务组织的流程
├── rule/                             # 编码规则、中间件规则、工程概念
├── adr/                              # 技术/架构决策记录
├── coe/                              # 问题复盘和改进行动
├── spec-prds/                        # AI 转换后的 PRD 知识版
└── templates/                        # 文档模板
```

## 包职责

| 包 | 职责 | 典型问题 | 入口 |
| --- | --- | --- | --- |
| `domains/` | 业务领域知识、领域模型、持久化模型、状态机、隐形知识 | “报告域有哪些状态？”“报名材料怎么存？” | 看具体领域包，例如 [domains/report/](domains/report/README.md) |
| `workflows/` | 按问题或任务组织的执行流程 | “为什么我没有上榜？”“怎么驳回报名材料？” | 看具体 workflow，例如 [为什么我没有上榜](workflows/为什么我没有上榜-workflow.md) |
| `rule/` | 编码规则、中间件接入规则、工程概念说明 | “日志应该怎么打？”“Redis 接入有什么要求？” | 看具体 rule，例如 [logging-rule.md](rule/logging-rule.md) |
| `adr/` | 技术/架构决策记录 | “为什么引入报告域模型？” | 看具体 ADR，例如 [引入报告域模型.md](adr/引入报告域模型.md) |
| `coe/` | 问题复盘、根因、改进动作、知识库反哺 | “上次报告未透出是什么原因？” | 看具体 COE，例如 [20260623-rank-report-missing.md](coe/20260623-rank-report-missing.md) |
| `spec-prds/` | 从原始 PRD 转换后的长期知识 | “这个需求沉淀了哪些业务规则？” | 看具体 PRD 知识版，例如 [report-结果页透出-20260623.md](spec-prds/report-结果页透出-20260623.md) |
| `templates/` | 6 类核心文档模板和 AI 转换指引 | “新增知识应该套哪个模板？” | 看具体模板，例如 [domain-template.md](templates/domain-template.md) |

## domains 包结构

`domains/` 是当前业务知识的核心包。每个领域都是一个文件夹，领域入口固定为 `README.md`。

| 领域包 | 中文名 | 主要内容 |
| --- | --- | --- |
| [domains/sign/](domains/sign/README.md) | 报名 | 报名边界、报名记录、报名资格、报名状态 |
| [domains/material/](domains/material/README.md) | 材料 | 报名材料、材料版本、材料驳回、重新提交 |
| [domains/review/](domains/review/README.md) | 评审 | 评审任务、评审结论、评审状态 |
| [domains/score/](domains/score/README.md) | 模拟打分 | 打分任务、评分版本、评分结果 |
| [domains/report/](domains/report/README.md) | 报告域 | 结果报告、榜单透出、未上榜解释 |

## 怎么往知识库补充知识

1. 从最新主分支创建 `knowledge/<topic>` 分支，例如 `knowledge/reject-material`。
2. 用自然语言写清楚事实、来源、问题背景和你认为应该补充的位置。
3. 如果有原始材料，只记录链接，不复制全文到知识库。
4. 让 AI 按 [workflows/知识补充与AI合并-workflow.md](workflows/知识补充与AI合并-workflow.md) 执行合并。
5. 人工 review AI 的归类、模板字段、`index.md` 更新和 TODO 标注。

## AI 如何合并知识

AI 合并规则统一维护在 [workflows/知识补充与AI合并-workflow.md](workflows/知识补充与AI合并-workflow.md)。根 README 只保留全库级原则：

- 优先更新已有文档，找不到合适文档时才创建新文档1。
- 只沉淀已给出的事实和可追溯来源，不凭空补业务事实。
- 缺失信息必须写成 `TODO:`，并说明需要谁确认或需要什么来源。
- 合并正式知识后同步更新 [index.md](index.md) 的关键词映射；每一行只能映射一个 Markdown 文件。
- 所有正式知识文档都使用轻量 frontmatter：`title`、`type`、`keywords`、`related_docs`、`source_refs`。

## 全库原则

- 主知识库存转换后的知识，不存原始材料。
- 原始来源通过 `source_refs` 追溯。
- 文档优先服务“人能读懂”和“AI 能路由、合并、引用”。
- 包级 README 不再维护；`index.md` 负责路由，合并规则统一放在 workflow。
