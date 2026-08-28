-- ============================================================================
-- sop · 11 步骤模板实体类型 sop_step_template
-- 前置：Flyway V73
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_entity_type (
  code, name, parent_id, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  physical_column_mapping, model_workbench_mode, group_name, work_scope,
  tenant_id, creator
) VALUES (
  'sop_step_template', 'SOP步骤模板', NULL,
  'SOP 步骤模板库；全平台共用步骤块与参数槽 schema',
  'fa:shoe-prints', '步骤模板', 21, 'active', 'USER',
  '{}', 'DEDICATED', 'ent_sop_step_template', FALSE,
  '{"param_slots_json":{"type":"JSONB","column":"param_slots_json"}}'::jsonb,
  'SINGLE', '知识库', 'NETWORK', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  model_workbench_mode = EXCLUDED.model_workbench_mode,
  group_name = EXCLUDED.group_name,
  work_scope = EXCLUDED.work_scope,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_entity_type
SET dedicated_table_name = dedicated_table_name || '_t' || tenant_id::text,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'sop_step_template'
  AND deleted = false
  AND tenant_id IS NOT NULL
  AND tenant_id > 0
  AND dedicated_table_name IS NOT NULL
  AND dedicated_table_name !~ '_t[0-9]+$';

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'sop_step_template', 'SOP步骤模板',
  'DEDICATED', 'ent_sop_step_template',
  NULL, TRUE,
  '步骤模板专用表；固定列 param_slots_json', 1,
  '{"param_slots_json":{"type":"JSONB","column":"param_slots_json"}}'::jsonb,
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_entity_type_config
SET dedicated_table_name = dedicated_table_name || '_t' || tenant_id::text,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'sop_step_template'
  AND deleted = false
  AND tenant_id IS NOT NULL
  AND tenant_id > 0
  AND dedicated_table_name IS NOT NULL
  AND dedicated_table_name !~ '_t[0-9]+$';

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
)
SELECT
  'sop_step_template',
  f.id,
  'param_slots_json',
  '参数槽定义',
  'TEXT',
  false,
  '[]',
  '步骤模板参数槽 schema JSON 数组',
  NULL,
  10,
  1,
  1,
  'seed'
FROM dynamic_field f
WHERE f.deleted = false AND f.tenant_id = 1 AND f.code = 'param_slots_json'
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

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'sop_step_template', 'SOP步骤模板', 'sop_step_template',
  '步骤模板规范型号（SINGLE）；具体步骤块建为实体实例', 1, 0, 1, 'seed'
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
  ON bf.deleted = false AND bf.tenant_id = 1 AND bf.entity_type_code = 'sop_step_template'
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = 1 AND f.code = bf.field_code
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'sop_step_template'
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

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, tenant_id, creator,
  category_mode, entity_association_mode
)
SELECT
  'sop_step_template', 'SOP步骤模板', '步骤模板库分类', 1, 1, 'seed',
  'SIMPLE', 'MULTI'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_category_type ct
  WHERE ct.deleted = false AND ct.tenant_id = 1 AND ct.category_type_code = 'sop_step_template'
);

INSERT INTO dynamic_category (
  code, name, category_type_code, parent_id, status, sort, tenant_id, creator, deleted
)
SELECT
  'sop_step_template_root', 'SOP步骤模板', 'sop_step_template', NULL, 1, 0, 1, 'seed', false
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_category c
  WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'sop_step_template_root'
);
