# com.rank

当前先按四层 DDD 骨架拆分：

- `rank-api`: 对外契约，放 Facade 接口与 DTO。
- `rank-application`: 用例编排，放 Command、Qry、CommandAppService、QueryAppService。
- `rank-domain`: 领域模型，放 Entity 与 Repository 接口。
- `rank-infrastructure`: 基础设施实现，放 RepositoryImpl、PO、Converter、Mapper。

五个聚合按独立 package 落位：

- `sign`: 报名
- `material`: 材料提报
- `review`: 专家评审
- `score`: 模拟打分
- `report`: 榜单报告
