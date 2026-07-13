-- ============================================================================
-- 基础字段标准关联：library_field_id 回填 + 删除别名表（列由 Flyway V10 添加）
-- 幂等：可重复执行
-- ============================================================================
SET search_path TO dynamicbusiness;

-- 将历史短编码登记对齐到字段库 code（一次性数据修复）
UPDATE dynamic_entity_type_base_field bf
SET
  library_field_id = f.id,
  field_code = f.code,
  updater = 'seed-base-field-standardize',
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
  updater = 'seed-base-field-standardize',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_field f
WHERE bf.deleted = false
  AND bf.tenant_id = f.tenant_id
  AND f.deleted = false
  AND bf.library_field_id IS NULL
  AND f.code = bf.field_code;

DROP TABLE IF EXISTS base_field_library_name_alias;
