SET search_path TO maintenance;

CREATE TABLE IF NOT EXISTS mm_binding_rule (
    id                    BIGSERIAL PRIMARY KEY,
    code                  VARCHAR(64)  NOT NULL,
    name                  VARCHAR(128) NOT NULL,
    scope                 VARCHAR(32)  NOT NULL,
    asset_id              BIGINT,
    asset_type_code       VARCHAR(64),
    frequency_code        VARCHAR(32),
    handbook_id           BIGINT,
    field_standard_id     BIGINT       NOT NULL,
    orchestration_template_code VARCHAR(64),
    priority              INT          NOT NULL DEFAULT 0,
    status                SMALLINT     NOT NULL DEFAULT 0,
    creator               VARCHAR(64)  DEFAULT '',
    create_time           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updater               VARCHAR(64)  DEFAULT '',
    update_time           TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted               BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id             BIGINT       NOT NULL DEFAULT 0
);
CREATE INDEX ix_mm_binding_scope ON mm_binding_rule (tenant_id, scope, status) WHERE deleted = FALSE;

CREATE SEQUENCE IF NOT EXISTS mm_binding_rule_seq START WITH 1000 INCREMENT BY 1;
ALTER TABLE mm_binding_rule ALTER COLUMN id DROP DEFAULT;
SELECT setval('mm_binding_rule_seq',
              GREATEST(COALESCE((SELECT MAX(id) FROM mm_binding_rule), 0), 999) + 1,
              false);
DROP SEQUENCE IF EXISTS mm_binding_rule_id_seq;
