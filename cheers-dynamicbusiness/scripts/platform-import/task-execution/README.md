# 任务执行步骤 · platform-import

**不**重复注册 `task_excution_record`（现网已有）。

## 本脚本做什么

1. 注册 **task_execution_step**（NATIVE + 字段 + 型号）
2. 对齐 **task_record_patrol** → 名称「巡检任务执行记录」；型号 `exec_patrol_round`

## 前置

- Flyway **V54**（`ent_task_execution_step`）
- `task_excution_record` / `ent_task_excution_record_t*` 已存在

## 执行

```bash
cd scripts/platform-import/task-execution
./import.sh
```

## 巡检流程（当前数据层）

```text
task（巡检任务定义）
  → 选巡检方式 → resolve 绑定 SOP 实例 → merge
  → 开跑 → task_excution_record（domain=巡检，型号 exec_patrol_round）
              · custom_fields.standard_snapshot / parameter_snapshot / gap_codes
              → task_execution_step（REF execution_record_id）
```

开跑 API：`POST /dynamicbusiness/task-execution/bootstrap-steps`（有 gapCodes 拒绝）。  
路径点位由前端 `collectPatrolPointRefsFromEffectiveConfigs` 收集后交给既有算路；**不**在本 bootstrap 内算路。

## 前置

- Flyway **V54**（`ent_task_execution_step`）
- `task_excution_record` / `ent_task_excution_record_t*` 已存在
- 型号 `task_execution_step` 已 seed（本目录 `04_models.sql`）

## 执行

```bash
cd scripts/platform-import/task-execution
./import.sh
```
