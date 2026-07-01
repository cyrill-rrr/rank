# 司南榜材料提报技术方案

## 关键决策（SPEC 澄清）
1. 调用顺序：先调UAP再写库，UAP失败保留草稿
2. String techId→Long.parseLong()适配SignRepository
3. MaterialConfigVO只含窗口期+uapTemplate，signProjectCode硬编码
4. RepositoryImpl负责JSON解析，Converter纯字段映射
5. 草稿允许空材料，送审时Facade层拦截空值
6. 查询无材料时全部返回null
7. JUnit4+Mockito
