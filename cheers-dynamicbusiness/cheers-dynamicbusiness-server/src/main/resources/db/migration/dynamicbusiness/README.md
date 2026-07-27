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
| V8 | `V8__ent_custom_fields_jsonb.sql` | 实体 custom_fields jsonb |
| V9 | `V9__dm_entity_dimension_dedupe.sql` | 去重 dm_entity_dimension 同 scope 有效行 |
| V10 | `V10__base_field_library_field_id.sql` | 基础字段 `library_field_id` 列、历史数据对齐、删除别名表 |
| V11 | `V11__entity_type_base_field_query_rules.sql` | 基础字段可搜索/可筛选/可排序规则列 |
| V12 | `V12__entity_type_group_name.sql` | 数据类型 `group_name` 分组列；历史 `parent_id` 子级迁移为同级分组 |
| V13 | `V13__entity_type_group_registry.sql` | 数据类型分组 seed 到通用 `dynamic_group`（`group_type=ENTITY_TYPE`） |
| V14 | `V14__entity_type_and_model_data_scope.sql` | 数据类型入口字段 + 型号业务域列（初建） |
| V15 | `V15__ent_point_and_route_indexes.sql` | 标准点位表 `ent_point`；`ent_route` 业务编码唯一索引 |
| V16 | `V16__category_user_relation.sql` | 分类-用户关联 |
| V17 | `V17__ent_scene_and_placement.sql` | 三维场景 `ent_scene`、三维摆放 `ent_scene_placement` |
| V18 | `V18__ent_scene_parent_tree_path.sql` | 补齐 `parent_id` / `tree_path`（对齐 EntityDO） |
| V19 | `V19__category_type_category_mode.sql` | 分类种类 `category_mode` |
| V20 | `V20__entity_type_scope_member.sql` | 划分数据成员表 `dynamic_entity_type_scope_member` |
| V21 | `V21__entity_domain.sql` | 入口/型号/实体业务域列统一为 `domain` 并回填存量 |
| V22 | `V22__entity_type_scope_member_ready.sql` | 版本链占位（V20 已直接落 scope_*） |
| V23 | `V23__domain_entry_and_scope_table.sql` | SCOPED→DOMAIN；划分表重命名为 `dynamic_entity_type_scope`；link 表补 `storage_entity_type_code` / `domain` |
| V24 | `V24__entity_category_relation_storage_and_domain.sql` | 分类关联 `entity_type_code` 归一为实际存储类型、补 `domain` 镜像列、去重并建唯一索引 |
| V25 | `V25__drop_generic_dynamic_entity.sql` | 废止 GENERIC：删除空壳通用表 `dynamic_entity`（实体仅 `ent_*`） |

下一新增版本应为 **V26**。  
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
| 元数据 seed | `scripts/platform-import/` | 实体类型、字段、模型、门户等；`system/import.sh` 结束时会执行 `evict_category_tree_cache.sh` 清理分类树 Redis 缓存 |
| 导出 | `scripts/export-platform-import.py` | 只写 seed，不写 DDL |

## 变更记录

| 日期 | 说明 |
|------|------|
| 2026-07-26 | V25：删除空壳 `dynamic_entity`；实体访问仅 `ent_*` + EntityRepository |
| 2026-07-26 | V24：分类关联只存实际存储类型（`task_patrol`→`task`）、新增 `domain` 镜像列、按「租户+存储类型+实体+分类」去重建唯一索引 |
| 2026-07-26 | V23：入口 SCOPED→DOMAIN；`dynamic_entity_type_scope_member`→`dynamic_entity_type_scope`；分类 link 存储类型列 |
| 2026-07-22 | V18：`ent_scene`/`ent_scene_placement` 补 `parent_id`、`tree_path`（修 query-by-scene 500） |
| 2026-07-22 | V17：`ent_scene` / `ent_scene_placement`（三维按动态业务收编阶段 1） |
| 2026-07-14 | V15：`ent_point` 标准点位表；`ent_route` code 唯一索引（巡检域 SCOPED 复用存储） |
| 2026-07-13 | V14：数据类型 `entry_kind` / `base_entity_type_code` / `data_scope`；模型 `data_scope` |
| 2026-07-13 | platform-import：`system/import.sh` 结束后清理分类树 Redis 缓存，避免 seed 后仍返回仅根节点的旧树 |
| 2026-07-13 | V13：数据类型分组复用通用 `dynamic_group`（`group_type=ENTITY_TYPE`），不再单独建表 |
| 2026-07-13 | V12：数据类型 `group_name` 分组列；历史 `parent_id` 子级迁移为同级分组 |
| 2026-07-13 | V10：基础字段 `library_field_id` 标准关联，删除 `base_field_library_name_alias` |
| 2026-07-08 | 迁移整理为 V1–V3；通用规则迁至 `flyway-migration.mdc` |
