SET search_path TO maintenance;

CREATE TABLE IF NOT EXISTS mm_calendar_entry (
    id              BIGSERIAL PRIMARY KEY,
    handbook_id     BIGINT       NOT NULL,
    asset_id        BIGINT       NOT NULL,
    scope           VARCHAR(32)  NOT NULL,
    planned_date    DATE         NOT NULL,
    status          VARCHAR(32)  NOT NULL,
    runtime_job_id  VARCHAR(64),
    work_order_ids  JSONB,
    last_error      VARCHAR(512),
    creator         VARCHAR(64)  DEFAULT '',
    create_time     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updater         VARCHAR(64)  DEFAULT '',
    update_time     TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id       BIGINT       NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uk_mm_cal_asset_day ON mm_calendar_entry (tenant_id, handbook_id, asset_id, planned_date) WHERE deleted = FALSE;

CREATE SEQUENCE IF NOT EXISTS mm_calendar_entry_seq START WITH 1000 INCREMENT BY 1;
ALTER TABLE mm_calendar_entry ALTER COLUMN id DROP DEFAULT;
SELECT setval('mm_calendar_entry_seq',
              GREATEST(COALESCE((SELECT MAX(id) FROM mm_calendar_entry), 0), 999) + 1,
              false);
DROP SEQUENCE IF EXISTS mm_calendar_entry_id_seq;
