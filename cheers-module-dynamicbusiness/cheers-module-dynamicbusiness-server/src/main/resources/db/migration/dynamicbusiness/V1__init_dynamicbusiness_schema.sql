-- ============================================================================
-- Dynamic Business Module - Initial Schema (aligned with DO definitions)
-- Schema: dynamicbusiness (see application-local.yaml flyway config)
-- ============================================================================

CREATE SCHEMA IF NOT EXISTS dynamicbusiness;
SET search_path TO dynamicbusiness;

-- ============================================================================
-- 1. Business Type
-- ============================================================================

CREATE TABLE IF NOT EXISTS dynamic_business_type (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(64)  NOT NULL,
    name            VARCHAR(200) NOT NULL,
    parent_id       BIGINT,
    description     VARCHAR(500),
    icon            VARCHAR(128),
    alias           VARCHAR(128),
    sort            INTEGER      NOT NULL DEFAULT 0,
    status          VARCHAR(32)  NOT NULL DEFAULT 'active',
    type_level      VARCHAR(32)  NOT NULL DEFAULT 'USER',
    association_fields      JSONB,
    storage_type            VARCHAR(32)  NOT NULL DEFAULT 'GENERIC',
    dedicated_table_name    VARCHAR(128),
    enable_rule_engine      BOOLEAN      NOT NULL DEFAULT FALSE,
    physical_column_mapping JSONB,
    creator         VARCHAR(64)  NOT NULL DEFAULT '',
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)  DEFAULT '',
    update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id       BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_business_type_code
    ON dynamic_business_type (code, tenant_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_business_type_base_field (
    id                  BIGSERIAL PRIMARY KEY,
    business_type_code  VARCHAR(64)  NOT NULL,
    field_code          VARCHAR(64)  NOT NULL,
    field_name          VARCHAR(128) NOT NULL,
    data_type           VARCHAR(32)  NOT NULL,
    required            BOOLEAN      NOT NULL DEFAULT FALSE,
    default_value       VARCHAR(500),
    description         VARCHAR(500),
    type_config         TEXT,
    sort_order          INTEGER      NOT NULL DEFAULT 0,
    status              SMALLINT     NOT NULL DEFAULT 1,
    creator             VARCHAR(64)  NOT NULL DEFAULT '',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_business_type_base_field
    ON dynamic_business_type_base_field (business_type_code, field_code, tenant_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_business_type_relation (
    id                       BIGSERIAL PRIMARY KEY,
    source_business_type_code VARCHAR(64) NOT NULL,
    target_business_type_code VARCHAR(64) NOT NULL,
    relation_name            VARCHAR(128),
    auto_create_field        BOOLEAN      NOT NULL DEFAULT FALSE,
    default_field_name       VARCHAR(128),
    creator                  VARCHAR(64) NOT NULL DEFAULT '',
    create_time              TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                  VARCHAR(64) DEFAULT '',
    update_time              TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                  BOOLEAN     NOT NULL DEFAULT FALSE,
    tenant_id                BIGINT      NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_business_type_relation
    ON dynamic_business_type_relation (source_business_type_code, target_business_type_code, tenant_id)
    WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_business_type_config (
    id                  BIGSERIAL PRIMARY KEY,
    business_type_code  VARCHAR(64)  NOT NULL,
    name                VARCHAR(200) NOT NULL,
    storage_type        VARCHAR(32)  NOT NULL DEFAULT 'GENERIC',
    dedicated_table_name VARCHAR(128),
    strategy_bean_name  VARCHAR(128),
    enable_rule_engine   BOOLEAN      NOT NULL DEFAULT FALSE,
    description         VARCHAR(500),
    status              SMALLINT     NOT NULL DEFAULT 1,
    creator             VARCHAR(64)  NOT NULL DEFAULT '',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT       NOT NULL DEFAULT 0,
    physical_column_mapping JSONB
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_business_type_config
    ON dynamic_business_type_config (business_type_code, tenant_id) WHERE deleted = FALSE;

-- ============================================================================
-- 2. Field / Unit / Reference Provider
-- ============================================================================

CREATE TABLE IF NOT EXISTS dynamic_field (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(64)  NOT NULL,
    name            VARCHAR(200) NOT NULL,
    type            VARCHAR(32)  NOT NULL,
    unit            VARCHAR(64),
    description     VARCHAR(500),
    source          VARCHAR(32)  NOT NULL DEFAULT 'USER',
    status          SMALLINT     NOT NULL DEFAULT 1,
    max_relations   INTEGER,
    index_strategy  VARCHAR(32),
    options         TEXT,
    provider_code   VARCHAR(64),
    semantic_type   VARCHAR(64),
    creator         VARCHAR(64)  NOT NULL DEFAULT '',
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)  DEFAULT '',
    update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id       BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_field_code
    ON dynamic_field (code, tenant_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_unit (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    code        VARCHAR(64)  NOT NULL,
    unit_type   VARCHAR(64),
    sort        INTEGER      NOT NULL DEFAULT 1,
    status      SMALLINT     NOT NULL DEFAULT 1,
    remark      VARCHAR(500),
    creator     VARCHAR(64)  NOT NULL DEFAULT '',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)  DEFAULT '',
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id   BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_unit_tenant_code
    ON dynamic_unit (tenant_id, code) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_reference_provider (
    id               BIGSERIAL PRIMARY KEY,
    provider_code    VARCHAR(64)  NOT NULL,
    provider_name    VARCHAR(128) NOT NULL,
    provider_type    VARCHAR(32)  NOT NULL DEFAULT 'INTERNAL',
    semantic_type    VARCHAR(64),
    capability_flags TEXT,
    config_json      TEXT,
    status           SMALLINT     NOT NULL DEFAULT 1,
    tenant_scope     VARCHAR(32)  DEFAULT 'GLOBAL',
    priority         INTEGER      DEFAULT 100,
    health_status    VARCHAR(32)  DEFAULT 'UNKNOWN',
    creator          VARCHAR(64)  NOT NULL DEFAULT '',
    create_time      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater          VARCHAR(64)  DEFAULT '',
    update_time      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id        BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_reference_provider_code
    ON dynamic_reference_provider (provider_code, tenant_id) WHERE deleted = FALSE;

-- ============================================================================
-- 3. Model / Template
-- ============================================================================

CREATE TABLE IF NOT EXISTS dynamic_model (
    id                  BIGSERIAL PRIMARY KEY,
    code                VARCHAR(64)  NOT NULL,
    name                VARCHAR(200) NOT NULL,
    business_type_code  VARCHAR(64)  NOT NULL,
    description         VARCHAR(500),
    status              SMALLINT     NOT NULL DEFAULT 1,
    sort                INTEGER      NOT NULL DEFAULT 0,
    field_groups_config TEXT,
    creator             VARCHAR(64)  NOT NULL DEFAULT '',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_model_code
    ON dynamic_model (code, tenant_id) WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_model_business_type
    ON dynamic_model (business_type_code, tenant_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_model_field_assignment (
    id                   BIGSERIAL PRIMARY KEY,
    model_id             BIGINT       NOT NULL,
    field_id             BIGINT       NOT NULL,
    required             BOOLEAN      NOT NULL DEFAULT FALSE,
    is_searchable        BOOLEAN      NOT NULL DEFAULT FALSE,
    is_filterable        BOOLEAN      NOT NULL DEFAULT FALSE,
    is_sortable          BOOLEAN      NOT NULL DEFAULT FALSE,
    default_value        VARCHAR(500),
    validation_rules     TEXT,
    sort                 INTEGER      NOT NULL DEFAULT 0,
    field_group_id       BIGINT,
    field_source         VARCHAR(32),
    ref_library_id       BIGINT,
    model_relation_id    BIGINT,
    target_business_type VARCHAR(64),
    creator              VARCHAR(64)  NOT NULL DEFAULT '',
    create_time          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater              VARCHAR(64)  DEFAULT '',
    update_time          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted              BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id            BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_model_field_assignment
    ON dynamic_model_field_assignment (model_id, field_id, tenant_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_model_relation (
    id                       BIGSERIAL PRIMARY KEY,
    business_type_relation_id BIGINT,
    source_model_id          BIGINT       NOT NULL,
    source_model_code        VARCHAR(64)  NOT NULL,
    target_model_id          BIGINT       NOT NULL,
    target_model_code        VARCHAR(64)  NOT NULL,
    relation_name            VARCHAR(128),
    field_code               VARCHAR(64),
    auto_generated           BOOLEAN      NOT NULL DEFAULT FALSE,
    creator                  VARCHAR(64)  NOT NULL DEFAULT '',
    create_time              TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                  VARCHAR(64)  DEFAULT '',
    update_time              TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                  BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id                BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_dynamic_model_relation_source
    ON dynamic_model_relation (source_model_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_model_relation_declaration (
    id                   BIGSERIAL PRIMARY KEY,
    model_id             BIGINT       NOT NULL,
    target_business_type VARCHAR(64)  NOT NULL,
    creator              VARCHAR(64)  NOT NULL DEFAULT '',
    create_time          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater              VARCHAR(64)  DEFAULT '',
    update_time          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted              BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id            BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_model_relation_declaration
    ON dynamic_model_relation_declaration (model_id, target_business_type, tenant_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_model_category_relation (
    id                  BIGSERIAL PRIMARY KEY,
    model_id            BIGINT       NOT NULL,
    category_id         BIGINT       NOT NULL,
    business_type_code  VARCHAR(64),
    sort                INTEGER      NOT NULL DEFAULT 0,
    creator             VARCHAR(64)  NOT NULL DEFAULT '',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_dynamic_model_category_relation_model
    ON dynamic_model_category_relation (model_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_template (
    id                  BIGSERIAL PRIMARY KEY,
    code                VARCHAR(64)  NOT NULL,
    name                VARCHAR(200) NOT NULL,
    business_type_code  VARCHAR(64)  NOT NULL,
    description         VARCHAR(500),
    status              SMALLINT     NOT NULL DEFAULT 1,
    is_system           BOOLEAN      NOT NULL DEFAULT FALSE,
    creator             VARCHAR(64)  NOT NULL DEFAULT '',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_template_code
    ON dynamic_template (code, tenant_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_template_field_assignment (
    id              BIGSERIAL PRIMARY KEY,
    template_id     BIGINT       NOT NULL,
    field_id        BIGINT       NOT NULL,
    sort_order      INTEGER      NOT NULL DEFAULT 0,
    required        BOOLEAN      NOT NULL DEFAULT FALSE,
    default_value   VARCHAR(500),
    creator         VARCHAR(64)  NOT NULL DEFAULT '',
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)  DEFAULT '',
    update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id       BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_template_field_assignment
    ON dynamic_template_field_assignment (template_id, field_id, tenant_id) WHERE deleted = FALSE;

-- ============================================================================
-- 4. Category
-- ============================================================================

CREATE TABLE IF NOT EXISTS dynamic_category_type (
    id                    BIGSERIAL PRIMARY KEY,
    category_type_code    VARCHAR(50)  NOT NULL,
    name                  VARCHAR(100) NOT NULL,
    description           VARCHAR(500),
    status                INTEGER      NOT NULL DEFAULT 1,
    top_level_category_id BIGINT,
    creator               VARCHAR(64)  NOT NULL DEFAULT '',
    create_time           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater               VARCHAR(64)  DEFAULT '',
    update_time           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted               BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id             BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_category_type_code
    ON dynamic_category_type (category_type_code, tenant_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_category (
    id                  BIGSERIAL PRIMARY KEY,
    parent_id           BIGINT,
    name                VARCHAR(200) NOT NULL,
    code                VARCHAR(64)  NOT NULL,
    category_type_code  VARCHAR(50)  NOT NULL,
    tree_path           VARCHAR(1024),
    level               INTEGER,
    sort                INTEGER      NOT NULL DEFAULT 0,
    status              SMALLINT     NOT NULL DEFAULT 1,
    description         VARCHAR(500),
    creator             VARCHAR(64)  NOT NULL DEFAULT '',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_category_code
    ON dynamic_category (code, tenant_id) WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_category_type_code
    ON dynamic_category (category_type_code, tenant_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_category_entity_link (
    id              BIGSERIAL PRIMARY KEY,
    category_id     BIGINT       NOT NULL,
    entity_id       BIGINT       NOT NULL,
    entity_model_id BIGINT,
    creator         VARCHAR(64)  NOT NULL DEFAULT '',
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)  DEFAULT '',
    update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id       BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_dynamic_category_entity_link_category
    ON dynamic_category_entity_link (category_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_category_permission (
    id          BIGSERIAL PRIMARY KEY,
    role_id     BIGINT       NOT NULL,
    category_id BIGINT       NOT NULL,
    can_view    BOOLEAN      NOT NULL DEFAULT TRUE,
    can_manage  BOOLEAN      NOT NULL DEFAULT FALSE,
    creator     VARCHAR(64)  NOT NULL DEFAULT '',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)  DEFAULT '',
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id   BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_category_permission
    ON dynamic_category_permission (role_id, category_id, tenant_id) WHERE deleted = FALSE;

-- ============================================================================
-- 5. Entity
-- ============================================================================

CREATE TABLE IF NOT EXISTS dynamic_entity (
    id                  BIGSERIAL PRIMARY KEY,
    business_type_code  VARCHAR(64)  NOT NULL,
    model_id            BIGINT       NOT NULL,
    name                VARCHAR(255) NOT NULL,
    parent_id           BIGINT,
    tree_path           VARCHAR(1024),
    sort                INTEGER      NOT NULL DEFAULT 0,
    status              SMALLINT     NOT NULL DEFAULT 1,
    custom_fields       JSONB,
    creator             VARCHAR(64)  NOT NULL DEFAULT '',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_dynamic_entity_model
    ON dynamic_entity (model_id, tenant_id) WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_entity_business_type
    ON dynamic_entity (business_type_code, tenant_id) WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_entity_parent
    ON dynamic_entity (parent_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_entity_category_relation (
    id                  BIGSERIAL PRIMARY KEY,
    entity_id           BIGINT       NOT NULL,
    category_id         BIGINT       NOT NULL,
    business_type_code  VARCHAR(64),
    sort                INTEGER      NOT NULL DEFAULT 0,
    creator             VARCHAR(64)  NOT NULL DEFAULT '',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_dynamic_entity_category_relation_entity
    ON dynamic_entity_category_relation (entity_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_entity_field_index (
    id              BIGSERIAL PRIMARY KEY,
    entity_id       BIGINT       NOT NULL,
    model_id        BIGINT       NOT NULL,
    field_code      VARCHAR(64)  NOT NULL,
    value_string    VARCHAR(1024),
    value_number    NUMERIC(20, 6),
    value_date      DATE,
    value_datetime  TIMESTAMP,
    value_boolean   BOOLEAN,
    creator         VARCHAR(64)  NOT NULL DEFAULT '',
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)  DEFAULT '',
    update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id       BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_dynamic_entity_field_index_lookup
    ON dynamic_entity_field_index (model_id, field_code, tenant_id) WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_entity_field_index_entity
    ON dynamic_entity_field_index (entity_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_entity_relation (
    id                       BIGSERIAL PRIMARY KEY,
    source_entity_id         BIGINT       NOT NULL,
    target_entity_id         BIGINT       NOT NULL,
    relation_type            VARCHAR(32)  NOT NULL,
    relation_name            VARCHAR(255),
    description              TEXT,
    relation_attributes      TEXT,
    status                   SMALLINT     NOT NULL DEFAULT 1,
    field_code               VARCHAR(64),
    source_model_code        VARCHAR(64),
    target_model_code        VARCHAR(64),
    source_business_type_code VARCHAR(64),
    target_business_type_code VARCHAR(64),
    creator                  VARCHAR(64)  NOT NULL DEFAULT '',
    create_time              TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                  VARCHAR(64)  DEFAULT '',
    update_time              TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                  BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id                BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_dynamic_entity_relation_source
    ON dynamic_entity_relation (source_entity_id) WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_entity_relation_target
    ON dynamic_entity_relation (target_entity_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_entity_sync_fail_log (
    id                  BIGSERIAL PRIMARY KEY,
    entity_id           BIGINT       NOT NULL,
    business_type_code  VARCHAR(64)  NOT NULL,
    model_id            BIGINT       NOT NULL,
    engine_type         VARCHAR(32)  NOT NULL DEFAULT 'postgresql',
    fail_reason         TEXT,
    retry_count         INTEGER      NOT NULL DEFAULT 0,
    last_retry_at       TIMESTAMP,
    status              VARCHAR(32)  NOT NULL DEFAULT 'PENDING',
    creator             VARCHAR(64)  NOT NULL DEFAULT '',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_dynamic_entity_sync_fail_log_entity
    ON dynamic_entity_sync_fail_log (entity_id) WHERE deleted = FALSE;

-- ============================================================================
-- 6. Entity Permissions
-- ============================================================================

CREATE TABLE IF NOT EXISTS dynamic_entity_access_permission (
    id          BIGSERIAL PRIMARY KEY,
    role_id     BIGINT       NOT NULL,
    entity_id   BIGINT       NOT NULL,
    can_view    BOOLEAN      NOT NULL DEFAULT TRUE,
    creator     VARCHAR(64)  NOT NULL DEFAULT '',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)  DEFAULT '',
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id   BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_entity_access_permission
    ON dynamic_entity_access_permission (role_id, entity_id, tenant_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_entity_operation_permission (
    id          BIGSERIAL PRIMARY KEY,
    role_id     BIGINT       NOT NULL,
    entity_id   BIGINT,
    model_id    BIGINT,
    can_create  BOOLEAN      NOT NULL DEFAULT FALSE,
    can_update  BOOLEAN      NOT NULL DEFAULT FALSE,
    can_delete  BOOLEAN      NOT NULL DEFAULT FALSE,
    creator     VARCHAR(64)  NOT NULL DEFAULT '',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)  DEFAULT '',
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id   BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_dynamic_entity_operation_permission_role
    ON dynamic_entity_operation_permission (role_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_entity_field_permission (
    id          BIGSERIAL PRIMARY KEY,
    role_id     BIGINT       NOT NULL,
    model_id    BIGINT       NOT NULL,
    field_id    BIGINT       NOT NULL,
    can_view    BOOLEAN      NOT NULL DEFAULT TRUE,
    can_edit    BOOLEAN      NOT NULL DEFAULT FALSE,
    creator     VARCHAR(64)  NOT NULL DEFAULT '',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)  DEFAULT '',
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id   BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_entity_field_permission
    ON dynamic_entity_field_permission (role_id, model_id, field_id, tenant_id) WHERE deleted = FALSE;

-- ============================================================================
-- 7. Relation Field Library / Ref Constraint
-- ============================================================================

CREATE TABLE IF NOT EXISTS dynamic_relation_field_library (
    id                  BIGSERIAL PRIMARY KEY,
    field_name          VARCHAR(64)  NOT NULL,
    field_code          VARCHAR(64)  NOT NULL,
    ref_business_type   VARCHAR(64)  NOT NULL,
    display_field_code  VARCHAR(64),
    constraint_enabled  BOOLEAN      NOT NULL DEFAULT FALSE,
    constraint_type     VARCHAR(64)  NOT NULL DEFAULT 'NONE',
    description         VARCHAR(512),
    usage_count         INTEGER      NOT NULL DEFAULT 0,
    is_system           BOOLEAN      NOT NULL DEFAULT FALSE,
    creator             VARCHAR(64)  NOT NULL DEFAULT '',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_relation_field_library_code
    ON dynamic_relation_field_library (field_code, tenant_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_ref_constraint_library (
    id                  BIGSERIAL PRIMARY KEY,
    business_type_code  VARCHAR(64)  NOT NULL,
    ref_target_type     VARCHAR(64)  NOT NULL,
    constraint_type     VARCHAR(64)  NOT NULL,
    constraint_name     VARCHAR(128) NOT NULL,
    status              SMALLINT     NOT NULL DEFAULT 0,
    sort                INTEGER      NOT NULL DEFAULT 0,
    creator             VARCHAR(64)  NOT NULL DEFAULT '',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_ref_constraint_library
    ON dynamic_ref_constraint_library (business_type_code, ref_target_type, constraint_type, tenant_id)
    WHERE deleted = FALSE;

-- ============================================================================
-- 8. Computed Fields
-- ============================================================================

CREATE TABLE IF NOT EXISTS dynamic_computed_field (
    id                   BIGSERIAL PRIMARY KEY,
    model_id             BIGINT       NOT NULL,
    field_name           VARCHAR(64)  NOT NULL,
    field_code           VARCHAR(64)  NOT NULL,
    compute_type         VARCHAR(32)  NOT NULL,
    aggregate_function   VARCHAR(16),
    target_business_type VARCHAR(64),
    target_model_code    VARCHAR(64),
    target_field_code    VARCHAR(64),
    relation_condition   JSONB,
    filter_condition     JSONB,
    formula_expression   VARCHAR(512),
    formula_fields       JSONB,
    result_type          VARCHAR(32)  NOT NULL,
    decimal_places       INTEGER      NOT NULL DEFAULT 0,
    null_display         VARCHAR(32)  NOT NULL DEFAULT '0',
    compute_strategy     VARCHAR(32)  NOT NULL DEFAULT 'REALTIME',
    cache_ttl_minutes    INTEGER      NOT NULL DEFAULT 5,
    description          VARCHAR(512),
    sort_order           INTEGER      NOT NULL DEFAULT 0,
    creator              VARCHAR(64)  NOT NULL DEFAULT '',
    create_time          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater              VARCHAR(64)  DEFAULT '',
    update_time          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted              BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id            BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_computed_field_code
    ON dynamic_computed_field (model_id, field_code, tenant_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_precomputed_value (
    id                  BIGSERIAL PRIMARY KEY,
    model_id            BIGINT       NOT NULL,
    entity_id           BIGINT       NOT NULL,
    field_code          VARCHAR(64)  NOT NULL,
    computed_value      TEXT,
    value_type          VARCHAR(32),
    compute_status      VARCHAR(32)  NOT NULL DEFAULT 'PENDING',
    last_compute_time   TIMESTAMP,
    next_compute_time   TIMESTAMP,
    compute_duration_ms BIGINT,
    error_message       TEXT,
    retry_count         INTEGER      NOT NULL DEFAULT 0,
    version             INTEGER      NOT NULL DEFAULT 0,
    creator             VARCHAR(64)  NOT NULL DEFAULT '',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_precomputed_value
    ON dynamic_precomputed_value (model_id, entity_id, field_code, tenant_id) WHERE deleted = FALSE;

-- ============================================================================
-- 9. Dynamic Table Management
-- ============================================================================

CREATE TABLE IF NOT EXISTS dynamic_dynamic_table (
    id                  BIGSERIAL PRIMARY KEY,
    model_id            BIGINT       NOT NULL,
    business_type_code  VARCHAR(64),
    table_name          VARCHAR(128) NOT NULL,
    table_comment       VARCHAR(500),
    column_config       TEXT,
    status              INTEGER      NOT NULL DEFAULT 1,
    version             INTEGER      NOT NULL DEFAULT 1,
    last_sync_time      TIMESTAMP,
    creator             VARCHAR(64)  NOT NULL DEFAULT '',
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_dynamic_dynamic_table_model
    ON dynamic_dynamic_table (model_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_dynamic_table_column (
    id               BIGSERIAL PRIMARY KEY,
    dynamic_table_id BIGINT       NOT NULL,
    field_id         BIGINT       NOT NULL,
    column_name      VARCHAR(64)  NOT NULL,
    data_type        VARCHAR(64)  NOT NULL,
    nullable         BOOLEAN      NOT NULL DEFAULT TRUE,
    default_value    VARCHAR(500),
    column_comment   VARCHAR(500),
    sort_order       INTEGER      NOT NULL DEFAULT 0,
    status           INTEGER      NOT NULL DEFAULT 1,
    creator          VARCHAR(64)  NOT NULL DEFAULT '',
    create_time      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater          VARCHAR(64)  DEFAULT '',
    update_time      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id        BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_dynamic_table_column
    ON dynamic_dynamic_table_column (dynamic_table_id, field_id, tenant_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_dynamic_table_audit_log (
    id               BIGSERIAL PRIMARY KEY,
    dynamic_table_id BIGINT       NOT NULL,
    operation_type   VARCHAR(32)  NOT NULL,
    operation_desc   VARCHAR(500),
    before_config    TEXT,
    after_config     TEXT,
    executed_sql     TEXT,
    execute_result   VARCHAR(32)  NOT NULL,
    error_message    TEXT,
    operation_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    operator_id      BIGINT,
    operator_name    VARCHAR(64),
    creator          VARCHAR(64)  NOT NULL DEFAULT '',
    create_time      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater          VARCHAR(64)  DEFAULT '',
    update_time      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id        BIGINT       NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS dynamic_dynamic_sql_audit_log (
    id              BIGSERIAL PRIMARY KEY,
    table_name      VARCHAR(128) NOT NULL,
    operation_type  VARCHAR(32)  NOT NULL,
    executed_sql    TEXT,
    parameters      TEXT,
    success         BOOLEAN      NOT NULL,
    error_message   TEXT,
    affected_rows   INTEGER,
    execution_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    operator_id     BIGINT,
    operator_name   VARCHAR(64),
    client_ip       VARCHAR(64),
    request_id      VARCHAR(64),
    creator         VARCHAR(64)  NOT NULL DEFAULT '',
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)  DEFAULT '',
    update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id       BIGINT       NOT NULL DEFAULT 0
);

-- ============================================================================
-- 10. Page / Page Config
-- ============================================================================

CREATE TABLE IF NOT EXISTS dynamic_page_config (
    id           BIGSERIAL PRIMARY KEY,
    config_code  VARCHAR(100),
    page_code    VARCHAR(100) NOT NULL,
    menu_id      BIGINT,
    page_type    VARCHAR(50)  NOT NULL DEFAULT 'data_management',
    config       JSONB        NOT NULL DEFAULT '{}',
    creator      VARCHAR(64)  NOT NULL DEFAULT '',
    create_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater      VARCHAR(64)  DEFAULT '',
    update_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted      BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id    BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_page_config_page_code
    ON dynamic_page_config (page_code, tenant_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_page (
    id              BIGSERIAL PRIMARY KEY,
    page_code       VARCHAR(100) NOT NULL,
    page_name       VARCHAR(100) NOT NULL,
    page_type       VARCHAR(50)  NOT NULL,
    description     VARCHAR(255) DEFAULT '',
    status          SMALLINT     NOT NULL DEFAULT 0,
    parent_menu_id  BIGINT,
    menu_id         BIGINT,
    page_config_id  BIGINT,
    icon            VARCHAR(100) DEFAULT '',
    tags            VARCHAR(255) DEFAULT '',
    route_path      VARCHAR(255) DEFAULT '',
    component       VARCHAR(255) DEFAULT '',
    layout          VARCHAR(50)  DEFAULT '',
    ui_schema       JSONB        NOT NULL DEFAULT '{}',
    ui_version      VARCHAR(50)  DEFAULT '',
    data_source     JSONB        NOT NULL DEFAULT '{}',
    remark          VARCHAR(255) DEFAULT '',
    creator         VARCHAR(64)  NOT NULL DEFAULT '',
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)  DEFAULT '',
    update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id       BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_page_code
    ON dynamic_page (page_code, tenant_id) WHERE deleted = FALSE;

-- ============================================================================
-- 11. Unified Group
-- ============================================================================

CREATE TABLE IF NOT EXISTS dynamic_group (
    id          BIGSERIAL PRIMARY KEY,
    group_type  VARCHAR(32)  NOT NULL,
    code        VARCHAR(128),
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(1024),
    parent_id   BIGINT,
    path        VARCHAR(1024),
    level       INTEGER,
    sort        INTEGER,
    status      INTEGER,
    creator     VARCHAR(64)  NOT NULL DEFAULT '',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)  DEFAULT '',
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id   BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_dynamic_group_tenant_type_parent
    ON dynamic_group (tenant_id, group_type, parent_id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_group_relation (
    id          BIGSERIAL PRIMARY KEY,
    group_type  VARCHAR(32)  NOT NULL,
    group_id    BIGINT       NOT NULL,
    target_id   BIGINT       NOT NULL,
    sort        INTEGER,
    creator     VARCHAR(64)  NOT NULL DEFAULT '',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)  DEFAULT '',
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id   BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_group_relation
    ON dynamic_group_relation (tenant_id, group_type, group_id, target_id) WHERE deleted = FALSE;

-- ============================================================================
-- 12. Mail (TenantIgnore - no tenant_id)
-- ============================================================================

CREATE TABLE IF NOT EXISTS dynamic_mail_account (
    id              BIGSERIAL PRIMARY KEY,
    mail            VARCHAR(255) NOT NULL,
    username        VARCHAR(255) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    host            VARCHAR(255) NOT NULL,
    port            INTEGER      NOT NULL,
    ssl_enable      BOOLEAN      NOT NULL DEFAULT FALSE,
    starttls_enable BOOLEAN      NOT NULL DEFAULT FALSE,
    creator         VARCHAR(64)  NOT NULL DEFAULT '',
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)  DEFAULT '',
    update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS dynamic_mail_template (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(63)  NOT NULL,
    code        VARCHAR(63)  NOT NULL,
    account_id  BIGINT       NOT NULL,
    nickname    VARCHAR(255),
    title       VARCHAR(255) NOT NULL,
    content     TEXT         NOT NULL,
    params      JSONB        NOT NULL DEFAULT '[]',
    status      SMALLINT     NOT NULL DEFAULT 0,
    remark      VARCHAR(255),
    creator     VARCHAR(64)  NOT NULL DEFAULT '',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)  DEFAULT '',
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_mail_template_code
    ON dynamic_mail_template (code) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_mail_log (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT,
    user_type         SMALLINT,
    to_mails          TEXT,
    cc_mails          TEXT,
    bcc_mails         TEXT,
    account_id        BIGINT       NOT NULL,
    from_mail         VARCHAR(255) NOT NULL,
    template_id       BIGINT       NOT NULL,
    template_code     VARCHAR(63)  NOT NULL,
    template_nickname VARCHAR(255),
    template_title    VARCHAR(255) NOT NULL,
    template_content  TEXT         NOT NULL,
    template_params   JSONB,
    send_status       SMALLINT     NOT NULL DEFAULT 0,
    send_time         TIMESTAMP,
    send_message_id   VARCHAR(255),
    send_exception    TEXT,
    creator           VARCHAR(64)  NOT NULL DEFAULT '',
    create_time       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater           VARCHAR(64)  DEFAULT '',
    update_time       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted           BOOLEAN      NOT NULL DEFAULT FALSE
);

-- ============================================================================
-- 13. Data Migration Log
-- ============================================================================

CREATE TABLE IF NOT EXISTS dynamic_data_migration_log (
    id                 BIGSERIAL PRIMARY KEY,
    migration_type     VARCHAR(64)  NOT NULL,
    migration_version  VARCHAR(32),
    dry_run            BOOLEAN      NOT NULL DEFAULT FALSE,
    start_time         TIMESTAMP    NOT NULL,
    end_time           TIMESTAMP,
    duration_ms        BIGINT,
    total_count        INTEGER      NOT NULL DEFAULT 0,
    success_count      INTEGER      NOT NULL DEFAULT 0,
    skipped_count      INTEGER      NOT NULL DEFAULT 0,
    failed_count       INTEGER      NOT NULL DEFAULT 0,
    status             VARCHAR(32)  NOT NULL DEFAULT 'RUNNING',
    error_message      TEXT,
    details            JSONB,
    creator            VARCHAR(64)  NOT NULL DEFAULT '',
    create_time        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater            VARCHAR(64)  DEFAULT '',
    update_time        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted            BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id          BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_dynamic_data_migration_log_type
    ON dynamic_data_migration_log (migration_type) WHERE deleted = FALSE;
