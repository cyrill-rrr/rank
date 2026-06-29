---
title: 知识库领域索引
type: index
keywords: [索引, 领域索引, 关键词路由, 文档映射, Agent路由]
related_docs: [README.md, workflows/知识补充与AI合并-workflow.md]
source_refs: []
---

# 知识库领域索引

这个文件负责把用户问题中的关键词路由到唯一的 Markdown 文档。

维护规则：

- 大标题按领域或知识域划分。
- 每一行可以维护多个关键词或别名。
- 每一行只能映射一个 Markdown 文件。
- 如果一个问题可能涉及多个文档，拆成多行，不要在同一行映射多个文件。
- 如果找不到明确映射，先读 [README.md](README.md) 理解包结构；如果是在合并知识，继续读 [workflows/知识补充与AI合并-workflow.md](workflows/知识补充与AI合并-workflow.md)。

## 报名域

| 关键词/别名 | 映射文档 | 说明 |
| --- | --- | --- |
| 报名, sign, 商家报名, 报名记录, 报名资格 | [domains/sign/README.md](domains/sign/README.md) | 报名域入口，查报名边界、报名记录、报名资格和报名状态。 |
| 有效报名, 报名状态, 报名取消, 报名过期 | [domains/sign/README.md](domains/sign/README.md) | 用于判断商家是否存在有效报名。 |
| 提报, 退报, SUBMIT, CANCEL, 机构榜, 医生榜 | [domains/sign/README.md](domains/sign/README.md) | 查报名操作类型、报名场景、主体口径和数量限制。 |
| 报名窗口期, 可选提报项目, 项目code, 报名配置 | [domains/sign/README.md](domains/sign/README.md) | 查报名配置、窗口期校验和可选项目规则。 |
| 司南榜, 司南榜报名, 报名需求, 报名业务规则, 报名验收标准 | [spec-prds/司南榜报名需求Spec.md](spec-prds/司南榜报名需求Spec.md) | 查司南榜报名需求背景、业务规则、流程变化、验收标准和技术方案落地结论。 |

## 材料域

| 关键词/别名 | 映射文档 | 说明 |
| --- | --- | --- |
| 材料, material, 报名材料, 材料提交, 材料补交 | [domains/material/README.md](domains/material/README.md) | 材料域入口，查材料模型、材料版本、材料状态和隐形知识。 |
| 材料驳回, rejected, 驳回原因, 重新提交 | [domains/material/README.md](domains/material/README.md) | 查材料状态机、驳回状态、版本和重新提交规则。 |
| 商家报名材料驳回, 驳回报名材料, 怎么驳回材料 | [workflows/商家报名材料驳回-workflow.md](workflows/商家报名材料驳回-workflow.md) | 查完整驳回流程，包括判断、原因、状态变更、通知和审计。 |

## 评审域

| 关键词/别名 | 映射文档 | 说明 |
| --- | --- | --- |
| 评审, review, 审核, 人工评审, 评审任务 | [domains/review/README.md](domains/review/README.md) | 评审域入口，查评审任务、评审结论和评审状态。 |
| 评审结论, 评审通过, 评审未通过, 需要补充 | [domains/review/README.md](domains/review/README.md) | 查评审结论和后续链路影响。 |

## 模拟打分域

| 关键词/别名 | 映射文档 | 说明 |
| --- | --- | --- |
| 模拟打分, score, 打分, 评分, 试算, 分数 | [domains/score/README.md](domains/score/README.md) | 模拟打分域入口，查打分任务、评分版本、评分结果。 |
| 打分失败, 打分未触发, 评分版本, 候选资格 | [domains/score/README.md](domains/score/README.md) | 查评分状态、失败原因、候选判断和报告域依赖。 |

## 报告域

| 关键词/别名 | 映射文档 | 说明 |
| --- | --- | --- |
| 报告域, report, 结果报告, 榜单透出, 榜单落地页 | [domains/report/README.md](domains/report/README.md) | 报告域入口，查报告生成、结果报告、透出规则和未上榜解释。 |
| 未上榜, 为什么没上榜, 榜单没有我, 没有排名 | [workflows/为什么我没有上榜-workflow.md](workflows/为什么我没有上榜-workflow.md) | 查“为什么我没有上榜”的全链路排查流程。 |
| 报告未透出, 结果页没展示, 报告不可见 | [domains/report/README.md](domains/report/README.md) | 查报告状态、可见性和透出规则。 |
| 报告结果页, 结果页透出, PRD知识版 | [spec-prds/report-结果页透出-20260623.md](spec-prds/report-结果页透出-20260623.md) | 查报告结果页需求提炼出的业务规则、流程变化和验收标准。 |
| 报告域模型, 引入报告域, 报告域设计背景 | [adr/引入报告域模型.md](adr/引入报告域模型.md) | 查为什么引入报告域模型的技术/架构决策。 |
| 报告未透出复盘, rank report missing, 报告问题复盘 | [coe/20260623-rank-report-missing.md](coe/20260623-rank-report-missing.md) | 查报告未透出问题的 COE 复盘示例。 |

## 工程规则域

| 关键词/别名 | 映射文档 | 说明 |
| --- | --- | --- |
| 命名, naming, 变量命名, 类命名, 方法命名 | [rule/naming-rule.md](rule/naming-rule.md) | 查命名规则。 |
| 日志, logging, 打日志, 日志字段, traceId | [rule/logging-rule.md](rule/logging-rule.md) | 查日志规则、关键字段和反例。 |
| 异常, exception, 错误处理, 失败, 重试 | [rule/exception-rule.md](rule/exception-rule.md) | 查业务失败、系统异常、重试和补偿规则。 |
| DDD, 领域模型, 聚合, 实体, 值对象, 仓储 | [rule/ddd-rule.md](rule/ddd-rule.md) | 查 DDD 概念说明。 |
| Kafka, 消息, topic, 消费者, 生产者 | [rule/kafka-rule.md](rule/kafka-rule.md) | 查 Kafka 标准接入和排查。 |
| Redis, 缓存, 分布式锁, TTL, key | [rule/redis-rule.md](rule/redis-rule.md) | 查 Redis 标准接入和常见坑。 |
| Haima, haima, 中间件 | [rule/haima-rule.md](rule/haima-rule.md) | 查 Haima 标准接入占位文档。 |
| MySQL, mysql, 表设计, 索引, 事务, source of truth | [rule/mysql-rule.md](rule/mysql-rule.md) | 查 MySQL 使用规则。 |

## 知识维护域

| 关键词/别名 | 映射文档 | 说明 |
| --- | --- | --- |
| 补知识, AI合并, knowledge分支, 自然语言补充 | [workflows/知识补充与AI合并-workflow.md](workflows/知识补充与AI合并-workflow.md) | 查人如何补知识、AI 如何归类、套模板、更新索引和标 TODO。 |
| 补齐知识库, 完善知识库, 更新知识库, 改知识库 | [workflows/知识补充与AI合并-workflow.md](workflows/知识补充与AI合并-workflow.md) | 只要用户明确说补齐知识库，或 AI 判断任务是在修改知识库，必须先走知识补充与 AI 合并 workflow。 |
| 领域模板, domain-template | [templates/domain-template.md](templates/domain-template.md) | 查领域文档模板。 |
| 流程模板, workflow-template | [templates/workflow-template.md](templates/workflow-template.md) | 查 workflow 文档模板。 |
| 规则模板, rule-template | [templates/rule-template.md](templates/rule-template.md) | 查 rule 文档模板。 |
| ADR模板, adr-template | [templates/adr-template.md](templates/adr-template.md) | 查 ADR 文档模板。 |
| COE模板, coe-template | [templates/coe-template.md](templates/coe-template.md) | 查 COE 文档模板。 |
| PRD模板, prd-template | [templates/prd-template.md](templates/prd-template.md) | 查 PRD 知识版模板。 |

## 项目入口

| 关键词/别名 | 映射文档 | 说明 |
| --- | --- | --- |
| 项目介绍, 包结构, 读取顺序, README | [README.md](README.md) | 查知识库项目介绍、包结构、Agent 读取顺序和全库原则。 |
