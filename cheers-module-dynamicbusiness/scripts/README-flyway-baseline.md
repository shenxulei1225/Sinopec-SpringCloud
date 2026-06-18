# 动态业务 Flyway 基线（V1 + V2）

zhgl 历史迁移已结束。新环境**不再依赖 zhgl / dblink**，只执行两条 Flyway：

| 版本 | 文件 | 说明 |
|------|------|------|
| V1 | `V1__init_dynamicbusiness_schema.sql` | 当前库完整 schema（含业务能力表、`biz_*` 专用表等） |
| V2 | `V2__seed_dynamicbusiness_data.sql` | 当前库全量业务数据 seed（业务类型、模型、字段、实体、能力投影等） |

执行顺序：`V1 → V2`。

## Windows / Mac 新库

1. 创建数据库 `sinopec`，确保 `application-local.yaml` 中 Flyway 指向 `classpath:db/migration/dynamicbusiness`。
2. 启动 `cheers-module-dynamicbusiness-server`（`local` profile），或：

```bash
cd cheers-module-dynamicbusiness-server
mvn flyway:migrate
```

3. 启动后 **system 业务能力** 仍由 `SystemCapabilityBootstrapRunner` 自动注册（不在 V2 seed 中）。

**平台资源库 props**（组件配置器里的列表模板）见 `cheers-module-platform-resource` 的 `V7__seed_pr_component_props_dynamic.sql`，与本文档无关。

## 从 Mac 开发库重新生成 V1 / V2

元数据或实体有变更、需要同步到 Windows 时，在**已迁移完成**的 `sinopec.dynamicbusiness` 上执行：

```bash
cd cheers-module-dynamicbusiness
PGPASSWORD=Coolhomer python3 scripts/generate-flyway-baseline.py
```

提交更新后的 `V1`、`V2` 即可。

环境变量（可选）：`PGHOST`、`PGPORT`、`PGDATABASE`、`PGUSER`、`PGPASSWORD`、`PG_BIN`（pg_dump 目录）。

## 已有库（曾跑过旧版 Flyway）

旧 Flyway 版本号与 checksum 与新版 **V1/V2 不兼容**，启动会报 `checksum mismatch` 或 `missing migration`。

### 先诊断（推荐）

```bash
cd cheers-module-dynamicbusiness
psql -h 127.0.0.1 -U postgres -d sinopec -f scripts/diagnose-baseline-readiness.sql
```

### 情形 A：只跑过 V1–V3 + V14–V16（Windows 常见）

说明：**未执行 V4–V13**（专用表、zhgl 元数据、实体导入等），库里的数据**不完整**，与 Mac 不一致。  
此时 **不要** 只修 Flyway 历史——启动虽可能不报错，但列表/ props 仍缺数据。

**应重建 schema，让新版 V1+V2 全量 seed：**

```bash
psql -h 127.0.0.1 -U postgres -d sinopec -f scripts/reset-dynamicbusiness-for-baseline.sql
# 再启动 dynamicbusiness-server 或 mvn flyway:migrate
```

会删除 `dynamicbusiness` schema 下全部数据，再自动导入仓库内 V2（约 1 万+ 设备实体等）。

### 情形 B：Mac 等已完整迁移的库（V1–V13 都跑过）

数据已完整，**不要清表**；只修 Flyway 历史：

```bash
psql -h 127.0.0.1 -U postgres -d sinopec -f scripts/repair-flyway-history-baseline.sql
```

然后（可选）：

```bash
cd cheers-module-dynamicbusiness-server
mvn flyway:repair flyway:validate
```

修复后历史应只剩 2 行：`version` = `1` 和 `2`，`success` = `t`。

### 情形 C：空库 / 测试库

```bash
dropdb sinopec && createdb sinopec
# 再 flyway:migrate 或启动服务
```

## V2 体积说明

V2 含约 1 万+ 设备实体等实例数据，体积约 **18MB**。属预期行为；若只需元数据不要实体，需改 `generate-flyway-baseline.py` 增加 `--exclude-table` 后重新生成。

## 变更记录

- **2026-06-18**：删除 V3–V17（zhgl 导入、对齐、能力分版迁移），合并为 V1 schema + V2 data seed。
