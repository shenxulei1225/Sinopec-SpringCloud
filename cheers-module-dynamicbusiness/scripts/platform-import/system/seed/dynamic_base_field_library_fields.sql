-- ============================================================================
-- 系统 · 基础字段写入字段库（须在 dynamic_entity_type_base_field 之前执行）
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
SELECT
  bf.field_code AS code,
  bf.field_name,
  CASE bf.data_type
    WHEN 'TEXT' THEN 'TEXT'
    WHEN 'NUMBER' THEN 'NUMBER'
    WHEN 'INTEGER' THEN 'INTEGER'
    WHEN 'ENUM' THEN 'ENUM'
    WHEN 'DATE' THEN 'DATE'
    WHEN 'DATETIME' THEN 'DATETIME'
    WHEN 'BOOLEAN' THEN 'BOOLEAN'
    WHEN 'JSON' THEN 'JSON'
    WHEN 'REF' THEN 'ENTITY_REF'
    WHEN 'REF_Multi' THEN 'ENTITY_REF_MULTI'
    ELSE 'TEXT'
  END AS type,
  NULL AS unit,
  bf.description,
  'BASE' AS source,
  bf.status,
  NULL AS max_relations,
  'NONE' AS index_strategy,
  bf.type_config AS options,
  NULL AS provider_code,
  NULL AS semantic_type,
  bf.tenant_id,
  'seed-base-field-lib'
FROM dynamic_entity_type_base_field bf
WHERE bf.deleted = false
  AND bf.tenant_id = 1
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  source = EXCLUDED.source,
  status = EXCLUDED.status,
  options = EXCLUDED.options,
  updater = 'seed-base-field-lib',
  update_time = CURRENT_TIMESTAMP;

-- 回填 library_field_id（字段库条目已就绪）
UPDATE dynamic_entity_type_base_field bf
SET
  library_field_id = f.id,
  updater = 'seed-base-field-lib',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_field f
WHERE bf.deleted = false
  AND bf.tenant_id = f.tenant_id
  AND f.deleted = false
  AND f.code = bf.field_code
  AND (bf.library_field_id IS NULL OR bf.library_field_id <> f.id);
