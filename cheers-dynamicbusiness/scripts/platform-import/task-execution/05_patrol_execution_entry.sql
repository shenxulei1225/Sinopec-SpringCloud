-- task-execution · 05 巡检任务执行记录（DOMAIN 入口对齐）
-- 前置：task_excution_record NATIVE、task_record_patrol 已存在则 UPDATE；不存在则 INSERT

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_entity_type (
  code, name, parent_id, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  physical_column_mapping, model_workbench_mode, entry_kind, base_entity_type_code,
  domain, group_name, work_scope, tenant_id, creator
)
SELECT
  'task_record_patrol', '巡检任务执行记录', NULL,
  '任务执行记录 · 巡检业务域；storage=task_excution_record；型号 exec_patrol_round',
  COALESCE(b.icon, 'fa:check-square-o'), '巡检执行记录', 40, 'active', 'USER',
  '{}', b.storage_type, b.dedicated_table_name, COALESCE(b.enable_rule_engine, FALSE),
  b.physical_column_mapping, COALESCE(b.model_workbench_mode, 'SINGLE'),
  'DOMAIN', 'task_excution_record', '巡检', '巡检管理', b.work_scope, 1, 'seed'
FROM dynamic_entity_type b
WHERE b.deleted = false AND b.tenant_id = 1 AND b.code = 'task_excution_record'
ON CONFLICT (code, tenant_id) WHERE deleted = false DO UPDATE SET
  name = '巡检任务执行记录',
  alias = '巡检执行记录',
  description = EXCLUDED.description,
  entry_kind = 'DOMAIN',
  base_entity_type_code = 'task_excution_record',
  domain = '巡检',
  group_name = '巡检管理',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 巡检轮次记录型号：作为巡检执行记录默认型号（已有则补说明）
UPDATE dynamic_model
SET
  name = '巡检轮次记录',
  description = '一次巡检开跑产生的执行记录；domain=巡检；步骤挂 task_execution_step',
  domain = '巡检',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code = 'exec_patrol_round';
