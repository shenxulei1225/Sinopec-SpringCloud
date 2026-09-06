# catalog-orchestration seed

数据目录编排头（`dm_catalog_orchestration`）platform-import 包。只写是否启用、点树还是点列表行。开哪些栏认 `dm_data_tab_layout`，本包不写槽表。

**前置**：Flyway 已执行至 **V88**（表已改名为 `dm_catalog_orchestration`）；`system/import.sh` 已导入对应 `dynamic_entity_type`。

## 顺序

| 文件 | 职责 |
|------|------|
| `00_helpers.sql` | 幂等 `_seed_catalog_orchestration`（只写编排头） |
| `01_p0_orchestration.sql` | P0：`equipment`、`region`、`inspection_item`、`inspection_content` |
| `02_p1_facility.sql` | P1：`facility`（分类即对象） |
| `03_p1_zone_constructure.sql` | P1：`zone`（分类即对象）+ `Constructure`（点列表行） |
| `04_p1_inspection_patrol.sql` | P1：`inspection_method`、`patrol_equipment`、`task_patrol`、`task_maintenance` |
| `05_p2_ledger_backfill.sql` | P2：批量回填其余无编排头的目录（不覆盖已有行） |
| `06_inspection_item_who_entity_layout.sql` | 标准检查库：关误配 MODEL、开 ENTITY |
| `07_stamp_column_section_object.sql` | 迁掉历史 workspaceBand；无盖章 MODEL/ENTITY 补栏所在区域 |

## 点树还是点列表行

| 目录注册编码 | 当前对象从哪来 |
|--------------|----------------|
| `equipment` / `inspection_item` / `inspection_content` / `Constructure` | 点列表这一行 |
| `region` / `facility` / `zone` | 点树节点 |
| **其余目录**（`05` 回填） | 分类目录点树；其它点列表行 |

`props_id` 本包不写进编排头；列上的展示配置在布局行，首进配置器时由前端 seed。

## 执行

```bash
cd cheers-dynamicbusiness/scripts/platform-import
./catalog-orchestration/import.sh
```

或挂在 `./import-dev-all.sh` 末尾（推荐）。

## 验收

```sql
SET search_path TO dynamicbusiness;

SELECT entity_type_code, enabled, object_pick_from
FROM dm_catalog_orchestration
WHERE entity_type_code IN ('equipment','region','inspection_item','inspection_content','facility','zone')
  AND tenant_id = 1 AND deleted = false
ORDER BY entity_type_code;
```

`region` / `facility` / `zone` 应为 `CATEGORY_NODE`；`equipment` / `inspection_item` 等台账为 `LIST_ROW`。

```http
GET /admin-api/dynamicbusiness/data-mgmt/catalog-orchestration/facility
tenant-id: 1
```

期望 `code=0`，非 404；响应有 `objectPickFrom`，无 What/How 槽。
