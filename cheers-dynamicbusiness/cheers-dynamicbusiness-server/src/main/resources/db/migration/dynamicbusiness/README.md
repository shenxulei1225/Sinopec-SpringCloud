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
| 管廊租户 seed | `scripts/platform-import/corridor-tenant/`（`CORRIDOR_TENANT_ID` · zone 模型） |
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
| V26 | `V26__dm_browse_column_rename.sql` | 布局字段对齐：`column_kind` / `category_column` / `model_tab_category` |
| V27 | `V27__dm_model_tab_category_split.sql` | 模型管理左侧分类栏独立表；从布局 MODEL 行迁出并删列 |
| V28 | `V28__dm_data_tab_layout_rename.sql` | `dm_entity_dimension` → `dm_data_tab_layout` |
| V29 | `V29__category_entity_link_entity_type_code.sql` | link 表 `storage_entity_type_code` → `entity_type_code`；索引重命名（合并时自对方原 V26 重编号，避免与布局迁移撞号） |
| V30 | `V30__ent_structure.sql` | 构筑物（structure）专用表 `ent_structure`（`facility_id` 必填，`zone_id` 可选；合并时自对方原 V27 重编号） |
| V31 | `V31__facility_ref_region_column_align.sql` | 设施 `region_id` → `fld_base_facility_ref_region`（对齐字段编码 `FLD-BASE-facility-REF_REGION`） |
| V32 | `V32__category_type_entity_association_mode.sql` | 分类种类 `entity_association_mode`（SINGLE/MULTI）；`region` 默认单归属 |
| V33 | `V33__ent_equipment_id_seq.sql` | 补齐 `ent_equipment.id` 自增序列与 DEFAULT（修创建设备 id 为 null） |
| V34 | `V34__align_ent_and_capability_id_sequences.sql` | 全量校准 `ent_*` 与能力投影相关表的 id 序列（缺 DEFAULT / 序列落后） |
| V35 | `V35__align_ref_base_field_columns.sql` | 废除 REF 捷径列 `zone_id`/`region_id`/`facility_id` → `fld_base_*`（对齐字段编码） |
| V36 | `V36__ent_core_columns_align_entity_do.sql` | 全量 `ent_*` 补齐 `domain` / `tree_path` / `sort`（对齐 EntityDO；修新建类型 query-by-scene 500） |
| V37 | `V37__tenant_physical_isolation.sql` | 实体专用表 + 运行时关联表按租户拆为 `*_t{tenantId}`；基表清空作模板；元数据表名补后缀 |
| V38 | `V38__model_entity_relation.sql` | 型号—实体多对多关联基表 + 按租户拆 `dynamic_model_entity_relation_t{id}` |
| V39 | `V39__dm_model_tab_model_list_props.sql` | 模型管理 Tab 型号列表独立 `model_list_props_id`（与数据 Tab MODEL 列解耦） |
| V40 | `V40__entity_type_model_workbench_mode.sql` | 数据类型 `model_workbench_mode`（MULTI/SINGLE）；`inspection_item` 置为 SINGLE |
| V41 | `V41__drop_equipment_orphan_facility_column.sql` | 删除设备表历史孤儿设施列（若存在） |
| V42 | `V42__ent_code_unique_indexes.sql` | 实体 `code` 租户内唯一索引 |
| V43 | `V43__ent_field_work_standard.sql` | 现场作业标准（SOP）专用表 `ent_field_work_standard`（`version_no` / `publish_status` / `steps_json`）+ 租户分表 |
| V44 | `V44__inspection_sop_relations.sql` | 检查项—SOP、实体—SOP 关联表（租户物理表 `*_t{tenantId}`） |
| V46 | `V46__entity_type_work_scope.sql` | 数据类型 `work_scope`（NETWORK=全网；FACILITY=站场级，默认） |
| V47 | `V47__seed_network_work_scope_samples.sql` | 样例：`region`/`facility` 标 `NETWORK` |
| V48 | `V48__facility_owning_field_and_network_seeds.sql` | `facility_id` 显示名曾写「所属站场」；应急/标准/检查内容等样例标 `NETWORK` |
| V49 | `V49__facility_owning_display_name_changzhan.sql` | 统一 `facility_id` 展示名为「所属场站」（REF→facility 不变） |
| V50 | `V50__inspection_method_instance_refs.sql` | **占位**（原 inspection_method 实例列方案已废弃；清理由 V55 执行） |
| V51 | `V51__dm_five_w_orchestration.sql` | 五维编排 bundle 语义块 + Who 槽位表（与 data-tab-layout 分表） |
| V52 | `V52__inspection_item_pure_standard_library.sql` | 标准检查内容库改回纯库编排（实体层 + 看详情；Who 本类分类+实体） |
| V53 | `V53__inspection_item_native_cascade_layout.sql` | 标准检查内容库普通三 Tab；数据区检查分类宿主+设备分类成员级联；型号关 |
| V54 | `V54__ent_task_execution_step.sql` | 任务执行步骤 `ent_task_execution_step`（租户分表；执行记录表已存在，不在此迁移建） |
| V55 | `V55__retire_inspection_method_legacy.sql` | 删除 `inspection_method` / `method_template_id` 遗留；可选从旧模板迁移至 SOP |
| V56 | `V56__rename_field_work_standard_to_sop.sql` | SOP 真源统一为 `sop`：表 `ent_sop*`、类型/型号改码，软删重复入口 |

> **版本号说明**：本仓库已登记至 **V56**。若本地另有未入库的脚本，不得在本节写成已登记版本；补齐或占用空号须另任务提交后再更新本节。  


> **跨机合并说明**：本机布局迁移已占用 V26–V28 且已执行；对方原 `V26__category_*` / `V27__ent_structure` 在合并后改为 V29/V30。若对方库已按旧文件名执行过 V26/V27，需对齐历史表 `version`/`script` 后 `flyway:repair`，再拉本分支。
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
| 2026-08-02 | V46：数据类型 `work_scope`（NETWORK/FACILITY）；API 字段 `workScope`，空默认 FACILITY，非法值 400 |
| 2026-08-02 | V47：样例全网目录 `region`/`facility` 写入 `work_scope=NETWORK` |
| 2026-07-29 | V36：`ent_*` 补齐 `domain`/`tree_path`/`sort`；同步修正运行时建表模板（修 standard 等新建类型 query-by-scene 500） |
| 2026-07-29 | V34：全量校准 `ent_*` / `model_crud_form_definition` / `business_capability` / `capability_component_projection` id 序列 |
| 2026-07-29 | V33：`ent_equipment` 补 `ent_equipment_id_seq` 与 id DEFAULT（对齐其它 ent_*） |
| 2026-07-28 | V28：`dm_entity_dimension` → `dm_data_tab_layout`；弃用 dimension 表述 |
| 2026-07-28 | V27：`dm_model_tab_category` 独立表；布局表删除 `model_tab_category` |
| 2026-07-28 | V26：浏览布局字段重命名对齐前端 |
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
