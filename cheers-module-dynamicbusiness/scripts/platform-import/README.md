# 动态业务 · 平台导入包

从 PostgreSQL **当前库**导出。Flyway **仅保留 V1 建表**；全部数据通过本目录 seed 导入。

## 目录结构

```
platform-import/
  system/
    01_schema.sql              与 Flyway V1 同源（全量 DDL）
    seed/                      按表拆分，便于单独更新
      dynamic_entity_type.sql
      dynamic_entity_type_config.sql
      dynamic_entity_type_relation.sql
      dynamic_entity_type_base_field.sql
      dynamic_field.sql
      dynamic_group.sql
      dynamic_group_relation.sql
      dynamic_model.sql
      dynamic_model_relation.sql
      dynamic_model_field_assignment.sql
      dynamic_category*.sql
      dynamic_business*.sql
      business_capability.sql
    import.sh
  smart-corridor/seed/         管廊增量（模型/分类/页面）
  smart-station/seed/          站场增量（若库中存在站场模型）
  import-dev-all.sh
```

## 新电脑部署

```bash
# 方式 A：Flyway V1 + seed
# 启动应用 → Flyway 执行 V1__init_dynamicbusiness_schema.sql
cd scripts/platform-import
./import-dev-all.sh

# 方式 B：纯 SQL（含建表）
cd scripts/platform-import
./system/import.sh
./smart-corridor/import.sh
```

## 从本机重新导出

```bash
cd scripts
PGPASSWORD=Coolhomer python3 export-platform-import.py
```

会依次：

1. `regenerate-v1-from-db.py` — 从库导出 DDL 写入 V1，旧 V2+ 归档到 `db/backup/flyway-legacy-pre-seed/`
2. 按表生成 `system/seed/*.sql` 与产品包 seed

## 幂等约定

- 不写 surrogate `id`；关联在 INSERT 时用 `code` / `field_code` / `page_code` 子查询解析
- `dynamic_group` 有唯一索引 `(tenant_id, group_type, code)`

## 备份与回滚

迁机前请：

1. `git commit` 当前 seed 快照
2. `pg_dump -n dynamicbusiness` 备份库（见仓库 `backups/db/`）

恢复：`pg_restore` 还原 dump 即可。

## 已知缺口

- `dynamic_model_field_assignment` 若库中为 0，不会生成有效绑定 seed
- 业务实例（`ent_*`）仅在 `*/seed/entities_optional.sql`，默认不导入
