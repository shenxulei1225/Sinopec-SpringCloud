# SOP（标准作业流程 · `sop`）seed

**定稿**：知识库入口名 **标准作业流程SOP**，类型编码 **`sop`**，专用表 `ent_sop*`。  
检查项 × 执行手段 → **SOP 模板** 走 V44 表 `dynamic_inspection_item_sop`（见 `../inspection/`）；设备 × 检查项 × 手段 → **独占实例** 走 V75 `dynamic_equipment_inspection_sop_binding`。**不再使用** `inspection_method` / `method_template_id` 第二套步骤正文。

## 升级顺序（已有 `field_work_standard` 的库）

1. 部署并执行 Flyway **V56**（表改名 + 类型码改为 `sop`，软删重复入口）
2. `./sop/import.sh`（幂等刷新元数据与样例）

## 新库

Flyway V43 建旧表名 → V56 改名为 `ent_sop*` → `./sop/import.sh`。

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
| `10_step_template_fields.sql` | 步骤模板字段库 `param_slots_json`（Flyway V73 后） |
| `11_step_template_entity_type.sql` | 类型 `sop_step_template`、型号、分类根 |
| `12_sop_template_instance_fields.sql` | SOP 模板/实例列元数据（Flyway V74 后） |
| `13_sop_categories_and_sample_templates.sql` | SOP 库分类子树、步骤模板实体、新模型示例模板 |
| `14_sop_library_layout.sql` | 统一库布局：分类|模板列表|详情；列表 filter `is_template=true`；软删型号栏 |
| `15_sop_library_layout_relations_fix.sql` | 修布局区段与 CE filter/write；未挂分类的模板挂「检查」 |

**升级顺序（已有库）**

1. Flyway **V73–V75**
2. `./sop/import.sh`

**产品入口**：知识库 → **标准作业流程SOP**。具体流程一律建为**实体实例**，不要再建第二型号。
