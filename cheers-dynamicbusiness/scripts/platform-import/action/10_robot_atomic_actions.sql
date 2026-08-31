-- ============================================================================
-- action · 10 地面机器人原子动作（与 UAV 同粒度拆解；幂等）
-- 前置：05_sample_actions、09_action_param_models
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO ent_action_t1 (
  tenant_id, entity_type_code, model_id, name, code, status,
  execution_means, param_slots_json, child_action_ids_json, is_composite,
  creator, deleted
)
SELECT
  1,
  'action',
  pm.id,
  v.name,
  v.code,
  1,
  'ROBOT',
  '[]'::jsonb,
  v.child_action_ids_json::jsonb,
  v.is_composite,
  'seed',
  false
FROM dynamic_model m
CROSS JOIN (
  VALUES
    ('act-robot-arrive', '到达作业位置', 'action_param_arrive', '[]', false),
    ('act-robot-hold', '停留观察', 'action_param_dwell', '[]', false),
    ('act-robot-aim', '对准拍摄', 'action_param_aim', '[]', false),
    ('act-robot-shoot', '拍摄取证', 'action_param_shoot', '[]', false),
    (
      'act-robot-leak-inspect',
      '机器人泄漏取证（复合）',
      'action_param_none',
      '["act-robot-arrive","act-robot-hold","act-robot-aim","act-robot-shoot","act-gas-detect"]',
      true
    )
) AS v(code, name, param_model_code, child_action_ids_json, is_composite)
JOIN dynamic_model pm
  ON pm.deleted = false AND pm.tenant_id = 1 AND pm.code = v.param_model_code
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'action'
  AND NOT EXISTS (
    SELECT 1 FROM ent_action_t1 a
    WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = v.code
  );

-- 既有「地面巡线」改为可执行的路线步（保留 route_ref 原子能力）
UPDATE ent_action_t1
SET
  name = '沿规划路线行进',
  execution_means = 'ROBOT',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'act-ground-patrol'
  AND name = '地面巡线';

-- 新动作挂机器人分类 + 参数型号
UPDATE ent_action_t1 a
SET
  model_id = m.id,
  param_slots_json = '[]'::jsonb,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND m.deleted = false
  AND m.tenant_id = 1
  AND (
    (a.code = 'act-robot-arrive' AND m.code = 'action_param_arrive')
    OR (a.code = 'act-robot-hold' AND m.code = 'action_param_dwell')
    OR (a.code = 'act-robot-aim' AND m.code = 'action_param_aim')
    OR (a.code = 'act-robot-shoot' AND m.code = 'action_param_shoot')
    OR (a.code = 'act-robot-leak-inspect' AND m.code = 'action_param_none')
    OR (a.code = 'act-ground-patrol' AND m.code = 'action_param_ground_patrol')
    OR (a.code = 'act-gas-detect' AND m.code = 'action_param_gas_detect')
  );

INSERT INTO dynamic_category_entity_link_t1 (
  tenant_id, category_id, entity_type_code, entity_id, entity_model_id, creator, deleted
)
SELECT
  1,
  cat.id,
  'action',
  a.id,
  a.model_id,
  'seed',
  false
FROM ent_action_t1 a
JOIN dynamic_category cat
  ON cat.deleted = false AND cat.tenant_id = 1 AND cat.code = 'cat-action-robot'
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.execution_means = 'ROBOT'
  AND a.code IN (
    'act-robot-arrive',
    'act-robot-hold',
    'act-robot-aim',
    'act-robot-shoot',
    'act-robot-leak-inspect',
    'act-ground-patrol',
    'act-gas-detect'
  )
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_category_entity_link_t1 l
    WHERE l.deleted = false
      AND l.tenant_id = 1
      AND l.category_id = cat.id
      AND l.entity_type_code = 'action'
      AND l.entity_id = a.id
  );

-- ---------------------------------------------------------------------------
-- 全量样例动作 → 参数型号同步（幂等；须在 05/10 插入之后）
-- ---------------------------------------------------------------------------
UPDATE ent_action_t1 a
SET
  model_id = m.id,
  param_slots_json = '[]'::jsonb,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND m.deleted = false
  AND m.tenant_id = 1
  AND (
    (a.code IN ('act-arrive', 'act-robot-arrive') AND m.code = 'action_param_arrive')
    OR (a.code IN ('act-hover', 'act-robot-hold') AND m.code = 'action_param_dwell')
    OR (a.code IN ('act-uav-leak-inspect', 'act-robot-leak-inspect') AND m.code = 'action_param_none')
    OR (a.code IN ('act-aim', 'act-robot-aim') AND m.code = 'action_param_aim')
    OR (a.code IN ('act-shoot', 'act-robot-shoot') AND m.code = 'action_param_shoot')
    OR (a.code = 'act-ground-patrol' AND m.code = 'action_param_ground_patrol')
    OR (a.code = 'act-gas-detect' AND m.code = 'action_param_gas_detect')
  );

UPDATE dynamic_category_entity_link_t1 l
SET
  entity_model_id = a.model_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM ent_action_t1 a
WHERE l.deleted = false
  AND l.tenant_id = 1
  AND l.entity_type_code = 'action'
  AND l.entity_id = a.id
  AND a.deleted = false
  AND a.tenant_id = 1
  AND l.entity_model_id IS DISTINCT FROM a.model_id;
