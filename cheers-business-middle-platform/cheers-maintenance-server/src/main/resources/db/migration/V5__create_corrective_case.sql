SET search_path TO maintenance;

CREATE TABLE IF NOT EXISTS mm_corrective_case (
    id                   BIGSERIAL PRIMARY KEY,
    case_no              VARCHAR(64)  NOT NULL,
    title                VARCHAR(256) NOT NULL,
    description          VARCHAR(2000),
    asset_id             BIGINT,
    asset_type_code      VARCHAR(64),
    priority             VARCHAR(16),
    status               VARCHAR(32)  NOT NULL,
    field_standard_id    BIGINT,
    work_order_id        BIGINT,
    process_instance_key VARCHAR(64),
    creator              VARCHAR(64)  DEFAULT '',
    create_time          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updater              VARCHAR(64)  DEFAULT '',
    update_time          TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted              BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id            BIGINT       NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX uk_mm_case_no ON mm_corrective_case (tenant_id, case_no) WHERE deleted = FALSE;

CREATE SEQUENCE IF NOT EXISTS mm_corrective_case_seq START WITH 1000 INCREMENT BY 1;
ALTER TABLE mm_corrective_case ALTER COLUMN id DROP DEFAULT;
SELECT setval('mm_corrective_case_seq',
              GREATEST(COALESCE((SELECT MAX(id) FROM mm_corrective_case), 0), 999) + 1,
              false);
DROP SEQUENCE IF EXISTS mm_corrective_case_id_seq;
