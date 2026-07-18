SET search_path TO work_order;

CREATE TABLE IF NOT EXISTS wo_field_work_standard (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(64)  NOT NULL,
    name            VARCHAR(128) NOT NULL,
    version_no      INT          NOT NULL DEFAULT 1,
    scope           VARCHAR(32)  NOT NULL,
    steps_json      JSONB        NOT NULL,
    status          SMALLINT     NOT NULL DEFAULT 0,
    creator         VARCHAR(64)  DEFAULT '',
    create_time     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updater         VARCHAR(64)  DEFAULT '',
    update_time     TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id       BIGINT       NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uk_wo_std_code_ver ON wo_field_work_standard (tenant_id, code, version_no) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS wo_work_order (
    id                    BIGSERIAL PRIMARY KEY,
    wo_no                 VARCHAR(64)  NOT NULL,
    scope                 VARCHAR(32)  NOT NULL,
    status                VARCHAR(32)  NOT NULL,
    asset_id              BIGINT,
    asset_type_code       VARCHAR(64),
    frequency_code        VARCHAR(32),
    standard_id           BIGINT       NOT NULL,
    standard_version_no   INT          NOT NULL,
    standard_snapshot_json JSONB       NOT NULL,
    runtime_job_id        VARCHAR(64),
    schedule_slot_id      VARCHAR(64),
    business_key          VARCHAR(128),
    assignee_user_id      BIGINT,
    title                 VARCHAR(256) NOT NULL,
    creator               VARCHAR(64)  DEFAULT '',
    create_time           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updater               VARCHAR(64)  DEFAULT '',
    update_time           TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted               BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id             BIGINT       NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uk_wo_no ON wo_work_order (tenant_id, wo_no) WHERE deleted = FALSE;
CREATE INDEX ix_wo_scope_status ON wo_work_order (tenant_id, scope, status) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS wo_work_order_step_result (
    id              BIGSERIAL PRIMARY KEY,
    work_order_id   BIGINT       NOT NULL,
    step_code       VARCHAR(64)  NOT NULL,
    step_order      INT          NOT NULL,
    completed       BOOLEAN      NOT NULL DEFAULT FALSE,
    result_json     JSONB,
    attachment_ids  JSONB,
    creator         VARCHAR(64)  DEFAULT '',
    create_time     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updater         VARCHAR(64)  DEFAULT '',
    update_time     TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id       BIGINT       NOT NULL DEFAULT 0
);
CREATE INDEX ix_wo_step_wo ON wo_work_order_step_result (work_order_id) WHERE deleted = FALSE;
