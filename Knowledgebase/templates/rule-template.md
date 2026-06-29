# Standard Template

用于 `rule/` 下的规范文档。

```md
---
title: TODO: 规范标题
type: rule
keywords: [TODO]
related_docs: []
source_refs: []
---

# TODO: 规范标题

## 适用范围

TODO: 这条规范适用于哪些代码、系统、中间件或流程。

## 接入方式

TODO: 如果是中间件或基础设施，写 SDK、权限、配置、发布流程；如果不是，可删除本节。

## 配置规范

| 配置 | 要求 |
| --- | --- |
| TODO | TODO |

## MUST

- TODO: 强制规则。

## SHOULD

- TODO: 推荐规则。

## 示例

TODO: 正例。

## 反例

TODO: 反例。

## 例外情况

TODO: 什么时候可以例外，以及例外需要谁确认。

## 常见坑

TODO: 中间件/基础设施文档需要保留；普通规范可按需删除。

## 监控/排查

TODO: 中间件/基础设施文档需要保留；普通规范可按需删除。

## 相关文档

- TODO
```

## AI 转换指引

- 强制规则写入 `MUST`，推荐规则写入 `SHOULD`。
- 如果输入只是在解释概念，例如 DDD，可以减少 MUST/SHOULD，改写为概念说明。
- 中间件文档必须关注接入方式、配置、常见坑、监控/排查。
- 缺少公司内部真实接入方式时写 `TODO:`。
