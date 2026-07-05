# 动态业务 Flyway 基线

| 版本 | 文件 | 内容 |
|------|------|------|
| V1 | `V1__init_dynamicbusiness_schema.sql` | 共用表结构 |
| V2 | `V2__seed_system_base.sql` | **系统基础数据**（设备业务类型、共用模型与基础字段） |

**智慧管廊 / 智慧站场**不在 Flyway，见 [`scripts/platform-import/`](./platform-import/README.md) 手工导入。

分用户边界见 [`docs/动态业务/分场景数据导入说明.md`](../../../../docs/动态业务/分场景数据导入说明.md)。

## 新库（推荐）

1. 创建数据库 `sinopec`，确保 Flyway 指向 `classpath:db/migration/dynamicbusiness`。
2. 启动 `cheers-module-dynamicbusiness-server`（`local` profile）或：

```bash
cd cheers-module-dynamicbusiness-server
mvn flyway:migrate
```

3. 按产品需要执行 `platform-import/import-smart-corridor.sh` 或 `import-smart-station.sh`。
4. 开发联调可 `./platform-import/import-dev-all.sh`（管廊 + 站场混装）。

启动后 **system 业务能力** 仍由 `SystemCapabilityBootstrapRunner` 自动注册（不在 V2 seed 中）。

## 重新生成 V2 与 platform-import

```bash
cd cheers-module-dynamicbusiness
PGPASSWORD=Coolhomer python3 scripts/export-platform-import.py
```

会从现网 `dynamicbusiness` 导出 `platform-import/system/`、`smart-corridor/`、`smart-station/`，并写入 `V2__seed_system_base.sql`。

旧脚本 `generate-flyway-baseline.py` 面向已废弃的「V2 全量管廊 seed」，新流程以 `export-platform-import.py` 为准。

## 已有库（曾跑过旧 V2/V3 产品 seed）

若 `flyway_schema_history` 仍记录 `V2__seed_dynamicbusiness_data` 或 `V3__seed_smart_station_data`：

- **开发库数据可保留**，执行 `flyway repair` 或手工修正 history，使 version 2 指向 `V2__seed_system_base.sql` checksum。
- **交付库**建议空库重建：Flyway V1+V2 → 再手工导入对应产品包。

诊断脚本：`scripts/diagnose-baseline-readiness.sql`（若仍存在）。

## 变更记录

| 日期 | 说明 |
|------|------|
| 2026-07-05 | Flyway 收窄为 V1+V2 系统基础；管廊/站场迁至 platform-import |
