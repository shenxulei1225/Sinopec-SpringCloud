# ZHGL 遗留字段种子（zhgl-legacy）

从老项目 PostgreSQL 备份（`XProject/backup/postgresql_ZHGL`）导出的**幂等 seed**，写入新库 `sinopec.dynamicbusiness`。

## 数据来源

| 备份文件 | 说明 |
|---------|------|
| `zhgl_20260401_132252.backup` | 当前使用的最新全量备份（2026-04-01） |

老库表 → 新库表映射：

| 老库 (public) | 新库 (dynamicbusiness) |
|---------------|------------------------|
| `system_field` | `dynamic_field` |
| `system_field_group` | `dynamic_group`（`group_type = FIELD`） |
| `system_field_group_relation` | `dynamic_group_relation` |
| `system_model` + `system_model_field_assignment` | `dynamic_model_field_assignment`（仅当 `model.code`、`field.code` 在新库已存在） |

约定与 `platform-import/system/03_fields.sql` 一致：**不写 surrogate id**，按 `code` upsert。

## 生成 seed（从备份库读取）

```bash
# 1. 将备份恢复到临时库（只需做一次）
createdb -h 127.0.0.1 -U postgres zhgl_import_temp
pg_restore -h 127.0.0.1 -U postgres -d zhgl_import_temp --no-owner --no-privileges \
  /path/to/zhgl_20260401_132252.backup

# 2. 生成 SQL
cd Sinopec-SpringCloud/cheers-module-dynamicbusiness/scripts
PGPASSWORD=Coolhomer python3 export-zhgl-seed.py --source-db zhgl_import_temp --with-assignments
```

输出：

- `01_field_pool.sql` — 205 字段 + 4 分组 + 16 分组关联
- `02_model_field_assignments.sql` — 模型字段分配（按 code 解析，跳过新库不存在的模型）

## 导入新库

```bash
psql -h 127.0.0.1 -U postgres -d sinopec -f platform-import/zhgl-legacy/01_field_pool.sql
psql -h 127.0.0.1 -U postgres -d sinopec -f platform-import/zhgl-legacy/02_model_field_assignments.sql
```

可重复执行；已存在记录会 `ON CONFLICT` 更新或 `NOT EXISTS` 跳过。

## 脚本位置

- 导出：`scripts/export-zhgl-seed.py`
- SQL 生成器：`scripts/seed_codegen.py`（`compose_zhgl_field_pool` 等）

勿再使用 `scripts/import-zhgl-fields.sql`（dblink 临时导入，已由本目录 seed 替代）。
