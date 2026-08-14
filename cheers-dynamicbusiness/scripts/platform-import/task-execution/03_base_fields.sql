-- task-execution · 03 执行步骤基础字段

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, description, sort_order, status, tenant_id, creator
)
SELECT 'task_execution_step', f.id, f.code, f.name,
  CASE f.type
    WHEN 'ENTITY_REF' THEN 'REF'
    WHEN 'JSON' THEN 'JSON'
    WHEN 'DATETIME' THEN 'DATETIME'
    WHEN 'INTEGER' THEN 'INTEGER'
    WHEN 'BOOLEAN' THEN 'BOOLEAN'
    ELSE 'STRING'
  END,
  CASE f.code WHEN 'execution_record_id' THEN TRUE WHEN 'step_code' THEN TRUE WHEN 'step_status' THEN TRUE ELSE FALSE END,
  f.description,
  CASE f.code
    WHEN 'execution_record_id' THEN 10 WHEN 'step_code' THEN 20 WHEN 'step_order' THEN 30
    WHEN 'step_title' THEN 40 WHEN 'step_type' THEN 50 WHEN 'step_required' THEN 60
    WHEN 'step_status' THEN 70 WHEN 'result_payload' THEN 80
    WHEN 'completed_at' THEN 90 WHEN 'completed_by' THEN 100 ELSE 999
  END,
  1, 1, 'seed'
FROM dynamic_field f
WHERE f.deleted = false AND f.tenant_id = 1
  AND f.code IN (
    'execution_record_id','step_code','step_order','step_title','step_type','step_required',
    'step_status','result_payload','completed_at','completed_by'
  )
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false DO UPDATE SET
  library_field_id = EXCLUDED.library_field_id, field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type, required = EXCLUDED.required, sort_order = EXCLUDED.sort_order,
  updater = 'seed', update_time = CURRENT_TIMESTAMP;
