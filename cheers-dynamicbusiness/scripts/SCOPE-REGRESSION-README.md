# Scope 回归测试数据与脚本

## 数据是否够用？

**油库样例 seed**（`platform-import/system/seed/dynamic_entity_task_scope_regression.sql`）覆盖日常巡检、维修、执行记录与 Scope 隔离：

| 维度 | 内容 |
|------|------|
| 原生 storage | `task`（任务）、`task_excution_record`（执行记录） |
| 域入口（SCOPED） | `task_patrol`、`task_maintenance`、`task_record_patrol`、`task_record_maintenance` |
| 已删除旧法 | `patrol` 类型上的任务模型、`task_patrol_smoke`、`general_admin_task` |
| 分类树 | `task`：日常/专项/维修；`task_excution_record`：巡检记录/维修记录 |
| Scope 隔离 | `general_station_task`（无 scope）用于验证巡检/维修 Scope 不串域 |

### 任务模型（storage=`task`，8 个）

| code | 业务域 | 实体数 |
|------|--------|--------|
| `patrol_task` | 巡检 | 5 |
| `patrol_pipeline` | 巡检 | 3 |
| `patrol_fire_facility` | 巡检 | 3 |
| `patrol_valve_special` | 巡检 | 2 |
| `patrol_adhoc` | 巡检 | 2（未挂分类） |
| `maint_work_order` | 维修 | 3 |
| `maint_emergency` | 维修 | 2 |
| `general_station_task` | 无 | 2 |

### 执行记录模型（storage=`task_excution_record`，3 个）

| code | 业务域 | 实体数 |
|------|--------|--------|
| `exec_patrol_round` | 巡检 | 4 |
| `exec_patrol_issue` | 巡检 | 4 |
| `exec_maint_log` | 维修 | 5 |

### 关键断言基数

| 对象 | 全量 | 巡检 Scope | 维修 Scope |
|------|------|------------|------------|
| 任务模型 | 8 | 5 | 2 |
| 任务实体 | 22 | 15 | 5 |
| 执行记录模型 | 3 | 2 | 1 |
| 执行记录实体 | 13 | 8 | 5 |
| 空域 Scope | — | 0 | — |

## 准备数据

```bash
# PostgreSQL（tenant_id=1）
psql -h 127.0.0.1 -U postgres -d sinopec -v ON_ERROR_STOP=1 \
  -f scripts/platform-import/system/seed/dynamic_entity_task_scope_regression.sql

# 清分类树 Redis 缓存（否则 API 可能仍返回旧树）
redis-cli DEL $(redis-cli KEYS 'dynamicbusiness:category:tree:*')
```

或跑完整 `platform-import/system/import.sh`（已追加本 seed）。

**注意**：`ent_task_excution_record` 需含 `sort`/`tree_path` 列（seed 内 `ALTER TABLE` 已补齐），否则 `query-by-scene` 会 500。

## 跑测试

```bash
python scripts/scope-regression-test.py
```

环境变量：`SCOPE_TEST_TOKEN`、`SCOPE_TEST_BASE_URL`（默认 `http://127.0.0.1:58096/admin-api/dynamicbusiness`）。

## 已知缺口（脚本标 KNOWN_GAP）

- `PATTERN_A_C_ENTITIES_BY_CATEGORY`、`PATTERN_B_ENTITIES_BY_CATEGORY` 传 `dataScope` 时**尚未**按 Scope 过滤（与 DATA_MGMT 场景行为不一致，后续可统一）。
