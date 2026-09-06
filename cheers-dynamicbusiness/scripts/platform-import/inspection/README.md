# 检查域 · 历史 seed 说明（请改用 recipes）

**正文约定**：除 SQL 代码示例外，英文/domain 术语均写作 **中文名（英文名）**。

本目录保留旧补丁路径说明。**新的检查演示配方**（How 挂 `sopHow`、V80 通用方法选用样例）在：

→ [`../recipes/inspection/`](../recipes/inspection/README.md)

## 为何迁移

| 旧 | 新 |
|----|----|
| `dynamic_inspection_item_sop` / `dynamic_equipment_inspection_sop_binding` | Flyway **V80** `dynamic_sop_method_binding` / `dynamic_sop_instance_binding` |
| How 检查专用面板 | 编排 `howSlot.capability=sopHow` + 平台 `features/sop/how` |
| 本目录 `patch_inspection_item_sop_method.sql` | `recipes/inspection/03_sample_method_bindings.sql` |

`patch_inspection_item_sop_method.sql` 仍指向已 DROP 的旧表，**勿在新库执行**；请跑 recipes。

## 前置仍有效

检查实体类型与检查项样例仍由 `../inspection-method/` 与 system seed 提供；目录编排头由 `../catalog-orchestration/` 提供。
