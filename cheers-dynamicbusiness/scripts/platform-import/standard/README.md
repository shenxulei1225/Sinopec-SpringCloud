# 规范标准（`standard`）平台 seed

全网知识库目录：**规范标准**。一个规范型号 + 标准条目实体；分类为国标 / 行标 / 企标。

## 前置

- Flyway **V90**（`ent_standard*` 固定列：`standard_no` / `standard_level` / `issuing_body` / `publish_year` / `summary` / `source_ref`）
- 本机 PostgreSQL：`sinopec` / schema `dynamicbusiness`

## 脚本顺序

| 文件 | 作用 |
|------|------|
| `01_fields.sql` | 字段库 6 个业务字段 |
| `02_entity_type.sql` | 类型 `standard`（全网 · SINGLE · 专用表） |
| `03_base_fields.sql` | 类型基础字段挂载 |
| `04_model.sql` | 规范型号 `standard` + BASE 字段分配 |
| `05_categories.sql` | 根 + 国标/行标/企标（稳定编码） |
| `06_sample_standards.sql` | 参考资料去重样例实体 + 分类挂接 |
| `07_library_layout.sql` | SINGLE 布局：软删型号栏；Who=标准条目实体；分类→实体连线 |

## 执行

```bash
# 1) 确保 V90 已 migrate（或本机先 psql 执行该迁移文件）
# 2) seed
cd scripts/platform-import/standard
./import.sh
```

## 样例数据来源

仓库根目录 `参考资料/`：

- 北方管道视频监控招标（§4.2 HSE / 管道规范清单）
- 西三线智慧融合平台技术规格书
- 线路 / 站场数字化恢复技术规格书
- 武汉光谷管廊运维手册
- 流量计投标补充（含 DEC-NGP 规格书）

## 产品约定

- **SINGLE**：具体标准一律建实体，不要再建第二型号
- **全网**：不加所属场站系统字段
- 实体 `code` 为稳定短码（`std-…`）；**列表主展示用 `standard_no`（如 GB50251-2015）**，不要拿系统短码当主列
- 导入 `07` 后若配置器展示列仍只有 ID/名称/编码：对 `standard` 执行能力重建（`POST .../capability/internal/rebuild/entity-type?entityTypeCode=standard`），再刷新页面
