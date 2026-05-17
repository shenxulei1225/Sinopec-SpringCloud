# Inspection Task Scheduling Design

## 1. 文档目标

本文用于统一巡检任务领域的概念、对象边界、排期模型与智能调度设计。

重点解决以下问题：

1. 任务主记录与未来多次计划点如何分层
2. 排期方案与调度结果如何分层
3. 单任务试算、多任务统一编排、局部重排分别处理什么对象
4. 调度系统为什么必须引入独立的计划点模型

---

## 2. 领域对象边界

### 2.1 InspectionTaskDO

表示任务主记录。

它是用户维护的长期定义，用来表达：
- 巡检对象
- 巡检内容
- 默认排期方案
- 默认资源策略
- 上下级任务关系

它不是未来某一次具体执行，也不是未来某一次具体调度结果。

### 2.2 InspectionTaskScheduleProfileDO / InspectionTaskScheduleRuleDO

表示排期规则模板。

它们负责定义：
- 调度模式
- 排期模式
- 周期单位与周期间隔
- 固定时间点 / 时间窗
- 冲突策略
- 资源策略

它们不直接表示最终计划结果。

### 2.3 TaskScheduleCandidate

表示规则展开后的候选计划点。

这是调度求解前的中间对象，用于：
- 单任务试算
- 多任务候选池汇总
- 冲突检测输入

### 2.4 InspectionTaskScheduleDO

表示最终计划点。

它是候选计划点经过调度求解后形成的最终计划安排。

### 2.5 InspectionTaskExecutionDO

表示某条计划点真实执行后的执行结果。

---

## 3. 为什么必须有计划点模型

如果只保留任务主记录，会出现以下问题：

1. 无法表达未来 7 天 / 30 天内的多次执行安排
2. 无法只调整其中一次冲突
3. 无法记录顺延、提前、换资源等单次调度结果
4. 无法支持局部重排与锁定保护

因此必须明确：

- 主任务只有一条
- 未来多次安排必须拆成多条计划点

---

## 4. 排期模型

### 4.1 调度模式

建议支持：
- 单次
- 每天
- 每周
- 每月
- 自定义间隔

字段：
- `scheduleMode`

### 4.2 排期模式

建议支持：
- 固定点
- 时间窗

字段：
- `schedulePattern`

### 4.3 规则输入建议

规则应至少包含：

- `scheduleMode`
- `schedulePattern`
- `cycleUnit`
- `cycleStep`
- `anchorDate`
- `requiredSchedulesPerCycle`
- `weekDays`
- `monthDays`
- `timePoints`
- `preferredTimeWindows`
- `forbiddenTimeWindows`
- `conflictStrategy`
- `resourceStrategy`
- `requiredRobotCount`
- `fixedRobotId`
- `allowResourceSwitch`

---

## 5. 单任务试算

### 5.1 输入

- 一条任务
- 一段时间范围
- 生效排期方案
- 生效规则

### 5.2 输出

- 候选计划点列表
- 规则告警
- 试算解释信息

### 5.3 特点

单任务试算只解决：
- 规则是否能正确展开
- 候选计划点是否合理

它不负责全局多任务求解。

---

## 6. 多任务统一编排

### 6.1 输入

- 多条任务
- 时间范围
- 候选计划点池
- 已存在计划点
- 已锁定计划点
- 资源可用性
- 外部占用

### 6.2 目标

不是逐任务各排各的，而是把所有候选计划点放入统一待排池，统一排序、统一求解。

### 6.3 输出

- 已排计划点
- 未排候选点
- 冲突列表
- 调整原因

---

## 7. 单次冲突只能改单次计划点

这是调度设计中非常关键的原则。

错误做法：
- 某次冲突后，把整条规则或整条任务永久整体后移

正确做法：
- 只调整当前受影响计划点
- 其他计划点仍按规则独立计算

因此调度系统必须以“计划点”为粒度求解，而不是以“任务整体”为粒度粗暴偏移。

---

## 8. 局部重排设计

### 8.1 触发源

- 机器人故障
- 设备故障
- 区域封锁
- 插队任务
- 上一任务延迟

### 8.2 流程

1. 定位受影响计划点
2. 保护已锁定 / 已执行 / 已确认计划点
3. 对剩余可变更计划点做局部求解
4. 生成新的计划点结果
5. 记录重排批次与变化明细

### 8.3 原则

- 不做无差别全量重排
- 不污染原始规则模板
- 不覆盖受保护计划点

---

## 9. 状态设计

### 9.1 计划层状态

- `PENDING`
- `PLANNED`
- `CONFLICT_PENDING_CONFIRM`
- `CANCELLED`
- `SUPERSEDED`

### 9.2 执行层状态

- `NOT_STARTED`
- `EXECUTING`
- `DONE`
- `FAILED`
- `SKIPPED`

### 9.3 锁定层状态

- `UNLOCKED`
- `SYSTEM_LOCKED`
- `MANUAL_LOCKED`

---

## 10. 执行记录不等于计划状态

即使计划点表上有状态字段，也不能省略执行记录。

原因：
- 计划状态描述的是“系统怎么安排”
- 执行记录描述的是“真实世界怎么发生”

因此：
- 计划点解决“排好了没有”
- 执行记录解决“真正执行得怎么样”

---

## 11. 推荐代码对象

### 11.1 调度输入
- `TaskSchedulingContext`
- `TaskSchedulingBatchContext`
- `StructuredScheduleRule`

### 11.2 候选点
- `TaskScheduleCandidate`
- `InspectionTaskScheduleCandidateGenerator`
- `TaskScheduleCandidateGenerationStrategy`

### 11.3 调度结果
- `SingleTaskSchedulePlan`
- `BatchSchedulingResult`
- `RescheduleResult`

### 11.4 落库对象
- `InspectionTaskScheduleDO`
- `InspectionTaskScheduleResourceDO`
- `InspectionTaskScheduleConflictDO`
- `InspectionTaskRescheduleBatchDO`
- `InspectionTaskRescheduleChangeDO`
- `InspectionTaskExecutionDO`

---

## 12. 一句话总结

用户维护的是任务，系统复用的是排期方案与规则模板，调度系统先生成候选计划点，再求解成最终计划点，真实执行结果单独记录，故障后只对受影响计划点做局部重排。
