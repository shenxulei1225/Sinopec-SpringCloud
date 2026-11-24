-- 管理单元（或场站、管线段等锚点）上启用的业务能力包。
-- 前提：租户已在 tenant_capability_pack 开通该包；本表只登记「这片资源上线哪条业务线」。
SET search_path TO dynamicbusiness;

CREATE SEQUENCE IF NOT EXISTS management_unit_capability_pack_id_seq;

CREATE TABLE IF NOT EXISTS management_unit_capability_pack (
    id                            BIGSERIAL PRIMARY KEY,
    tenant_id                     BIGINT       NOT NULL,
    pack_code                     VARCHAR(64)  NOT NULL,
    management_unit_registry_code VARCHAR(128) NOT NULL,
    management_unit_id            BIGINT       NOT NULL,
    creator                       VARCHAR(64)  DEFAULT '',
    create_time                   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                       VARCHAR(64)  DEFAULT '',
    update_time                   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                       BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_management_unit_capability_pack
    ON management_unit_capability_pack (
        tenant_id,
        pack_code,
        management_unit_registry_code,
        management_unit_id
    )
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_management_unit_capability_pack_anchor
    ON management_unit_capability_pack (tenant_id, management_unit_registry_code, management_unit_id)
    WHERE deleted = FALSE;

COMMENT ON TABLE management_unit_capability_pack IS
    '管理单元等业务锚点上已启用的业务能力包；租户须先开通对应包。';
COMMENT ON COLUMN management_unit_capability_pack.management_unit_registry_code IS
    '锚点所在数据目录注册编码，如 organization、facility、pipeline_pipe。';
COMMENT ON COLUMN management_unit_capability_pack.management_unit_id IS
    '锚点记录编号（该目录下的实体 id）。';