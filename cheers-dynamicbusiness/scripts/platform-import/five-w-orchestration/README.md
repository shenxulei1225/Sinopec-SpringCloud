# five-w-orchestration seed

五维编排 **定义层**（`dm_five_w_orchestration`）platform-import 包。开哪些栏认 `dm_data_tab_layout`，本包不写筛选槽/谁槽表。

**前置**：Flyway 已执行 `V63__orchestration_object_pick_from_on_head.sql`；`system/import.sh` 已导入对应 `dynamic_entity_type`。

## 顺序

| 文件 | 职责 |
|------|------|
| `00_helpers.sql` | 幂等 `_seed_five_w_semantic` / 配方函数（只写编排头） |
| `01_p0_orchestration.sql` | P0：`equipment`、`region`、`inspection_item`、`inspection_content` |
| `02_p1_facility.sql` | P1：`facility`（点列表这一行；区域筛选栏认布局） |
| `03_p1_zone_constructure.sql` | P1：`zone` + `Constructure` |
| `04_p1_inspection_patrol.sql` | P1：`inspection_method`、`patrol_equipment`、`task_patrol`、`task_maintenance` |
| `05_p2_ledger_backfill.sql` | P2：**批量回填**其余无编排头的 `dynamic_entity_type`（不覆盖已有行） |
| `06_inspection_item_who_entity_layout.sql` | 标准检查库：Who 实体列对齐（关误配 MODEL、开 ENTITY + columnSection=OBJECT） |
| `07_stamp_column_section_object.sql` | 迁掉 workspaceBand；无盖章 MODEL/ENTITY 补 `columnSection=OBJECT`（视角列须在视角配置重存为 FILTER） |

## 配方对照

| registry | 配方 | 当前对象从哪来 | What |
|----------|------|----------------|------|
| `equipment` | recipe-ledger-3col | LIST_ROW | VIEW_DETAIL（绑实体） |
| `region` | recipe-category-as-entity | CATEGORY_NODE | VIEW_DETAIL（绑实体） |
| `inspection_item` | recipe-foreign-model | LIST_ROW | VIEW_DETAIL（绑实体） |
| `inspection_content` | recipe-config-object-3col | LIST_ROW | SITE_PREP（05 回填时） |
| `facility` | 定制（非 ledger） | LIST_ROW | VIEW_DETAIL |
| `zone` / `Constructure` | 2col / 3col | LIST_ROW | VIEW_DETAIL |
| `inspection_method` 等 P1 | 见 `04_*.sql` | LIST_ROW | — |
| **其余目录** | `05` 按 `entry_kind` 自动：NATIVE 默认台账；CATEGORY 树节点即实体 | LIST_ROW 或 CATEGORY_NODE | VIEW_DETAIL（默认） |

`props_id` 本包不写进编排头；列上的展示配置在布局行，首进配置器时由前端 seed。

## 执行

```bash
cd cheers-dynamicbusiness/scripts/platform-import
./five-w-orchestration/import.sh
```

或挂在 `./import-dev-all.sh` 末尾（推荐）。

## 验收

```sql
SET search_path TO dynamicbusiness;

SELECT entity_type_code, what_mode, object_pick_from, what_config->>'bindLayer' AS bind_layer
FROM dm_five_w_orchestration
WHERE entity_type_code IN ('equipment','region','inspection_item','inspection_content','facility')
  AND tenant_id = 1 AND deleted = false
ORDER BY entity_type_code;
```

`region` 应为 `CATEGORY_NODE`；其余上表应为 `LIST_ROW`。

```http
GET /admin-api/dynamicbusiness/data-mgmt/five-w-orchestration/facility
tenant-id: 1
```

期望 `code=0`，非 404；响应无 `filterSlots` / `whoSlots`，有 `objectPickFrom`。
