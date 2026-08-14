# SOP（标准作业流程 · `sop`）seed

**定稿**：知识库入口名 **标准作业流程SOP**，类型编码 **`sop`**，专用表 `ent_sop*`。  
检查项挂 SOP、实体本台选用 SOP 均走 **关联关系矩阵**（`dynamic_entity_relation` / `dynamic_model_entity_relation`），**不**再建 `dynamic_inspection_item_sop` 等专用关联表。**不再使用** `inspection_method` / `method_template_id`。

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
| `09_retire_field_work_standard_artifacts.sql` | 改组件 props / 软删旧码能力投影，避免列表仍查 `field_work_standard` |

**产品入口**：知识库 → **标准作业流程SOP**。具体流程一律建为**实体实例**，不要再建第二型号。
