-- ============================================================================
-- action · 02 实体类型 action（动作库）
-- 前置：Flyway V76
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_entity_type (
  code, name, parent_id, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  physical_column_mapping, model_workbench_mode, group_name, work_scope,
  tenant_id, creator
) VALUES (
  'action', '动作库', NULL,
  '平台标准动作定义；参数槽与复合子动作；供 SOP 动作树引用',
  'fa:person-running', '动作', 19, 'active', 'USER',
  '{}', 'DEDICATED', 'ent_action', FALSE,
  '{
    "execution_means":{"type":"VARCHAR","column":"execution_means"},
    "param_slots_json":{"type":"JSONB","column":"param_slots_json"},
    "child_action_ids_json":{"type":"JSONB","column":"child_action_ids_json"},
    "is_composite":{"type":"BOOLEAN","column":"is_composite"}
  }'::jsonb,
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
WHERE code = 'action'
  AND deleted = false
  AND tenant_id IS NOT NULL
  AND tenant_id > 0
  AND dedicated_table_name IS NOT NULL
  AND dedicated_table_name !~ '_t[0-9]+$';

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'action', '动作库',
  'DEDICATED', 'ent_action',
  NULL, TRUE,
  '动作库专用表；固定列 execution_means / param_slots_json / child_action_ids_json / is_composite', 1,
  '{
    "execution_means":{"type":"VARCHAR","column":"execution_means"},
    "param_slots_json":{"type":"JSONB","column":"param_slots_json"},
    "child_action_ids_json":{"type":"JSONB","column":"child_action_ids_json"},
    "is_composite":{"type":"BOOLEAN","column":"is_composite"}
  }'::jsonb,
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
WHERE entity_type_code = 'action'
  AND deleted = false
  AND tenant_id IS NOT NULL
  AND tenant_id > 0
  AND dedicated_table_name IS NOT NULL
  AND dedicated_table_name !~ '_t[0-9]+$';
