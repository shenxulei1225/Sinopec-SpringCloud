-- 管理点上启用的业务：按数据目录（业务台账）登记，界面为可勾选业务列表。
SET search_path TO dynamicbusiness;

CREATE SEQUENCE IF NOT EXISTS management_unit_business_catalog_id_seq;

CREATE TABLE IF NOT EXISTS management_unit_business_catalog (
    id                            BIGSERIAL PRIMARY KEY,
    tenant_id                     BIGINT       NOT NULL,
    management_unit_registry_code VARCHAR(128) NOT NULL,
    management_unit_id            BIGINT       NOT NULL,
    catalog_registry_code         VARCHAR(128) NOT NULL,
    creator                       VARCHAR(64)  DEFAULT '',
    create_time                   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                       VARCHAR(64)  DEFAULT '',
    update_time                   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                       BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_management_unit_business_catalog
    ON management_unit_business_catalog (
        tenant_id,
        management_unit_registry_code,
        management_unit_id,
        catalog_registry_code
    )
    WHERE deleted = FALSE;

COMMENT ON TABLE management_unit_business_catalog IS
    '某管理点上勾选启用的业务（数据目录）；与按型号登记的可管目录不同，本条按具体管理点实例。';