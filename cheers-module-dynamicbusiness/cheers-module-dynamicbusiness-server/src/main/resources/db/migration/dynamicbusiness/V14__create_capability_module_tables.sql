-- ============================================================================
-- V14: 业务能力模块新表（从旧 capability_full / instance_registry 迁移）
-- 说明：V1/V2 能力脚本与历史 Flyway 版本号冲突，存量库通过 V14+ 升级。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) business_capability（由 capability_full 迁移或新建）
CREATE SEQUENCE IF NOT EXISTS business_capability_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS business_capability (
    id BIGINT PRIMARY KEY DEFAULT nextval('business_capability_seq'),
    business_type_code VARCHAR(64) NOT NULL,
    capability_full JSONB NOT NULL,
    version BIGINT NOT NULL DEFAULT 1,
    creator VARCHAR(64) NOT NULL DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) NOT NULL DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_business_capability_btc_tenant
    ON business_capability (business_type_code, tenant_id)
    WHERE deleted = FALSE;

DO $$
BEGIN
    IF to_regclass('dynamicbusiness.capability_full') IS NOT NULL THEN
        INSERT INTO business_capability (
            business_type_code, capability_full, version,
            creator, create_time, updater, update_time, deleted, tenant_id
        )
        SELECT
            cf.business_type_code, cf.capability_full, cf.version,
            cf.creator, cf.create_time, cf.updater, cf.update_time, cf.deleted, cf.tenant_id
        FROM capability_full cf
        WHERE cf.deleted = FALSE
          AND NOT EXISTS (
              SELECT 1 FROM business_capability bc
              WHERE bc.business_type_code = cf.business_type_code
                AND bc.tenant_id = cf.tenant_id
                AND bc.deleted = FALSE
          );

        DROP TABLE capability_full CASCADE;
        DROP SEQUENCE IF EXISTS capability_full_id_seq CASCADE;
        DROP SEQUENCE IF EXISTS capability_full_seq CASCADE;
    END IF;
END $$;

COMMENT ON TABLE business_capability IS '业务能力全集表（按 businessTypeCode 索引）';

-- 2) capability_component_projection（运行时由重建写入，此处仅建表）
CREATE SEQUENCE IF NOT EXISTS capability_component_projection_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS capability_component_projection (
    id BIGINT PRIMARY KEY DEFAULT nextval('capability_component_projection_seq'),
    business_type_code VARCHAR(64) NOT NULL,
    component_code VARCHAR(32) NOT NULL,
    component_interface JSONB NOT NULL,
    version BIGINT NOT NULL DEFAULT 1,
    creator VARCHAR(64) NOT NULL DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) NOT NULL DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_capability_projection_key
    ON capability_component_projection (business_type_code, component_code, tenant_id)
    WHERE deleted = FALSE;

COMMENT ON TABLE capability_component_projection IS '组件能力投影表（按 businessTypeCode + componentCode 索引）';

-- 3) model_crud_form_definition
CREATE SEQUENCE IF NOT EXISTS model_crud_form_definition_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS model_crud_form_definition (
    id BIGINT PRIMARY KEY DEFAULT nextval('model_crud_form_definition_seq'),
    business_type_code VARCHAR(64) NOT NULL,
    model_id BIGINT NOT NULL,
    crud_form_fields JSONB NOT NULL,
    version BIGINT NOT NULL DEFAULT 1,
    creator VARCHAR(64) NOT NULL DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) NOT NULL DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_model_crud_form_definition_key
    ON model_crud_form_definition (business_type_code, model_id, tenant_id)
    WHERE deleted = FALSE;

COMMENT ON TABLE model_crud_form_definition IS '模型 CRUD 表单定义表（按 businessTypeCode + modelId 索引）';

-- 4) 清理旧 instance 能力注册表（已由 business_capability + projection 替代）
DROP TABLE IF EXISTS dynamic_instance_capability_registry CASCADE;
DROP TABLE IF EXISTS instance_capability_registry CASCADE;
DROP TABLE IF EXISTS dynamic_business_capability CASCADE;
DROP TABLE IF EXISTS dynamic_capability_projection CASCADE;
DROP TABLE IF EXISTS dynamic_model_crud_form_definition CASCADE;
