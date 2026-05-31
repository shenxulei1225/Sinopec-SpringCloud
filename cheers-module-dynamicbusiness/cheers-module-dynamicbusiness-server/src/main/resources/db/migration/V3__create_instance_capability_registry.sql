-- ============================================================================
-- Instance Capability Registry - 实例能力注册表
-- ============================================================================

SET search_path TO dynamicbusiness;

CREATE TABLE IF NOT EXISTS dynamic_instance_capability_registry (
    id                  BIGSERIAL PRIMARY KEY,
    instance_key        VARCHAR(256) NOT NULL,
    domain              VARCHAR(64)  NOT NULL,
    business_type_code  VARCHAR(64),
    model_id            BIGINT,
    resource_code       VARCHAR(64),
    label               VARCHAR(256) NOT NULL,
    contract_json       JSONB        NOT NULL,
    version             INTEGER      NOT NULL DEFAULT 1,
    status              SMALLINT     NOT NULL DEFAULT 1,
    creator             VARCHAR(64)  NOT NULL DEFAULT '',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_instance_capability_registry_key
    ON dynamic_instance_capability_registry (instance_key, tenant_id)
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_instance_capability_registry_business
    ON dynamic_instance_capability_registry (business_type_code, tenant_id)
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_instance_capability_registry_model
    ON dynamic_instance_capability_registry (model_id, tenant_id)
    WHERE deleted = FALSE;
