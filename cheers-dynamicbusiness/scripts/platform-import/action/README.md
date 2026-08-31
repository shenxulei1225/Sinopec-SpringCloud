# 动作库（`action`）seed

**定稿**：知识库入口名 **动作库**，类型编码 **`action`**，专用表 `ent_action*`。  
启用关系走 Flyway **V77** `dynamic_action_enablement`（本目录不 seed 业务启用行）。

## 前置

1. Flyway 已执行至 **V76**（`ent_action` / `ent_action_t*`）与 **V77**（启用表）
2. 建议与 SOP 种子配合：Flyway **V78–V79** 后 SOP 只认 `action_tree_json`，样例动作 code 与 SOP 模板里的 `actionId`（`act-*`）对齐

## 新库 / 已有库

```bash
./action/import.sh
```

幂等；可重复执行。

## 文件

| 文件 | 职责 |
|------|------|
| `01_fields.sql` | 字段库：`param_slots_json` / `child_action_ids_json` / `execution_means` / `is_composite` |
| `02_entity_type.sql` | 类型 `action`（动作库），知识库 · 全网 · SINGLE |
| `03_base_fields.sql` | 基础字段挂载 |
| `04_model.sql` | 规范型号 `action`（目录）+ 字段分配；分类按执行手段 |
| `09_action_param_models.sql` | **常用执行参数字段库** + **参数型号**及 LIBRARY 分配（须在 05 前） |
| `05_sample_actions.sql` | 样例 UAV/ROBOT 动作；创建时绑定参数型号 |
| `06_ensure_data_layout.sql` | 从通用台账模版挂载 `data_layout_id`（关系图 / 数据页必需） |
| `07_ensure_five_w_orchestration.sql` | 默认五维编排（与建类型 `LEDGER_3COL` 同口径） |
| `08_ensure_layout_props.sql` | 分类/型号/实体栏 `propsId`（与创建后 `writeDefaultDataTabLayouts` 同口径） |
| `11_ensure_model_tab_list_props.sql` | 模型管理 Tab：`模型管理列表-action` + `dm_model_tab_category.model_list_props_id`（与 `ensureModelTabListPropsId` 同口径） |
| `10_robot_atomic_actions.sql` | 地面机器人原子动作 + 全量参数型号同步 |

## 常用参数字段 → 参数型号

| 参数型号 | 用途 | 字段 |
|----------|------|------|
| `action_param_none` | 复合块 / 无参 | （无） |
| `action_param_arrive` | 到指定位置 | `location_ref` → 路网点位 |
| `action_param_dwell` | 悬停/停留观察 | `dwell_duration`（秒） |
| `action_param_aim` | 调整摄像机 | `yaw` / `pitch` / `roll` / `focal_length` |
| `action_param_shoot` | 拍摄取证 | `shot_count` |
| `action_param_ground_patrol` | 沿规划路线行进 | `route_ref` → 路线 |
| `action_param_gas_detect` | 气体检测 | `gas_threshold` |

样例动作实体的 `model_id` 指向上表参数型号；`param_slots_json` 留空。How / SOP 实例侧通过 `model-crud-form` 读 LIBRARY 字段填值。

**产品入口**：知识库 → **动作库**。具体动作一律建为**实体实例**，不要再建第二型号。

SQL 建类型不会走界面「创建类型」时的 bootstrap，须补全与建类型相同的四步：

1. **`06`**：挂 `data_layout_id` + 默认筛选边；否则关系图报「尚未挂载 dataLayoutId」
2. **`07`**：写 `dm_five_w_orchestration`；否则数据页 What 报「五维编排 bundle 未配置：action」
3. **`08`**：为各栏写入/绑定组件配置 `propsId`；否则栏上一直「加载组件配置…」
4. **`11`**：写模型管理列表展示配置并挂到 `dm_model_tab_category.model_list_props_id`；否则「模型管理」Tab 报「缺少型号列表展示配置」

`07` 依赖库内已有 `_seed_five_w_semantic`（通常已跑过 `five-w-orchestration/00_helpers.sql`）。`08`/`11` 优先复用已有 action 的 props 行，否则从同类 tree/list 模板克隆。
