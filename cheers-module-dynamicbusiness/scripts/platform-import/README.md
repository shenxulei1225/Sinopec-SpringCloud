# 动态业务 · 平台导入包

从 PostgreSQL **当前库**导出，按 **系统共用 / 智慧管廊 / 智慧站场** 三个目录分包。全部以 `code` / `field_code` 为幂等键，不写 surrogate id。

**字段库与通用设备模型库只在 system 包维护一份**；管廊/站场包只导各自业务模型与分类。

## 目录结构

```
platform-import/
  system/
    01_schema.sql           建表（与 Flyway V1 同源）
    02_business_types.sql   业务类型 + config + 业务间关联
    03_fields.sql           实体默认字段 + 完整 dynamic_field 字段库
    04_field_groups.sql     字段分组 FIELD + 字段-分组关联
    05_models.sql           通用设备模型库（equipment）
    06_categories.sql       设备模型常用分类
    07_business_portal.sql  业务门户树 + 入口
    08_capabilities.sql     能力投影 business_capability
    import.sh
  smart-corridor/           管廊产品（在 system 之后）
  smart-station/            站场产品（在 system 之后）
  import-dev-all.sh         开发混装
```

## 新电脑 / 空库导入（推荐）

**方式 A：Flyway + 产品包（与线上一致）**

1. 配置 PostgreSQL，创建库 `sinopec`，schema `dynamicbusiness`
2. 启动 `cheers-module-dynamicbusiness-server`，Flyway 自动执行 V1–V7、V14–V27
3. 若需管廊/站场完整模型与分类，再执行：

```bash
cd scripts/platform-import
./smart-corridor/import.sh   # 和/或
./smart-station/import.sh
```

**方式 B：纯 SQL 导入（不依赖 Flyway 历史）**

```bash
cd scripts/platform-import
./import-dev-all.sh
```

等价于 system 全包 + corridor + station。

## 从本机重新导出（迁机前必做）

在本机数据库已是「正确状态」时执行：

```bash
cd Sinopec-SpringCloud/cheers-module-dynamicbusiness/scripts
PGPASSWORD=Coolhomer python3 export-platform-import.py
```

会同步更新：

| 输出 | 说明 |
|------|------|
| `platform-import/system/*.sql` | 系统 seed |
| `platform-import/smart-*/` | 管廊/站场模型与分类 |
| Flyway `V2__seed_system_base.sql` | 业务类型 + 字段 + 设备模型 |
| Flyway `V26__seed_business_portal_and_capabilities.sql` | 门户 + 能力投影 |
| Flyway `V27__init_industry_field_library.sql` | 字段分组与关联 |

## 本机已跑过 Flyway 的注意

若重新生成了 `V2__seed_system_base.sql` 且本机 `flyway_schema_history_dynamicbusiness` 里 V2 已执行，启动时可能报 **checksum mismatch**。可选：

- 新库迁机：无此问题
- 本机继续开发：对 V2 执行 `flyway repair`，或保留旧 V2 文件、只提交 V26/V27 与 platform-import

## 已知数据缺口（导出时会如实反映）

- `dynamic_model_field_assignment`：若库中为 0 条，导出模型不含字段绑定，需在字段管理/模型配置界面重新挂字段
- 业务实例（`dynamic_entity` / `ent_*`）：默认不导出；保留在 `smart-corridor/07_entities.sql` 等可选文件中
