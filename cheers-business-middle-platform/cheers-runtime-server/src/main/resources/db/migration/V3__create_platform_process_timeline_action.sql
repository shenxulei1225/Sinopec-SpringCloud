SET search_path TO platform;

CREATE TABLE IF NOT EXISTS platform_process_timeline_action (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL DEFAULT 0,
    site_id         BIGINT,
    target_type     VARCHAR(64)  NOT NULL,
    target_id       VARCHAR(64)  NOT NULL,
    occurred_at     TIMESTAMPTZ  NOT NULL,
    actor_id        VARCHAR(64),
    actor_name      VARCHAR(128),
    action_code     VARCHAR(64)  NOT NULL,
    how_summary     VARCHAR(512) NOT NULL,
    payload_json    JSONB,
    creator         VARCHAR(64)  DEFAULT '',
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)  DEFAULT '',
    update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_ppta_target_time
    ON platform_process_timeline_action (tenant_id, target_type, target_id, occurred_at DESC);
