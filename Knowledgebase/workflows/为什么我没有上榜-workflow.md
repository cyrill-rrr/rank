---
title: 为什么我没有上榜
type: workflow
keywords: [未上榜, 为什么没上榜, 榜单没有我, 排查榜单, 结果报告]
related_docs: [domains/sign/README.md, domains/material/README.md, domains/review/README.md, domains/score/README.md, domains/report/README.md]
source_refs: []
---

# 为什么我没有上榜

## 适用场景

用户、运营或客服询问“为什么我没有上榜”“为什么榜单没有我”“为什么报告没有透出”时，使用这个流程排查。

这个流程跨 5 个领域：报名、材料、评审、模拟打分、报告域。

## 需要输入

| 输入 | 用途 |
| --- | --- |
| 商家标识 | 定位报名主体 |
| 活动/榜单/批次 | 定位要排查的榜单范围 |
| 用户看到的问题描述 | 区分未报名、未通过、未打分、不透出等情况 |
| 时间范围 | 排查状态流转、任务执行和报告生成 |

## 执行步骤

| 步骤 | 检查点 | 查看知识 | 可能结论 |
| --- | --- | --- | --- |
| 1 | 是否存在有效报名 | [../domains/sign/README.md](../domains/sign/README.md) | 没有报名、报名取消、报名过期 |
| 2 | 报名材料是否完整且可用 | [../domains/material/README.md](../domains/material/README.md) | 材料未提交、材料被驳回、材料待补交 |
| 3 | 是否进入并完成评审 | [../domains/review/README.md](../domains/review/README.md) | 未进入评审、评审中、评审未通过 |
| 4 | 是否完成模拟打分 | [../domains/score/README.md](../domains/score/README.md) | 打分未触发、打分失败、分数不满足候选条件 |
| 5 | 报告是否生成并透出 | [../domains/report/README.md](../domains/report/README.md) | 报告未生成、生成失败、不满足透出规则 |

## 可能结果

| 结果 | 说明 | 下一步 |
| --- | --- | --- |
| 无有效报名 | 上游报名链路未完成 | 回到报名域确认报名记录和状态 |
| 材料不可用 | 材料缺失、被驳回或未重新提交 | 走 [商家报名材料驳回-workflow.md](商家报名材料驳回-workflow.md) 或材料域排查 |
| 评审未通过 | 评审结论导致无法进入后续链路 | 查看评审结论和原因 |
| 打分未产出 | 模拟打分未触发、失败或不满足规则 | 查看打分任务、版本和失败原因 |
| 报告未透出 | 上游已有结果，但报告域未展示 | 查看报告状态、可见性和透出规则 |

## 相关知识

- [../domains/sign/README.md](../domains/sign/README.md)
- [../domains/material/README.md](../domains/material/README.md)
- [../domains/review/README.md](../domains/review/README.md)
- [../domains/score/README.md](../domains/score/README.md)
- [../domains/report/README.md](../domains/report/README.md)

## 待补充

- 各领域真实查询入口。
- 每一步对应的后台页面、接口或 SQL。
- 面向客服/运营的解释话术。
