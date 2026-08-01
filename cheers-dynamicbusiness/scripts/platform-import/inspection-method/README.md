# inspection-method seed

检查方法实体类型（模板 / 实例同表）的 platform-import 增量包。

**前置**：Flyway 已执行至 `V44__ent_inspection_item_method_template_id.sql`（含 V43 检查方法表 + 检查内容 `method_template_id` 列）。

## 顺序

字段库 → 类型 → 基础字段 → 规范型号 → 检查内容 `method_template_id` → 样例绑定

| 文件 | 职责 |
|------|------|
| `01_fields.sql` | 字段库：`is_template`、`action_duration_sec` |
| `02_entity_type.sql` | 类型 `inspection_method`、`DEDICATED`、`model_workbench_mode=SINGLE` |
| `03_base_fields.sql` | 类型基础字段挂载（编码=列名） |
| `04_model.sql` | 规范型号 `code=inspection_method` + 字段分配 |
| `05_patch_inspection_item_method_ref.sql` | 字段库/基础字段/型号分配 `method_template_id`；停用旧 `FLD-INS-003` |
| `06_sample_bind.sql` | 样例模板 + 绑定 `INS-ITEM-101`（验收用） |
| `07_sample_templates.sql` | 扩展标准库方法模板（目视/仪表/机泵阀门/视频门禁等）+ 首轮名称绑定 |
| `08_bind_inspection_items.sql` | 补齐缺口模板 + 检查内容↔方法模板语义绑定（仅填空，不整库兜底） |

**不在本包**：`equipment_id`（Wave 2）；角度等实例参数字段（待字段契约）。

## 执行

```bash
# 推荐：挂在 import-dev-all 末尾
cd cheers-dynamicbusiness/scripts/platform-import
./import-dev-all.sh

# 或单独
./inspection-method/import.sh
```

Windows（PowerShell）也可：

```powershell
$env:PGPASSWORD='Coolhomer'
psql -h 127.0.0.1 -U postgres -d sinopec -v ON_ERROR_STOP=1 -f inspection-method/01_fields.sql
# …依次 02→03→04→05→06
```

## 验收

1. 管理端数据类型列表出现「检查方法」`code=inspection_method`
2. 专用表（模板表 `ent_inspection_method` 与租户表 `ent_inspection_method_t1`）存在列 `is_template`、`action_duration_sec`，以及核心 `name`/`code`
3. 创建一条 `is_template=true` 的实体可作为方法模板（标准库只展示模板）
4. `inspection_item` 存在基础字段/列 `method_template_id`（REF → `inspection_method`）；旧 `FLD-INS-003` 分配已停用

```sql
SET search_path TO dynamicbusiness;

SELECT code, dedicated_table_name, model_workbench_mode, storage_type
FROM dynamic_entity_type
WHERE code = 'inspection_method' AND deleted = false;

SELECT column_name
FROM information_schema.columns
WHERE table_schema = 'dynamicbusiness'
  AND table_name = 'ent_inspection_method'
  AND column_name IN ('is_template', 'action_duration_sec', 'name', 'code')
ORDER BY 1;

SELECT field_code, data_type
FROM dynamic_entity_type_base_field
WHERE entity_type_code = 'inspection_method' AND deleted = false
ORDER BY sort_order;

SELECT code, entity_type_code
FROM dynamic_model
WHERE code = 'inspection_method' AND deleted = false;

-- Task 3
SELECT field_code, field_name, data_type, status
FROM dynamic_entity_type_base_field
WHERE entity_type_code = 'inspection_item' AND deleted = false
  AND field_code = 'method_template_id';

SELECT table_name, column_name, data_type
FROM information_schema.columns
WHERE table_schema = 'dynamicbusiness'
  AND table_name LIKE 'ent_inspection_item%'
  AND column_name = 'method_template_id'
ORDER BY 1;

SELECT i.code, i.method_template_id, t.name AS template_name
FROM ent_inspection_item_t1 i
LEFT JOIN ent_inspection_method_t1 t ON t.id = i.method_template_id AND t.deleted = false
WHERE i.code = 'INS-ITEM-101' AND i.deleted = false;
```

约定：字段编码 = 物理列名；禁止 `FLD-BASE-`；本类不挂 `equipment_id`。
