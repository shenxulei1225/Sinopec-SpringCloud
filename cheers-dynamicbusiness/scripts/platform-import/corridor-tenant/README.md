# 管廊租户 · platform-import

**智慧管廊**使用独立租户（默认 **tenant 2**），登录账号 **`zhgl`**，密码与 tenant 1 **`admin` 相同（本环境为 `123456`）**。

与 **tenant 1（管网组织）** 隔离。本租户 **不建运营区域（region）实例**；空间定稿见 [地理区域-设施-站内分区定稿.md](../../../../docs/动态业务/地理区域-设施-站内分区定稿.md)「管廊 tenant 2」。

## 空间模型（定稿）

| 对象 | 实体 | 说明 |
|------|------|------|
| 武汉光谷管廊 | **唯一 facility** + **根 zone**（同名） | 所有 **REF_FACILITY** 指向该 facility |
| 管廊段 / 防火区 / **舱室** | **zone** | Pattern C 分类树；舱室用 `MODEL-ZONE-UT-*CABIN*` 等 |
| 口部 / 风亭 / 交叉口等 | **structure** | **ent_structure**；`zone_id` 挂 **防火区** |
| **集水坑 / 排水坑** | **structure** | `zone_id` 挂同防火区下 **舱室**（轮询分配；zhgl 排水坑 model **156**，当前无实例） |

### 分区（zone）模型是否完整

| 模型码 | 用途 | 分类树 |
|--------|------|--------|
| `MODEL-ZONE-UT-CORRIDOR-ROOT` | 空间根 | 是 |
| `MODEL-ZONE-UT-TUNNEL-SEGMENT` | 管廊段 | 是 |
| `MODEL-ZONE-UT-FIRE-COMPARTMENT` | 防火区 | 是 |
| `MODEL-ZONE-UT-CABIN-SEGMENT` | 舱段（可选实例） | 是 |
| `MODEL-ZONE-UT-*-CABIN`（8 类） | 舱室 | 是 |
| 口部 / 风亭 / 坑 / 交叉口等 `MODEL-ZONE-UT-*` | **不得保留** | 否 → 仅 `MODEL-STRUCTURE-UT-*` |

`dynamic_corridor_spatial_entity_types.sql` 只 INSERT 上表「有效」行；`dynamic_corridor_zone_structure_split.sql` 软删历史误注册的构筑物 zone 模型及段/防火区/舱室的 structure 重复模型。

## 数据类型（创建顺序）

| 顺序 | 实体类型编码 | 说明 |
|------|-------------|------|
| 1 | `facility` | 仅 **1** 条「武汉光谷管廊」 |
| 2 | `zone` | 根 + 段 + 防火区 + 舱室 |
| — | `structure` | 口部/风亭/坑等；zhgl model **83～93** |

**空间实例**：`export_corridor_spatial_from_zhgl.py` 读 **`biz_region`**；父子来自 **`system_category`（region）**。zhgl model **73～82** → **ent_zone**；**83～93** → **ent_structure**。

## 全量导入（推荐）

```bash
bash Sinopec-SpringCloud/cheers-dynamicbusiness/scripts/platform-import/corridor-tenant/import.sh
```

顺序：`00_repair_sequences` → `system_tenant_zhgl.sql` → `dynamic_corridor_spatial_entity_types.sql` → `dynamic_corridor_structure.sql` → **`dynamic_corridor_zone_structure_split.sql`** → **`dynamic_corridor_field_library.sql`**（tenant 1 全量字段 + 分组）→ **`dynamic_corridor_model_fields.sql`**（设施/分区/构筑物模型字段分配：`FLD-BASE-*` + `FLD-UT-*`）→ **`dynamic_corridor_base_field_labels.sql`** → 导出 Python → **`dynamic_corridor_spatial_entities.generated.sql`** → 分类树缓存失效。

**字段库**：`dynamic_corridor_field_library.sql` 将 tenant 1 **全部**启用字段、21 个 FIELD 分组及分组关系复制到管廊 tenant（**FLD-UT-*** 等本租户独有 code 保留）；无用字段在界面自行删除。请用 **zhgl** 登录 tenant 2 查看。

**模型字段分配**（`dynamic_corridor_model_fields.sql`）：

| 模型 | 分配内容 |
|------|----------|
| `MODEL-FACILITY-UT-*` | 设施基础字段 `FLD-BASE-facility-*`（6） |
| `MODEL-ZONE-UT-*` | 分区基础字段 `FLD-BASE-zone-*`（11）+ 管廊展示 `FLD-UT-*` |
| `MODEL-STRUCTURE-UT-*` | 空间基础（挂 `FLD-BASE-zone-*` 同义项）+ 构筑物 `FLD-UT-*` |

依赖：本机 PostgreSQL `sinopec` + `zhgl_import_temp`；导出需 Python 与 `psycopg2`。

### 源库分类树修复（3# 误挂 2#）

若 `zhgl_import_temp.system_category` 中光谷五路北 **3#防火区(3668)** 仍挂在 **2#** 下，先执行：

```bash
psql -h 127.0.0.1 -U postgres -d zhgl_import_temp -v ON_ERROR_STOP=1 \
  -f Sinopec-SpringCloud/cheers-dynamicbusiness/scripts/platform-import/corridor-tenant/repair_zhgl_misnested_fire_category.sql
```

再重新 export + 导入实例 SQL（或跑完整 `import.sh`）。

## 登录验证

| 项 | 值 |
|----|-----|
| 租户 | 登录页下拉选 **智慧管廊** |
| 账号 | `zhgl` |
| 密码 | **`123456`** |

**数据管理权限**：`system_tenant_zhgl.sql` 为 `zhgl` 绑定租户 2 **super_admin**。改角色后执行 `evict_zhgl_role_cache.sh`（`redis-cli DEL user_role_ids:200`）。

`dynamic_corridor_tenant_zone.sql` **已废弃**（模型已并入 `dynamic_corridor_spatial_entity_types.sql`）。

## 文档

[地理区域-设施-站内分区定稿.md](../../../../docs/动态业务/地理区域-设施-站内分区定稿.md)
