-- ============================================================================
-- action · 05 样例动作（写入租户物理表 ent_action_t1）
-- code 与 SOP 动作树 actionId（act-*）对齐；创建时即绑定参数型号（09 前置）
-- param_slots_json 留空 []，执行参数字段由参数型号 LIBRARY 分配权威
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
  v.execution_means,
  '[]'::jsonb,
  v.child_action_ids_json::jsonb,
  v.is_composite,
  'seed',
  false
FROM (
  VALUES
    -- UAV 原子动作
    ('act-arrive', '到达作业位置', 'UAV', 'action_param_arrive', '[]', false),
    ('act-hover', '悬停观察', 'UAV', 'action_param_dwell', '[]', false),
    ('act-aim', '对准拍摄', 'UAV', 'action_param_aim', '[]', false),
    ('act-shoot', '拍摄取证', 'UAV', 'action_param_shoot', '[]', false),
    -- ROBOT 原子（巡线 / 气体；机器人到达/拍摄等见 10）
    ('act-ground-patrol', '沿规划路线行进', 'ROBOT', 'action_param_ground_patrol', '[]', false),
    ('act-gas-detect', '气体检测', 'ROBOT', 'action_param_gas_detect', '[]', false),
    -- 复合：无人机泄漏取证流程块
    (
      'act-uav-leak-inspect',
      '无人机泄漏取证（复合）',
      'UAV',
      'action_param_none',
      '["act-arrive","act-hover","act-aim","act-shoot"]',
      true
    )
) AS v(code, name, execution_means, param_model_code, child_action_ids_json, is_composite)
JOIN dynamic_model pm
  ON pm.deleted = false AND pm.tenant_id = 1 AND pm.code = v.param_model_code
WHERE NOT EXISTS (
  SELECT 1 FROM ent_action_t1 a
  WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = v.code
);

-- 挂手段分类（entity_model_id = 实体当前参数型号）
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
    SELECT 1 FROM dynamic_category_entity_link_t1 l
    WHERE l.deleted = false
      AND l.tenant_id = 1
      AND l.category_id = cat.id
      AND l.entity_type_code = 'action'
      AND l.entity_id = a.id
  );
