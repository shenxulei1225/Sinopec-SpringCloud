SET search_path TO platform;

CREATE TABLE IF NOT EXISTS platform_path_network (
    id                      VARCHAR(64)  PRIMARY KEY,
    network_kind            VARCHAR(32)  NOT NULL,
    facility_id             BIGINT,
    scope_id                VARCHAR(64),
    status                  VARCHAR(16)  NOT NULL,
    version                 INTEGER      NOT NULL DEFAULT 0,
    nodes                   JSONB        NOT NULL DEFAULT '[]',
    edges                   JSONB        NOT NULL DEFAULT '[]',
    tenant_id               BIGINT       NOT NULL DEFAULT 0,
    creator                 VARCHAR(64)  DEFAULT '',
    create_time             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                 VARCHAR(64)  DEFAULT '',
    update_time             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                 BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_path_network_facility_kind
    ON platform_path_network (facility_id, network_kind)
    WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS platform_path_portal (
    id                      VARCHAR(64)  PRIMARY KEY,
    from_network_id         VARCHAR(64)  NOT NULL,
    from_node_id            VARCHAR(64)  NOT NULL,
    to_network_id           VARCHAR(64)  NOT NULL,
    to_node_id              VARCHAR(64)  NOT NULL,
    allowed_profile_ids     JSONB        NOT NULL DEFAULT '[]',
    tenant_id               BIGINT       NOT NULL DEFAULT 0,
    creator                 VARCHAR(64)  DEFAULT '',
    create_time             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                 VARCHAR(64)  DEFAULT '',
    update_time             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                 BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS platform_mobility_profile (
    id                      VARCHAR(64)  PRIMARY KEY,
    display_name            VARCHAR(128) NOT NULL,
    allowed_network_kinds   JSONB        NOT NULL,
    layer                   VARCHAR(16)  NOT NULL,
    respect_doors           BOOLEAN      NOT NULL DEFAULT TRUE,
    allow_portal_hop        BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id               BIGINT       NOT NULL DEFAULT 0,
    creator                 VARCHAR(64)  DEFAULT '',
    create_time             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                 VARCHAR(64)  DEFAULT '',
    update_time             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                 BOOLEAN      NOT NULL DEFAULT FALSE
);
