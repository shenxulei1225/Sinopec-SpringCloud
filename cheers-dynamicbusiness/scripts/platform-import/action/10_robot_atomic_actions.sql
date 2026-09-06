-- ============================================================================
-- action · 10 地面机器人原子动作（与 UAV 同粒度拆解；幂等）
-- 前置：05_sample_actions、09_action_param_models（字段库）
-- 权威：规范型号 action + param_slots_json；不再把动作挂到 action_param_* 型号
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
  am.id,
  v.name,
  v.code,
  1,
  'ROBOT',
  v.param_slots_json::jsonb,
  v.child_action_ids_json::jsonb,
  v.is_composite,
  'seed',
  false
FROM (
  VALUES
    (
      'act-robot-arrive', '到达作业位置',
      '{"version":1,"fields":[{"fieldCode":"location_ref","required":true,"defaultValue":null}]}',
      '[]', false
    ),
    (
      'act-robot-hold', '停留观察',
      '{"version":1,"fields":[{"fieldCode":"dwell_duration","required":false,"defaultValue":null}]}',
      '[]', false
    ),
    (
      'act-robot-aim', '对准拍摄',
      '{"version":1,"fields":[{"fieldCode":"yaw","required":false,"defaultValue":null},{"fieldCode":"pitch","required":false,"defaultValue":null},{"fieldCode":"roll","required":false,"defaultValue":null},{"fieldCode":"focal_length","required":false,"defaultValue":null}]}',
      '[]', false
    ),
    (
      'act-robot-shoot', '拍摄取证',
      '{"version":1,"fields":[{"fieldCode":"shot_count","required":false,"defaultValue":null}]}',
      '[]', false
    ),
    (
      'act-robot-leak-inspect',
      '机器人泄漏取证（复合）',
      '{"version":1,"fields":[]}',
      '["act-robot-arrive","act-robot-hold","act-robot-aim","act-robot-shoot","act-gas-detect"]',
      true
    )
) AS v(code, name, param_slots_json, child_action_ids_json, is_composite)
JOIN dynamic_model am
  ON am.deleted = false AND am.tenant_id = 1 AND am.code = 'action'
WHERE NOT EXISTS (
    SELECT 1 FROM ent_action_t1 a
    WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = v.code
  );

-- 既有「地面巡线」改为可执行的路线步
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

-- 机器人动作挂「机器人」手段分类：写分类–实体，不写分类–台账绑定
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
    SELECT 1 FROM dynamic_entity_category_relation_t1 r
    WHERE r.tenant_id = 1
      AND r.category_id = cat.id
      AND r.entity_type_code = 'action'
      AND r.entity_id = a.id
  );

-- ---------------------------------------------------------------------------
-- 现网纠偏：凡仍挂在 action_param_* 上的动作 → 规范型号 action；空清单按旧型号补写
-- ---------------------------------------------------------------------------
UPDATE ent_action_t1 a
SET
  param_slots_json = CASE
    WHEN (
      a.param_slots_json IS NULL
      OR a.param_slots_json = '[]'::jsonb
      OR a.param_slots_json = '{}'::jsonb
    ) AND m.code = 'action_param_arrive' THEN
      '{"version":1,"fields":[{"fieldCode":"location_ref","required":true,"defaultValue":null}]}'::jsonb
    WHEN (
      a.param_slots_json IS NULL
      OR a.param_slots_json = '[]'::jsonb
      OR a.param_slots_json = '{}'::jsonb
    ) AND m.code = 'action_param_dwell' THEN
      '{"version":1,"fields":[{"fieldCode":"dwell_duration","required":false,"defaultValue":null}]}'::jsonb
    WHEN (
      a.param_slots_json IS NULL
      OR a.param_slots_json = '[]'::jsonb
      OR a.param_slots_json = '{}'::jsonb
    ) AND m.code = 'action_param_aim' THEN
      '{"version":1,"fields":[{"fieldCode":"yaw","required":false,"defaultValue":null},{"fieldCode":"pitch","required":false,"defaultValue":null},{"fieldCode":"roll","required":false,"defaultValue":null},{"fieldCode":"focal_length","required":false,"defaultValue":null}]}'::jsonb
    WHEN (
      a.param_slots_json IS NULL
      OR a.param_slots_json = '[]'::jsonb
      OR a.param_slots_json = '{}'::jsonb
    ) AND m.code = 'action_param_shoot' THEN
      '{"version":1,"fields":[{"fieldCode":"shot_count","required":false,"defaultValue":null}]}'::jsonb
    WHEN (
      a.param_slots_json IS NULL
      OR a.param_slots_json = '[]'::jsonb
      OR a.param_slots_json = '{}'::jsonb
    ) AND m.code = 'action_param_ground_patrol' THEN
      '{"version":1,"fields":[{"fieldCode":"route_ref","required":false,"defaultValue":null}]}'::jsonb
    WHEN (
      a.param_slots_json IS NULL
      OR a.param_slots_json = '[]'::jsonb
      OR a.param_slots_json = '{}'::jsonb
    ) AND m.code = 'action_param_gas_detect' THEN
      '{"version":1,"fields":[{"fieldCode":"gas_threshold","required":false,"defaultValue":null}]}'::jsonb
    WHEN (
      a.param_slots_json IS NULL
      OR a.param_slots_json = '[]'::jsonb
      OR a.param_slots_json = '{}'::jsonb
    ) AND m.code = 'action_param_none' THEN
      '{"version":1,"fields":[]}'::jsonb
    ELSE a.param_slots_json
  END,
  model_id = am.id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
JOIN dynamic_model am
  ON am.deleted = false AND am.tenant_id = 1 AND am.code = 'action'
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.model_id = m.id
  AND m.deleted = false
  AND m.tenant_id = 1
  AND m.code LIKE 'action_param_%';
