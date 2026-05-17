# Inspection Task SQL Design v1

## 1. 设计目标

本设计稿描述巡检任务模块在支持智能调度后的数据落库模型，重点是把：

- 长期任务定义
- 排期规则模板
- 候选计划点生成输入
- 最终计划点落库结果
- 执行记录
- 重排历史

明确拆开，避免把“任务主记录”“计划点”“执行结果”混在一起。

---

## 2. 领域对象分层

### 2.1 InspectionTaskDO

表示长期稳定存在的一条任务主记录。

它描述：
- 巡检对象
- 巡检内容
- 默认排期方案
- 默认资源策略
- 上下级继承关系

它不是未来某一次具体执行，也不会因为未来 30 天内要执行多次而裂变成多条任务主记录。

### 2.2 InspectionTaskScheduleProfileDO / InspectionTaskScheduleRuleDO

表示排期方案与排期规则模板。

它们定义：
- 调度模式
- 排期模式
- 周期单位与步长
- 固定时间点 / 时间窗
- 资源策略
- 冲突策略

它们是“规则模板”，不是调度结果。

### 2.3 TaskScheduleCandidate

表示规则展开后得到的候选计划点。

它是调度求解前的中间对象，不直接落库为最终计划结果。

### 2.4 InspectionTaskScheduleDO

表示最终计划点。

它是候选计划点经过冲突检测、资源分配、求解落位后的最终结果。

### 2.5 InspectionTaskExecutionDO

表示计划点真实执行后的过程与结果。

---

## 3. 建模原则

### 3.1 一条任务主记录，对应多条计划点

周期任务不会生成多条 `inspection_task` 记录。

未来多次执行安排应写入：

- `inspection_task_schedule`

因此：
- 任务是一条长期定义
- 计划点是未来多次调度结果
- 执行记录是真实执行结果

### 3.2 单次冲突只改单次计划点

规则保持不变。

如果某一次调度因为资源冲突或时间冲突需要顺延 / 提前 / 换资源，应只调整对应计划点，而不是把整条规则永久改坏。

### 3.3 计划点必须保留理论时间与最终时间

计划点建议至少保留：
- `original_start_time`
- `original_end_time`
- `planned_start_time`
- `planned_end_time`

这样可以解释：
- 为什么发生偏移
- 偏移了多少
- 是自动调整还是人工调整

---

## 4. 核心表

### 4.1 inspection_task_schedule

最终计划点主表。

建议承载：
- 任务 ID
- 站点 ID
- 生效排期方案 ID
- 命中的规则 ID
- `scheduled_date`
- `planned_start_time`
- `planned_end_time`
- `original_start_time`
- `original_end_time`
- `source_pattern`
- `schedule_status`
- `execution_status`
- `lock_status`
- `adjustment_type`
- `adjustment_reason`
- `reschedule_version`
- `manual_confirm_required`
- `manual_confirmed`
- 规则快照 / 任务快照 / 资源策略快照

### 4.2 inspection_task_schedule_resource

计划点与资源分配关系表。

用于表达：
- 一条计划点可分配多个资源
- 固定资源 / 资源池分配 / 重排替换

### 4.3 inspection_task_schedule_conflict

计划点冲突明细表。

用于记录：
- 时间冲突
- 资源冲突
- 锁定冲突
- 外部占用冲突
- 业务先后约束冲突

### 4.4 inspection_task_execution

执行结果表。

用于记录：
- 对应计划点 ID
- 实际开始时间 / 结束时间
- 实际执行资源
- 执行状态
- 结果说明

### 4.5 inspection_task_reschedule_batch

重排批次头表。

### 4.6 inspection_task_reschedule_change

重排变化明细表。

用于描述某次重排中：
- 哪条计划点被调整
- 调整前后的时间变化
- 调整前后的资源变化
- 调整原因

---

## 5. 规则字段建议

排期规则建议使用以下命名：

- `scheduleMode`
- `schedulePattern`
- `requiredSchedulesPerCycle`
- `weekDays`
- `monthDays`
- `timePoints`
- `preferredTimeWindows`
- `forbiddenTimeWindows`
- `requiredRobotCount`
- `allowResourceSwitch`

不再使用容易歧义的 `occurrencePattern` 作为主口径。

---

## 6. 计划点状态建议

### 6.1 scheduleStatus

- `PENDING`
- `PLANNED`
- `CONFLICT_PENDING_CONFIRM`
- `CANCELLED`
- `SUPERSEDED`

### 6.2 executionStatus

- `NOT_STARTED`
- `EXECUTING`
- `DONE`
- `FAILED`
- `SKIPPED`

### 6.3 lockStatus

- `UNLOCKED`
- `SYSTEM_LOCKED`
- `MANUAL_LOCKED`

---

## 7. 重排支持要求

为了支持局部重排，计划点表建议补：

- `reschedule_version`
- `adjustment_type`
- `adjustment_reason`
- `manual_confirm_required`
- `manual_confirmed`

重排明细建议补：

- `before_start_time`
- `after_start_time`
- `before_resource_json`
- `after_resource_json`

这样才能完整回答：
- 哪条计划点被重排
- 为什么被重排
- 重排前后发生了什么变化

---

## 8. 一句话总结

SQL 设计必须体现：

**任务只保留长期稳定主记录，排期方案与规则只定义未来应如何安排，最终调度结果落为计划点，真实发生过程落为执行记录，重排变化单独留痕。**
