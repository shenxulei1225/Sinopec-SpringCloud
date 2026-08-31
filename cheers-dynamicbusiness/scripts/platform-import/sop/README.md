# SOP（标准作业流程 · `sop`）seed

**定稿**：知识库入口名 **标准作业流程SOP**，类型编码 **`sop`**，专用表 `ent_sop*`。  
编排权威列为 **`action_tree_json`**（节点 `nodeKey` / `actionId` / `order`）；模板默认参数为 **`default_params_by_node_json`**。  
步骤模板（`sop_step_template`）已由 Flyway **V79** 废弃；动作定义见 `../action/`。

检查项 × 执行手段 → **SOP 模板**、设备 × 检查项 × 手段 → **独占实例**：权威表为 Flyway **V80** 通用绑定
`dynamic_sop_method_binding` / `dynamic_sop_instance_binding`（类型码写在配方/调用方，不写死在 Java/TS 绑定模块）。
旧表 `dynamic_inspection_item_sop` / `dynamic_equipment_inspection_sop_binding` 已由 V80 迁出并 DROP。
**不再使用** `inspection_method` / `method_template_id` 第二套步骤正文。

## 升级顺序（已有 `field_work_standard` 的库）

1. 部署并执行 Flyway **V56**（表改名 + 类型码改为 `sop`，软删重复入口）
2. `./sop/import.sh`（幂等刷新元数据与样例）

## 新库

Flyway V43 建旧表名 → V56 改名为 `ent_sop*` → Flyway **V76–V80** → 先 `../action/import.sh` → `./sop/import.sh`。

## 文件

| 文件 | 职责 |
|------|------|
| `01_fields.sql` | `version_no` / `publish_status` / `steps_json` |
| `02_entity_type.sql` | 类型 `sop`，知识库 · 全网 · SINGLE |
| `03_base_fields.sql` | 基础字段挂载 |
| `04_model.sql` | 规范型号 `sop` |
| `05_retire_inspection_method_metadata.sql` | 软删 inspection_method 元数据 |
| `06_sample_sops.sql` | 样例 SOP 实例 |
| `07_seed_leak_and_ppe_sops.sql` | 实例：跑冒滴漏检查、工服穿戴检测 |
| `08_five_w_orchestration.sql` | 数据 Tab 五维编排（语义块 + Who 三栏；可恢复被软删的头）；并把布局里分类编码从旧的 `field_work_standard` 改成 `sop` |
| （前置）`../five-w-orchestration/00_helpers.sql` | `import.sh` 在跑 08 前会刷新 `_seed_five_w_*` 函数签名 |
| `09_retire_field_work_standard_artifacts.sql` | 改组件 props / 软删旧码能力投影，避免列表仍查 `field_work_standard` |
| `10_step_template_fields.sql` | **已废弃**（V79）；`import.sh` 不再执行 |
| `11_step_template_entity_type.sql` | **已废弃**（V79）；`import.sh` 不再执行 |
| `12_sop_template_instance_fields.sql` | SOP 模板/实例列元数据（含 `action_tree_*`；停挂 `default_steps_json`） |
| `13_sop_categories_and_sample_templates.sql` | SOP 库分类子树、动作树示例模板（`act-*`） |
| `14_sop_library_layout.sql` | 统一库布局：分类\|模板列表\|详情；列表 filter `is_template=true`；软删型号栏 |
| `15_sop_library_layout_relations_fix.sql` | 修布局区段与 CE filter/write；未挂分类的模板挂「检查」 |

**升级顺序（已有库）**

1. Flyway **V76–V79**
2. `../action/import.sh`
3. `./sop/import.sh`

**产品入口**：知识库 → **标准作业流程SOP**。具体流程一律建为**实体实例**，不要再建第二型号。
