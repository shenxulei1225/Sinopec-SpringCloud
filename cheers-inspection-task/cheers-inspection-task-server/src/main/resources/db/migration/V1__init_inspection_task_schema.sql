-- ============================================================================
-- Inspection Task Module Schema Initialization
-- ============================================================================

-- inspection_task table
CREATE TABLE inspection_task (
    id                                BIGINT      NOT NULL PRIMARY KEY,
    parent_id                         BIGINT      DEFAULT NULL,
    category_id                       BIGINT      DEFAULT NULL,
    task_code                         VARCHAR(64) NOT NULL,
    task_name                         VARCHAR(255) NOT NULL,
    status                            SMALLINT    NOT NULL DEFAULT 1,
    enabled                           BOOLEAN     NOT NULL DEFAULT TRUE,
    remark                            TEXT        DEFAULT NULL,
    inherit_parent_schedule           BOOLEAN     NOT NULL DEFAULT FALSE,
    inherit_parent_resource_policy    BOOLEAN     NOT NULL DEFAULT FALSE,
    inspection_content                TEXT        DEFAULT NULL,
    schedule_requirement_id           BIGINT      DEFAULT NULL,
    schedule_policy_id                BIGINT      DEFAULT NULL,
    resource_policy                   TEXT        DEFAULT NULL,
    active_plan_id                    BIGINT      DEFAULT NULL,
    plan_ids                          TEXT        DEFAULT NULL,
    creator                           VARCHAR(64) DEFAULT '',
    create_time                       TIMESTAMP    DEFAULT NULL,
    updater                           VARCHAR(64) DEFAULT '',
    update_time                       TIMESTAMP    DEFAULT NULL,
    deleted                           BOOLEAN     NOT NULL DEFAULT FALSE,
    tenant_id                         BIGINT      NOT NULL DEFAULT 100,
    CONSTRAINT uk_inspection_task_code UNIQUE (task_code, tenant_id)
);

COMMENT ON TABLE inspection_task IS 'Inspection task table';
COMMENT ON COLUMN inspection_task.status IS 'Status: 1-draft, 2-published, 3-stopped';
COMMENT ON COLUMN inspection_task.inspection_content IS 'Inspection content (JSON)';
COMMENT ON COLUMN inspection_task.resource_policy IS 'Resource policy (JSON)';
COMMENT ON COLUMN inspection_task.plan_ids IS 'Historical plan ID list (JSON)';

-- inspection_task_template table
CREATE TABLE inspection_task_template (
    id                  BIGINT      NOT NULL PRIMARY KEY,
    template_code       VARCHAR(64) NOT NULL,
    template_name       VARCHAR(255) NOT NULL,
    template_type       SMALLINT    NOT NULL DEFAULT 1,
    content_template    TEXT        DEFAULT NULL,
    schedule_template   TEXT        DEFAULT NULL,
    resource_template   TEXT        DEFAULT NULL,
    status              SMALLINT    NOT NULL DEFAULT 1,
    remark              TEXT        DEFAULT NULL,
    creator             VARCHAR(64) DEFAULT '',
    create_time         TIMESTAMP    DEFAULT NULL,
    updater             VARCHAR(64) DEFAULT '',
    update_time         TIMESTAMP    DEFAULT NULL,
    deleted             BOOLEAN     NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT      NOT NULL DEFAULT 100,
    CONSTRAINT uk_inspection_task_template_code UNIQUE (template_code, tenant_id)
);

COMMENT ON TABLE inspection_task_template IS 'Inspection task template table';
COMMENT ON COLUMN inspection_task_template.template_type IS 'Template type: 1-inspection, 2-maintenance';

-- inspection_task_schedule_requirement table（模板组合配置容器）
CREATE TABLE inspection_task_schedule_requirement (
    id                    BIGINT      NOT NULL PRIMARY KEY,
    requirement_code      VARCHAR(64) NOT NULL,
    requirement_name      VARCHAR(128) NOT NULL,
    task_id               BIGINT      NOT NULL,
    schedule_policy_id    BIGINT      DEFAULT NULL,
    schedule_templates    TEXT        DEFAULT NULL,
    description           TEXT        DEFAULT NULL,
    creator               VARCHAR(64) DEFAULT '',
    create_time           TIMESTAMP    DEFAULT NULL,
    updater               VARCHAR(64) DEFAULT '',
    update_time           TIMESTAMP    DEFAULT NULL,
    deleted               BOOLEAN     NOT NULL DEFAULT FALSE,
    tenant_id             BIGINT      NOT NULL DEFAULT 100,
    CONSTRAINT uk_inspection_task_schedule_requirement_code UNIQUE (requirement_code, tenant_id)
);

COMMENT ON TABLE inspection_task_schedule_requirement IS 'Inspection task schedule requirement table (template config container)';
COMMENT ON COLUMN inspection_task_schedule_requirement.schedule_policy_id IS 'Associated schedule policy ID';
COMMENT ON COLUMN inspection_task_schedule_requirement.schedule_templates IS 'Template config list (JSON): templateId, templateName, originalConfig, changedFields, enabled';
COMMENT ON COLUMN inspection_task_schedule_requirement.description IS 'Requirement description';

-- inspection_task_schedule_policy table
CREATE TABLE inspection_task_schedule_policy (
    id                  BIGINT      NOT NULL PRIMARY KEY,
    policy_code         VARCHAR(64) NOT NULL,
    policy_name         VARCHAR(255) NOT NULL,
    policy_type         SMALLINT    NOT NULL,
    cron_expression     VARCHAR(128) DEFAULT NULL,
    cycle_description   VARCHAR(255) DEFAULT NULL,
    trigger_time       VARCHAR(32) DEFAULT NULL,
    enabled             BOOLEAN     NOT NULL DEFAULT TRUE,
    remark              TEXT        DEFAULT NULL,
    creator             VARCHAR(64) DEFAULT '',
    create_time         TIMESTAMP    DEFAULT NULL,
    updater             VARCHAR(64) DEFAULT '',
    update_time         TIMESTAMP    DEFAULT NULL,
    deleted             BOOLEAN     NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT      NOT NULL DEFAULT 100,
    CONSTRAINT uk_inspection_task_schedule_policy_code UNIQUE (policy_code, tenant_id)
);

COMMENT ON TABLE inspection_task_schedule_policy IS 'Inspection task schedule policy table';
COMMENT ON COLUMN inspection_task_schedule_policy.policy_type IS 'Policy type: 1-cycle, 2-event, 3-manual';
COMMENT ON COLUMN inspection_task_schedule_policy.trigger_time IS 'Trigger time in HH:mm format';

-- inspection_task_schedule_resource table
CREATE TABLE inspection_task_schedule_resource (
    id               BIGINT      NOT NULL PRIMARY KEY,
    task_id          BIGINT      NOT NULL,
    schedule_id      BIGINT      DEFAULT NULL,
    resource_type    SMALLINT    NOT NULL,
    resource_id      BIGINT      NOT NULL,
    resource_name    VARCHAR(255) NOT NULL,
    resource_config  TEXT        DEFAULT NULL,
    sort_no          INTEGER     NOT NULL DEFAULT 0,
    creator          VARCHAR(64) DEFAULT '',
    create_time      TIMESTAMP    DEFAULT NULL,
    updater          VARCHAR(64) DEFAULT '',
    update_time      TIMESTAMP    DEFAULT NULL,
    deleted          BOOLEAN     NOT NULL DEFAULT FALSE,
    tenant_id        BIGINT      NOT NULL DEFAULT 100
);

COMMENT ON TABLE inspection_task_schedule_resource IS 'Inspection task schedule resource table';
COMMENT ON COLUMN inspection_task_schedule_resource.resource_type IS 'Resource type: 1-person, 2-device, 3-vehicle';
COMMENT ON COLUMN inspection_task_schedule_resource.resource_config IS 'Resource config (JSON)';

-- inspection_task_schedule_plan table
CREATE TABLE inspection_task_schedule_plan (
    id                       BIGINT      NOT NULL PRIMARY KEY,
    plan_code                VARCHAR(64) NOT NULL,
    task_id                  BIGINT      NOT NULL,
    schedule_requirement_id  BIGINT      NOT NULL,
    horizon_start_date       DATE        NOT NULL,
    horizon_end_date         DATE        NOT NULL,
    schedule_count           INTEGER     NOT NULL DEFAULT 0,
    plan_status              SMALLINT    NOT NULL DEFAULT 1,
    trigger_type             SMALLINT    NOT NULL DEFAULT 1,
    trigger_by               VARCHAR(255) DEFAULT NULL,
    activated_by             BIGINT      DEFAULT NULL,
    activated_at             TIMESTAMP    DEFAULT NULL,
    remark                   TEXT        DEFAULT NULL,
    creator                  VARCHAR(64) DEFAULT '',
    create_time              TIMESTAMP    DEFAULT NULL,
    updater                  VARCHAR(64) DEFAULT '',
    update_time              TIMESTAMP    DEFAULT NULL,
    deleted                  BOOLEAN     NOT NULL DEFAULT FALSE,
    tenant_id                BIGINT      NOT NULL DEFAULT 100,
    CONSTRAINT uk_inspection_task_schedule_plan_code UNIQUE (plan_code, tenant_id)
);

COMMENT ON TABLE inspection_task_schedule_plan IS 'Inspection task schedule plan table';
COMMENT ON COLUMN inspection_task_schedule_plan.plan_status IS 'Plan status: 1-pending, 2-activated, 3-stopped';
COMMENT ON COLUMN inspection_task_schedule_plan.trigger_type IS 'Trigger type: 1-manual, 2-scheduled, 3-fault-recovery';

-- inspection_task_schedule table
CREATE TABLE inspection_task_schedule (
    id               BIGINT      NOT NULL PRIMARY KEY,
    plan_id          BIGINT      NOT NULL,
    task_id          BIGINT      NOT NULL,
    task_name        VARCHAR(255) DEFAULT NULL,
    scheduled_time   TIMESTAMP    NOT NULL,
    start_time       TIMESTAMP    DEFAULT NULL,
    end_time         TIMESTAMP    DEFAULT NULL,
    status           SMALLINT    NOT NULL DEFAULT 1,
    executor_id      BIGINT      DEFAULT NULL,
    executor_name    VARCHAR(255) DEFAULT NULL,
    result_summary   TEXT        DEFAULT NULL,
    remark           TEXT        DEFAULT NULL,
    creator          VARCHAR(64) DEFAULT '',
    create_time      TIMESTAMP    DEFAULT NULL,
    updater          VARCHAR(64) DEFAULT '',
    update_time      TIMESTAMP    DEFAULT NULL,
    deleted          BOOLEAN     NOT NULL DEFAULT FALSE,
    tenant_id        BIGINT      NOT NULL DEFAULT 100
);

COMMENT ON TABLE inspection_task_schedule IS 'Inspection task schedule table';
COMMENT ON COLUMN inspection_task_schedule.status IS 'Schedule status: 1-pending, 2-in-progress, 3-completed, 4-timeout, 5-cancelled';
COMMENT ON COLUMN inspection_task_schedule.result_summary IS 'Inspection result summary (JSON)';

-- inspection_task_execution table
CREATE TABLE inspection_task_execution (
    id               BIGINT      NOT NULL PRIMARY KEY,
    schedule_id      BIGINT      NOT NULL,
    task_id          BIGINT      NOT NULL,
    execution_no     VARCHAR(64) NOT NULL,
    status           SMALLINT    NOT NULL DEFAULT 1,
    executor_id      BIGINT      DEFAULT NULL,
    executor_name    VARCHAR(255) DEFAULT NULL,
    start_time       TIMESTAMP    DEFAULT NULL,
    end_time         TIMESTAMP    DEFAULT NULL,
    actual_duration  INTEGER      DEFAULT NULL,
    result_data      TEXT        DEFAULT NULL,
    remark           TEXT        DEFAULT NULL,
    creator          VARCHAR(64) DEFAULT '',
    create_time      TIMESTAMP    DEFAULT NULL,
    updater          VARCHAR(64) DEFAULT '',
    update_time      TIMESTAMP    DEFAULT NULL,
    deleted          BOOLEAN     NOT NULL DEFAULT FALSE,
    tenant_id        BIGINT      NOT NULL DEFAULT 100,
    CONSTRAINT uk_inspection_task_execution_no UNIQUE (execution_no, tenant_id)
);

COMMENT ON TABLE inspection_task_execution IS 'Inspection task execution table';
COMMENT ON COLUMN inspection_task_execution.status IS 'Execution status: 1-pending, 2-in-progress, 3-completed, 4-timeout, 5-cancelled';
COMMENT ON COLUMN inspection_task_execution.actual_duration IS 'Actual execution duration (seconds)';
COMMENT ON COLUMN inspection_task_execution.result_data IS 'Execution result data (JSON)';

-- inspection_object_collection table
CREATE TABLE inspection_object_collection (
    id               BIGINT      NOT NULL PRIMARY KEY,
    object_id        BIGINT      NOT NULL,
    collection_type  SMALLINT    NOT NULL,
    collection_url   VARCHAR(1024) NOT NULL,
    collection_time  TIMESTAMP    NOT NULL,
    thumbnail_url    VARCHAR(1024) DEFAULT NULL,
    file_size        BIGINT      DEFAULT NULL,
    duration         INTEGER      DEFAULT NULL,
    remark           TEXT        DEFAULT NULL,
    creator          VARCHAR(64) DEFAULT '',
    create_time      TIMESTAMP    DEFAULT NULL,
    updater          VARCHAR(64) DEFAULT '',
    update_time      TIMESTAMP    DEFAULT NULL,
    deleted          BOOLEAN     NOT NULL DEFAULT FALSE,
    tenant_id        BIGINT      NOT NULL DEFAULT 100
);

COMMENT ON TABLE inspection_object_collection IS 'Inspection object collection table';
COMMENT ON COLUMN inspection_object_collection.collection_type IS 'Collection type: 1-image, 2-video, 3-file';
COMMENT ON COLUMN inspection_object_collection.duration IS 'Video duration (seconds)';

-- inspection_object_source table
CREATE TABLE inspection_object_source (
    id               BIGINT      NOT NULL PRIMARY KEY,
    object_id        BIGINT      NOT NULL,
    source_type      SMALLINT    NOT NULL,
    source_system    VARCHAR(128) DEFAULT NULL,
    source_device    VARCHAR(128) DEFAULT NULL,
    upload_time      TIMESTAMP    NOT NULL,
    upload_by        VARCHAR(255) DEFAULT NULL,
    remark           TEXT        DEFAULT NULL,
    creator          VARCHAR(64) DEFAULT '',
    create_time      TIMESTAMP    DEFAULT NULL,
    updater          VARCHAR(64) DEFAULT '',
    update_time      TIMESTAMP    DEFAULT NULL,
    deleted          BOOLEAN     NOT NULL DEFAULT FALSE,
    tenant_id        BIGINT      NOT NULL DEFAULT 100
);

COMMENT ON TABLE inspection_object_source IS 'Inspection object source table';
COMMENT ON COLUMN inspection_object_source.source_type IS 'Source type: 1-manual, 2-device, 3-system';

-- ============================================================================
-- Indexes
-- ============================================================================

CREATE INDEX idx_inspection_task_category ON inspection_task (category_id);
CREATE INDEX idx_inspection_task_status ON inspection_task (status);
CREATE INDEX idx_inspection_task_enabled ON inspection_task (enabled);
CREATE INDEX idx_inspection_task_schedule_req ON inspection_task (schedule_requirement_id);
CREATE INDEX idx_inspection_task_active_plan ON inspection_task (active_plan_id);

CREATE INDEX idx_requirement_task ON inspection_task_schedule_requirement (task_id);
CREATE INDEX idx_requirement_policy ON inspection_task_schedule_requirement (schedule_policy_id);

CREATE INDEX idx_policy_type ON inspection_task_schedule_policy (policy_type);
CREATE INDEX idx_policy_enabled ON inspection_task_schedule_policy (enabled);

CREATE INDEX idx_resource_task ON inspection_task_schedule_resource (task_id);
CREATE INDEX idx_resource_schedule ON inspection_task_schedule_resource (schedule_id);
CREATE INDEX idx_resource_type ON inspection_task_schedule_resource (resource_type);

CREATE INDEX idx_plan_task ON inspection_task_schedule_plan (task_id);
CREATE INDEX idx_plan_requirement ON inspection_task_schedule_plan (schedule_requirement_id);
CREATE INDEX idx_plan_status ON inspection_task_schedule_plan (plan_status);
CREATE INDEX idx_plan_dates ON inspection_task_schedule_plan (horizon_start_date, horizon_end_date);

CREATE INDEX idx_schedule_plan ON inspection_task_schedule (plan_id);
CREATE INDEX idx_schedule_task ON inspection_task_schedule (task_id);
CREATE INDEX idx_schedule_status ON inspection_task_schedule (status);
CREATE INDEX idx_schedule_executor ON inspection_task_schedule (executor_id);
CREATE INDEX idx_schedule_time ON inspection_task_schedule (scheduled_time);

CREATE INDEX idx_execution_schedule ON inspection_task_execution (schedule_id);
CREATE INDEX idx_execution_task ON inspection_task_execution (task_id);
CREATE INDEX idx_execution_status ON inspection_task_execution (status);
CREATE INDEX idx_execution_executor ON inspection_task_execution (executor_id);

CREATE INDEX idx_collection_object ON inspection_object_collection (object_id);
CREATE INDEX idx_collection_type ON inspection_object_collection (collection_type);
CREATE INDEX idx_collection_time ON inspection_object_collection (collection_time);

CREATE INDEX idx_source_object ON inspection_object_source (object_id);
CREATE INDEX idx_source_type ON inspection_object_source (source_type);
