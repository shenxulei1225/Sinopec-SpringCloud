-- ============================================================================
-- sop · 12 SOP 模板/实例字段（is_template、override、default_*）
-- 前置：Flyway V74
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
SELECT
  v.code, v.name, v.type, NULL, v.description, 'BASE', 1, 1, 'NONE',
  NULL, NULL, v.code, 1, 'seed'
FROM (
  VALUES
    ('is_template', '是否模板', 'BOOLEAN', 'SOP 模板行=true；实例=false'),
    ('sop_template_id', 'SOP模板', 'REF', '实例引用的 SOP 模板 id（REF → sop）'),
    ('step_override_json', '步骤差量', 'TEXT', '实例步骤差量 JSON'),
    ('param_override_json', '参数差量', 'TEXT', '实例参数差量 JSON'),
    ('default_steps_json', '默认步骤', 'TEXT', '模板默认步骤列表 JSON'),
    ('default_params_json', '默认参数', 'TEXT', '模板默认参数 JSON'),
    ('execution_means', '执行手段', 'TEXT', 'MANUAL/UAV/ROBOT/FIXED_CAMERA 等'),
    ('procedure_kind', '流程种类', 'TEXT', 'leak/corrosion 等（可选）')
) AS v(code, name, type, description)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_entity_type
SET physical_column_mapping = COALESCE(physical_column_mapping, '{}'::jsonb)
  || '{
    "is_template":{"type":"BOOLEAN","column":"is_template"},
    "sop_template_id":{"type":"BIGINT","column":"sop_template_id"},
    "step_override_json":{"type":"JSONB","column":"step_override_json"},
    "param_override_json":{"type":"JSONB","column":"param_override_json"},
    "default_steps_json":{"type":"JSONB","column":"default_steps_json"},
    "default_params_json":{"type":"JSONB","column":"default_params_json"},
    "execution_means":{"type":"VARCHAR","column":"execution_means"},
    "procedure_kind":{"type":"VARCHAR","column":"procedure_kind"}
  }'::jsonb,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE code = 'sop' AND deleted = false AND tenant_id = 1;

UPDATE dynamic_entity_type_config
SET physical_column_mapping = COALESCE(physical_column_mapping, '{}'::jsonb)
  || '{
    "is_template":{"type":"BOOLEAN","column":"is_template"},
    "sop_template_id":{"type":"BIGINT","column":"sop_template_id"},
    "step_override_json":{"type":"JSONB","column":"step_override_json"},
    "param_override_json":{"type":"JSONB","column":"param_override_json"},
    "default_steps_json":{"type":"JSONB","column":"default_steps_json"},
    "default_params_json":{"type":"JSONB","column":"default_params_json"},
    "execution_means":{"type":"VARCHAR","column":"execution_means"},
    "procedure_kind":{"type":"VARCHAR","column":"procedure_kind"}
  }'::jsonb,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'sop' AND deleted = false AND tenant_id = 1;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
)
SELECT
  'sop',
  f.id,
  v.field_code,
  v.field_name,
  v.data_type,
  v.required,
  v.default_value,
  v.description,
  CASE
    WHEN v.field_code = 'sop_template_id' THEN '{"targetEntityTypeCode":"sop"}'::jsonb
    ELSE NULL
  END,
  v.sort_order,
  1,
  1,
  'seed'
FROM dynamic_field f
JOIN (
  VALUES
    ('is_template', '是否模板', 'BOOLEAN', true, 'false', 'SOP 模板行=true；实例=false', 40),
    ('sop_template_id', 'SOP模板', 'REF', false, NULL, '实例引用的 SOP 模板 id', 41),
    ('step_override_json', '步骤差量', 'TEXT', false, NULL, '实例步骤差量 JSON', 42),
    ('param_override_json', '参数差量', 'TEXT', false, NULL, '实例参数差量 JSON', 43),
    ('default_steps_json', '默认步骤', 'TEXT', false, '[]', '模板默认步骤列表 JSON', 44),
    ('default_params_json', '默认参数', 'TEXT', false, '{}', '模板默认参数 JSON', 45),
    ('execution_means', '执行手段', 'TEXT', false, NULL, 'MANUAL/UAV/ROBOT/FIXED_CAMERA', 46),
    ('procedure_kind', '流程种类', 'TEXT', false, NULL, 'leak/corrosion 等', 47)
) AS v(field_code, field_name, data_type, required, default_value, description, sort_order)
  ON f.code = v.field_code AND f.deleted = false AND f.tenant_id = 1
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  library_field_id = EXCLUDED.library_field_id,
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  default_value = EXCLUDED.default_value,
  description = EXCLUDED.description,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  status = EXCLUDED.status,
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
  bf.default_value,
  CASE WHEN bf.field_code = 'sop_template_id' THEN 'sop' ELSE NULL END,
  'BASE', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_entity_type_base_field bf
  ON bf.deleted = false AND bf.tenant_id = 1 AND bf.entity_type_code = 'sop'
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = 1 AND f.code = bf.field_code
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'sop'
  AND bf.field_code IN (
    'is_template', 'sop_template_id', 'step_override_json', 'param_override_json',
    'default_steps_json', 'default_params_json', 'execution_means', 'procedure_kind'
  )
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  sort = EXCLUDED.sort,
  default_value = EXCLUDED.default_value,
  target_entity_type = EXCLUDED.target_entity_type,
  field_source = EXCLUDED.field_source,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
