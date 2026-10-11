-- 定点拍照给每一款相机型号补上参数格子。
-- 负责：还没有格子的大疆相机，抄 Matrice 30 相机上已经写好的那一份。
-- 不负责：改已经写过的相机，包括海康的偏航、俯仰、焦距、张数；改设备上已经填的数。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

UPDATE ent_action_t1 action
SET param_slots_json = jsonb_set(
      action.param_slots_json,
      '{methods}',
      action.param_slots_json->'methods' || added.rows
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT COALESCE(jsonb_agg(
    jsonb_build_object(
      'executionMeans', 'UAV',
      'equipmentModelId', camera.id,
      'fields', template.fields
    )
    ORDER BY camera.code
  ), '[]'::jsonb) AS rows
  FROM dynamic_model camera
  CROSS JOIN (
    SELECT elem->'fields' AS fields
    FROM ent_action_t1 source
    JOIN LATERAL jsonb_array_elements(source.param_slots_json->'methods') elem ON true
    JOIN dynamic_model model
      ON model.id::text = elem->>'equipmentModelId'
     AND model.code = 'dji-pl-m30-cam'
     AND model.deleted = false
    WHERE source.deleted = false
      AND source.tenant_id = 1
      AND source.code = 'orientedShoot'
      AND elem->>'executionMeans' = 'UAV'
    LIMIT 1
  ) template
  WHERE camera.deleted = false
    AND camera.tenant_id = 1
    AND camera.entity_type_code = 'equipment'
    AND (camera.code LIKE 'dji-pl-%' OR camera.code = 'hik-uav-cam')
    AND camera.code <> 'dji-pl-m3ta-cam'
    AND NOT EXISTS (
      SELECT 1
      FROM ent_action_t1 current_action
      JOIN LATERAL jsonb_array_elements(current_action.param_slots_json->'methods') existing ON true
      WHERE current_action.id = (
        SELECT id FROM ent_action_t1
        WHERE deleted = false AND tenant_id = 1 AND code = 'orientedShoot'
      )
        AND existing->>'executionMeans' = 'UAV'
        AND existing->>'equipmentModelId' = camera.id::text
    )
) added
WHERE action.deleted = false
  AND action.tenant_id = 1
  AND action.code = 'orientedShoot'
  AND jsonb_typeof(action.param_slots_json->'methods') = 'array'
  AND added.rows <> '[]'::jsonb;

COMMIT;
