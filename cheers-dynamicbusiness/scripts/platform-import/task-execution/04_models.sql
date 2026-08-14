-- task-execution · 04 执行步骤规范型号

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, domain, tenant_id, creator
) VALUES (
  'task_execution_step', '任务执行步骤',
  'task_execution_step',
  '过程明细；必须 REF execution_record_id',
  1, 1, '[{"id":"basic","name":"步骤信息","sort":1},{"id":"result","name":"执行结果","sort":2}]',
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false DO UPDATE SET
  name = EXCLUDED.name, description = EXCLUDED.description,
  field_groups_config = EXCLUDED.field_groups_config, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code, required, is_searchable, is_filterable, is_sortable, sort, field_source, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code,
  CASE f.code WHEN 'execution_record_id' THEN true WHEN 'step_code' THEN true WHEN 'step_status' THEN true ELSE false END,
  f.code IN ('execution_record_id','step_code','step_status'),
  f.code IN ('step_status','step_type'),
  f.code IN ('step_order','completed_at'),
  CASE f.code
    WHEN 'execution_record_id' THEN 10 WHEN 'step_code' THEN 20 WHEN 'step_order' THEN 30
    WHEN 'step_title' THEN 40 WHEN 'step_type' THEN 50 WHEN 'step_required' THEN 60
    WHEN 'step_status' THEN 70 WHEN 'result_payload' THEN 80
    WHEN 'completed_at' THEN 90 WHEN 'completed_by' THEN 100 ELSE 999 END,
  'BASE', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_entity_type_base_field bf ON bf.entity_type_code = 'task_execution_step' AND bf.deleted = false AND bf.tenant_id = 1
JOIN dynamic_field f ON f.id = bf.library_field_id AND f.deleted = false
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'task_execution_step'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false DO UPDATE SET
  field_id = EXCLUDED.field_id, sort = EXCLUDED.sort, required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable, is_filterable = EXCLUDED.is_filterable,
  field_source = EXCLUDED.field_source, updater = 'seed', update_time = CURRENT_TIMESTAMP;
