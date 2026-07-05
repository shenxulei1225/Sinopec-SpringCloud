# 动态业务 · 平台导入包

从 PostgreSQL 导出，按 **系统共用 / 智慧管廊 / 智慧站场** 三个目录分包。全部以 `code` / `field_code` 为幂等键，不写 surrogate id。

**字段库与通用设备模型库只在 system 包维护一份**；管廊/站场包只导各自业务模型与分类，不再重复设备模型。

- `03_fields.sql`：全部默认字段 + 完整 `dynamic_field`
- `05_models.sql`：全部通用设备模型（含原 `data_collection` / `custom_6128` 下的传感器、电缆等，导出时归入 `equipment`）；跨业务关联（如 region→equipment）在管廊包

## 目录结构

```
platform-import/
  system/                 系统共用（任何项目都要）
    01_schema.sql         建表
    02_business_types.sql 15 个业务类型 + config + 业务间关联
    03_fields.sql         全部默认字段 + 完整字段库（dynamic_field）
    05_models.sql         通用设备模型库（equipment，含历史设备模型）
    06_categories.sql     设备模型常用分类
    import.sh
  smart-corridor/         管廊产品（在 system 之后）
    05_models.sql         管廊模型
    06_categories.sql     管廊分类树
    07_entities.sql       业务实例（可选）
    import.sh
  smart-station/          站场产品（在 system 之后）
    05_models.sql         库区/罐组模型
    06_categories.sql     站场分类 + 页面
    07_entities.sql       示例实例（可选）
    import.sh
  import-dev-all.sh       开发混装
```

## 新平台导入

```bash
cd scripts/platform-import
./system/import.sh
./smart-corridor/import.sh    # 或 smart-station/import.sh
```

开发混装：`./import-dev-all.sh`

## 重新导出

```bash
cd scripts
PGPASSWORD=Coolhomer python3 export-platform-import.py
```

同步更新 Flyway `V2__seed_system_base.sql`（= system 的 02 + 03 + 05）。
