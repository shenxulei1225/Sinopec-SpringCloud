-- PostgreSQL Test Schema for Emergency Module
-- Used with Testcontainers PostgreSQL

-- Create extension for UUID generation (if needed)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================
-- Sequences
-- ============================================
CREATE SEQUENCE IF NOT EXISTS system_category_seq START 1;
CREATE SEQUENCE IF NOT EXISTS emergency_event_seq START 1;
CREATE SEQUENCE IF NOT EXISTS emergency_plan_seq START 1;
CREATE SEQUENCE IF NOT EXISTS emergency_plan_step_seq START 1;
CREATE SEQUENCE IF NOT EXISTS emergency_response_seq START 1;
CREATE SEQUENCE IF NOT EXISTS emergency_task_seq START 1;
CREATE SEQUENCE IF NOT EXISTS emergency_command_seq START 1;

-- ============================================
-- system_category table (for plan groups)
-- ============================================
CREATE TABLE IF NOT EXISTS system_category (
    id           BIGINT PRIMARY KEY DEFAULT nextval('system_category_seq'),
    name         VARCHAR(100) NOT NULL,
    code         VARCHAR(100),
    parent_id    BIGINT,
    business_type VARCHAR(50),
    sort         INTEGER DEFAULT 0,
    status       INTEGER DEFAULT 0,
    description  TEXT,
    extra_attrs  JSONB,
    tree_path    VARCHAR(500),
    level        INTEGER,
    create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted      BOOLEAN NOT NULL DEFAULT FALSE,
    creator      VARCHAR(64) DEFAULT '',
    updater      VARCHAR(64) DEFAULT '',
    tenant_id    BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_category_parent ON system_category(parent_id);
CREATE INDEX IF NOT EXISTS idx_category_business_type ON system_category(business_type);

-- ============================================
-- emergency_event table
-- ============================================
CREATE TABLE IF NOT EXISTS emergency_event (
    id                      BIGSERIAL PRIMARY KEY,
    event_code              VARCHAR(64) NOT NULL UNIQUE,
    event_name              VARCHAR(200), -- 事件名称
    event_type              BIGINT NOT NULL,
    event_sub_type          VARCHAR(50),
    event_level             VARCHAR(50),
    occurred_at             TIMESTAMP,
    discovered_at           TIMESTAMP NOT NULL,
    location_address        VARCHAR(500),
    location_gis            VARCHAR(255), -- WKT format string (e.g., "POINT(116.397128 39.916527)"), use GEOMETRY(POINT, 4326) if PostGIS extension is enabled
    location_bim            VARCHAR(255),
    description             TEXT,
    impact_summary          JSONB,
    status                  VARCHAR(50) NOT NULL DEFAULT 'pending',
    reported_by             VARCHAR(255),
    command_org             VARCHAR(255),
    is_government_confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    attachments             JSONB,
    create_time             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creator                 VARCHAR(64) DEFAULT '',
    updater                 VARCHAR(64) DEFAULT '',
    deleted                 BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id               BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_event_code ON emergency_event(event_code) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_event_type ON emergency_event(event_type) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_event_sub_type ON emergency_event(event_sub_type) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_event_level ON emergency_event(event_level) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_event_status ON emergency_event(status) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_event_reported_by ON emergency_event(reported_by) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_event_command_org ON emergency_event(command_org) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_event_government_confirmed ON emergency_event(is_government_confirmed) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_event_occurred_at ON emergency_event(occurred_at) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_event_discovered_at ON emergency_event(discovered_at) WHERE deleted = FALSE;
-- Note: GIST index for GEOMETRY type requires PostGIS extension
-- CREATE INDEX IF NOT EXISTS idx_event_location_gis ON emergency_event USING GIST(location_gis) WHERE location_gis IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_event_location_gis ON emergency_event(location_gis) WHERE location_gis IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_event_tenant ON emergency_event(tenant_id, create_time) WHERE deleted = FALSE;

-- ============================================
-- emergency_event_report_internal table (事件内部上报表)
-- ============================================
CREATE TABLE IF NOT EXISTS emergency_event_report_internal (
    id                  BIGSERIAL PRIMARY KEY,
    emergency_event_id  BIGINT NOT NULL,
    form_template_id    BIGINT,
    reporter_id         BIGINT NOT NULL,
    reporter_name       VARCHAR(64) NOT NULL,
    report_time         TIMESTAMP NOT NULL,
    content             TEXT,
    custom_fields       JSONB,
    attachments         JSONB,
    location_address    VARCHAR(500),
    location_gis        VARCHAR(255), -- WKT format string (e.g., "POINT(116.397128 39.916527)")
    location_bim        VARCHAR(255),
    create_time         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creator             VARCHAR(64) DEFAULT '',
    updater             VARCHAR(64) DEFAULT '',
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_report_internal_event_time ON emergency_event_report_internal(emergency_event_id, report_time);
CREATE INDEX IF NOT EXISTS idx_report_internal_creator ON emergency_event_report_internal(creator, create_time);
CREATE INDEX IF NOT EXISTS idx_report_internal_reporter ON emergency_event_report_internal(reporter_id, report_time);

-- ============================================
-- emergency_event_report_external table (事件对外上报表)
-- ============================================
CREATE TABLE IF NOT EXISTS emergency_event_report_external (
    id                          BIGSERIAL PRIMARY KEY,
    emergency_event_id          BIGINT NOT NULL,
    form_template_id            BIGINT,
    report_org_name             VARCHAR(255) NOT NULL,
    reporter_id                 BIGINT NOT NULL,
    reporter_name               VARCHAR(64) NOT NULL,
    reporter_phone              VARCHAR(20),
    report_time                 TIMESTAMP NOT NULL,
    signer_id                   BIGINT,
    signer_name                 VARCHAR(64) NOT NULL,
    receive_org_names           JSONB NOT NULL,
    occurred_at                 TIMESTAMP,
    province                    VARCHAR(100),
    city_or_county              VARCHAR(100),
    town                        VARCHAR(100),
    location_address            VARCHAR(500),
    location_gis                VARCHAR(255), -- WKT format string
    location_bim                VARCHAR(255),
    pipeline_name               VARCHAR(255),
    section_name                VARCHAR(255),
    unit_name                   VARCHAR(255),
    site_name                   VARCHAR(255),
    event_org_type              VARCHAR(50),
    event_org_name              VARCHAR(255),
    description_part1           TEXT,
    description_part2           TEXT,
    description_part3           TEXT,
    description_other           TEXT,
    measure_pipeline_and_other  TEXT,
    measure_report_to_upper_lower TEXT,
    measure_response_activation TEXT,
    measure_report_to_government TEXT,
    measure_other               TEXT,
    attachments                 JSONB,
    audit_status                VARCHAR(50) NOT NULL DEFAULT 'draft',
    audit_time                  TIMESTAMP,
    auditor_id                  BIGINT,
    auditor_name                VARCHAR(64),
    audit_comment               TEXT,
    receive_time                TIMESTAMP,
    receive_dept_name           VARCHAR(255),
    receiver_name               VARCHAR(64),
    receiver_phone              VARCHAR(20),
    leader_instruction          TEXT,
    custom_fields               JSONB,
    create_time                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creator                     VARCHAR(64) DEFAULT '',
    updater                     VARCHAR(64) DEFAULT '',
    deleted                     BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id                   BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_report_external_event_time ON emergency_event_report_external(emergency_event_id, report_time);
CREATE INDEX IF NOT EXISTS idx_report_external_creator ON emergency_event_report_external(creator, create_time);
CREATE INDEX IF NOT EXISTS idx_report_external_audit_status ON emergency_event_report_external(audit_status, audit_time);
CREATE INDEX IF NOT EXISTS idx_report_external_reporter ON emergency_event_report_external(reporter_id, report_time);
CREATE INDEX IF NOT EXISTS idx_report_external_signer ON emergency_event_report_external(signer_id, report_time) WHERE signer_id IS NOT NULL;

-- ============================================
-- emergency_event_assess table (事件研判历史表)
-- ============================================
CREATE TABLE IF NOT EXISTS emergency_event_assess (
    id                  BIGSERIAL PRIMARY KEY,
    emergency_event_id  BIGINT NOT NULL,
    assess_time         TIMESTAMP NOT NULL,
    original_level      VARCHAR(50),
    new_level           VARCHAR(50),
    reason              TEXT,
    meeting_record      TEXT,
    create_time         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creator             VARCHAR(64) DEFAULT '',
    updater             VARCHAR(64) DEFAULT '',
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_assess_event_time ON emergency_event_assess(emergency_event_id, assess_time);

-- ============================================
-- emergency_event_status_history table (事件状态变更历史表)
-- ============================================
CREATE TABLE IF NOT EXISTS emergency_event_status_history (
    id              BIGSERIAL PRIMARY KEY,
    event_id        BIGINT NOT NULL, -- 使用event_id
    from_status    VARCHAR(50),
    to_status       VARCHAR(50) NOT NULL,
    reason          TEXT,
    operator_id     BIGINT,
    operator_name   VARCHAR(64),
    operate_time    TIMESTAMP NOT NULL,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creator         VARCHAR(64) DEFAULT '',
    updater         VARCHAR(64) DEFAULT '',
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id       BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_status_history_event ON emergency_event_status_history(event_id, operate_time);
CREATE INDEX IF NOT EXISTS idx_status_history_operator ON emergency_event_status_history(operator_id, operate_time);

-- ============================================
-- emergency_event_handling table (事件处置历史表)
-- ============================================
CREATE TABLE IF NOT EXISTS emergency_event_handling (
    id                  BIGSERIAL PRIMARY KEY,
    emergency_event_id  BIGINT NOT NULL,
    handler_id          BIGINT NOT NULL,
    handler_name        VARCHAR(64) NOT NULL,
    handling_time       TIMESTAMP NOT NULL,
    measures            TEXT,
    results             TEXT,
    attachments         JSONB,
    create_time         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creator             VARCHAR(64) DEFAULT '',
    updater             VARCHAR(64) DEFAULT '',
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_handling_event_time ON emergency_event_handling(emergency_event_id, handling_time);

-- ============================================
-- emergency_response table
-- ============================================
CREATE TABLE IF NOT EXISTS emergency_response (
    id                BIGSERIAL PRIMARY KEY,
    response_no       VARCHAR(64), -- 响应编号（唯一标识）
    event_id          BIGINT NOT NULL,
    event_name        VARCHAR(200), -- 事件名称（冗余字段）
    response_level    VARCHAR(20) NOT NULL, -- 响应级别（I/II/III/IV/V级）
    plan_id           BIGINT, -- 关联预案ID
    plan_name         VARCHAR(200), -- 预案名称（冗余字段）
    status            VARCHAR(20) DEFAULT 'pending' NOT NULL, -- 状态（pending/executing/completed/cancelled）
    start_info        JSONB, -- 启动信息（JSONB）
    execution_info    JSONB, -- 执行信息（JSONB）
    tracking_info     JSONB, -- 跟踪信息（JSONB）
    end_info          JSONB, -- 结束信息（JSONB）
    upgrade_info      JSONB, -- 升级信息（JSONB）
    cancel_info       JSONB, -- 取消信息（JSONB）
    history           JSONB, -- 历史记录（JSONB）
    coordination_info JSONB, -- 协调信息（JSONB）
    create_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    creator           VARCHAR(64) DEFAULT '',
    updater           VARCHAR(64) DEFAULT '',
    deleted           BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id         BIGINT DEFAULT 0 NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_response_status ON emergency_response(status);
CREATE INDEX IF NOT EXISTS idx_response_level ON emergency_response(response_level);
CREATE UNIQUE INDEX IF NOT EXISTS uk_response_no_tenant ON emergency_response(response_no, tenant_id) WHERE deleted = false;

-- ============================================
-- emergency_response_history table
-- ============================================
CREATE TABLE IF NOT EXISTS emergency_response_history (
    id          BIGSERIAL PRIMARY KEY,
    response_id BIGINT NOT NULL,
    type        VARCHAR(50),
    from_level  VARCHAR(50),
    to_level    VARCHAR(50),
    reason      TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creator     VARCHAR(64) DEFAULT '',
    updater     VARCHAR(64) DEFAULT '',
    tenant_id   BIGINT DEFAULT 0
);

-- ============================================
-- emergency_plan table
-- ============================================
CREATE TABLE IF NOT EXISTS emergency_plan (
    id              BIGSERIAL PRIMARY KEY,
    plan_no         VARCHAR(50) NOT NULL,
    plan_name       VARCHAR(200) NOT NULL,
    plan_type       INTEGER NOT NULL,
    plan_levels     JSONB, -- JSONB array for plan levels
    plan_group_id   BIGINT, -- References system_category
    custom_config   JSONB,
    review_info     JSONB,
    status          VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    version_number  VARCHAR(50), -- 版本号（可选，用于版本管理）
    is_version_locked BOOLEAN DEFAULT FALSE, -- 版本是否锁定（锁定后创建响应时使用快照）
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    creator         VARCHAR(64) DEFAULT '',
    updater         VARCHAR(64) DEFAULT '',
    tenant_id       BIGINT DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_plan_no_tenant ON emergency_plan(plan_no, tenant_id);
CREATE INDEX IF NOT EXISTS idx_plan_group ON emergency_plan(plan_group_id, tenant_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_plan_status ON emergency_plan(status, tenant_id);
CREATE INDEX IF NOT EXISTS idx_plan_levels_gin ON emergency_plan USING GIN (plan_levels) WHERE plan_levels IS NOT NULL;

-- ============================================
-- emergency_plan_attachment table
-- ============================================
CREATE TABLE IF NOT EXISTS emergency_plan_attachment (
    id           BIGSERIAL PRIMARY KEY,
    plan_id      BIGINT NOT NULL,
    name         VARCHAR(255) NOT NULL,
    type         VARCHAR(50),
    content      JSONB,
    create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted      BOOLEAN NOT NULL DEFAULT FALSE,
    creator      VARCHAR(64) DEFAULT '',
    updater      VARCHAR(64) DEFAULT '',
    tenant_id    BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_plan_attachment_plan ON emergency_plan_attachment(plan_id, tenant_id) WHERE deleted = FALSE;

-- ============================================
-- emergency_plan_step table
-- ============================================
CREATE TABLE IF NOT EXISTS emergency_plan_step (
    id                   BIGSERIAL PRIMARY KEY,
    plan_id              BIGINT NOT NULL,
    plan_level           VARCHAR(10), -- Plan level (optional)
    parent_id            BIGINT,
    step_order           INTEGER,
    step_title           VARCHAR(200),
    step_stage           VARCHAR(50),
    step_description     TEXT,
    scheduled_start_time INTEGER DEFAULT 0,
    responsible_post_id  BIGINT,
    responsible_dept_id  BIGINT,
    responsible_user_id  BIGINT,
    name                 VARCHAR(200),
    responsible_role     VARCHAR(200),
    resources            JSONB, -- 资源清单（JSONB类型，存储结构化数据）
    contacts             JSONB, -- 联系人信息（JSONB类型，存储结构化数据）
    planned_start_time   TIMESTAMP,
    command_id           BIGINT, -- 关联指令ID（可选，向后兼容）
    command              JSONB, -- 关联指令ID列表（JSONB数组类型，可选，用于存储多个指令ID）
    time_limit           INTEGER, -- 执行时限（分钟，可选）
    version              INTEGER DEFAULT 0, -- 版本号（乐观锁字段，用于并发控制）
    create_time          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted              BOOLEAN NOT NULL DEFAULT FALSE,
    creator              VARCHAR(64) DEFAULT '',
    updater              VARCHAR(64) DEFAULT '',
    tenant_id            BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_plan_step_plan ON emergency_plan_step(plan_id, tenant_id);
CREATE INDEX IF NOT EXISTS idx_plan_step_level ON emergency_plan_step(plan_id, plan_level, tenant_id);
CREATE INDEX IF NOT EXISTS idx_plan_step_parent ON emergency_plan_step(parent_id);
CREATE INDEX IF NOT EXISTS idx_plan_step_command ON emergency_plan_step USING GIN(command) WHERE command IS NOT NULL;

-- ============================================
-- emergency_task table
-- ============================================
CREATE TABLE IF NOT EXISTS emergency_task (
    id                 BIGSERIAL PRIMARY KEY,
    task_code          VARCHAR(64) NOT NULL UNIQUE, -- 任务编号（唯一）
    event_id           BIGINT NOT NULL,
    response_id        BIGINT, -- 预警阶段时为NULL
    plan_step_id       BIGINT,
    title              VARCHAR(200) NOT NULL, -- 任务标题
    content            TEXT, -- 任务内容
    task_type          VARCHAR(50), -- 任务类型
    stage              VARCHAR(50), -- 任务阶段：预警阶段、响应阶段等
    status             VARCHAR(50) NOT NULL DEFAULT 'pending', -- 任务状态：pending/in_progress/completed/terminated
    priority           VARCHAR(20) DEFAULT 'NORMAL', -- 优先级（LOW/NORMAL/HIGH/URGENT）
    is_key             BOOLEAN DEFAULT false, -- 是否关键任务
    assignee           VARCHAR(64), -- 分配人
    start_time         TIMESTAMP, -- 开始时间
    complete_time      TIMESTAMP, -- 完成时间
    due_time           TIMESTAMP, -- 截止时间
    time_limit         INTEGER, -- 执行时限（分钟）
    actual_duration    INTEGER, -- 实际耗时（分钟）
    execution_records  JSONB, -- 执行记录（JSONB）
    version            INTEGER DEFAULT 0, -- 乐观锁版本号
    create_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creator            VARCHAR(64) DEFAULT '',
    updater            VARCHAR(64) DEFAULT '',
    deleted            BOOLEAN DEFAULT FALSE,
    tenant_id          BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_task_status ON emergency_task(status);
CREATE INDEX IF NOT EXISTS idx_task_response_id ON emergency_task(response_id);
CREATE INDEX IF NOT EXISTS idx_task_event_id ON emergency_task(event_id);
CREATE INDEX IF NOT EXISTS idx_task_code ON emergency_task(task_code) WHERE deleted = FALSE;
-- CREATE INDEX IF NOT EXISTS idx_task_resources_gin ON emergency_task USING GIN (resources);
-- 注释掉：resources字段已移除，不再需要此索引

-- ============================================
-- emergency_task_history table
-- ============================================
CREATE TABLE IF NOT EXISTS emergency_task_history (
    id          BIGSERIAL PRIMARY KEY,
    response_id BIGINT,
    task_id     BIGINT NOT NULL,
    type        VARCHAR(50),
    from_status VARCHAR(50),
    to_status   VARCHAR(50),
    reason      TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creator     VARCHAR(64) DEFAULT '',
    updater     VARCHAR(64) DEFAULT '',
    deleted     BOOLEAN DEFAULT FALSE,
    tenant_id   BIGINT DEFAULT 0
);

-- ============================================
-- resource_dispatch table
-- ============================================
CREATE TABLE IF NOT EXISTS resource_dispatch (
    id             BIGSERIAL PRIMARY KEY,
    dispatch_no    VARCHAR(64), -- 调度编号（唯一标识）
    event_id       BIGINT NOT NULL,
    response_id    BIGINT, -- 预警阶段时为NULL
    resource_id    BIGINT NOT NULL,
    stage          VARCHAR(50), -- 调度阶段：预警阶段、响应阶段等
    status         VARCHAR(50),
    dispatch_info  JSONB, -- 派发信息（JSONB）
    allocation_info JSONB, -- 分配信息（JSONB）
    usage_info     JSONB, -- 使用信息（JSONB）
    recovery_info  JSONB, -- 回收信息（JSONB）
    dispatch_time  TIMESTAMP,
    eta_minutes    INTEGER,
    comment        TEXT,
    location       JSONB,
    recovered_time TIMESTAMP,
    create_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creator        VARCHAR(64) DEFAULT '',
    updater        VARCHAR(64) DEFAULT '',
    deleted        BOOLEAN DEFAULT FALSE,
    tenant_id      BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_dispatch_status ON resource_dispatch(status);
CREATE INDEX IF NOT EXISTS idx_dispatch_response ON resource_dispatch(response_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_dispatch_no_tenant ON resource_dispatch(dispatch_no, tenant_id) WHERE deleted = false;

-- ============================================
-- resource_pool table
-- ============================================
CREATE TABLE IF NOT EXISTS resource_pool (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    type            VARCHAR(50) NOT NULL,
    description     TEXT,
    status          VARCHAR(20) DEFAULT 'available',
    location        JSONB,
    contact_info    JSONB,
    capacity        INTEGER DEFAULT 1,
    unit            VARCHAR(50),
    organization_id BIGINT,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creator         VARCHAR(64) DEFAULT '',
    updater         VARCHAR(64) DEFAULT '',
    deleted         BOOLEAN DEFAULT FALSE,
    tenant_id       BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_resource_pool_type ON resource_pool(type);
CREATE INDEX IF NOT EXISTS idx_resource_pool_status ON resource_pool(status);
CREATE INDEX IF NOT EXISTS idx_resource_pool_organization ON resource_pool(organization_id);

-- ============================================
-- emergency_command table (应急指令表)
-- ============================================
CREATE TABLE IF NOT EXISTS emergency_command (
    id              BIGSERIAL PRIMARY KEY,
    command_no      VARCHAR(50) NOT NULL,
    title           VARCHAR(255),
    content         TEXT NOT NULL,
    command_type    VARCHAR(20) NOT NULL,
    priority        VARCHAR(20) DEFAULT 'normal',
    deadline        TIMESTAMP,
    status          VARCHAR(20) DEFAULT 'draft',
    stage           VARCHAR(50),
    event_id        BIGINT,
    response_id     BIGINT,
    template_id     BIGINT,
    attachments     JSONB,
    issue_info      JSONB DEFAULT '{}'::jsonb NOT NULL, -- 注意：完整Schema中是NOT NULL，但Service实现可能传入null，这里设置默认值
    execution_info  JSONB,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    creator         VARCHAR(64) DEFAULT '',
    updater         VARCHAR(64) DEFAULT '',
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id       BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_command_no_tenant ON emergency_command(command_no, tenant_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_command_status ON emergency_command(status, deleted);
CREATE INDEX IF NOT EXISTS idx_command_event ON emergency_command(event_id, deleted);
CREATE INDEX IF NOT EXISTS idx_command_response ON emergency_command(response_id, deleted);
CREATE INDEX IF NOT EXISTS idx_command_type ON emergency_command(command_type, deleted);

-- ============================================
-- emergency_command_step table (指令步骤表)
-- ============================================
CREATE TABLE IF NOT EXISTS emergency_command_step (
    id                      BIGSERIAL PRIMARY KEY,
    command_id              BIGINT NOT NULL,
    step_content            TEXT NOT NULL,
    time_limit_minutes      INTEGER NOT NULL CHECK (time_limit_minutes > 0 AND time_limit_minutes <= 1440),
    start_time              TIMESTAMP,
    complete_time           TIMESTAMP,
    status                  VARCHAR(50) NOT NULL DEFAULT 'pending',
    timeout_flag            BOOLEAN NOT NULL DEFAULT FALSE,
    actual_duration_minutes INTEGER,
    plan_step_id            BIGINT,
    executor_id             BIGINT,
    executor_name           VARCHAR(64),
    timeout_reason          TEXT,
    handling_measures       TEXT,
    create_time             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creator                 VARCHAR(64) DEFAULT '',
    updater                 VARCHAR(64) DEFAULT '',
    deleted                 BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id               BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_command_step_command ON emergency_command_step(command_id, deleted);
CREATE INDEX IF NOT EXISTS idx_command_step_status ON emergency_command_step(status, deleted);
CREATE INDEX IF NOT EXISTS idx_command_step_timeout ON emergency_command_step(timeout_flag, status, deleted);

-- ============================================
-- alert_configuration table (告警配置表)
-- ============================================
CREATE TABLE IF NOT EXISTS alert_configuration (
    id                    BIGSERIAL PRIMARY KEY,
    alert_type            VARCHAR(50) NOT NULL,
    alert_level           VARCHAR(20) NOT NULL,
    trigger_condition     JSONB,
    receivers             JSONB,
    notification_channels JSONB,
    enabled               BOOLEAN NOT NULL DEFAULT TRUE,
    create_time           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creator               VARCHAR(64) DEFAULT '',
    updater               VARCHAR(64) DEFAULT '',
    deleted               BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id             BIGINT DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_alert_config_type_level ON alert_configuration(alert_type, alert_level, deleted);
CREATE INDEX IF NOT EXISTS idx_alert_config_enabled ON alert_configuration(enabled, deleted);

-- ============================================
-- 更新 emergency_task 表，添加 stage 字段
-- ============================================
-- 注意：如果表已存在，需要执行 ALTER TABLE 语句
-- ALTER TABLE emergency_task ADD COLUMN IF NOT EXISTS stage VARCHAR(50);
-- 在新建表时添加 stage 字段
-- 由于是测试schema，这里在表定义中添加

-- ============================================
-- emergency_information_report table (信息报送表)
-- ============================================
CREATE TABLE IF NOT EXISTS emergency_information_report (
    id                  BIGSERIAL PRIMARY KEY,
    event_id            BIGINT NOT NULL,
    report_type         VARCHAR(50) NOT NULL,
    report_target       VARCHAR(255),
    report_content      TEXT NOT NULL,
    status              VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    report_time         TIMESTAMP,
    confirm_time        TIMESTAMP,
    confirm_person      VARCHAR(64),
    confirm_remark      TEXT,
    time_limit          INTEGER,
    timeout             BOOLEAN DEFAULT FALSE,
    create_time         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creator             VARCHAR(64) DEFAULT '',
    updater             VARCHAR(64) DEFAULT '',
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_information_report_event ON emergency_information_report(event_id, deleted);
CREATE INDEX IF NOT EXISTS idx_information_report_status ON emergency_information_report(status, deleted);
CREATE INDEX IF NOT EXISTS idx_information_report_type ON emergency_information_report(report_type, deleted);

-- ============================================
-- emergency_organization table (应急组织表)
-- ============================================
CREATE TABLE IF NOT EXISTS emergency_organization (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(200) NOT NULL,
    code                VARCHAR(64) UNIQUE NOT NULL,
    type                VARCHAR(50),
    parent_id           BIGINT,
    level               INTEGER DEFAULT 1,
    path                VARCHAR(500),
    leader              VARCHAR(100),
    contact_phone       VARCHAR(200),
    contact_address     VARCHAR(500),
    description         TEXT,
    sort                INTEGER,
    status              VARCHAR(20) DEFAULT 'ENABLE',
    create_time         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creator             VARCHAR(64) DEFAULT '',
    updater             VARCHAR(64) DEFAULT '',
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_organization_code ON emergency_organization(code, deleted);
CREATE INDEX IF NOT EXISTS idx_organization_parent ON emergency_organization(parent_id, deleted);
CREATE INDEX IF NOT EXISTS idx_organization_status ON emergency_organization(status, deleted);

-- ============================================
-- operation_audit_log table (操作审计日志表)
-- ============================================
CREATE TABLE IF NOT EXISTS emergency_operation_audit_log (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT,
    user_name           VARCHAR(64),
    operation_type      VARCHAR(50) NOT NULL,
    business_module     VARCHAR(50),
    business_id         BIGINT,
    business_name       VARCHAR(200),
    operation_content   TEXT,
    before_data         TEXT,
    after_data          TEXT,
    operation_ip        VARCHAR(50),
    request_path        VARCHAR(500),
    request_method      VARCHAR(10),
    operation_result    VARCHAR(20),
    error_message       TEXT,
    operation_time      TIMESTAMP NOT NULL,
    create_time         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creator             VARCHAR(64) DEFAULT '',
    updater             VARCHAR(64) DEFAULT '',
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_audit_log_user ON emergency_operation_audit_log(user_id, deleted);
CREATE INDEX IF NOT EXISTS idx_audit_log_module ON emergency_operation_audit_log(business_module, deleted);
CREATE INDEX IF NOT EXISTS idx_audit_log_time ON emergency_operation_audit_log(operation_time, deleted);
CREATE INDEX IF NOT EXISTS idx_audit_log_type ON emergency_operation_audit_log(operation_type, deleted);

-- ============================================
-- 更新 resource_dispatch 表，添加 stage 字段
-- ============================================
-- 注意：如果表已存在，需要执行 ALTER TABLE 语句
-- ALTER TABLE resource_dispatch ADD COLUMN IF NOT EXISTS stage VARCHAR(50);
-- 在新建表时添加 stage 字段
-- 由于是测试schema，这里在表定义中添加

-- ============================================
-- resource_type_share_rule table (资源类型共享规则配置表)
-- ============================================
CREATE TABLE IF NOT EXISTS resource_type_share_rule (
    id                      BIGSERIAL PRIMARY KEY,
    resource_type           VARCHAR(50) NOT NULL,
    allow_multi_event_share BOOLEAN NOT NULL DEFAULT FALSE,
    max_share_count         INTEGER,
    description             VARCHAR(500),
    creator                 VARCHAR(64) DEFAULT '',
    create_time             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updater                 VARCHAR(64) DEFAULT '',
    update_time             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted                 BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id               BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_resource_type ON resource_type_share_rule(resource_type, tenant_id, deleted);
CREATE INDEX IF NOT EXISTS idx_tenant_id ON resource_type_share_rule(tenant_id);

-- 插入默认共享规则配置
INSERT INTO resource_type_share_rule (resource_type, allow_multi_event_share, max_share_count, description, creator, create_time, updater, update_time, tenant_id)
VALUES
    ('personnel', TRUE, 3, '人员资源允许多事件共享，最多3个事件', 'system', NOW(), 'system', NOW(), 1),
    ('vehicle', FALSE, NULL, '车辆资源不允许多事件共享', 'system', NOW(), 'system', NOW(), 1),
    ('equipment', TRUE, 5, '设备资源允许多事件共享，最多5个事件', 'system', NOW(), 'system', NOW(), 1),
    ('material', TRUE, NULL, '物资资源允许多事件共享，无数量限制', 'system', NOW(), 'system', NOW(), 1)
ON CONFLICT (resource_type, tenant_id, deleted) DO UPDATE SET
    allow_multi_event_share = EXCLUDED.allow_multi_event_share,
    max_share_count = EXCLUDED.max_share_count,
    description = EXCLUDED.description,
    update_time = NOW();







