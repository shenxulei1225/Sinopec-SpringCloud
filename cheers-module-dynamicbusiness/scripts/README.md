# dynamicbusiness 脚本

## 保留脚本（常用）

| 脚本 | 用途 |
|------|------|
| `export-platform-import.py` | 从库导出 seed（可按 fields/models/assignments 等独立导出） |
| `regenerate-v1-from-db.py` | 从库 pg_dump 刷新 Flyway V1 DDL |
| `seed_codegen.py` | SQL 渲染与 upsert 逻辑（导出/生成共用） |
| `field_library_catalog.py` | 字段库定稿 catalog |
| `smart_station_catalog.py` | 站场产品 catalog |
| `generate-smart-station-import.py` | 生成 `platform-import/smart-station/` |
| `generate-field-group-seed.py` | 字段分组 seed 辅助 |
| `apply-base-fields.py` | 运维：按 canonical 补实体类型基础字段 |
| `rebuild-capability-projections.py` | 运维：重建能力投影 |
| `sanitize-entity-inserts.py` | 实体实例 SQL 清洗 |
| `normalize-platform-import-seeds.py` | 将 seed 中遗留拼音 entity_type_code / 拼音 dedicated_table_name 规范为英文 |
| `entity_type_canonical.py` | 拼音→英文映射（导出与 seed 规范化共用） |
| `platform-import/` | 标准导入包（见其中 README） |
| `flyway-repair-local.sh` | 本机 repair：重命名/改 V1 后对齐 checksum |

## 空库流程（一句话）

**Flyway V1→V2→V3（应用启动）→ `platform-import/import-dev-all.sh`（仅 seed）**

## 导出示例

```bash
cd scripts
PGPASSWORD=Coolhomer python3 export-platform-import.py --schema --rewrite-import-scripts
PGPASSWORD=Coolhomer python3 export-platform-import.py --package system --artifacts fields,models,assignments
```

## 已清理（勿再引用）

- `generate-v5/v6/v7-*`、`clean-v5-ddl`、`normalize-v1-schema`
- `export-zhgl-seed`、`import-zhgl-fields`、`platform-import/zhgl-legacy/`
- `generate-field-library`（原写 Flyway V27，已由 seed 导出取代）
- `platform-import/system/01_schema.sql` 等重复 DDL

历史 Flyway V4+ 在 `db/backup/flyway-legacy-pre-seed/`。

## Flyway（通用 + 本模块）

**通用规范**（改任何 `V*.sql` 前必读）：仓库根 `.cursor/rules/flyway-migration.mdc`  
**本模块快照**：`cheers-module-dynamicbusiness-server/.../db/migration/dynamicbusiness/README.md`

已执行过的迁移：**immutable**；改文件名或 SQL 后必须 `version`+`script`+`checksum` 对齐，并跑 `./flyway-repair-local.sh`，再重启验证。默认用**新增下一版本**表达结构变更，而不是改旧文件。
