-- 无人机任务头改为准备并启动航线，不再指向已删除的起飞。
-- 这一步填写安全起飞高度、飞行速度、返航高度，以及飞完回到机库还是在指定点降落。
-- 机器人任务尾不再固定带返回充电。返回充电仍留在动作库，由检查方法自己放进去。
-- 不负责：把这三个数写成默认值；把路线终点写进返回充电。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

INSERT INTO ent_action_t1 (
  tenant_id, entity_type_code, model_id, name, code, status,
  execution_means, param_slots_json, child_action_ids_json, is_composite,
  custom_fields, creator, deleted
)
SELECT
  tenant_id, entity_type_code, model_id,
  '准备并启动航线',
  'act-uav-wayline-prepare',
  status,
  '["UAV"]'::jsonb,
  jsonb_build_object(
    'version', 2,
    'methods', jsonb_build_array(
      jsonb_build_object(
        'executionMeans', 'UAV',
        'fields', jsonb_build_array(
          jsonb_build_object('fieldCode', 'security_takeoff_height', 'required', true, 'defaultValue', null, 'valueSource', 'ACTION_PARAM'),
          jsonb_build_object('fieldCode', 'max_speed', 'required', true, 'defaultValue', null, 'valueSource', 'ACTION_PARAM'),
          jsonb_build_object('fieldCode', 'rth_altitude', 'required', true, 'defaultValue', null, 'valueSource', 'ACTION_PARAM'),
          jsonb_build_object('fieldCode', 'finish_action', 'required', true, 'defaultValue', null, 'valueSource', 'ACTION_PARAM')
        )
      )
    )
  ),
  '[]'::jsonb,
  false,
  custom_fields,
  'seed',
  false
FROM ent_action_t1 source
WHERE source.tenant_id = 1
  AND source.id = 35
  AND NOT EXISTS (
    SELECT 1 FROM ent_action_t1 existing
    WHERE existing.tenant_id = 1 AND existing.code = 'act-uav-wayline-prepare'
  );

INSERT INTO dynamic_entity_category_relation_t1 (
  entity_id, category_id, entity_type_code, sort, creator, deleted, tenant_id
)
SELECT action.id, link.category_id, 'action', link.sort, 'seed', false, 1
FROM ent_action_t1 action
JOIN dynamic_entity_category_relation_t1 link
  ON link.tenant_id = 1
 AND link.deleted = false
 AND link.entity_type_code = 'action'
 AND link.entity_id = 59
WHERE action.tenant_id = 1
  AND action.code = 'act-uav-wayline-prepare'
  AND action.deleted = false
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_category_relation_t1 existing
    WHERE existing.tenant_id = 1
      AND existing.deleted = false
      AND existing.entity_type_code = 'action'
      AND existing.entity_id = action.id
      AND existing.category_id = link.category_id
  );

UPDATE ent_action_t1
SET child_action_ids_json = '["act-uav-wayline-prepare"]'::jsonb,
    is_composite = true,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND code = 'act-uav-head';

UPDATE ent_action_t1
SET child_action_ids_json = '[]'::jsonb,
    is_composite = false,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND code = 'act-robot-tail';
