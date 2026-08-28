-- ============================================================================
-- sop · 10 步骤模板字段库：param_slots_json
-- 前置：Flyway V73（ent_sop_step_template.param_slots_json 列）
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'param_slots_json', '参数槽定义', 'TEXT', NULL,
  '步骤模板参数槽 schema JSON 数组', 'BASE', 1, 1, 'NONE',
  NULL, NULL, 'param_slots_json', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
