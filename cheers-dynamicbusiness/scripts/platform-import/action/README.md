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
| `01_fields.sql` | 字段库：`param_slots_json` / `child_action_ids_json` / `is_composite`（**不含** execution_means） |
| `02_entity_type.sql` | 类型 `action`（动作库），知识库 · 全网 · SINGLE |
| `03_base_fields.sql` | 基础字段挂载（仅结构列；通过 `type_config.createVisible=false` 控制新建不展示） |
| `04_model.sql` | 规范型号 `action`（目录）+ 字段分配；**必须 status=1** |
| `09_action_param_models.sql` | **常用执行参数字段库**（供勾进 param_slots_json）；取消历史 action_param_* 实体必填 |
| `12_retire_param_models_and_execution_means.sql` | **纠偏**：启用 `action`、停用 `action_param_*`、去掉执行手段基础字段、清表单缓存 |
| `05_sample_actions.sql` | 样例 UAV/ROBOT 动作；挂规范型号 `action` + 写 param_slots_json；手段分类写分类–实体 |
| `06_ensure_data_layout.sql` | 从通用台账模版挂载 `data_layout_id`（关系图 / 数据页必需） |
| `07_ensure_catalog_orchestration.sql` | 默认目录编排头（与建类型同口径：点列表这一行） |
| `08_ensure_layout_props.sql` | 分类/型号/实体栏 `propsId`（与创建后 `writeDefaultDataTabLayouts` 同口径） |
| `11_ensure_model_tab_list_props.sql` | 模型管理 Tab：`模型管理列表-action` + `dm_model_tab_category.model_list_props_id`（与 `ensureModelTabListPropsId` 同口径） |
| `10_robot_atomic_actions.sql` | 地面机器人原子动作；现网纠偏挂回规范型号 `action` |

## 常用参数字段（勾进动作参数定义）

| fieldCode | 显示名 | 说明 |
|-----------|--------|------|
| `location_ref` | 到达位置 | 路网点位 REF |
| `dwell_duration` | 停留时长 | 秒 |
| `yaw` / `pitch` / `roll` / `focal_length` | 摄像机 | 度 / 毫米 |
| `shot_count` | 拍摄张数 | |
| `route_ref` | 路线引用 | 路线 REF |
| `gas_threshold` | 气体阈值 | ppm |

样例动作挂规范型号 **`action`**。参数清单权威在 **`param_slots_json`**（外形 `{version,fields[{fieldCode,required,defaultValue}]}`）。How 侧宿主填值写入宿主 SOP 参数包，不长期双写实例 `param_override`。

**禁止**：再用 `action_param_*` 模型字段冒充动作参数清单。  
**禁止**：把 `location_ref` / 执行手段等挂成动作实体 CRUD 字段——新建表单只投影名称/状态等标准字段；参数在详情「配置参数」维护。

**产品入口**：知识库 → **动作库**。具体动作一律建为**实体实例**，不要再建第二型号。

SQL 建类型不会走界面「创建类型」时的 bootstrap，须补全与建类型相同的四步：

1. **`06`**：挂 `data_layout_id` + 默认筛选边；否则关系图报「尚未挂载 dataLayoutId」
2. **`07`**：写 `dm_catalog_orchestration`；否则数据页报编排头未配置
3. **`08`**：为各栏写入/绑定组件配置 `propsId`；否则栏上一直「加载组件配置…」
4. **`11`**：写模型管理列表展示配置并挂到 `dm_model_tab_category.model_list_props_id`；否则「模型管理」Tab 报「缺少型号列表展示配置」

`07` 依赖库内已有 `_seed_catalog_orchestration`（通常已跑过 `catalog-orchestration/00_helpers.sql`）。`08`/`11` 优先复用已有 action 的 props 行，否则从同类 tree/list 模板克隆。
