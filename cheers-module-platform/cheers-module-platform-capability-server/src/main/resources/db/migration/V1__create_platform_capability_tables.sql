SET search_path TO platform;

CREATE TABLE IF NOT EXISTS platform_capability_pack (
    id                      VARCHAR(64) PRIMARY KEY,
    display_name            VARCHAR(128) NOT NULL,
    domain                  VARCHAR(64)  NOT NULL,
    version                 VARCHAR(32)  NOT NULL,
    required_engines        JSONB        NOT NULL DEFAULT '[]',
    orchestration_ref       VARCHAR(128),
    default_policy_template_id VARCHAR(64),
    manifest                JSONB        NOT NULL,
    enabled                 BOOLEAN      NOT NULL DEFAULT TRUE,
    tenant_id               BIGINT       NOT NULL DEFAULT 0,
    creator                 VARCHAR(64)  DEFAULT '',
    create_time             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                 VARCHAR(64)  DEFAULT '',
    update_time             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                 BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS platform_process_capability_binding (
    id                  BIGSERIAL PRIMARY KEY,
    business_type_code  VARCHAR(64)  NOT NULL,
    capability_pack_id  VARCHAR(64)  NOT NULL,
    orchestration_ref   VARCHAR(128),
    policy_set_id       VARCHAR(36),
    mapping_profile_ids JSONB        NOT NULL DEFAULT '[]',
    engine_bindings     JSONB        NOT NULL DEFAULT '{}',
    status              VARCHAR(16)  NOT NULL,
    version             INTEGER      NOT NULL DEFAULT 0,
    site_id             BIGINT,
    tenant_id           BIGINT       NOT NULL DEFAULT 0,
    creator             VARCHAR(64)  DEFAULT '',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_platform_process_binding_btc_tenant
    ON platform_process_capability_binding (business_type_code, tenant_id)
    WHERE deleted = FALSE;
