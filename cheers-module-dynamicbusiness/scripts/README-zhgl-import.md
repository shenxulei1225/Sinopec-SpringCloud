# 动态业务数据迁移（Flyway）

从原项目库 **`zhgl`** 导入到新库 **`sinopec.dynamicbusiness`**，**全部通过 Flyway SQL**，Windows / Mac 一致。

## 迁移文件一览

| 版本 | 文件 | 说明 |
|------|------|------|
| V4 | `V4__fix_dedicated_table_names.sql` | 修正 `dedicated_table_name`（如 `biz_spare_part`、`biz_inspection_point`） |
| V5 | `V5__create_dedicated_entity_tables.sql` | 创建 `biz_*` 专用实体表 DDL |
| V7 | `V7__import_model_metadata_from_zhgl.sql` | 导入 **模型 / 模型字段分配 / 分类**（字段库已有时跳过 `dynamic_field`） |
| V8 | `V8__normalize_business_type_codes.sql` | 将拼音旧 `business_type_code` 统一为 canonical code |
| V9 | `V9__import_field_groups_from_zhgl.sql` | 导入字段库分组：`system_field_group` → `dynamic_group(FIELD)` + `dynamic_group_relation` |
| V10 | `V10__reconcile_model_field_groups.sql` | 对齐模型字段分组：`field_groups_config` ↔ MFA ↔ 字段表 |
| V11 | `V11__remap_field_ids_by_code.sql` | **按 `code`（备选 `name`）将 zhgl fieldId 映射到本地 `dynamic_field.id`** |
| V12 | `V12__import_entity_data_from_zhgl.sql` | 通过 `dblink` 导入 **实体实例** + 字段索引 + 分类/实体关联（原 V6，置于元数据迁移之后） |
| V13 | `V13__reconcile_base_fields_with_field_library.sql` | **业务基础字段与字段库对齐**：`type_config.libraryFieldCode` / `refField` → `dynamic_field.code` |

> **说明：** 原 `V6` 实体导入已重命名为 **`V12`**，避免 Flyway 按版本号在 V7–V11 之前提前执行。Flyway **按版本号自动顺序**执行，无需手工调序。

## Flyway 执行顺序（版本号）

```
V4 → V5 → V7 → V8 → V9 → V10 → V11 → V12 → V13
```

| 阶段 | 版本 | 内容 |
|------|------|------|
| 表结构 | V4, V5 | 专用表名修正 + `biz_*` DDL |
| 模型元数据 | V7, V8 | 模型/分配/分类 + business_type_code 规范化 |
| 字段对齐 | V9, V10, V11 | 字段库分组 + 模型分组 + fieldId 重映射 |
| 实体数据 | **V12** | `biz_*` 实例数据（依赖前述元数据） |
| 基础字段 | **V13** | 业务基础字段 ↔ 字段库 `type_config` 对齐 |

若本地已有与 zhgl 等量的模型（115 条），V7 会跳过导入，**仍建议执行 V8–V13**。

## 前置条件

1. PostgreSQL 同时存在 **`sinopec`**（目标）和 **`zhgl`**（源）
2. `zhgl` 含 `system_model`、`system_field`、`biz_equipment` 等表
3. 目标库已执行 V1–V3

## 配置（Mac / Windows 通用）

`application-local.yaml`：

```yaml
spring.flyway.placeholders:
  zhglHost: 127.0.0.1
  zhglDb: zhgl
  zhglUser: postgres
  zhglPassword: Coolhomer
```

Maven `pom.xml` 中 `zhgl.db.*` 属性与上表一致。

## 执行

**启动服务（推荐）**

```bash
cd cheers-module-dynamicbusiness-server
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**仅 Flyway**

```bash
mvn flyway:migrate
```

## business_type_code 映射（V8）

| 旧 code (zhgl/历史) | 新 canonical code |
|---------------------|-------------------|
| `jian_cha_nei_rong` | `inspection_item` |
| `shou_fei` | `billing` |
| `ke_hu` | `customer` |
| `ying_ji_zi_yuan` | `emergency_resource` |
| `ying_ji_dui_wu` | `emergency_team` |
| `dian_wei` | `inspection_point` |
| `lu_xian_guan_li` | `route` |
| `spare_part` | `spare_parts` |
| `xun_jian` | `patrol` |
| `customer_management` | `customer` |

未映射的 orphan code（如 `data_collection`、`custom_6128`）保留不动，需人工处理或后续补充映射。

V8 会更新：

- `dynamic_model`、`dynamic_model_field_assignment`、`dynamic_model_category_relation`
- `dynamic_business_type_base_field` / `_config` / `_relation`
- `dynamic_entity_category_relation`、`dynamic_entity_relation`
- 所有 `biz_*` 表的 `business_type_code`（若列存在）
- `dynamic_entity`（GENERIC 存储）

## 字段与分组对应关系

动态业务里有两层「分组」概念：

| 层级 | zhgl 源表 | 新库目标 | 说明 |
|------|-----------|----------|------|
| 字段库分组 | `system_field_group` + `system_field_group_relation` | `dynamic_group` + `dynamic_group_relation`（`group_type='FIELD'`） | 全局字段归类，如「设备类」「单位」 |
| 模型内 UI 分组 | `system_model.field_groups_config` (JSON) | 同名列 | 某模型表单里的 Tab/分组，引用 `fieldId` |
| 模型字段分配 | `system_model_field_assignment` | `dynamic_model_field_assignment` | 模型 ↔ 字段库 `field_id`；`field_group_id` 为 UI 分组 id 的 **Java hashCode** |

**V10 reconciliation 做什么：**

1. 按 `field_groups_config` 中的 `group.id` 重算 `field_group_id`（与前端 Java `String.hashCode` 一致）
2. 从 JSON 中剔除不存在于 `dynamic_field` 或未分配给该模型的 `fieldId`
3. 删除 `field_id` 已失效的 MFA 行
4. 对「已分配但未出现在任何分组」的字段打印 NOTICE，便于在 UI 中重新分组

## 字段 ID 不一致（V11）

**典型场景：字段库已单独导入** — 本地 `dynamic_field` 已有数据，但 serial 与 zhgl 不同，模型/MFA/`field_groups_config` 里仍引用 zhgl 的 `field_id`。此时 **不能按 ID 对齐，必须按 `code`（优先）或 `name`（兜底）对齐**。

V7 检测到本地已有字段时会 **跳过 `dynamic_field` 导入**；**必须执行 V11** 完成 ID 重映射。

映射结果保留在 `legacy_zhgl_field_id_map`，便于排查。

**注意：** 实体专用表 `biz_*` 的 `custom_fields` JSON 一般按 **字段 code** 存值；`dynamic_entity_field_index` 使用 `field_code` 列，通常无需按 id 重映射。

## 实体实例导入（V12）

原 V6 已重命名为 **V12**，在 V7–V11 元数据与字段对齐完成后再导入 `biz_*` 等业务数据。

- 目标表已有数据且行数 ≥ zhgl 时跳过
- `zhgl` 不可用时打印 NOTICE 并跳过，**不导致 Flyway 失败**

## 业务基础字段与字段库对齐（V13）

业务基础字段（`dynamic_business_type_base_field`）已在 V2 导入，**物理列 `field_code` 保持不变**（如 `equipment_code`、`guid`）。

与字段库的对齐写在 `type_config` JSON 中：

| 键 | 含义 |
|----|------|
| `libraryFieldCode` | 对应 `dynamic_field.code`（如 `F-18af5bff...`） |
| `refField` | 关联类字段（`REF_Multi` 等）指向字段库编码，替代 legacy `REL_*` |

**V13 匹配规则（按优先级）：**

1. **字段名唯一匹配**：`base_field.field_name` = `dynamic_field.name`（本地无重名）
2. **名称别名表** `base_field_library_name_alias`：名称不一致时手工补充，例如：
   - 「关联设备管理」→ `REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id`
   - 「关联区域管理」→ `F-cc746ce0224145af88d5428d0b03213a`
3. **已有合法 refField**：若 `refField` 已是有效 `dynamic_field.code`，补全 `libraryFieldCode`

**扩展别名**（迁移前或新 migration 中）：

```sql
SET search_path TO dynamicbusiness;
INSERT INTO base_field_library_name_alias (base_field_name, library_field_code)
VALUES ('显示名称', 'F-xxxxxxxx')
ON CONFLICT (base_field_name) DO UPDATE SET library_field_code = EXCLUDED.library_field_code;
-- 然后重新执行 flyway repair + migrate，或单独跑 V13 中的 DO 块逻辑
```

验证：

```sql
SET search_path TO dynamicbusiness;

SELECT business_type_code, field_code, field_name,
       type_config::jsonb->>'libraryFieldCode' AS lib_code,
       type_config::jsonb->>'refField' AS ref_field
FROM dynamic_business_type_base_field
WHERE deleted = false AND data_type IN ('REF_Multi', 'REFERENCE')
ORDER BY business_type_code, sort_order;
```

## 幂等与跳过规则

- **V12 / V7**：目标表已有数据且行数 ≥ zhgl 时跳过；**`dynamic_field` 只要本地已有任意行即跳过**（字段库预导入场景）
- **V8 / V11 / V13**：可重复执行（仅更新仍需对齐的行）
- **zhgl 不可用**：V7/V12 打印 NOTICE 并跳过，**不导致 Flyway 失败**

### 强制全量重导

```sql
SET search_path TO dynamicbusiness;
TRUNCATE dynamic_model_field_assignment, dynamic_model_category_relation,
         dynamic_model, dynamic_category, dynamic_field RESTART IDENTITY CASCADE;
TRUNCATE biz_equipment, biz_region, ... RESTART IDENTITY CASCADE;
-- 从 flyway_schema_history_dynamicbusiness 删除 V7/V8/V12 等对应行后重新 migrate
```

### 若曾手动执行过旧版 V12（基础字段）

Flyway 历史里若已有 `12 ... reconcile_base_fields`，需修正：

```sql
UPDATE flyway_schema_history_dynamicbusiness
SET version = '13', description = 'reconcile base fields with field library'
WHERE version = '12' AND description LIKE '%reconcile_base_fields%';
```

若旧 `V6` 已写入历史但未执行，删除对应行后 `mvn flyway:migrate` 即可按新编号执行。

## 重新生成 SQL（zhgl 表结构变更时）

```bash
# V5 DDL
python scripts/generate-v5-ddl.py

# V12 实体 / V7 模型
python scripts/generate-v6-import.py   # 输出 V12__import_entity_data_from_zhgl.sql
python scripts/generate-v7-import.py
```

生成后提交 `V5/V7/V12`；中间文件不必提交。

## 导入后

重启 dynamicbusiness-server 后，建议调用能力重建：

```bash
curl -X POST "http://127.0.0.1:58096/admin-api/dynamicbusiness/capability/internal/rebuild/dynamic" \
  -H "Authorization: Bearer <token>" -H "tenant-id: 1"
```
