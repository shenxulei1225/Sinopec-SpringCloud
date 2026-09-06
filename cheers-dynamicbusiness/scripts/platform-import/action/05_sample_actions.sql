-- ============================================================================
-- action · 05 样例动作（写入租户物理表 ent_action_t1）
-- code 与 SOP 动作树 actionId（act-*）对齐
-- 权威：规范型号 action + param_slots_json 动作参数定义（不再用参数型号冒充参数清单）
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
    -- UAV 原子动作
    (
      'act-arrive', '到达作业位置', 'UAV',
      '{"version":1,"fields":[{"fieldCode":"location_ref","required":true,"defaultValue":null}]}',
      '[]', false
    ),
    (
      'act-hover', '悬停观察', 'UAV',
      '{"version":1,"fields":[{"fieldCode":"dwell_duration","required":false,"defaultValue":null}]}',
      '[]', false
    ),
    (
      'act-aim', '对准拍摄', 'UAV',
      '{"version":1,"fields":[{"fieldCode":"yaw","required":false,"defaultValue":null},{"fieldCode":"pitch","required":false,"defaultValue":null},{"fieldCode":"roll","required":false,"defaultValue":null},{"fieldCode":"focal_length","required":false,"defaultValue":null}]}',
      '[]', false
    ),
    (
      'act-shoot', '拍摄取证', 'UAV',
      '{"version":1,"fields":[{"fieldCode":"shot_count","required":false,"defaultValue":null}]}',
      '[]', false
    ),
    -- ROBOT 原子（巡线 / 气体；机器人到达/拍摄等见 10）
    (
      'act-ground-patrol', '沿规划路线行进', 'ROBOT',
      '{"version":1,"fields":[{"fieldCode":"route_ref","required":false,"defaultValue":null}]}',
      '[]', false
    ),
    (
      'act-gas-detect', '气体检测', 'ROBOT',
      '{"version":1,"fields":[{"fieldCode":"gas_threshold","required":false,"defaultValue":null}]}',
      '[]', false
    ),
    -- 复合：无人机泄漏取证流程块
    (
      'act-uav-leak-inspect',
      '无人机泄漏取证（复合）',
      'UAV',
      '{"version":1,"fields":[]}',
      '["act-arrive","act-hover","act-aim","act-shoot"]',
      true
    )
) AS v(code, name, execution_means, param_slots_json, child_action_ids_json, is_composite)
JOIN dynamic_model am
  ON am.deleted = false AND am.tenant_id = 1 AND am.code = 'action'
WHERE NOT EXISTS (
    SELECT 1 FROM ent_action_t1 a
    WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = v.code
);

-- 挂手段分类：动作种类是简单分类，权威写分类–实体（N:N）。
-- 禁止写入 dynamic_category_entity_link（那是高级分类「节点=台账」1:1）。
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
    WHEN 'MANUAL' THEN 'cat-action-manual'
    WHEN 'FIXED_CAMERA' THEN 'cat-action-fixed-camera'
    ELSE 'action_root'
  END
WHERE a.deleted = false AND a.tenant_id = 1
  AND a.code IN (
    'act-arrive', 'act-hover', 'act-aim', 'act-shoot',
    'act-ground-patrol', 'act-gas-detect', 'act-uav-leak-inspect'
  )
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_category_relation_t1 r
    WHERE r.tenant_id = 1
      AND r.category_id = cat.id
      AND r.entity_type_code = 'action'
      AND r.entity_id = a.id
  );
