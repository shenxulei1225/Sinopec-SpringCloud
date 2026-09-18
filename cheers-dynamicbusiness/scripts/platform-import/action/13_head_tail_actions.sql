-- ============================================================================
-- action · 13 任务头尾动作（按执行手段备在动作库；幂等）
-- 权威：docs/动态业务/巡检/巡检模型与任务开跑规则.md §8.3.1
-- 负责：机器人/无人机各一套头、尾复合动作，以及头尾用到的原子动作。
-- 不负责：写进检查方法、自动挂到任务步骤图、协议对照填包。
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
  v.execution_means,
  v.param_slots_json::jsonb,
  v.child_action_ids_json::jsonb,
  v.is_composite,
  'seed',
  false
FROM (
  VALUES
    (
      'act-robot-self-check', '开机自检', 'ROBOT',
      '{"version":1,"fields":[]}',
      '[]', false
    ),
    (
      'act-robot-battery-check', '电量检查', 'ROBOT',
      '{"version":1,"fields":[]}',
      '[]', false
    ),
    (
      'act-robot-return-charge', '返回充电', 'ROBOT',
      '{"version":1,"fields":[{"fieldCode":"location_ref","required":false,"defaultValue":null}]}',
      '[]', false
    ),
    (
      'act-robot-head', '机器人任务头', 'ROBOT',
      '{"version":1,"fields":[]}',
      '["act-robot-self-check","act-robot-battery-check"]',
      true
    ),
    (
      'act-robot-tail', '机器人任务尾', 'ROBOT',
      '{"version":1,"fields":[]}',
      '["act-robot-return-charge"]',
      true
    ),
    (
      'act-uav-self-check', '开机自检', 'UAV',
      '{"version":1,"fields":[]}',
      '[]', false
    ),
    (
      'act-uav-battery-check', '电量检查', 'UAV',
      '{"version":1,"fields":[]}',
      '[]', false
    ),
    (
      'act-uav-takeoff', '起飞', 'UAV',
      '{"version":1,"fields":[]}',
      '[]', false
    ),
    (
      'act-uav-land', '降落', 'UAV',
      '{"version":1,"fields":[]}',
      '[]', false
    ),
    (
      'act-uav-return-charge', '返回充电', 'UAV',
      '{"version":1,"fields":[{"fieldCode":"location_ref","required":false,"defaultValue":null}]}',
      '[]', false
    ),
    (
      'act-uav-head', '无人机任务头', 'UAV',
      '{"version":1,"fields":[]}',
      '["act-uav-self-check","act-uav-battery-check","act-uav-takeoff"]',
      true
    ),
    (
      'act-uav-tail', '无人机任务尾', 'UAV',
      '{"version":1,"fields":[]}',
      '["act-uav-land","act-uav-return-charge"]',
      true
    )
) AS v(code, name, execution_means, param_slots_json, child_action_ids_json, is_composite)
JOIN dynamic_model am
  ON am.deleted = false AND am.tenant_id = 1 AND am.code = 'action'
WHERE NOT EXISTS (
    SELECT 1 FROM ent_action_t1 a
    WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = v.code
);

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
  ON cat.deleted = false AND cat.tenant_id = 1
  AND cat.code = CASE a.execution_means
    WHEN 'UAV' THEN 'cat-action-uav'
    WHEN 'ROBOT' THEN 'cat-action-robot'
    ELSE 'action_root'
  END
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code IN (
    'act-robot-self-check',
    'act-robot-battery-check',
    'act-robot-return-charge',
    'act-robot-head',
    'act-robot-tail',
    'act-uav-self-check',
    'act-uav-battery-check',
    'act-uav-takeoff',
    'act-uav-land',
    'act-uav-return-charge',
    'act-uav-head',
    'act-uav-tail'
  )
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_category_relation_t1 r
    WHERE r.tenant_id = 1
      AND r.category_id = cat.id
      AND r.entity_type_code = 'action'
      AND r.entity_id = a.id
  );
