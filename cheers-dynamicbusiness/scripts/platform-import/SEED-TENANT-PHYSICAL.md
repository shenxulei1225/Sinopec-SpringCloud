# Seed：租户物理表写法（强制）

与 [多租户物理隔离定稿](../../../../docs/动态业务/多租户物理隔离定稿.md) 一致。

**正文约定**：除 SQL 代码示例外，术语写作 **中文名（英文名）**。

## 术语

| 中文名 | 英文名 | 含义 |
|--------|--------|------|
| 租户物理表 | tenant physical table | 带租户表后缀的实际存数表，如 `ent_region_t1` |
| 无后缀基表 | bare base table | 仅作 DDL 模板的原表名，如 `ent_region` |
| 租户表后缀 | tenant table suffix | `_t{tenantId}` |

## 标准（只保留这一种）

| 数据 | 写哪里 | 禁止 |
|------|--------|------|
| 分类树节点 | `dynamic_category`（共享 + `tenant_id`） | — |
| 实体台账 | 租户物理表（tenant physical table），如 `ent_region_t1` | 无后缀基表（bare base table） |
| 分类即实体 link | `dynamic_category_entity_link_t{tenantId}` | 无后缀 `dynamic_category_entity_link` |
| 型号–分类等运行时关联 | 对应 `*_t{tenantId}` | 无后缀关联基表 |

运行时由 `EntityTableNameHandler` 把 MyBatis 基名路由到租户物理表；**seed / 手工 SQL 不会走该拦截器**，必须直接写带后缀的表名。

无后缀基表仅作 DDL 模板（Flyway V37 起），**不得**再承载租户业务活数据。禁止「基表与物理表双写」当正式流程。

## 自检

导入后：无后缀 `dynamic_category_entity_link` / `ent_region` 等隔离清单表的 `deleted=false` 行数应为 **0**。业务核对以对应 `*_t{tenantId}` 为准。
