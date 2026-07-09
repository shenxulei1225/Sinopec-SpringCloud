# dynamicbusiness · Flyway 迁移

**通用规范**（immutable 已执行版本、三列对齐、DDL/seed 分离、repair 流程）：  
`.cursor/rules/flyway-migration.mdc`

修改本目录 `V*.sql` 前必读通用规范；以下为 **本模块常量** 与当前快照。

## 本模块配置

| 项 | 值 |
|----|-----|
| 迁移目录 | `db/migration/dynamicbusiness/`（本目录） |
| 历史表 | `dynamicbusiness.flyway_schema_history_dynamicbusiness` |
| 应用配置 | `application-local.yaml` → `spring.flyway.*` |
| 数据 seed | `scripts/platform-import/`（**无 DDL**） |
| 本机 repair | `scripts/flyway-repair-local.sh` |

## 当前 classpath 迁移（快照）

| 版本 | 文件 | 职责 |
|------|------|------|
| V1 | `V1__init_dynamicbusiness_schema.sql` | 全量 DDL |
| V2 | `V2__model_field_assignment_codes.sql` | 模型字段分配 code 幂等键 |
| V3 | `V3__cross_platform_association_codes.sql` | 关联表 code 幂等键 |

下一新增版本应为 **V4**。  
已停用脚本在 `db/backup/flyway-legacy-pre-seed/`，不得放回本目录。

> 上表随发版更新；改版本链时同步更新本节，并遵守通用规范中的历史对齐流程。

## 部署路径

**空库**

1. 启动 `dynamicbusiness-server` → Flyway 顺序执行 classpath 中全部 `V*.sql`
2. `scripts/platform-import/import-dev-all.sh`（仅 seed）

**已有库**（改过迁移文件名/SQL、或跑过 `regenerate-v1-from-db.py`）

1. 按通用规范对齐历史表 `version` / `script`
2. `./scripts/flyway-repair-local.sh`
3. 重启服务验证

## 查看历史

```bash
PGPASSWORD=Coolhomer psql -h 127.0.0.1 -U postgres -d sinopec -c \
  "SELECT installed_rank, version, script, checksum, success
   FROM dynamicbusiness.flyway_schema_history_dynamicbusiness
   ORDER BY installed_rank;"
```

## 与 platform-import 分工

| 层 | 位置 | 内容 |
|----|------|------|
| DDL | 本目录 Flyway | 建表 / 改表 |
| 元数据 seed | `scripts/platform-import/` | 实体类型、字段、模型、门户等 |
| 导出 | `scripts/export-platform-import.py` | 只写 seed，不写 DDL |

## 变更记录

| 日期 | 说明 |
|------|------|
| 2026-07-08 | 迁移整理为 V1–V3；通用规则迁至 `flyway-migration.mdc` |
