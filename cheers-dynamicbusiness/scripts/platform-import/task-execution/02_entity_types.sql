-- task-execution · 02 执行步骤 NATIVE（task_excution_record 已存在，不在此重复注册）

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_entity_type (
  code, name, parent_id, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  physical_column_mapping, model_workbench_mode, entry_kind, group_name, work_scope,
  tenant_id, creator
) VALUES (
  'task_execution_step', '任务执行步骤', NULL,
  '过程明细：执行记录下各步完成情况；开跑批量创建；REF execution_record_id',
  'fa:list-ol', '执行步骤', 41, 'active', 'USER',
  '{}', 'DEDICATED', 'ent_task_execution_step', FALSE,
  NULL, 'SINGLE', 'NATIVE', '任务管理', 'FACILITY', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false DO UPDATE SET
  name = EXCLUDED.name, description = EXCLUDED.description,
  storage_type = EXCLUDED.storage_type, dedicated_table_name = EXCLUDED.dedicated_table_name,
  group_name = EXCLUDED.group_name, updater = 'seed', update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_entity_type
SET dedicated_table_name = dedicated_table_name || '_t' || tenant_id::text,
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE code = 'task_execution_step'
  AND deleted = false AND tenant_id > 0
  AND dedicated_table_name IS NOT NULL
  AND dedicated_table_name !~ '_t[0-9]+$';

SELECT setval(
  'dynamicbusiness.dynamic_entity_type_config_id_seq',
  COALESCE((SELECT MAX(id) FROM dynamicbusiness.dynamic_entity_type_config), 0) + 1,
  false
);

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'task_execution_step', '任务执行步骤', 'DEDICATED', 'ent_task_execution_step', NULL, FALSE,
  '过程明细；REF execution_record_id；Flyway V54', 1,
  '{"execution_record_id":{"type":"BIGINT","column":"execution_record_id"},"step_code":{"type":"VARCHAR","column":"step_code"},"step_status":{"type":"VARCHAR","column":"step_status"},"result_payload":{"type":"JSONB","column":"result_payload"}}'::jsonb,
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false DO UPDATE SET
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed', update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_entity_type_config
SET dedicated_table_name = dedicated_table_name || '_t' || tenant_id::text,
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'task_execution_step'
  AND deleted = false AND tenant_id > 0
  AND dedicated_table_name IS NOT NULL
  AND dedicated_table_name !~ '_t[0-9]+$';
