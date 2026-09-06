-- ============================================================================
-- action · 09 动作常用参数字段库（供 param_slots_json 勾选；不再用「参数型号」冒充参数清单）
-- 前置：01_fields、04_model、system/seed dynamic_entity_point_route_scope（路网点位 DOMAIN）
-- 历史：曾建 action_param_* 型号并挂 LIBRARY；现网动作应挂规范型号 action，参数写在 param_slots_json。
-- 本脚本仍幂等写入字段库；旧参数型号若存在则取消实体必填，避免挡住保存参数清单。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- ---------------------------------------------------------------------------
-- 1. 字段库：常用动作执行参数字段（field_code 与 How 填参 / merge 槽 key 一致）
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
-- 2. 历史「参数型号」若仍存在：取消 LIBRARY 实体必填（参数必填只认 param_slots_json）
-- ---------------------------------------------------------------------------
UPDATE dynamic_model_field_assignment a
SET
  required = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.model_code LIKE 'action_param_%'
  AND a.required = true;
