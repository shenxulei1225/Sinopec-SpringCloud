# 可选配方 · 检查演示（纯数据）

**正文约定**：除 SQL 代码示例外，英文/domain 术语均写作 **中文名（英文名）**。

本目录是 **业务配方（recipe）**，不是平台内核。只写入检查域演示数据与编排挂载；**不**改动作库 / SOP / 通用绑定 / SOP How 的运行时代码。

## 术语

| 中文名 | 英文名 | 含义 |
|--------|--------|------|
| 业务配方 | recipe | 可选种子：类型码、目录、五维 How 挂载、样例绑定 |
| SOP How 能力 | sopHow | 编排 How 槽能力码；工作台由平台 `features/sop/how` 渲染 |
| 方法选用 | method binding | `dynamic_sop_method_binding`：对象 + 维度 → SOP 模板 |
| 实例绑定 | instance binding | `dynamic_sop_instance_binding`：宿主 + 对象 + 维度 → SOP 实例 |

## 平台 vs 配方

| 层 | 有没有本配方 | 结果 |
|----|--------------|------|
| 平台 | 不跑本目录 | 动作库、SOP 库、通用绑定 API、SOP How 组件仍可单测与手工 props 挂载 |
| 配方 | 跑本目录 | 标准检查库目录的 How 挂上 `sopHow`；可选样例方法选用行 |

删掉本配方后：**平台能力保留**；检查演示 How 配置与样例绑定可按下方「如何移除」清掉。

## 前置（先跑已有导入，勿在此重建整套平台）

检查实体类型、检查项样例、分类与布局已在别处 seed，本配方 **不复制** 整包：

1. Flyway 至 **V80**（通用绑定表；V78+ 动作树列）
2. 平台：`../action/import.sh`、`../sop/import.sh`
3. 检查类型与目录：`../inspection-method/`（及 system seed 中的 `inspection_item`）
4. 五维编排头：`../five-w-orchestration/import.sh`（会为 `inspection_item` 写默认 How=`NONE`）

然后才跑本配方，把 **标准检查库（`inspection_item`）** 的 How 改成 `sopHow`。

旧目录 `../inspection/patch_inspection_item_sop_method.sql` 面向已 DROP 的检查专用绑定表，**请改用本配方**的 `03_sample_method_bindings.sql`。

## 如何导入

```bash
cd Sinopec-SpringCloud/cheers-dynamicbusiness/scripts/platform-import
./recipes/inspection/import.sh
```

或逐步：

```bash
psql "$DATABASE_URL" -f recipes/inspection/01_ensure_inspection_prereqs.sql
psql "$DATABASE_URL" -f recipes/inspection/02_patch_inspection_item_sop_how.sql
psql "$DATABASE_URL" -f recipes/inspection/03_sample_method_bindings.sql
```

`DATABASE_URL` 示例：`postgresql://postgres:Coolhomer@127.0.0.1:5432/sinopec`

## 本包做什么

| 文件 | 职责 |
|------|------|
| `01_ensure_inspection_prereqs.sql` | 检查 `inspection_item` / `equipment` / 编排头是否存在；缺则 NOTICE，**不**建整套类型 |
| `02_patch_inspection_item_sop_how.sql` | 更新 `dm_five_w_orchestration`（目录码 `inspection_item`）：`how_mode=FOLLOW_WHAT`，`how_config` 挂 `capability=sopHow` 与键映射 |
| `03_sample_method_bindings.sql` | 若存在 V80 表，幂等写入泄漏类检查项 × MANUAL/UAV → 样例 SOP 模板；**不**强制造实例绑定（需真实设备 id，由管理端创建） |
| `04_demo_tank_sop_bindings.sql` | 储罐演示：原油储罐 6 项补 UAV 方法行；北 1# / 南 10# 储罐 × MANUAL/UAV 实例 + 绑定（任务创建可解析 ready） |
| `05_demo_tank_route_bindings.sql` | 储罐演示：设备↔停靠点（金桥厂区 44）；SOP 实例挂 LEAK 模板并写 location_ref，路线规划可展开 |
| `06_patrol_target_layout_default_identities.sql` | **布局数据整理**：`patrol_target` 页面布局型号/实体栏 tabId 收成 `default`，边上栏身份与之一致（与 `patrol_equipment` 同口径）；可重复执行 |
| `remove.sql` | 还原 How 为 NONE；软删本配方写入的方法选用、实例绑定与停靠点绑定 |
| `import.sh` | 按序执行 01→06 |

全库「边改名 / 补型号→实体边」（**绝不自动删边**）见：  
`../system/repair_dm_layout_column_identities.sql`（需时单独 `psql -f`，不默认挂进本配方以免误跑）。

### 巡检 SCOPE 页面布局 · 栏身份定稿（方案 A）

任务创建浏览目录用 **巡检目标管理**（目录注册编码 `patrol_target`），与 **巡检设备管理**（`patrol_equipment`）一样：

- 型号栏、实体栏的标签页编号（tabId）用 **`default`**（空也会被读成 `MODEL:default` / `ENTITY:default`）
- 栏间关系边只写 **`MODEL:default` / `ENTITY:default`**，禁止一边具名 tab、一边还写 default

**正确规则只有这一条**：边上的「从哪栏 / 到哪栏」必须等于当前布局行算出来的栏身份。不要再搞「有的目录全 default、有的目录用 equipment-tab-1」两套修法。

How 键映射（写在配方 SQL 里，合法）：

- `subjectType` = `inspection_item`
- `hostType` = `equipment`
- `dimensionKey` = `execution_means`
- 维度选项：MANUAL 人工 / UAV 无人机 / ROBOT 机器人 / FIXED_CAMERA 固定摄像机

## 如何移除

```bash
psql "$DATABASE_URL" -f recipes/inspection/remove.sql
```

移除后：检查目录仍可存在（来自 inspection-method / system seed）；仅本配方改过的 How 与样例方法选用被清掉。平台动作库 / SOP / 绑定 API / How 组件不受影响。

## 验收

```sql
SET search_path TO dynamicbusiness;
SELECT entity_type_code, how_mode,
       how_config->>'capability' AS capability,
       how_config->'sopHow'->>'subjectType' AS subject_type
FROM dm_five_w_orchestration
WHERE entity_type_code = 'inspection_item' AND tenant_id = 1 AND deleted = false;
```

期望：`FOLLOW_WHAT` / `sopHow` / `inspection_item`。

```http
GET /admin-api/dynamicbusiness/data-mgmt/five-w-orchestration/inspection_item
```

期望响应 `howSlot.capability=sopHow` 且含 `sopHow` 键映射（依赖服务端已透传 `how_config`）。
