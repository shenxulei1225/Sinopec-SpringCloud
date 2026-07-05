SET search_path TO platform;

CREATE TABLE IF NOT EXISTS platform_policy_template (
    id              VARCHAR(64) PRIMARY KEY,
    display_name    VARCHAR(128) NOT NULL,
    domain          VARCHAR(64)  NOT NULL,
    version         VARCHAR(32)  NOT NULL,
    default_spec    JSONB        NOT NULL,
    enabled         BOOLEAN      NOT NULL DEFAULT TRUE,
    tenant_id       BIGINT       NOT NULL DEFAULT 0,
    creator         VARCHAR(64)  DEFAULT '',
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)  DEFAULT '',
    update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS platform_policy_set (
    id                  VARCHAR(36) PRIMARY KEY,
    business_type_code  VARCHAR(64)  NOT NULL,
    template_id         VARCHAR(64)  NOT NULL,
    status              VARCHAR(16)  NOT NULL,
    version             INTEGER      NOT NULL DEFAULT 0,
    spec_params         JSONB        NOT NULL,
    site_id             BIGINT,
    tenant_id           BIGINT       NOT NULL DEFAULT 0,
    creator             VARCHAR(64)  DEFAULT '',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_platform_policy_set_btc
    ON platform_policy_set (business_type_code);

CREATE TABLE IF NOT EXISTS platform_policy_snapshot (
    id                  VARCHAR(64) PRIMARY KEY,
    policy_set_id       VARCHAR(36)  NOT NULL,
    policy_set_version  INTEGER      NOT NULL,
    business_type_code  VARCHAR(64)  NOT NULL,
    platform_law_version VARCHAR(32) NOT NULL DEFAULT '2.4.0-mvp',
    resolved_spec       JSONB        NOT NULL,
    provenance_index    JSONB        NOT NULL DEFAULT '[]',
    site_id             BIGINT,
    tenant_id           BIGINT       NOT NULL DEFAULT 0,
    creator             VARCHAR(64)  DEFAULT '',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_platform_policy_snapshot_set
    ON platform_policy_snapshot (policy_set_id);
