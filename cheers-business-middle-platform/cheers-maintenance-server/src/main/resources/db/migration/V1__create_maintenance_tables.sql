SET search_path TO maintenance;

CREATE TABLE IF NOT EXISTS mm_field_work_standard (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(64)  NOT NULL,
    name            VARCHAR(128) NOT NULL,
    version_no      INT          NOT NULL DEFAULT 1,
    scope           VARCHAR(32)  NOT NULL,
    steps_json      JSONB        NOT NULL,
    status          SMALLINT     NOT NULL DEFAULT 0,
    creator         VARCHAR(64)  DEFAULT '',
    create_time     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updater         VARCHAR(64)  DEFAULT '',
    update_time     TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id       BIGINT       NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uk_mm_std_code_ver ON mm_field_work_standard (tenant_id, code, version_no) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS mm_handbook (
    id                BIGSERIAL PRIMARY KEY,
    code              VARCHAR(64)  NOT NULL,
    name              VARCHAR(128) NOT NULL,
    scope             VARCHAR(32)  NOT NULL,
    asset_type_code   VARCHAR(64),
    frequency_code    VARCHAR(32),
    field_standard_id BIGINT       NOT NULL,
    crew_hint         VARCHAR(256),
    material_hint     VARCHAR(512),
    version_no        INT          NOT NULL DEFAULT 1,
    status            SMALLINT     NOT NULL DEFAULT 0,
    creator           VARCHAR(64)  DEFAULT '',
    create_time       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updater           VARCHAR(64)  DEFAULT '',
    update_time       TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id         BIGINT       NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uk_mm_hb_code_ver ON mm_handbook (tenant_id, code, version_no) WHERE deleted = FALSE;
