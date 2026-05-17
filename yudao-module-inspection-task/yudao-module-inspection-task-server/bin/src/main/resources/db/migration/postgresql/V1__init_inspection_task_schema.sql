-- 巡检任务域重构版初始化表结构
-- 模块：yudao-module-inspection-task
-- 数据库：PostgreSQL

CREATE TABLE inspection_task (
    id BIGINT PRIMARY KEY,
    task_code VARCHAR(64) NOT NULL,
    task_name VARCHAR(128) NOT NULL,
    site_id BIGINT NOT NULL,
    status INT4 NOT NULL,
    task_type INT4,
    inspection_type INT4,
    template_id BIGINT,
    default_robot_pool_id BIGINT,
    resource_policy_id BIGINT,
    route_id VARCHAR(64),
    route_description VARCHAR(512),
    estimate_distance BIGINT,
    estimate_process_time INT4,
    estimate_battery_consumption INT4,
    remark VARCHAR(512),
    creator VARCHAR(64),
    create_time TIMESTAMP NOT NULL,
    updater VARCHAR(64),
    update_time TIMESTAMP NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE inspection_task IS '巡检任务主表';
COMMENT ON COLUMN inspection_task.task_code IS '任务编码';
COMMENT ON COLUMN inspection_task.task_name IS '任务名称';
COMMENT ON COLUMN inspection_task.site_id IS '站场ID';
COMMENT ON COLUMN inspection_task.status IS '主任务状态';
COMMENT ON COLUMN inspection_task.task_type IS '任务类型';
COMMENT ON COLUMN inspection_task.inspection_type IS '巡检类型';
COMMENT ON COLUMN inspection_task.template_id IS '来源模板ID';
COMMENT ON COLUMN inspection_task.default_robot_pool_id IS '默认机器人资源池ID';
COMMENT ON COLUMN inspection_task.resource_policy_id IS '资源策略ID';
COMMENT ON COLUMN inspection_task.route_id IS '路线ID';
COMMENT ON COLUMN inspection_task.route_description IS '路线说明';
COMMENT ON COLUMN inspection_task.estimate_distance IS '预估距离（米）';
COMMENT ON COLUMN inspection_task.estimate_process_time IS '预估耗时（分钟）';
COMMENT ON COLUMN inspection_task.estimate_battery_consumption IS '预估耗电量';
COMMENT ON COLUMN inspection_task.remark IS '备注';

CREATE UNIQUE INDEX uk_inspection_task_code ON inspection_task(task_code);
CREATE INDEX idx_inspection_task_site_status ON inspection_task(site_id, status);
CREATE INDEX idx_inspection_task_template_id ON inspection_task(template_id);

CREATE TABLE inspection_task_template (
    id BIGINT PRIMARY KEY,
    template_code VARCHAR(64) NOT NULL,
    template_name VARCHAR(128) NOT NULL,
    site_id BIGINT NOT NULL,
    status INT4 NOT NULL,
    task_type INT4,
    inspection_type INT4,
    route_id VARCHAR(64),
    route_description VARCHAR(512),
    inspection_target_json TEXT,
    inspection_object_json TEXT,
    resource_pool_json TEXT,
    estimate_distance BIGINT,
    estimate_process_time INT4,
    estimate_battery_consumption INT4,
    remark VARCHAR(512),
    creator VARCHAR(64),
    create_time TIMESTAMP NOT NULL,
    updater VARCHAR(64),
    update_time TIMESTAMP NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE inspection_task_template IS '巡检任务模板表';
COMMENT ON COLUMN inspection_task_template.template_code IS '模板编码';
COMMENT ON COLUMN inspection_task_template.template_name IS '模板名称';
COMMENT ON COLUMN inspection_task_template.inspection_target_json IS '任务目标预设JSON';
COMMENT ON COLUMN inspection_task_template.inspection_object_json IS '巡检对象组合预设JSON';
COMMENT ON COLUMN inspection_task_template.resource_pool_json IS '资源池预设JSON';

CREATE UNIQUE INDEX uk_inspection_task_template_code ON inspection_task_template(template_code);
CREATE INDEX idx_inspection_task_template_site_status ON inspection_task_template(site_id, status);

CREATE TABLE inspection_task_target_rel (
    id BIGINT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    target_type INT4 NOT NULL,
    target_id BIGINT NOT NULL,
    sort_no INT4,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    creator VARCHAR(64),
    create_time TIMESTAMP NOT NULL,
    updater VARCHAR(64),
    update_time TIMESTAMP NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE inspection_task_target_rel IS '任务目标关联表';
COMMENT ON COLUMN inspection_task_target_rel.task_id IS '任务ID';
COMMENT ON COLUMN inspection_task_target_rel.target_type IS '目标类型';
COMMENT ON COLUMN inspection_task_target_rel.target_id IS '目标ID';
COMMENT ON COLUMN inspection_task_target_rel.sort_no IS '排序号';
COMMENT ON COLUMN inspection_task_target_rel.enabled IS '是否启用';

CREATE INDEX idx_inspection_task_target_rel_task_id ON inspection_task_target_rel(task_id);
CREATE INDEX idx_inspection_task_target_rel_target ON inspection_task_target_rel(target_type, target_id);

CREATE TABLE inspection_task_schedule_rule (
    id BIGINT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    schedule_profile_id BIGINT,
    rule_code VARCHAR(64),
    rule_name VARCHAR(128) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    sort_no INT4,
    schedule_mode INT4 NOT NULL,
    schedule_pattern INT4 NOT NULL,
    effective_start_date DATE,
    effective_end_date DATE,
    anchor_date DATE,
    required_schedules_per_cycle INT4,
    cycle_unit VARCHAR(32),
    min_interval_minutes INT4,
    max_interval_minutes INT4,
    week_days_json TEXT,
    month_days_json TEXT,
    time_points_json TEXT,
    preferred_time_windows_json TEXT,
    forbidden_time_windows_json TEXT,
    auto_adjust_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    conflict_strategy INT4,
    priority INT4,
    max_delay_minutes INT4,
    max_advance_minutes INT4,
    manual_confirm_required BOOLEAN NOT NULL DEFAULT FALSE,
    resource_strategy INT4,
    fixed_robot_id BIGINT,
    resource_pool_json TEXT,
    allow_resource_switch BOOLEAN NOT NULL DEFAULT FALSE,
    required_robot_count INT4,
    creator VARCHAR(64),
    create_time TIMESTAMP NOT NULL,
    updater VARCHAR(64),
    update_time TIMESTAMP NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE inspection_task_schedule_rule IS '巡检任务排期规则表';
COMMENT ON COLUMN inspection_task_schedule_rule.task_id IS '任务ID';
COMMENT ON COLUMN inspection_task_schedule_rule.schedule_profile_id IS '排期配置ID';
COMMENT ON COLUMN inspection_task_schedule_rule.schedule_mode IS '调度模式';
COMMENT ON COLUMN inspection_task_schedule_rule.schedule_pattern IS '排期模式';
COMMENT ON COLUMN inspection_task_schedule_rule.required_schedules_per_cycle IS '每周期要求调度次数';
COMMENT ON COLUMN inspection_task_schedule_rule.cycle_unit IS '周期单位';
COMMENT ON COLUMN inspection_task_schedule_rule.time_points_json IS '固定时点配置JSON';
COMMENT ON COLUMN inspection_task_schedule_rule.preferred_time_windows_json IS '候选时间窗JSON';
COMMENT ON COLUMN inspection_task_schedule_rule.forbidden_time_windows_json IS '禁止时间窗JSON';
COMMENT ON COLUMN inspection_task_schedule_rule.conflict_strategy IS '冲突处理策略';
COMMENT ON COLUMN inspection_task_schedule_rule.resource_strategy IS '资源策略';
COMMENT ON COLUMN inspection_task_schedule_rule.resource_pool_json IS '资源池JSON';

CREATE INDEX idx_inspection_task_schedule_rule_task_id ON inspection_task_schedule_rule(task_id);
CREATE INDEX idx_inspection_task_schedule_rule_profile_id ON inspection_task_schedule_rule(schedule_profile_id);

CREATE TABLE inspection_task_schedule_plan (
    id BIGINT PRIMARY KEY,
    plan_code VARCHAR(64) NOT NULL,
    plan_type INT4 NOT NULL,
    plan_status INT4 NOT NULL,
    plan_horizon_start TIMESTAMP NOT NULL,
    plan_horizon_end TIMESTAMP NOT NULL,
    trigger_source VARCHAR(64),
    version INT4,
    remark VARCHAR(512),
    creator VARCHAR(64),
    create_time TIMESTAMP NOT NULL,
    updater VARCHAR(64),
    update_time TIMESTAMP NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE inspection_task_schedule_plan IS '巡检调度计划批次表';
COMMENT ON COLUMN inspection_task_schedule_plan.plan_code IS '计划批次编码';
COMMENT ON COLUMN inspection_task_schedule_plan.plan_type IS '计划类型';
COMMENT ON COLUMN inspection_task_schedule_plan.plan_status IS '计划状态';
COMMENT ON COLUMN inspection_task_schedule_plan.plan_horizon_start IS '计划窗口开始';
COMMENT ON COLUMN inspection_task_schedule_plan.plan_horizon_end IS '计划窗口结束';

CREATE UNIQUE INDEX uk_inspection_task_schedule_plan_code ON inspection_task_schedule_plan(plan_code);
CREATE INDEX idx_inspection_task_schedule_plan_horizon ON inspection_task_schedule_plan(plan_horizon_start, plan_horizon_end);

CREATE TABLE inspection_task_schedule (
    id BIGINT PRIMARY KEY,
    plan_id BIGINT NOT NULL,
    task_id BIGINT NOT NULL,
    schedule_profile_id BIGINT,
    schedule_rule_id BIGINT NOT NULL,
    scheduled_date DATE NOT NULL,
    original_start_time TIMESTAMP NOT NULL,
    original_end_time TIMESTAMP,
    planned_start_time TIMESTAMP NOT NULL,
    planned_end_time TIMESTAMP,
    assigned_robot_id BIGINT,
    assigned_resource_snapshot_json TEXT,
    schedule_status INT4 NOT NULL,
    has_conflict BOOLEAN NOT NULL DEFAULT FALSE,
    conflict_type INT4,
    conflict_reason VARCHAR(512),
    conflict_task_id BIGINT,
    conflict_schedule_id BIGINT,
    schedule_decision INT4,
    adjust_reason VARCHAR(512),
    adjusted_at TIMESTAMP,
    manually_confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    confirmed_by BIGINT,
    confirmed_at TIMESTAMP,
    superseded BOOLEAN NOT NULL DEFAULT FALSE,
    source_schedule_id BIGINT,
    replaced_by_schedule_id BIGINT,
    creator VARCHAR(64),
    create_time TIMESTAMP NOT NULL,
    updater VARCHAR(64),
    update_time TIMESTAMP NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE inspection_task_schedule IS '巡检任务计划点表';
COMMENT ON COLUMN inspection_task_schedule.plan_id IS '计划批次ID';
COMMENT ON COLUMN inspection_task_schedule.task_id IS '任务ID';
COMMENT ON COLUMN inspection_task_schedule.schedule_profile_id IS '生效排期配置ID';
COMMENT ON COLUMN inspection_task_schedule.schedule_rule_id IS '命中的排期规则ID';
COMMENT ON COLUMN inspection_task_schedule.scheduled_date IS '计划日期';
COMMENT ON COLUMN inspection_task_schedule.original_start_time IS '原始理想开始时间';
COMMENT ON COLUMN inspection_task_schedule.original_end_time IS '原始理想结束时间';
COMMENT ON COLUMN inspection_task_schedule.planned_start_time IS '当前生效计划开始时间';
COMMENT ON COLUMN inspection_task_schedule.planned_end_time IS '当前生效计划结束时间';
COMMENT ON COLUMN inspection_task_schedule.assigned_robot_id IS '分配机器人ID';
COMMENT ON COLUMN inspection_task_schedule.schedule_status IS '计划点状态';
COMMENT ON COLUMN inspection_task_schedule.has_conflict IS '是否存在冲突';
COMMENT ON COLUMN inspection_task_schedule.conflict_type IS '冲突类型';
COMMENT ON COLUMN inspection_task_schedule.schedule_decision IS '调度决策';

CREATE INDEX idx_inspection_task_schedule_plan_id ON inspection_task_schedule(plan_id);
CREATE INDEX idx_inspection_task_schedule_task_id ON inspection_task_schedule(task_id);
CREATE INDEX idx_inspection_task_schedule_profile_id ON inspection_task_schedule(schedule_profile_id);
CREATE INDEX idx_inspection_task_schedule_rule_id ON inspection_task_schedule(schedule_rule_id);
CREATE INDEX idx_inspection_task_schedule_planned_time ON inspection_task_schedule(planned_start_time, planned_end_time);

CREATE TABLE inspection_task_execution (
    id BIGINT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    schedule_id BIGINT NOT NULL,
    robot_id BIGINT,
    actual_start_time TIMESTAMP,
    actual_end_time TIMESTAMP,
    execution_status INT4,
    result_status INT4,
    result_json TEXT,
    exception_json TEXT,
    creator VARCHAR(64),
    create_time TIMESTAMP NOT NULL,
    updater VARCHAR(64),
    update_time TIMESTAMP NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE inspection_task_execution IS '巡检任务执行记录表';
COMMENT ON COLUMN inspection_task_execution.task_id IS '任务ID';
COMMENT ON COLUMN inspection_task_execution.schedule_id IS '计划点ID';
COMMENT ON COLUMN inspection_task_execution.robot_id IS '执行机器人ID';
COMMENT ON COLUMN inspection_task_execution.execution_status IS '执行状态';
COMMENT ON COLUMN inspection_task_execution.result_status IS '结果状态';

CREATE INDEX idx_inspection_task_execution_task_id ON inspection_task_execution(task_id);
CREATE UNIQUE INDEX uk_inspection_task_execution_schedule_id ON inspection_task_execution(schedule_id);

CREATE TABLE inspection_task_runtime_log (
    id BIGINT PRIMARY KEY,
    task_id BIGINT,
    schedule_id BIGINT,
    execution_id BIGINT,
    log_type INT4 NOT NULL,
    event_code VARCHAR(64),
    event_name VARCHAR(128),
    event_time TIMESTAMP NOT NULL,
    content TEXT,
    extra_json TEXT,
    creator VARCHAR(64),
    create_time TIMESTAMP NOT NULL,
    updater VARCHAR(64),
    update_time TIMESTAMP NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE inspection_task_runtime_log IS '巡检任务运行日志表';
COMMENT ON COLUMN inspection_task_runtime_log.task_id IS '任务ID';
COMMENT ON COLUMN inspection_task_runtime_log.schedule_id IS '计划点ID';
COMMENT ON COLUMN inspection_task_runtime_log.execution_id IS '执行记录ID';
COMMENT ON COLUMN inspection_task_runtime_log.log_type IS '日志类型';
COMMENT ON COLUMN inspection_task_runtime_log.event_code IS '事件编码';
COMMENT ON COLUMN inspection_task_runtime_log.event_name IS '事件名称';
COMMENT ON COLUMN inspection_task_runtime_log.content IS '日志内容';

CREATE INDEX idx_inspection_task_runtime_log_schedule_id ON inspection_task_runtime_log(schedule_id);
CREATE INDEX idx_inspection_task_runtime_log_execution_id ON inspection_task_runtime_log(execution_id);
CREATE INDEX idx_inspection_task_runtime_log_event_time ON inspection_task_runtime_log(event_time);
