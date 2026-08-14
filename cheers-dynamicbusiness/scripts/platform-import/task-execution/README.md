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
  → 开跑 → task_excution_record（domain=巡检，型号 exec_patrol_round）
              → task_execution_step（REF execution_record_id）
```

下一步（未在本脚本）：执行记录过程字段、开跑服务、SOP 快照装填。
