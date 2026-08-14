-- ============================================================================
-- sop · 04 规范型号 sop（SINGLE）
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'sop', '标准作业流程SOP', 'sop',
  'SOP 规范型号（SINGLE）；具体流程建为实体实例，勿再建第二型号', 1, 0, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, field_source, tenant_id, creator
)
SELECT
  m.id, f.id, m.code, bf.field_code,
  bf.required, true, true, false, bf.sort_order,
  bf.default_value, NULL, 'BASE', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_entity_type_base_field bf
  ON bf.deleted = false AND bf.tenant_id = 1 AND bf.entity_type_code = 'sop'
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = 1 AND f.code = bf.field_code
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'sop'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  sort = EXCLUDED.sort,
  default_value = EXCLUDED.default_value,
  field_source = EXCLUDED.field_source,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
