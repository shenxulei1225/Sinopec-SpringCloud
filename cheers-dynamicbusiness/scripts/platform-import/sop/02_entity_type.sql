-- ============================================================================
-- sop · 02 实体类型 sop（标准作业流程SOP）
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_entity_type (
  code, name, parent_id, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  physical_column_mapping, model_workbench_mode, group_name, work_scope,
  tenant_id, creator
) VALUES (
  'sop', '标准作业流程SOP', NULL,
  '全网标准作业流程（SOP）；有序步骤与参数槽；可版本发布；检查项直接关联多条 SOP',
  'fa:list-check', 'SOP', 20, 'active', 'USER',
  '{}', 'DEDICATED', 'ent_sop', FALSE,
  '{"version_no":{"type":"INTEGER","column":"version_no"},"publish_status":{"type":"VARCHAR","column":"publish_status"},"steps_json":{"type":"JSONB","column":"steps_json"}}'::jsonb,
  'SINGLE', '知识库', 'NETWORK', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  alias = EXCLUDED.alias,
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
WHERE code = 'sop'
  AND deleted = false
  AND tenant_id IS NOT NULL
  AND tenant_id > 0
  AND dedicated_table_name IS NOT NULL
  AND dedicated_table_name !~ '_t[0-9]+$';

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'sop', '标准作业流程SOP',
  'DEDICATED', 'ent_sop',
  NULL, TRUE,
  'SOP 专用表；固定列 version_no / publish_status / steps_json', 1,
  '{"version_no":{"type":"INTEGER","column":"version_no"},"publish_status":{"type":"VARCHAR","column":"publish_status"},"steps_json":{"type":"JSONB","column":"steps_json"}}'::jsonb,
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
WHERE entity_type_code = 'sop'
  AND deleted = false
  AND tenant_id IS NOT NULL
  AND tenant_id > 0
  AND dedicated_table_name IS NOT NULL
  AND dedicated_table_name !~ '_t[0-9]+$';

-- 历史入口软删（若仍存在）
UPDATE dynamic_entity_type
SET deleted = true, status = 'inactive', updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE code = 'field_work_standard' AND deleted = false;

UPDATE dynamic_entity_type_config
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'field_work_standard' AND deleted = false;
