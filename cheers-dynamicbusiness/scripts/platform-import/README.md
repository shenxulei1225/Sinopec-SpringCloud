# 动态业务 · 平台导入包

**空库唯一路径**：Flyway 建表 → 本目录 **仅 seed**（不在 import 重复 DDL）。  
Flyway 通用规范：`.cursor/rules/flyway-migration.mdc`；本模块版本快照见 `db/migration/dynamicbusiness/README.md`。

## 目录结构

```
platform-import/
  system/seed/           系统共用元数据（实体类型、字段、模型、门户等）
  system/import.sh       仅 seed，不含建表
  smart-corridor/seed/   管廊产品增量
  smart-station/         站场产品包（04–07，由 generate-smart-station-import.py 维护）
  inspection-method/     检查方法类型与字段（01–04；前置 Flyway V43）
  import-dev-all.sh      system + corridor + station + inspection-method
```

## 新空库部署（推荐）

```bash
# 1. 创建库 schema（任选其一）
#    A. 启动 dynamicbusiness 服务 → Flyway 自动执行 V1→V2→V3
#    B. 手动 flyway migrate（同 classpath 下三份 SQL）

# 2. 导入 seed（不要先跑 01_schema 之类 DDL，已删除）
cd cheers-dynamicbusiness/scripts/platform-import
./import-dev-all.sh
```

**不要**再使用「import 里建表 + Flyway 再建表」双路径；旧版 `01_schema.sql` / `02_*` / `03_*` 已移除。

## Flyway 版本（dynamicbusiness 当前快照）

见 `cheers-dynamicbusiness-server/src/main/resources/db/migration/dynamicbusiness/README.md`。

归档脚本在 `db/backup/flyway-legacy-pre-seed/`，不参与启动。

**修改迁移**：遵守 `.cursor/rules/flyway-migration.mdc`；本机对齐 `./flyway-repair-local.sh`。

## 从本机导出

```bash
cd scripts

# 全量（推荐发版前）
PGPASSWORD=Coolhomer python3 export-platform-import.py --schema --rewrite-import-scripts

# 只导出字段库 + 分组
PGPASSWORD=Coolhomer python3 export-platform-import.py --package system --artifacts fields

# 只导出模型 + 字段分配 + 关联
PGPASSWORD=Coolhomer python3 export-platform-import.py --package system --artifacts models,assignments

# 只刷新 Flyway V1 DDL
python3 regenerate-v1-from-db.py
```

导出单元（`--artifacts`）：

| 单元 | 内容 |
|------|------|
| `entity-types` | 实体类型、配置、类型间关系、基础字段 |
| `fields` | 字段库、字段分组、分组-字段关联 |
| `models` | 模型、模型关系、关联声明 |
| `assignments` | 模型字段分配 |
| `categories` | 分类类型、分类树、模型-分类、页面配置 |
| `portal` | 业务门户、入口、能力投影 |
| `entities` | 可选业务实例 SQL（默认不导入） |

## 与 platform 模块 Flyway 的关系

**platform-resource** 等模块有独立 Flyway（约 V1–V16），由各自服务启动执行，  
与 dynamicbusiness 的 `platform-import` **无重复 DDL**。  
清理范围仅限 dynamicbusiness + 本 scripts 目录。

## 幂等约定

- seed 不写 surrogate `id`；关联用 `code` / `field_code` / `page_code` 子查询解析
- `dynamic_group` 唯一索引 `(tenant_id, group_type, code)`

## 备份

迁机前：`git commit` seed 快照 + `pg_dump -n dynamicbusiness`。
