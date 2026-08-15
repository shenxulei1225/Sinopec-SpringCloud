# five-w-orchestration seed

五维编排 **定义层**（`dm_five_w_orchestration` + `dm_five_w_who_layout`）platform-import 包。与 `dm_data_tab_layout`（旧数据 Tab）**分表**，不自动复制。

**前置**：Flyway 已执行 `V51__dm_five_w_orchestration.sql`；`system/import.sh` 已导入对应 `dynamic_entity_type`。

## 顺序

| 文件 | 职责 |
|------|------|
| `00_helpers.sql` | 幂等 `_seed_five_w_who_slot` 函数 |
| `01_p0_orchestration.sql` | P0：`equipment`、`region`、`inspection_item`、`inspection_content` |
| `02_p1_facility.sql` | P1：`facility`（§38/§42：region 分类 + facility 实体，无型号栏） |
| `03_p1_zone_constructure.sql` | P1：`zone` + `Constructure` |
| `04_p1_inspection_patrol.sql` | P1：`inspection_method`、`patrol_equipment`、`task_patrol`、`task_maintenance` |
| `05_p2_ledger_backfill.sql` | P2：**批量回填**其余无 bundle 的 `dynamic_entity_type`（不覆盖已有行） |
| `06_inspection_item_who_entity_layout.sql` | 标准检查库：Who 实体列对齐（关误配 MODEL、开 ENTITY+WHO 盖章） |
| `07_stamp_workspace_band_who.sql` | 历史 MODEL/ENTITY 无盖章 → 一律补 `workspaceBand=WHO`（视角列须在视角配置重存为 FILTER） |

## 配方对照

| registry | 配方 | selection_level | What |
|----------|------|-----------------|------|
| `equipment` | recipe-ledger-3col | ENTITY | VIEW_DETAIL |
| `region` | recipe-category-as-entity | ENTITY | VIEW_DETAIL |
| `inspection_item` | recipe-foreign-model | MODEL | PICK_ENTITY |
| `inspection_content` | recipe-config-object-3col | ENTITY | SITE_PREP |
| `facility` | 定制（非 ledger） | ENTITY | VIEW_DETAIL |
| `zone` / `Constructure` | 2col / 3col | ENTITY | VIEW_DETAIL |
| `inspection_method` 等 P1 | 见 `04_*.sql` | — | — |
| **其余目录** | `05` 按 `entry_kind` 自动：NATIVE 默认三列台账；无型号类两列；CATEGORY 树节点即实体 | ENTITY（默认） | VIEW_DETAIL（默认） |

`props_id` 本包不写；首进配置器时由前端 seed。

## 执行

```bash
cd cheers-dynamicbusiness/scripts/platform-import
./five-w-orchestration/import.sh
```

或挂在 `./import-dev-all.sh` 末尾（推荐）。

## 验收

```sql
SET search_path TO dynamicbusiness;

SELECT entity_type_code, selection_level, what_mode
FROM dm_five_w_orchestration
WHERE entity_type_code IN ('equipment','region','inspection_item','inspection_content','facility')
  AND tenant_id = 1 AND deleted = false
ORDER BY entity_type_code;

SELECT entity_type_code, column_kind, slot_ref, context_outputs, entity_id_rule, category_column
FROM dm_five_w_who_layout
WHERE entity_type_code = 'facility' AND tenant_id = 1 AND deleted = false
ORDER BY column_kind, slot_ref;
```

`facility` 应两行 Who：`region-filter`（CATEGORY）+ `facility-entity`（ENTITY）。

```http
GET /admin-api/dynamicbusiness/data-mgmt/five-w-orchestration/facility
tenant-id: 1
```

期望 `code=0`，非 404。
