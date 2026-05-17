# Inspection Task Code Build Checklist

## 1. 总体目标

本清单用于约束巡检任务模块在支持智能调度后的代码建设方式，确保以下边界始终清楚：

- 主任务是长期定义
- 排期方案与规则是模板
- 候选计划点是调度输入中间对象
- 计划点是最终调度结果
- 执行记录是真实执行结果
- 重排只作用于受影响计划点

---

## 2. 核心概念检查

在编码前先确认以下认知：

1. 周期任务未来多次安排必须作为**计划点**独立存在
2. 单次冲突只能改单次计划点，不能整体改坏规则
3. 调度只负责输出计划，不负责回写污染任务定义与规则模板
4. 执行记录不等于计划点状态字段

---

## 3. 对象分层检查

### 3.1 任务定义

- `InspectionTaskDO`
- 只保留长期稳定主记录

### 3.2 排期模板

- `InspectionTaskScheduleProfileDO`
- `InspectionTaskScheduleRuleDO`

### 3.3 候选计划点

- `TaskScheduleCandidate`

### 3.4 计划点

- `InspectionTaskScheduleDO`

### 3.5 执行记录

- `InspectionTaskExecutionDO`

### 3.6 重排记录

- `InspectionTaskRescheduleBatchDO`
- `InspectionTaskRescheduleChangeDO`

---

## 4. 包结构检查

推荐结构：

```text
service/schedule/
├── facade/
├── context/
├── validator/
├── candidate/
├── conflict/
├── solver/
├── engine/
├── result/
├── persistence/
└── reschedule/
```

检查点：

- [ ] 不再使用模糊的 `occurrence` 作为主包语义
- [ ] 候选计划点生成统一放在 `candidate/`
- [ ] 冲突检测统一放在 `conflict/`
- [ ] 最终落库统一放在 `persistence/`

---

## 5. 命名规范检查

### 5.1 规则字段

应统一：

- `scheduleMode`
- `schedulePattern`
- `requiredSchedulesPerCycle`
- `scheduledDate`

检查点：

- [ ] 不再新增 `occurrencePattern`
- [ ] 不再新增 `occurrenceDate`

### 5.2 候选点相关

应统一：

- `TaskScheduleCandidate`
- `TaskScheduleCandidateGenerationStrategy`
- `InspectionTaskScheduleCandidateGenerator`
- `SingleTaskScheduleCandidateNormalizer`

### 5.3 计划点相关

应统一：

- `InspectionTaskScheduleDO`
- `TaskSchedulePersistenceService`
- `AffectedTaskScheduleLocator`

### 5.4 预览输出对象

应统一：

- `TaskScheduleCandidatePreviewRespVO`
- `SingleTaskSchedulePreviewRespVO.candidates`

检查点：

- [ ] 不再保留 `OccurrencePreviewRespVO`
- [ ] 不再保留 `occurrences` 这种语义不清的列表名

---

## 6. 单任务试算检查

检查点：

- [ ] 是否通过 `TaskSchedulingContextBuilder` 构造输入
- [ ] 是否通过 `ScheduleRuleValidator` 等完成规则校验
- [ ] 是否通过 `InspectionTaskScheduleCandidateGenerator` 生成候选计划点
- [ ] 是否通过 `SingleTaskScheduleCandidateNormalizer` 做归一化
- [ ] 输出是否是候选计划点，而不是伪装成最终落库结果

---

## 7. 多任务统一编排检查

检查点：

- [ ] 是否先汇总所有候选计划点再统一求解
- [ ] 是否存在 `TaskSchedulingBatchContext`
- [ ] 是否引入时间冲突检测器
- [ ] 是否引入资源冲突检测器
- [ ] 是否通过统一 solver 决定落位
- [ ] 是否能输出已排 / 未排 / 冲突 / warning 分类结果

---

## 8. 计划点落库检查

检查点：

- [ ] 是否使用 `TaskSchedulePersistenceService`
- [ ] 是否写入计划点主表
- [ ] 是否写入资源分配表
- [ ] 是否写入冲突明细表
- [ ] 是否保留理论时间与最终时间
- [ ] 是否保留自动调整 / 人工确认 / 锁定等状态

---

## 9. 局部重排检查

检查点：

- [ ] 是否定义 `RescheduleCommand`
- [ ] 是否通过 `AffectedTaskScheduleLocator` 定位受影响计划点
- [ ] 是否保护已锁定 / 已执行 / 已确认计划点
- [ ] 是否只对受影响集合做局部求解
- [ ] 是否记录重排批次与变化明细

---

## 10. SQL 对齐检查

检查点：

- [ ] 是否对齐 `inspection_task_schedule`
- [ ] 是否对齐 `inspection_task_schedule_resource`
- [ ] 是否对齐 `inspection_task_schedule_conflict`
- [ ] 是否对齐 `inspection_task_reschedule_batch`
- [ ] 是否对齐 `inspection_task_reschedule_change`
- [ ] 是否不再新增新的 `inspection_task_occurrence*` 命名

---

## 11. Controller / VO 检查

检查点：

- [ ] Controller 只做输入接收与结果返回
- [ ] 不在 Controller 中写调度算法
- [ ] VO 字段名是否与领域语义一致
- [ ] 预览接口返回的是 candidate 还是 schedule item，是否命名清楚

---

## 12. 最终验收清单

### 12.1 术语一致性

- [ ] Task / Profile / Rule / Candidate / Schedule / Execution / Reschedule 边界清楚
- [ ] 文档与代码术语一致
- [ ] 不再把 occurrence 同时表示候选点与计划点

### 12.2 结构一致性

- [ ] 单任务试算链路清楚
- [ ] 批量编排链路清楚
- [ ] 落库链路清楚
- [ ] 局部重排链路清楚

### 12.3 数据一致性

- [ ] 任务主记录不裂变
- [ ] 未来多次安排进入计划点表
- [ ] 执行结果进入执行表
- [ ] 重排变化单独留痕

---

## 13. 一句话总结

代码建设必须完整体现：

**任务由主记录承载，排期方案与规则决定未来如何安排，调度系统先生成候选计划点，再求解成最终计划点，执行结果单独记录，重排只作用于受影响计划点。**
