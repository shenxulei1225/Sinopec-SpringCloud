SET search_path TO platform;

CREATE TABLE IF NOT EXISTS platform_topology_graph (
    id                      VARCHAR(64)  PRIMARY KEY,
    site_id                 BIGINT       NOT NULL,
    status                  VARCHAR(16)  NOT NULL,
    version                 INTEGER      NOT NULL DEFAULT 0,
    nodes                   JSONB        NOT NULL DEFAULT '[]',
    edges                   JSONB        NOT NULL DEFAULT '[]',
    zone_boundaries         JSONB        NOT NULL DEFAULT '[]',
    tenant_id               BIGINT       NOT NULL DEFAULT 0,
    creator                 VARCHAR(64)  DEFAULT '',
    create_time             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                 VARCHAR(64)  DEFAULT '',
    update_time             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                 BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_platform_topology_graph_site
    ON platform_topology_graph (site_id)
    WHERE deleted = FALSE;
