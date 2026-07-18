SET search_path TO platform;

CREATE TABLE IF NOT EXISTS platform_runtime_job (
    id                  VARCHAR(36) PRIMARY KEY,
    entity_type_code  VARCHAR(64)  NOT NULL,
    trigger_action      VARCHAR(64)  NOT NULL,
    status              VARCHAR(32)  NOT NULL,
    source_work_ids     JSONB        NOT NULL DEFAULT '[]',
    policy_snapshot_id  VARCHAR(64),
    orchestration_ref   VARCHAR(128),
    site_id             BIGINT,
    tenant_id           BIGINT       NOT NULL DEFAULT 0,
    creator             VARCHAR(64)  DEFAULT '',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_platform_runtime_job_business_type
    ON platform_runtime_job (entity_type_code);
CREATE INDEX IF NOT EXISTS idx_platform_runtime_job_tenant
    ON platform_runtime_job (tenant_id);

CREATE TABLE IF NOT EXISTS platform_schedule_slot (
    id                  VARCHAR(36) PRIMARY KEY,
    runtime_job_id      VARCHAR(36)  NOT NULL REFERENCES platform_runtime_job (id),
    work_id             VARCHAR(64)  NOT NULL,
    entity_type_code  VARCHAR(64)  NOT NULL,
    planned_start       TIMESTAMPTZ  NOT NULL,
    planned_end         TIMESTAMPTZ  NOT NULL,
    assigned_resources  JSONB,
    lock_state          VARCHAR(16)  NOT NULL,
    slot_status         VARCHAR(16)  NOT NULL,
    policy_snapshot_id  VARCHAR(64),
    decision_trace_id   VARCHAR(64),
    site_id             BIGINT,
    tenant_id           BIGINT       NOT NULL DEFAULT 0,
    creator             VARCHAR(64)  DEFAULT '',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_platform_schedule_slot_job
    ON platform_schedule_slot (runtime_job_id);
CREATE INDEX IF NOT EXISTS idx_platform_schedule_slot_business_type
    ON platform_schedule_slot (entity_type_code);
CREATE INDEX IF NOT EXISTS idx_platform_schedule_slot_planned_start
    ON platform_schedule_slot (planned_start);
