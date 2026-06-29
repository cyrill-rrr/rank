---
title: 命名规范
type: rule
keywords: [命名, naming, 类命名, 方法命名, 变量命名]
related_docs: [rule/ddd-rule.md]
source_refs: []
---

# 命名规范

## 适用范围

适用于代码中的包、类、方法、变量、领域对象、数据库字段和配置项命名。

## MUST

- MUST 使用能表达业务含义的命名，避免 `data`、`info`、`handle` 这类泛化词单独出现。
- MUST 保持同一业务概念在代码、文档和数据库中的命名一致。
- MUST 对领域对象使用领域语言，例如报名、材料、评审、报告域对应的英文标识应保持稳定。
- MUST 避免使用缩写，除非缩写已经是团队公认术语。

## SHOULD

- SHOULD 使用动宾结构命名有副作用的方法，例如 `rejectSignMaterial`。
- SHOULD 使用查询语义命名无副作用方法，例如 `findRankReport`、`getSignStatus`。
- SHOULD 在状态、类型、原因等枚举中体现业务含义，而不是只写数字编码。

## 示例

```text
rejectSignMaterial
findRankReportBySignId
materialStatus
```

## 反例

```text
handle
doProcess
dataInfo
status1
```

## 例外情况

- 对接外部系统时可保留外部字段名，但必须在转换层映射为内部领域语言。

## 相关文档

- [ddd-rule.md](ddd-rule.md)
