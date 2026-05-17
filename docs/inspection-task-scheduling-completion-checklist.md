# 智能调度开发完成度对照清单

## 1. 总体结论

当前代码已经完成了**单任务排期试算的最小骨架**，包括：

- controller 入口
- request 基础校验
- 调度上下文构建
- 单任务试算引擎
- 规则结构化转换
- candidate 候选计划点生成
- candidate 结果归一化
- preview 响应转换

但距离设计文档中的“智能调度功能”仍有明显差距。当前实现更准确地说是：

> **单任务 candidate 预览原型已完成，多任务统一编排 / 冲突求解 / 重排 / 执行记录链路尚未完成。**

---

## 2. 按设计分层的完成度对照

### 2.1 controller / service 入口层

#### 已完成
- [x] `InspectionTaskScheduleController`
- [x] `preview-single` 单任务试算接口
- [x] `InspectionTaskScheduleService.previewSingleTask(...)`
- [x] `SchedulePreviewRequestValidator` 请求范围校验

#### 未完成
- [ ] 多任务统一编排接口
- [ ] 局部重排接口
- [ ] 调度结果确认 / 落库接口
- [ ] 冲突查询接口
- [ ] 调度批次查询接口

#### 当前判断
当前只覆盖了“单任务预览”，未进入真正的调度作业流程。

---

### 2.2 context 上下文层

#### 已完成
- [x] `TaskSchedulingContext`
- [x] `TaskSchedulingContextBuilder`
- [x] 从任务查询有效 profile
- [x] 从 profile 查询 rules
- [x] 读取 effective resource policy

#### 未完成
- [ ] `TaskSchedulingBatchContext`
- [ ] 多任务统一上下文聚合
- [ ] 已存在计划点装载
- [ ] 已锁定计划点装载
- [ ] 外部资源占用装载
- [ ] 机器人实时可用性装载

#### 当前判断
当前 context 只够支撑**单任务试算**，还不足以支撑真正的“统一编排”。

---

### 2.3 validator 层

#### 已完成
- [x] `SchedulePreviewRequestValidator`
- [x] `TaskSchedulingContextValidator`
- [x] 基础存在性 / 是否有巡检内容 / 是否有生效方案 / 是否有启用规则 校验

#### 未完成
- [ ] `ScheduleRuleValidator`
- [ ] `ScheduleWindowValidator`
- [ ] `ScheduleResourceValidator`
- [ ] rule 级别字段组合校验
- [ ] `schedulePattern` 与时间字段匹配校验
- [ ] `requiredSchedulesPerCycle` 约束校验
- [ ] `weekDays` / `monthDays` / `timePoints` 空值与合法值校验
- [ ] 时间窗交叉、开始结束顺序校验
- [ ] 资源策略与固定机器人 / 资源池组合校验

#### 当前判断
validator 目前只做了**请求级和上下文级校验**，设计文档要求的 rule/window/resource 三类校验尚未落地。

---

### 2.4 parser / rule 结构化层

#### 已完成
- [x] `ScheduleRuleParser`
- [x] `InspectionTaskScheduleRuleDO -> StructuredScheduleRule` 基本转换

#### 未完成
- [ ] parser 口径收敛后的命名复核
- [ ] 彻底消除旧字段名残留
- [ ] parse 后的规则合法性联动校验

#### 当前判断
结构化转换已存在，但仍有**旧命名残留**，且未与 validator 串联形成完整防线。

---

### 2.5 candidate 候选计划点生成层

#### 已完成
- [x] `InspectionTaskScheduleCandidateGenerator`
- [x] `TaskScheduleCandidateGenerationStrategy`
- [x] `DailyScheduleCandidateStrategy`
- [x] `WeeklyScheduleCandidateStrategy`
- [x] `MonthlyScheduleCandidateStrategy`
- [x] `FixedPointScheduleCandidateStrategy`
- [x] `SingleTaskScheduleCandidateNormalizer`
- [x] `TaskScheduleCandidate`

#### 未完成
- [ ] `CustomIntervalScheduleCandidateStrategy`
- [ ] `TimeWindowScheduleCandidateStrategy`
- [ ] `scheduleMode` / `schedulePattern` 双维度分派
- [ ] 每周期多次生成能力（`requiredSchedulesPerCycle`）
- [ ] `plannedEndTime` 推导
- [ ] 根据最小/最大间隔生成多候选点
- [ ] preferred window 真正参与生成而非仅取开始时间
- [ ] forbidden window 与 preferred window 冲突告警
- [ ] 无匹配策略时的显式告警

#### 当前判断
candidate 层已经成型，但还只是**简化版候选点展开器**，离设计里的完整规则求值还有较大差距。

---

### 2.6 单任务试算结果层

#### 已完成
- [x] `SingleTaskSchedulePlan`
- [x] `TaskScheduleCandidatePreviewRespVO`
- [x] `SingleTaskSchedulePreviewRespVO`
- [x] `InspectionTaskSchedulePreviewConvert`

#### 未完成
- [ ] 更丰富的 warning / explain 信息
- [ ] rule 级告警明细
- [ ] 候选点不可生成原因
- [ ] 时间窗命中说明
- [ ] 资源约束提示

#### 当前判断
结果对象能返回列表，但解释性输出还比较弱。

---

### 2.7 solver / 多任务统一编排层

#### 已完成
- [ ] 无

#### 未完成
- [ ] `SchedulingCandidatePrioritySorter`
- [ ] `InspectionTaskBatchScheduler`
- [ ] `BatchSchedulingResult`
- [ ] 全局冲突检测
- [ ] 统一待排池排序
- [ ] 资源冲突判定
- [ ] 未排候选回收
- [ ] 调度决策解释

#### 当前判断
这是当前最核心的缺口，**多任务算法实际上还没有开始实现**。

---

### 2.8 重排 / reschedule 层

#### 已完成
- [ ] 无

#### 未完成
- [ ] `RescheduleResult`
- [ ] 受影响计划点识别
- [ ] 锁定计划点保护
- [ ] 局部重排上下文
- [ ] 重排批次与明细输出

#### 当前判断
重排仍停留在设计阶段。

---

### 2.9 落库 DO / 执行记录层

#### 已完成
- [x] `InspectionTaskScheduleProfileDO`
- [x] `InspectionTaskScheduleRuleDO`
- [x] PostgreSQL 初版 SQL 中存在 `inspection_task_schedule` / `inspection_task_execution` 表结构

#### 未完成
- [ ] `InspectionTaskScheduleDO`
- [ ] `InspectionTaskExecutionDO` / `TaskExecutionRecord` 对应实现
- [ ] 计划点 Mapper / Service
- [ ] 执行记录 Mapper / Service
- [ ] 调度批次 DO
- [ ] 冲突 DO
- [ ] 重排批次 / 明细 DO

#### 当前判断
数据库设计有基础，但 Java 落库对象还没有补齐。

---

## 3. 当前代码里的具体问题清单

## 3.1 命名残留问题

### 问题 1：`ScheduleRuleParser` 内仍然残留旧字段名调用
- 文件：`service/schedule/parser/ScheduleRuleParser.java`
- 现状：
  - `rule.setRequiredOccurrencesPerCycle(ruleDO.getRequiredOccurrencesPerCycle());`
- 问题：
  - 当前结构化模型已使用 `requiredSchedulesPerCycle`
  - 这里仍残留 `Occurrences` 口径，说明命名迁移没有清干净
- 风险：
  - 直接导致编译错误或字段错配
- 优先级：**P0**

### 问题 2：`parser` 命名是否仍过重
- 文件：`service/schedule/parser/ScheduleRuleParser.java`
- 现状：当前类并不解析 JSON，而是做 DO -> StructuredRule 字段搬运
- 问题：`Parser` 语义偏“解析字符串/协议”，当前更接近 assembler/mapper/transformer
- 建议：
  - 若团队希望突出“结构化转换”，可评估改为 `ScheduleRuleAssembler` 或 `StructuredScheduleRuleAssembler`
  - 如果阶段性先不动，也至少要保持内部注释准确
- 优先级：**P2**

---

## 3.2 candidate 策略实现漏洞

### 问题 3：策略分派只按“第一个 supports 命中”处理，缺少双维度判定
- 文件：`InspectionTaskScheduleCandidateGenerator.java`
- 现状：
  - 遍历 `strategies.stream().filter(candidate -> candidate.supports(rule)).findFirst()`
- 问题：
  - 当前 `supports` 判断比较粗
  - `FixedPointScheduleCandidateStrategy` 只要 `timePoints` 非空就会命中
  - 若后续存在 `DAY + FIXED_POINT`、`DAY + TIME_WINDOW`、`CUSTOM_INTERVAL + FIXED_POINT` 等组合，单一 `supports` 很容易抢占错误
- 风险：
  - `scheduleMode` 与 `schedulePattern` 组合扩展时会出现错误策略被选中
- 优先级：**P0**

### 问题 4：还没有 `CustomIntervalScheduleCandidateStrategy`
- 影响：设计里明确支持“自定义间隔”，当前未实现
- 风险：规则能力不完整
- 优先级：**P1**

### 问题 5：还没有 `TimeWindowScheduleCandidateStrategy`
- 影响：设计里支持时间窗，但当前没有真正基于时间窗产出 candidate
- 风险：`schedulePattern=TIME_WINDOW` 无法成立
- 优先级：**P1**

### 问题 6：`requiredSchedulesPerCycle` 完全未参与生成
- 文件：`Daily/Weekly/Monthly/FixedPoint...Strategy`
- 现状：每个周期通常只生成 1 个点，或按 `timePoints` 直接铺开
- 问题：
  - “每周期要求调度次数”是核心业务字段，但当前算法未用
- 风险：字段存在但无业务效果，属于假实现
- 优先级：**P0**

### 问题 7：`plannedEndTime` 没有被赋值
- 文件：`AbstractTaskScheduleCandidateGenerationStrategy.java`
- 现状：只设置了 `plannedStartTime`
- 问题：后续冲突检测、资源占用、时间重叠判断都依赖结束时间
- 风险：后续 solver 无法建立正确时间区间模型
- 优先级：**P0**

### 问题 8：preferred window 只取第一个开始时间，语义过弱
- 文件：`AbstractTaskScheduleCandidateGenerationStrategy.resolvePreferredOrDefaultTime(...)`
- 现状：
  - 只取第一个 preferred window 的 startTime
- 问题：
  - 这不是“时间窗调度”，只是“取一个固定时间点”
- 风险：用户会误以为时间窗已实现
- 优先级：**P1**

### 问题 9：禁止窗判断只校验时间点，不校验时间段
- 文件：`AbstractTaskScheduleCandidateGenerationStrategy.isForbidden(...)`
- 现状：只判断单个 `LocalTime` 是否落入禁止窗
- 问题：
  - 如果后续 candidate 有持续时长，仅校验开始点不够
- 优先级：**P1**

### 问题 10：按 ruleCode + plannedStartTime 去重，规则过于粗糙
- 文件：`SingleTaskScheduleCandidateNormalizer.java`
- 问题：
  - 若同规则同一时间有不同资源策略 / 不同候选来源，可能被误去重
  - 当前去重键没有 taskId/profileId/sourcePattern/endTime
- 优先级：**P1**

---

## 3.3 validator 缺失问题

### 问题 11：缺少 `ScheduleRuleValidator`
- 影响：规则字段组合完全未系统校验
- 典型缺项：
  - `schedulePattern` 与 `timePoints/preferredTimeWindows` 是否匹配
  - `cycleStep` 是否合法
  - `anchorDate` 是否必须
  - `requiredSchedulesPerCycle` 是否 >= 1
- 优先级：**P0**

### 问题 12：缺少 `ScheduleWindowValidator`
- 影响：时间窗是否交叉、开始结束是否正确、禁止窗与偏好窗是否重叠都没校验
- 优先级：**P1**

### 问题 13：缺少 `ScheduleResourceValidator`
- 影响：
  - `resourceStrategy`
  - `fixedRobotId`
  - `requiredRobotCount`
  - `allowResourceSwitch`
  - `resourcePool`
 之间没有组合校验
- 优先级：**P1**

### 问题 14：`TaskSchedulingContextValidator` 只做存在性校验，没有规则内容校验
- 现状：只校验任务、内容、profile、rule 是否存在/启用
- 问题：无效规则仍可能进入 candidate 层
- 优先级：**P1**

---

## 3.4 SQL / DO / 代码口径不一致问题

### 问题 15：PostgreSQL SQL 与当前 Java 命名口径不一致
- 文件：`V1__init_inspection_task_schema.sql`
- 典型问题：
  - `schedule_scheme_id`
  - `scheduling_pattern`
  - `required_schedules_per_cycle`
  - `inspection_schedule_plan`
- 问题：与当前 Java 中的 `profileId`、`schedulePattern`、`requiredSchedulesPerCycle` 等口径不一致
- 风险：后续真正落库时会产生映射混乱
- 优先级：**P0**

### 问题 16：Java 侧缺少 `InspectionTaskScheduleDO` / `InspectionTaskExecutionDO`
- 影响：当前只有 profile/rule 侧 DO，调度结果层 DO 尚未补齐
- 优先级：**P1**

---

## 3.5 功能边界问题

### 问题 17：当前返回的是 candidate 预览，不是最终 schedule 预览
- 文件：
  - `SingleTaskSchedulePlan`
  - `SingleTaskSchedulePreviewRespVO`
  - `TaskScheduleCandidatePreviewRespVO`
- 现状：命名已经比较清楚，但要继续保证前后端不会把它误解为“最终调度结果”
- 建议：
  - controller 注释、swagger 描述里明确“候选计划点预览”
- 优先级：**P2**

### 问题 18：单任务引擎还不是 solver，只是 candidate preview
- 文件：`InspectionSingleTaskScheduler`
- 现状：方法名叫 scheduler，但内部还没做冲突求解和资源分配
- 问题：类名会给人“已经是调度器”的错觉
- 建议：
  - 若短期内继续只做预览，可考虑命名更贴近 preview/generator orchestration
  - 或保留类名但在注释里明确“当前阶段仅做候选展开”
- 优先级：**P2**

---

## 4. 建议的下一步开发顺序

### 第一批：先补齐能影响正确性的 P0
- [ ] 修正 `requiredOccurrencesPerCycle` 残留为 `requiredSchedulesPerCycle`
- [ ] 增加 `ScheduleRuleValidator`
- [ ] 重构 candidate strategy 分派机制，按 `scheduleMode + schedulePattern` 明确分派
- [ ] 让 `requiredSchedulesPerCycle` 真正参与 candidate 生成
- [ ] 给 candidate 补齐 `plannedEndTime`
- [ ] 对齐 PostgreSQL SQL 与 Java 当前命名口径

### 第二批：补齐单任务试算能力
- [ ] 增加 `ScheduleWindowValidator`
- [ ] 增加 `ScheduleResourceValidator`
- [ ] 实现 `CustomIntervalScheduleCandidateStrategy`
- [ ] 实现 `TimeWindowScheduleCandidateStrategy`
- [ ] 优化 warning / explain 输出

### 第三批：进入真正的智能调度
- [ ] 设计并实现 `TaskSchedulingBatchContext`
- [ ] 实现 `SchedulingCandidatePrioritySorter`
- [ ] 实现多任务统一待排池
- [ ] 实现冲突检测与资源分配
- [ ] 输出 `BatchSchedulingResult`

### 第四批：补齐重排与执行闭环
- [ ] `InspectionTaskScheduleDO`
- [ ] `TaskExecutionRecord` / `InspectionTaskExecutionDO`
- [ ] 重排批次 / 变更明细
- [ ] 局部重排流程

---

## 5. 当前阶段结论

如果只看“单任务排期预览骨架”，当前完成度可以评估为：

- **接口骨架：80%**
- **单任务 candidate 生成骨架：60%**
- **规则校验完整度：25%**
- **多任务统一编排：0%**
- **重排：0%**
- **执行闭环：10%**

如果按“智能调度整体方案”衡量，当前整体完成度大致处于：

> **20% ~ 30%：已完成基础建模与单任务试算原型，尚未进入真正的统一调度与重排实现阶段。**
