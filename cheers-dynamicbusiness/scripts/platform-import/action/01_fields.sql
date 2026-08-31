-- ============================================================================
-- action · 01 字段库：param_slots_json / child_action_ids_json / execution_means / is_composite
-- 前置：Flyway V76（ent_action 固定列）
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
SELECT
  v.code, v.name, v.type, NULL, v.description, 'BASE', 1, 1, 'NONE',
  NULL, NULL, v.code, 1, 'seed'
FROM (
  VALUES
    ('param_slots_json', '参数槽定义', 'TEXT', '动作参数槽 schema JSON 数组（字段编码列表）'),
    ('child_action_ids_json', '子动作列表', 'TEXT', '复合动作的有序子动作 code 列表 JSON 数组'),
    ('execution_means', '执行手段', 'TEXT', 'MANUAL/UAV/ROBOT/FIXED_CAMERA 等'),
    ('is_composite', '是否复合动作', 'BOOLEAN', 'true=开跑时按 child_action_ids_json 展开')
) AS v(code, name, type, description)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
