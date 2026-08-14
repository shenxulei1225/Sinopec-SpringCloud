-- task-execution · 01 执行步骤字段库

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_field (code, name, type, description, source, status, max_relations, index_strategy, provider_code, semantic_type, tenant_id, creator)
VALUES
  ('execution_record_id', '执行记录', 'ENTITY_REF', '所属任务执行记录（REF → task_excution_record）', 'BASE', 1, 1, 'NONE', 'dynamic-entity:task_excution_record', 'execution_record_id', 1, 'seed'),
  ('step_code', '步骤编码', 'STRING', '与标准快照 step code 一致', 'BASE', 1, NULL, 'NONE', NULL, 'step_code', 1, 'seed'),
  ('step_order', '步骤顺序', 'INTEGER', '步骤顺序号', 'BASE', 1, NULL, 'NONE', NULL, 'step_order', 1, 'seed'),
  ('step_title', '步骤标题', 'STRING', '快照冗余标题', 'BASE', 1, NULL, 'NONE', NULL, 'step_title', 1, 'seed'),
  ('step_type', '步骤类型', 'STRING', 'confirm/capture/move 等通用类型码', 'BASE', 1, NULL, 'NONE', NULL, 'step_type', 1, 'seed'),
  ('step_required', '是否必做', 'BOOLEAN', '是否必做步骤', 'BASE', 1, NULL, 'NONE', NULL, 'step_required', 1, 'seed'),
  ('step_status', '步骤状态', 'STRING', 'pending/in_progress/completed/skipped', 'BASE', 1, NULL, 'NONE', NULL, 'step_status', 1, 'seed'),
  ('result_payload', '步骤结果', 'JSON', '本步结构化结果', 'BASE', 1, NULL, 'NONE', NULL, 'result_payload', 1, 'seed'),
  ('completed_at', '完成时间', 'DATETIME', '步骤完成时间', 'BASE', 1, NULL, 'NONE', NULL, 'completed_at', 1, 'seed'),
  ('completed_by', '完成人', 'STRING', '步骤完成人', 'BASE', 1, NULL, 'NONE', NULL, 'completed_by', 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false DO UPDATE SET
  name = EXCLUDED.name, type = EXCLUDED.type, description = EXCLUDED.description,
  provider_code = EXCLUDED.provider_code, semantic_type = EXCLUDED.semantic_type,
  updater = 'seed', update_time = CURRENT_TIMESTAMP;
