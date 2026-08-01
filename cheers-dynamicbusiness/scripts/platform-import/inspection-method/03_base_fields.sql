-- ============================================================================
-- inspection-method · 03 基础字段
-- field_code = 字段库 code = 物理列名；禁止 FLD-BASE-
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
)
SELECT
  'inspection_method',
  f.id,
  'is_template',
  '是否模板',
  'BOOLEAN',
  FALSE,
  'false',
  'true=方法模板（标准库可见）；false=按台实例',
  NULL,
  10,
  1,
  1,
  'seed'
FROM dynamic_field f
WHERE f.deleted = false AND f.tenant_id = 1 AND f.code = 'is_template'
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  library_field_id = EXCLUDED.library_field_id,
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  default_value = EXCLUDED.default_value,
  description = EXCLUDED.description,
  sort_order = EXCLUDED.sort_order,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
)
SELECT
  'inspection_method',
  f.id,
  'action_duration_sec',
  '动作耗时（秒）',
  'INTEGER',
  FALSE,
  NULL,
  '单次检查动作耗时（秒）',
  NULL,
  20,
  1,
  1,
  'seed'
FROM dynamic_field f
WHERE f.deleted = false AND f.tenant_id = 1 AND f.code = 'action_duration_sec'
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  library_field_id = EXCLUDED.library_field_id,
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  description = EXCLUDED.description,
  sort_order = EXCLUDED.sort_order,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
