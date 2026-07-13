-- ============================================================================
-- 系统 · 基础字段分组与模型分配（依赖 library_field_id）
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD',
  g.code,
  f.code,
  g.id,
  f.id,
  bf.sort_order,
  1,
  'seed-base-field-lib'
FROM dynamic_entity_type_base_field bf
JOIN dynamic_field f
  ON f.deleted = false
 AND f.tenant_id = 1
 AND f.id = bf.library_field_id
JOIN dynamic_group g
  ON g.deleted = false
 AND g.tenant_id = 1
 AND g.group_type = 'FIELD'
 AND g.code = CASE bf.entity_type_code
    WHEN 'facility' THEN 'FG-BIZ-SPATIAL'
    WHEN 'zone' THEN 'FG-BIZ-SPATIAL'
    WHEN 'region' THEN 'FG-BIZ-LOCATION'
    WHEN 'equipment' THEN 'FG-BIZ-EQUIPMENT'
    WHEN 'pipeline' THEN 'FG-BIZ-PIPELINE'
    WHEN 'fault' THEN 'FG-BIZ-FAULT'
    WHEN 'maintenance' THEN 'FG-BIZ-MAINTENANCE'
    WHEN 'billing' THEN 'FG-BIZ-BILLING'
    WHEN 'customer' THEN 'FG-BIZ-CUSTOMER'
    WHEN 'spare_parts' THEN 'FG-BIZ-SPARE'
    WHEN 'patrol_object' THEN 'FG-BIZ-INSPECTION'
    WHEN 'patrol_point' THEN 'FG-BIZ-INSPECTION'
    WHEN 'patrol_schedule' THEN 'FG-BIZ-INSPECTION'
    WHEN 'inspection_item' THEN 'FG-BIZ-INSPECTION'
    WHEN 'emergency_resource' THEN 'FG-BIZ-EMERGENCY'
    ELSE 'FG-BIZ-COMMON'
  END
WHERE bf.deleted = false
  AND bf.tenant_id = 1
  AND bf.library_field_id IS NOT NULL
ON CONFLICT (group_type, group_code, target_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  group_id = EXCLUDED.group_id,
  target_id = EXCLUDED.target_id,
  sort = EXCLUDED.sort,
  updater = 'seed-base-field-lib',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  field_source, tenant_id, creator
)
SELECT
  m.id,
  f.id,
  m.code,
  f.code,
  COALESCE(bf.required, false),
  true,
  true,
  true,
  bf.sort_order,
  'BASE',
  1,
  'seed-base-field-lib'
FROM dynamic_entity_type_base_field bf
JOIN dynamic_model m
  ON m.deleted = false
 AND m.tenant_id = 1
 AND m.entity_type_code = bf.entity_type_code
JOIN dynamic_field f
  ON f.deleted = false
 AND f.tenant_id = 1
 AND f.id = bf.library_field_id
WHERE bf.deleted = false
  AND bf.tenant_id = 1
  AND bf.library_field_id IS NOT NULL
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  field_id = EXCLUDED.field_id,
  model_id = EXCLUDED.model_id,
  updater = 'seed-base-field-lib',
  update_time = CURRENT_TIMESTAMP;
