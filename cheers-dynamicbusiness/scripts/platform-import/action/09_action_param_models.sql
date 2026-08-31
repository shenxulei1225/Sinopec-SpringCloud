-- ============================================================================
-- action · 09 动作参数型号（LIBRARY 字段 → 型号分配；替代 param_slots_json 旁路）
-- 前置：01_fields、04_model、system/seed dynamic_entity_point_route_scope（路网点位 DOMAIN）
-- 须在 05_sample_actions 之前执行：样例动作创建时即绑定参数型号。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- ---------------------------------------------------------------------------
-- 1. 字段库：常用动作执行参数字段（field_code 与 How 填参 / merge 槽 key 一致）
--    · 位置：location_ref
--    · 摄像机：yaw / pitch / roll / focal_length
--    · 拍摄：shot_count
--    · 停留：dwell_duration
--    · 路线：route_ref
--    · 检测：gas_threshold
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
SELECT
  v.code, v.name, v.type, v.unit, v.description, 'LIBRARY', 1, v.max_relations,
  'NONE', NULL, NULL, v.semantic_type, 1, 'seed'
FROM (
  VALUES
    (
      'location_ref', '到达位置', 'ENTITY_REF', NULL, 1,
      '到指定位置：路网点位 REF（route_network_point Domain）', 'route_network_point'
    ),
    ('yaw', '偏航角', 'NUMBER', 'deg', 1, '调整摄像机：偏航角（度）', NULL),
    ('pitch', '俯仰角', 'NUMBER', 'deg', 1, '调整摄像机：俯仰角（度）', NULL),
    ('roll', '横滚角', 'NUMBER', 'deg', 1, '调整摄像机：横滚角（度）', NULL),
    ('focal_length', '焦距', 'NUMBER', 'mm', 1, '调整摄像机：焦距', NULL),
    ('shot_count', '拍摄张数', 'NUMBER', NULL, 1, '拍摄取证张数', NULL),
    ('dwell_duration', '停留时长', 'NUMBER', 's', 1, '悬停/停留观察时长（秒）', NULL),
    (
      'route_ref', '路线引用', 'ENTITY_REF', NULL, 1,
      '沿规划路线行进：路线 REF（route 底座）', 'route'
    ),
    ('gas_threshold', '气体阈值', 'NUMBER', 'ppm', 1, '气体检测报警阈值', NULL)
) AS v(code, name, type, unit, max_relations, description, semantic_type)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  semantic_type = EXCLUDED.semantic_type,
  source = 'LIBRARY',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 2. 参数型号（entity_type 仍为 action；只挂 LIBRARY 参数字段）
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
)
SELECT v.code, v.name, 'action', v.description, 1, v.sort, 1, 'seed'
FROM (
  VALUES
    ('action_param_none', '动作·无参', '无执行参数（复合动作块等）', 100),
    ('action_param_arrive', '动作·到达位置', '到指定位置：路网点位 REF', 110),
    ('action_param_dwell', '动作·停留观察', '悬停/停留时长（秒）', 115),
    ('action_param_aim', '动作·调整摄像机', '偏航/俯仰/横滚/焦距', 120),
    ('action_param_shoot', '动作·拍摄', '拍摄张数', 130),
    ('action_param_ground_patrol', '动作·沿路线行进', '规划路线 REF', 140),
    ('action_param_gas_detect', '动作·气体检测', '气体报警阈值', 150)
) AS v(code, name, description, sort)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 3. 型号字段分配（仅 LIBRARY；不写 BASE 目录字段）
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, field_source, tenant_id, creator
)
SELECT
  m.id, f.id, m.code, f.code,
  v.required, false, false, false, v.sort,
  v.target_entity_type, 'LIBRARY', 1, 'seed'
FROM (
  VALUES
    ('action_param_arrive', 'location_ref', true, 10, 'route_network_point'),
    ('action_param_dwell', 'dwell_duration', false, 10, NULL),
    ('action_param_aim', 'yaw', false, 10, NULL),
    ('action_param_aim', 'pitch', false, 20, NULL),
    ('action_param_aim', 'roll', false, 30, NULL),
    ('action_param_aim', 'focal_length', false, 40, NULL),
    ('action_param_shoot', 'shot_count', false, 10, NULL),
    ('action_param_ground_patrol', 'route_ref', false, 10, 'route'),
    ('action_param_gas_detect', 'gas_threshold', false, 10, NULL)
) AS v(model_code, field_code, required, sort, target_entity_type)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = v.model_code
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = v.field_code
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  sort = EXCLUDED.sort,
  target_entity_type = EXCLUDED.target_entity_type,
  field_source = 'LIBRARY',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 样例动作 → 参数型号同步见 10_robot_atomic_actions.sql 末尾（须在 05/10 插入之后）
