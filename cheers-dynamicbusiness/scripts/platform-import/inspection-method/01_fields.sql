-- ============================================================================
-- inspection-method · 01 字段库
-- is_template / action_duration_sec（编码 = 物理列名）
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'is_template', '是否模板', 'BOOLEAN',
  NULL, '方法模板标记；标准库只展示 true',
  'BASE', 1,
  NULL, 'NONE',
  NULL, NULL,
  'is_template', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  source = EXCLUDED.source,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'action_duration_sec', '动作耗时（秒）', 'INTEGER',
  's', '单次检查动作耗时（秒）；供路径耗时最少与排期任务时长累计',
  'BASE', 1,
  NULL, 'NONE',
  NULL, NULL,
  'action_duration_sec', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  source = EXCLUDED.source,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
