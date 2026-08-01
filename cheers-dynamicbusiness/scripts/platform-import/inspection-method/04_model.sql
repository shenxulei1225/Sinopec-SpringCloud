-- ============================================================================
-- inspection-method · 04 规范型号 + 字段分配
-- 单型号模式：model.code = inspection_method
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'inspection_method', '检查方法',
  'inspection_method',
  '检查方法规范型号；SINGLE 工作台自动选用。is_template 区分模板与按台实例。',
  1, 1,
  '[{"id":"basic","name":"基本信息","sort":1}]',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  field_groups_config = EXCLUDED.field_groups_config,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, true, true, false, 10, 'false', NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'is_template'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'inspection_method'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_id = EXCLUDED.field_id,
  sort = EXCLUDED.sort,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  default_value = EXCLUDED.default_value,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, true, true, 20, NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'action_duration_sec'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'inspection_method'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_id = EXCLUDED.field_id,
  sort = EXCLUDED.sort,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
