-- 基础字段标准关联：library_field_id 外键列；移除历史别名表

SET search_path TO dynamicbusiness;

ALTER TABLE dynamic_entity_type_base_field
    ADD COLUMN IF NOT EXISTS library_field_id BIGINT;

-- 历史短编码 → 字段库完整 code，并回填 library_field_id
UPDATE dynamic_entity_type_base_field bf
SET
    library_field_id = f.id,
    field_code = f.code,
    updater = 'flyway-v10-base-field-standardize',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_field f
WHERE bf.deleted = false
  AND bf.tenant_id = f.tenant_id
  AND f.deleted = false
  AND bf.library_field_id IS NULL
  AND f.code = 'FLD-BASE-' || bf.entity_type_code || '-' || bf.field_code;

UPDATE dynamic_entity_type_base_field bf
SET
    library_field_id = f.id,
    updater = 'flyway-v10-base-field-standardize',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_field f
WHERE bf.deleted = false
  AND bf.tenant_id = f.tenant_id
  AND f.deleted = false
  AND bf.library_field_id IS NULL
  AND f.code = bf.field_code;

CREATE INDEX IF NOT EXISTS idx_dynamic_entity_type_base_field_library_field_id
    ON dynamic_entity_type_base_field (library_field_id)
    WHERE deleted = false AND library_field_id IS NOT NULL;

DROP TABLE IF EXISTS base_field_library_name_alias;
