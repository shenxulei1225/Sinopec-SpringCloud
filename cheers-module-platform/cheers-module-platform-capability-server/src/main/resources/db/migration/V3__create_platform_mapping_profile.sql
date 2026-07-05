SET search_path TO platform;

CREATE TABLE IF NOT EXISTS platform_mapping_profile (
    id                      VARCHAR(64)  PRIMARY KEY,
    business_type_code      VARCHAR(64)  NOT NULL,
    source_model_code       VARCHAR(64)  NOT NULL,
    display_name            VARCHAR(128),
    field_mappings          JSONB        NOT NULL DEFAULT '{}',
    default_duration_minutes INTEGER     NOT NULL DEFAULT 60,
    default_priority        INTEGER      NOT NULL DEFAULT 5,
    status                  VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE',
    tenant_id               BIGINT       NOT NULL DEFAULT 0,
    creator                 VARCHAR(64)  DEFAULT '',
    create_time             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                 VARCHAR(64)  DEFAULT '',
    update_time             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                 BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_platform_mapping_profile_btc
    ON platform_mapping_profile (business_type_code)
    WHERE deleted = FALSE;
