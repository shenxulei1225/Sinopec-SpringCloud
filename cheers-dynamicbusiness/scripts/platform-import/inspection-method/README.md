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
| `09_apply_equipment_inspection_packages.py` | **按分类—实体关联同步型号适用集合**（不再创建 `INSP-PKG-*`，不用型号名猜类） |
| `16_fix_video_monitoring_leaf_items.py` | **视频监控叶子整理**：父类清挂；按叶子显式映射创建「内容（分类名）」检查项；编码稳定数字码；同义不合并 |
| `17_dedupe_and_suffix_inspection_items.py` | **全库检查项**：同名克隆软删；单叶子/单前沿改「内容（分类名）」；多叶子糊挂按叶拆分；清无设备挂接短名 |
| `18_fix_security_specialty_leaf_items.py` | **安防专业叶子**：入侵报警/出入口控制/可视对讲/人员定位/大屏与显控；清 L3 糊挂；双挂安防系统检查；同步型号适用 |
| `19_fix_remaining_security_leaf_items.py` | **安防剩余专业叶子**：周界/广播/巡更/综合管理/传输机柜/停车场/一卡通/安检/无人机；同 18 口径 |
| `20_fix_instrumentation_leaf_items.py` | **仪器仪表与自动化叶子**：清 L3 糊挂；按叶子挂「内容（叶子名）」；双挂「仪表与自动化检查」；软删 L3 无后缀短名 |
| `21_fill_remaining_equipment_leaf_items.py` | **其余设备专业空叶子补全**：供配电/电气/动静设备/消防/通信/机房/暖通/给排水/管线监测/自动化/计算等；按 L3 角色模板挂叶子 |
| `22_sync_inspection_category_links_from_cascade.py` | **按级联补检查分类挂接**：已有「检查分类→设备分类」级联边时，把成员设备分类（含子孙）上的检查项挂到宿主检查分类；不臆造级联 |
| `10_domain_equipment_inspection_item.sql` | **已废**：曾建 DOMAIN「设备检查内容」并填 `domain=equipment`（由 12 收口） |
| `11_flip_inspection_catalog_ia.sql` | **已废**：曾把 NATIVE/DOMAIN 分挂知识库与设备·管线（由 12 收口） |
| `12_retire_inspection_item_domains.sql` | **定稿**：软删检查内容 DOMAIN 入口；清空检查内容实体 `domain`；保留知识库 NATIVE |
| `13_fix_inspection_method_catalog.sql` | **定稿**：`inspection_method` 名称「检查方法」、分组「知识库」、`work_scope=NETWORK`（纠正误标「检查内容」/站场管理） |
| `14_seed_inspection_method_categories.py` | **定稿**：检查方法分类树（手段分组）+ `dynamic_entity_category_relation` 关联模板 |
| `15_instance_fields.sql` | **Wave 2**：字段库/基础字段/型号分配 `equipment_id`、`inspection_item_id`（前置 Flyway V50） |

**目录定稿**

| 侧栏 | 入口名 | 类型 |
|------|--------|------|
| 知识库 | 标准检查内容库 | NATIVE `inspection_item`（直接标准库；唯一管理入口） |
| 知识库 | 检查方法 | NATIVE `inspection_method`（全网方法模板库；内容经 `method_template_id` 引用） |

站场管理**不**挂检查方法入口；站场侧「设备检查内容」目录本期不做（适用检查项仍在巡检任务新建等业务页）。

**检查方法分类（14）**：根下按手段分 9 类——通用外观与记录、仪表读数、机泵阀门与附件、视频安防、通信与网络、门禁与通道、消防报警、电气与电源、软件与系统；38 条方法模板均已挂到对应分类。

**不在本包**：角度等实例参数字段（待字段契约）；标准库左树借设备产品分类（另步）。`equipment_id` / `inspection_item_id` 见 `15_instance_fields.sql`（Wave 2）。

### 检查项编码与分类挂接（定稿 2026-08-11）

| 规则 | 说明 |
|------|------|
| 编码 | 稳定数字码 `INSP-#######`；不当说明书，改名称不改码 |
| 名称 | 表达检查内容；按分类区分时用 `内容（叶子分类名）` |
| 分类 | 只认分类—实体关联；**禁止**用编码前缀 /「规范包」推断所属分类 |
| 同义项 | **不合并**（便于后续配不同检查方法） |
| 粒度 | 有叶子挂叶子；无叶子可留 L3；父类糊清单不盲拷子类 |

```bash
# 视频监控叶子挂接（幂等）
python inspection-method/16_fix_video_monitoring_leaf_items.py --dry-run
python inspection-method/16_fix_video_monitoring_leaf_items.py

# 全库同名去重 + 按叶子/前沿补后缀 + 多叶拆分（幂等）
python inspection-method/17_dedupe_and_suffix_inspection_items.py --dry-run
python inspection-method/17_dedupe_and_suffix_inspection_items.py

# 安防专业叶子（入侵报警/门禁/对讲/定位/大屏，幂等）
python inspection-method/18_fix_security_specialty_leaf_items.py --dry-run
python inspection-method/18_fix_security_specialty_leaf_items.py

# 安防剩余专业叶子（周界/广播/巡更/综合管理/传输机柜/停车场/一卡通/安检/无人机，幂等）
python inspection-method/19_fix_remaining_security_leaf_items.py --dry-run
python inspection-method/19_fix_remaining_security_leaf_items.py

# 仪器仪表与自动化叶子（幂等）
python inspection-method/20_fix_instrumentation_leaf_items.py --dry-run
python inspection-method/20_fix_instrumentation_leaf_items.py

# 其余设备专业空叶子补全（幂等）
python inspection-method/21_fill_remaining_equipment_leaf_items.py --dry-run
python inspection-method/21_fill_remaining_equipment_leaf_items.py

# 按检查分类↔设备分类级联，补检查项→检查分类挂接（幂等；不臆造级联）
python inspection-method/22_sync_inspection_category_links_from_cascade.py --dry-run
python inspection-method/22_sync_inspection_category_links_from_cascade.py

# 再按分类—实体同步型号适用
python inspection-method/09_apply_equipment_inspection_packages.py --dry-run
python inspection-method/09_apply_equipment_inspection_packages.py
```

历史 `INSP-PKG-*` / `INS-ITEM-*` / `INSP-SEED-*` 已由 16 改为数字码；09 不再重建包编码。

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
