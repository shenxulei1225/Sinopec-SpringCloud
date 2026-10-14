-- ============================================================================
-- action · 19 带跑冒滴漏的任务开始和结束
-- 负责：动作库增加这两步。任务选了准备方式 leak 时，生成步骤图把它们放进准备和结束。
-- 不负责：改设备上的检查项，也不替换原来的电量检查和全程录像。
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO ent_action_t1 (
  tenant_id, entity_type_code, model_id, name, code, status,
  execution_means, param_slots_json, child_action_ids_json, is_composite,
  action_domain, creator, deleted
)
SELECT
  1,
  'action',
  model.id,
  v.name,
  v.code,
  1,
  '["UAV","ROBOT"]'::jsonb,
  '{"version":1,"fields":[{"fieldCode":"action_duration","required":false,"defaultValue":0}]}'::jsonb,
  '[]'::jsonb,
  false,
  v.action_domain,
  'seed',
  false
FROM (
  VALUES
    ('act-task-head-leak', '任务开始（带跑冒滴漏）', 'action_intelligent_analysis', 'INTELLIGENT_ANALYSIS'),
    ('act-task-tail-leak', '任务结束（带跑冒滴漏）', 'action_platform', 'PLATFORM')
) AS v(code, name, model_code, action_domain)
JOIN dynamic_model model
  ON model.deleted = false
 AND model.tenant_id = 1
 AND model.entity_type_code = 'action'
 AND model.code = v.model_code
WHERE NOT EXISTS (
  SELECT 1 FROM ent_action_t1 a
  WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = v.code
);

UPDATE ent_action_t1 action
SET analysis_capability_id = capability.id,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM intelligent_analysis_capability capability
WHERE action.deleted = false
  AND action.tenant_id = 1
  AND action.code = 'act-task-head-leak'
  AND capability.tenant_id = action.tenant_id
  AND capability.code = 'LIQUID_LEAKAGE'
  AND capability.deleted = false;

INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT
  1,
  cat.id,
  'action',
  a.id,
  NULL,
  0,
  'seed',
  false
FROM ent_action_t1 a
JOIN dynamic_category cat
  ON cat.deleted = false
 AND cat.tenant_id = 1
 AND cat.code IN ('cat-action-uav', 'cat-action-robot')
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code IN ('act-task-head-leak', 'act-task-tail-leak')
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_category_relation_t1 r
    WHERE r.tenant_id = 1
      AND r.category_id = cat.id
      AND r.entity_type_code = 'action'
      AND r.entity_id = a.id
  );
