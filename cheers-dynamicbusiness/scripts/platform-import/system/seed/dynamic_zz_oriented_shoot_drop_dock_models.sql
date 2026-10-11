-- 定点拍照不再把三款机场写成已经支持。机场要支持这个动作时，在动作库里添加型号。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT COALESCE(jsonb_agg(elem), '[]'::jsonb)
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
        WHERE NOT (
          elem->>'executionMeans' = 'UAV'
          AND elem ? 'equipmentModelId'
          AND (elem->>'equipmentModelId')::bigint IN (
            SELECT id
            FROM dynamic_model
            WHERE deleted = false
              AND tenant_id = 1
              AND code IN ('dji-dock-1', 'dji-dock-2', 'dji-dock-3')
          )
        )
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code = 'orientedShoot'
  AND jsonb_typeof(a.param_slots_json->'methods') = 'array';
