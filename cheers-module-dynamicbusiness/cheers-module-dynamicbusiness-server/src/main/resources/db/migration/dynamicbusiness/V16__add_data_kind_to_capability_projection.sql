-- ============================================================================
-- V16: capability_component_projection 增加 data_kind，支持 model / entity 双投影
-- ============================================================================

SET search_path TO dynamicbusiness;

ALTER TABLE capability_component_projection
    ADD COLUMN IF NOT EXISTS data_kind VARCHAR(16) NOT NULL DEFAULT 'entity';

UPDATE capability_component_projection
SET data_kind = 'entity'
WHERE data_kind IS NULL OR TRIM(data_kind) = '';

DROP INDEX IF EXISTS uk_capability_projection_key;

CREATE UNIQUE INDEX IF NOT EXISTS uk_capability_projection_key
    ON capability_component_projection (business_type_code, component_code, data_kind, tenant_id)
    WHERE deleted = FALSE;

COMMENT ON COLUMN capability_component_projection.data_kind IS '数据种类：model（模型目录）/ entity（实例数据）';
