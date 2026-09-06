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
4. 目录编排头：`../catalog-orchestration/import.sh`（为 `inspection_item` 写点列表这一行）

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
| `02_patch_inspection_item_sop_how.sql` | 确保检查项编排头存在：只写启用与点列表行即对象；不再写 How 槽 |
| `03_sample_method_bindings.sql` | 若存在 V80 表，幂等写入泄漏类检查项 × MANUAL/UAV → 样例 SOP 模板；**不**强制造实例绑定（需真实设备 id，由管理端创建） |
| `04_demo_tank_sop_bindings.sql` | 储罐演示：原油储罐 6 项补 UAV 方法行；北 1# / 南 10# 储罐 × MANUAL/UAV 实例 + 绑定（任务创建可解析 ready） |
| `05_demo_tank_route_bindings.sql` | 储罐演示：设备↔停靠点（金桥厂区 44）；SOP 实例挂 LEAK 模板并写 location_ref，路线规划可展开 |
| `06_patrol_target_layout_default_identities.sql` | **布局数据整理**：`patrol_target` 页面布局型号/实体栏 tabId 收成 `default`，边上栏身份与之一致（与 `patrol_equipment` 同口径）；可重复执行 |
| `07_what_workface_props.sql` | 已退场：编排头不再写工作面指针；详情认栏 + 关系图连线 |
| `remove.sql` | 软删本配方写入的方法选用、实例绑定、停靠点绑定与历史工作面 props |
| `import.sh` | 按序执行 01→07 |

全库「边改名 / 补型号→实体边」（**绝不自动删边**）见：  
`../system/repair_dm_layout_column_identities.sql`（需时单独 `psql -f`，不默认挂进本配方以免误跑）。

### 巡检 SCOPE 页面布局 · 栏身份定稿（方案 A）

任务创建浏览目录用 **巡检目标管理**（目录注册编码 `patrol_target`）。布局建议：

- **分类区**：按运维需要配置多个分类维 Tab（分区、专业、检查项视角等）
- **实体列多 Tab**：每 Tab 一种底座类型（如 `equipment`、`pipeline`、`building`、摄像机类型等）
- **数据关系图**：配置分类→实体、型号→实体等查数边（任务嵌入内可打开关系图维护）
- **SCOPE 成员**：仅圈内、且任务页叠当前站场（`workScope=FACILITY` + `pageScopeMember`）

可执行 `06_patrol_target_layout_default_identities.sql` 整理栏身份；多实体 Tab 须在数据管理「巡检目标管理」布局中自行添加实体列并保存关系图。

作业指导不再写进编排头。任务创建页的作业面走自己的绑定，不认编排 How 槽。

## 如何移除

```bash
psql "$DATABASE_URL" -f recipes/inspection/remove.sql
```

移除后：检查目录仍可存在（来自 inspection-method / system seed）；仅本配方写入的样例方法选用被清掉。平台动作库 / SOP / 绑定 API 不受影响。

## 验收

```sql
SET search_path TO dynamicbusiness;
SELECT entity_type_code, enabled, object_pick_from
FROM dm_catalog_orchestration
WHERE entity_type_code = 'inspection_item' AND tenant_id = 1 AND deleted = false;
```

期望：启用、`LIST_ROW`。编排头不再有 What/How 列。

```http
GET /admin-api/dynamicbusiness/data-mgmt/catalog-orchestration/inspection_item
```

期望响应只有目录编码、启用、`objectPickFrom`，没有 `whatSlot` / `howSlot`。
