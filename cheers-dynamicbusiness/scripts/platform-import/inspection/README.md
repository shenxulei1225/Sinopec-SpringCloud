# 检查域 · 检查项方法 / 设备 SOP 绑定 seed

**正文约定**：除 SQL 代码示例外，英文/domain 术语均写作 **中文名（英文名）**。

## 术语

| 中文名 | 英文名 | 含义 |
|--------|--------|------|
| 检查项方法 | inspection item method | 检查项 × 执行手段 → 默认 SOP 模板引用 |
| 设备检查绑定 | equipment inspection SOP binding | 设备 × 检查项 × 执行手段 → 独占 SOP 实例 |
| 执行手段 | executionMeans | MANUAL / UAV / ROBOT / FIXED_CAMERA |

## 权威表（谁写入、谁读取）

检查项方法（inspection item method）权威在 Flyway **V44** 表 `dynamic_inspection_item_sop`：列 `sop_id` 存 **SOP 模板**实体编号，API 对外称 `sopTemplateId`。设备检查绑定（equipment inspection SOP binding）权威在 Flyway **V75** 表 `dynamic_equipment_inspection_sop_binding`，指向 `is_template=false` 的实例行。

不再用独立 `inspection_method` 实体承载步骤正文；与 SOP 库分类「检查」共用同一套模板。

## 文件

| 文件 | 职责 |
|------|------|
| `patch_inspection_item_sop_method.sql` | 文档化权威；幂等挂样例检查项×手段→模板（依赖 SOP seed 样例模板） |

设备检查绑定无 seed 样例行：由管理端「从模板创建实例」写路径产生。

## 前置

1. Flyway **V74–V75** 已执行  
2. `scripts/platform-import/sop/` 中分类与样例模板（如 `SOP-TPL-MANUAL-LEAK`）已导入  
3. 存在可匹配的检查项样例（名称含泄漏/跑冒滴漏或 code 含 leak；读 `ent_inspection_item_t1`）

```bash
psql "$DATABASE_URL" -f scripts/platform-import/inspection/patch_inspection_item_sop_method.sql
```
