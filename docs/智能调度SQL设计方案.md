# 智能调度 SQL 设计方案

## 1. 设计目标

智能调度落库模型要解决四件事：

1. 保存任务定义与排期规则之外的“未来执行计划”
2. 支持单任务试算、多任务统一编排、故障后局部重排
3. 支持时间冲突、资源冲突、锁定保护、人工确认
4. 保证用户看到的仍是一条长期任务，但排期表上可以看到多条计划点

因此，调度域建议新增以下几类表：

- 计划主表
- 计划资源分配表
- 冲突明细表
- 重排批次表
- 重排影响明细表
- 外部占用表
- 资源日历 / 可用性表

---

## 2. 表清单总览

### 2.1 核心表

1. `inspection_task_schedule`
   - 巡检任务计划点主表
2. `inspection_task_schedule_resource`
   - 计划点资源分配表
3. `inspection_task_schedule_conflict`
   - 计划点冲突明细表
4. `inspection_task_reschedule_batch`
   - 一次重排操作的批次头
5. `inspection_task_reschedule_change`
   - 一次重排中每条计划点的变化明细

### 2.2 支撑表

6. `inspection_resource_calendar`
   - 资源可用性日历
7. `inspection_external_occupation`
   - 外部任务 / 外部业务占用表
8. `inspection_schedule_lock_record`
   - 锁定记录表

---

# 3. 核心表设计

## 3.1 inspection_task_schedule

表示某个任务在某个时间范围内生成的一条具体计划点。

### 3.1.1 建表建议

```sql
CREATE TABLE inspection_task_schedule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户 ID',

    schedule_code VARCHAR(64) NOT NULL COMMENT '计划点编码',

    task_id BIGINT NOT NULL COMMENT '任务 ID',
    task_code VARCHAR(64) NOT NULL COMMENT '任务编码冗余',
    task_name VARCHAR(128) NOT NULL COMMENT '任务名称冗余',
    parent_task_id BIGINT NULL COMMENT '父任务 ID',
    site_id BIGINT NOT NULL COMMENT '站点 ID',
    category_id BIGINT NULL COMMENT '任务分类 ID',

    schedule_profile_id BIGINT NOT NULL COMMENT '生效排期方案 ID',
    schedule_rule_id BIGINT NOT NULL COMMENT '命中的排期规则 ID',

    scheduled_date DATE NOT NULL COMMENT '计划日期',
    planned_start_time DATETIME NOT NULL COMMENT '计划开始时间',
    planned_end_time DATETIME NOT NULL COMMENT '计划结束时间',

    original_start_time DATETIME NOT NULL COMMENT '原始理想开始时间',
    original_end_time DATETIME NOT NULL COMMENT '原始理想结束时间',

    estimate_duration_minutes INT NULL COMMENT '预计耗时分钟数',
    estimate_distance DECIMAL(18,2) NULL COMMENT '预计里程',
    estimate_battery_consumption DECIMAL(18,2) NULL COMMENT '预计耗电量',

    source_pattern VARCHAR(64) NULL COMMENT '来源规则模式',
    priority INT NULL COMMENT '优先级',
    conflict_strategy INT NULL COMMENT '冲突策略',
    resource_strategy INT NULL COMMENT '资源策略',

    schedule_status INT NOT NULL COMMENT '计划状态',
    execution_status INT NOT NULL COMMENT '执行状态',
    lock_status INT NOT NULL COMMENT '锁定状态',

    adjustment_type INT NULL COMMENT '调整类型',
    adjustment_reason VARCHAR(500) NULL COMMENT '调整原因',
    auto_adjusted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否自动调整过',

    scheduling_batch_no VARCHAR(64) NULL COMMENT '本次编排批次号',
    reschedule_version INT NOT NULL DEFAULT 0 COMMENT '重排版本号',

    manual_confirm_required BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否需要人工确认',
    manual_confirmed BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已人工确认',
    manual_confirm_by BIGINT NULL COMMENT '人工确认人',
    manual_confirm_time DATETIME NULL COMMENT '人工确认时间',

    task_snapshot JSON NULL COMMENT '任务快照',
    rule_snapshot JSON NULL COMMENT '规则快照',
    resource_policy_snapshot JSON NULL COMMENT '资源策略快照',

    remark VARCHAR(500) NULL COMMENT '备注',

    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT NOT NULL DEFAULT b'0' COMMENT '是否删除',

    UNIQUE KEY uk_schedule_code (schedule_code),
    KEY idx_task_date (task_id, scheduled_date),
    KEY idx_site_time (site_id, planned_start_time, planned_end_time),
    KEY idx_profile_rule (schedule_profile_id, schedule_rule_id),
    KEY idx_batch (scheduling_batch_no),
    KEY idx_status (schedule_status, execution_status, lock_status)
) COMMENT='巡检任务计划点表';
```

### 3.1.2 字段说明

#### 身份与归属
- `task_id`：来源任务
- `schedule_profile_id`：本次计划命中的排期方案
- `schedule_rule_id`：本次计划命中的具体规则

#### 时间相关
- `planned_start_time` / `planned_end_time`：当前生效计划时间
- `original_start_time` / `original_end_time`：初始理想时间

这样可以对比：
- 是否被顺延 / 提前
- 偏移了多少

#### 状态相关
建议拆成三类状态，而不是一个字段全包：

- `schedule_status`：计划层状态
  - 待排
  - 已排
  - 冲突待确认
  - 已取消
- `execution_status`：执行层状态
  - 未开始
  - 执行中
  - 已完成
  - 执行失败
- `lock_status`：是否允许再动
  - 未锁定
  - 系统锁定
  - 人工锁定

#### 快照字段
快照保留是有价值的，因为计划点一旦生成，后续任务定义可能被改掉。

- `task_snapshot`：生成时任务内容快照
- `rule_snapshot`：生成时规则快照
- `resource_policy_snapshot`：生成时资源策略快照

这些快照用于“追溯计划生成依据”，不是页面实时展示来源。

---

## 3.2 inspection_task_schedule_resource

一条计划点可能分配多个机器人，因此不要把资源字段硬塞进主表。

```sql
CREATE TABLE inspection_task_schedule_resource (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户 ID',

    schedule_id BIGINT NOT NULL COMMENT '计划点 ID',
    resource_type VARCHAR(32) NOT NULL COMMENT '资源类型',
    resource_id BIGINT NOT NULL COMMENT '资源 ID',
    resource_code VARCHAR(64) NULL COMMENT '资源编码',
    resource_name VARCHAR(128) NULL COMMENT '资源名称',

    assign_type INT NOT NULL COMMENT '分配类型',
    is_primary BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否主资源',

    assign_start_time DATETIME NOT NULL COMMENT '分配开始时间',
    assign_end_time DATETIME NOT NULL COMMENT '分配结束时间',

    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT NOT NULL DEFAULT b'0' COMMENT '是否删除',

    KEY idx_schedule (schedule_id),
    KEY idx_resource_time (resource_id, assign_start_time, assign_end_time)
) COMMENT='巡检任务计划点资源分配表';
```

### 分配类型建议
- 固定分配
- 资源池自动分配
- 人工指定
- 重排替换

---

## 3.3 inspection_task_schedule_conflict

记录某个计划点在编排过程中遇到的冲突。

```sql
CREATE TABLE inspection_task_schedule_conflict (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户 ID',

    schedule_id BIGINT NOT NULL COMMENT '当前计划点 ID',
    blocking_schedule_id BIGINT NULL COMMENT '阻塞计划点 ID',
    blocking_external_id BIGINT NULL COMMENT '阻塞外部占用 ID',

    conflict_type INT NOT NULL COMMENT '冲突类型',
    resolvable BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否可自动解决',
    resolution_type INT NULL COMMENT '解决方案类型',

    detail_message VARCHAR(1000) NULL COMMENT '冲突明细',
    suggestion_message VARCHAR(1000) NULL COMMENT '建议说明',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted BIT NOT NULL DEFAULT b'0' COMMENT '是否删除',

    KEY idx_schedule (schedule_id),
    KEY idx_blocking_schedule (blocking_schedule_id)
) COMMENT='巡检任务计划点冲突明细表';
```

### 冲突类型建议
- 时间冲突
- 资源冲突
- 锁定冲突
- 外部业务冲突
- 业务先后约束冲突

---

## 3.4 inspection_task_reschedule_batch

表示一次“重排操作”的头记录。

```sql
CREATE TABLE inspection_task_reschedule_batch (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户 ID',

    batch_no VARCHAR(64) NOT NULL COMMENT '重排批次号',
    site_id BIGINT NOT NULL COMMENT '站点 ID',

    trigger_type INT NOT NULL COMMENT '触发类型',
    trigger_source_id BIGINT NULL COMMENT '触发源 ID',
    trigger_reason VARCHAR(500) NULL COMMENT '触发原因',

    scope_type INT NOT NULL COMMENT '重排范围类型',
    scope_json JSON NULL COMMENT '重排范围',

    total_candidate_count INT NOT NULL DEFAULT 0 COMMENT '参与重排候选数',
    rescheduled_count INT NOT NULL DEFAULT 0 COMMENT '成功重排数',
    unchanged_count INT NOT NULL DEFAULT 0 COMMENT '未变化数',
    failed_count INT NOT NULL DEFAULT 0 COMMENT '失败数',

    status INT NOT NULL COMMENT '批次状态',

    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT NOT NULL DEFAULT b'0' COMMENT '是否删除',

    UNIQUE KEY uk_batch_no (batch_no),
    KEY idx_site_time (site_id, create_time)
) COMMENT='巡检任务重排批次表';
```

### 触发类型建议
- 新增任务统一排程
- 手工发起批量排程
- 设备故障
- 机器人故障
- 外部任务插入
- 实际执行延迟

---

## 3.5 inspection_task_reschedule_change

记录一次重排中，每条计划点发生了什么变化。

```sql
CREATE TABLE inspection_task_reschedule_change (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户 ID',

    batch_id BIGINT NOT NULL COMMENT '重排批次 ID',
    schedule_id BIGINT NOT NULL COMMENT '计划点 ID',

    before_start_time DATETIME NULL COMMENT '重排前开始时间',
    before_end_time DATETIME NULL COMMENT '重排前结束时间',
    after_start_time DATETIME NULL COMMENT '重排后开始时间',
    after_end_time DATETIME NULL COMMENT '重排后结束时间',

    before_resource_json JSON NULL COMMENT '重排前资源',
    after_resource_json JSON NULL COMMENT '重排后资源',

    change_type INT NOT NULL COMMENT '变化类型',
    change_reason VARCHAR(500) NULL COMMENT '变化原因',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted BIT NOT NULL DEFAULT b'0' COMMENT '是否删除',

    KEY idx_batch (batch_id),
    KEY idx_schedule (schedule_id)
) COMMENT='巡检任务重排变化明细表';
```

### 变化类型建议
- 时间顺延
- 时间提前
- 更换资源
- 从未排到已排
- 从已排到未排
- 保持不变

---

## 4. 支撑表设计

### 4.1 inspection_resource_calendar

表示资源在某时间段是否可用。

```sql
CREATE TABLE inspection_resource_calendar (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户 ID',

    resource_type VARCHAR(32) NOT NULL COMMENT '资源类型',
    resource_id BIGINT NOT NULL COMMENT '资源 ID',

    available_date DATE NOT NULL COMMENT '日期',
    available_windows JSON NULL COMMENT '可用时间窗集合',
    unavailable_windows JSON NULL COMMENT '不可用时间窗集合',

    status INT NOT NULL COMMENT '资源状态',
    reason VARCHAR(500) NULL COMMENT '状态原因',

    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT NOT NULL DEFAULT b'0' COMMENT '是否删除',

    KEY idx_resource_date (resource_id, available_date)
) COMMENT='巡检资源可用性日历表';
```

---

### 4.2 inspection_external_occupation

表示外部业务对时间 / 资源的占用，例如别的系统任务、维护窗口等。

```sql
CREATE TABLE inspection_external_occupation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户 ID',

    site_id BIGINT NOT NULL COMMENT '站点 ID',
    business_type VARCHAR(64) NOT NULL COMMENT '业务类型',
    business_id BIGINT NULL COMMENT '业务 ID',
    business_code VARCHAR(64) NULL COMMENT '业务编码',
    business_name VARCHAR(128) NULL COMMENT '业务名称',

    occupation_type INT NOT NULL COMMENT '占用类型',
    start_time DATETIME NOT NULL COMMENT '占用开始时间',
    end_time DATETIME NOT NULL COMMENT '占用结束时间',

    resource_type VARCHAR(32) NULL COMMENT '资源类型',
    resource_id BIGINT NULL COMMENT '资源 ID',

    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否生效',
    remark VARCHAR(500) NULL COMMENT '备注',

    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT NOT NULL DEFAULT b'0' COMMENT '是否删除',

    KEY idx_site_time (site_id, start_time, end_time),
    KEY idx_resource_time (resource_id, start_time, end_time)
) COMMENT='外部业务占用表';
```

---

### 4.3 inspection_schedule_lock_record

记录谁、何时、为何锁定了某条计划点。

```sql
CREATE TABLE inspection_schedule_lock_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户 ID',

    schedule_id BIGINT NOT NULL COMMENT '计划点 ID',
    lock_type INT NOT NULL COMMENT '锁定类型',
    lock_reason VARCHAR(500) NULL COMMENT '锁定原因',
    locked_by BIGINT NULL COMMENT '锁定人',
    locked_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '锁定时间',
    unlock_time DATETIME NULL COMMENT '解锁时间',
    unlocked_by BIGINT NULL COMMENT '解锁人',

    deleted BIT NOT NULL DEFAULT b'0' COMMENT '是否删除',
    KEY idx_schedule (schedule_id)
) COMMENT='计划锁定记录表';
```

---

## 5. 状态枚举建议

### 5.1 schedule_status
- 1 待排
- 2 已排
- 3 冲突待确认
- 4 已取消
- 5 已失效

### 5.2 execution_status
- 1 未开始
- 2 执行中
- 3 已完成
- 4 执行失败
- 5 已跳过

### 5.3 lock_status
- 1 未锁定
- 2 系统锁定
- 3 人工锁定

### 5.4 adjustment_type
- 1 无调整
- 2 自动顺延
- 3 自动提前
- 4 自动换资源
- 5 人工调整
- 6 故障重排

---

## 6. 表之间关系

### 6.1 主关系
- `inspection_task` 1 -> N `inspection_task_schedule`
- `inspection_task_schedule` 1 -> N `inspection_task_schedule_resource`
- `inspection_task_schedule` 1 -> N `inspection_task_schedule_conflict`
- `inspection_task_reschedule_batch` 1 -> N `inspection_task_reschedule_change`

### 6.2 关联关系
- `inspection_task_schedule.schedule_profile_id` -> `inspection_task_schedule_profile.id`
- `inspection_task_schedule.schedule_rule_id` -> `inspection_task_schedule_rule.id`

---

## 7. 为什么不直接把所有字段放一张表

因为智能调度不是单一实体，而是：

- 计划点
- 资源分配
- 冲突过程
- 重排历史

这些数据有不同生命周期：

- 计划点长期存在
- 冲突可能多次发生
- 重排变化需要留痕
- 资源分配可能一对多

如果全塞进一张表：
- 字段会极度膨胀
- 多资源分配无法优雅表达
- 重排前后变化难追踪
- 后续统计困难

---

## 8. 与当前任务 / 排期模型的衔接

### 8.1 任务定义侧
- `InspectionTaskDO`：长期任务定义
- `InspectionTaskScheduleProfileDO`：排期方案头
- `InspectionTaskScheduleRuleDO`：排期规则明细

### 8.2 智能调度侧
- `inspection_task_schedule`：生成后的计划点
- `inspection_task_schedule_resource`：计划点资源分配
- `inspection_task_schedule_conflict`：冲突明细
- `inspection_task_reschedule_batch` / `inspection_task_reschedule_change`：重排留痕

这套结构正好把：
- 定义
- 规则
- 计划
- 重排

分清楚。

---

## 9. 落地顺序建议

### 第一阶段
先落：
- `inspection_task_schedule`
- `inspection_task_schedule_resource`

满足：
- 单任务试算
- 批量排期结果落库

### 第二阶段
再落：
- `inspection_task_schedule_conflict`
- `inspection_schedule_lock_record`

满足：
- 冲突展示
- 锁定保护

### 第三阶段
最后落：
- `inspection_task_reschedule_batch`
- `inspection_task_reschedule_change`
- `inspection_external_occupation`
- `inspection_resource_calendar`

满足：
- 重排追踪
- 故障重排
- 外部占用协同

---

## 10. 一句话总结

智能调度 SQL 设计的核心，不是把任务裂变成很多任务，而是：

**在长期任务定义之外，引入独立的“计划点 + 资源分配 + 冲突 + 重排历史”模型，承载未来执行计划与动态调度结果。**
