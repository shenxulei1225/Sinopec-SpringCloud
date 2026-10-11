-- 定点拍照的相机列表去掉 Mavic 3TA。官网定向拍照支持列没有这一款。
-- 负责：删掉这一动作上 Mavic 3TA 相机的参数行，并去掉设备检查参数里记在这一款下的数。
-- 不负责：删设备型号本身。型号还在，只是定点拍照不再列出。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

CREATE TEMP TABLE dropped_camera ON COMMIT DROP AS
SELECT id::text AS camera_id
FROM dynamic_model
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'dji-pl-m3ta-cam';

UPDATE ent_action_t1 action
SET param_slots_json = jsonb_set(
      action.param_slots_json,
      '{methods}',
      (
        SELECT COALESCE(jsonb_agg(elem ORDER BY ord), '[]'::jsonb)
        FROM jsonb_array_elements(action.param_slots_json->'methods') WITH ORDINALITY AS listed(elem, ord)
        WHERE NOT (
          elem->>'executionMeans' = 'UAV'
          AND elem->>'equipmentModelId' IN (SELECT camera_id FROM dropped_camera)
        )
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE action.deleted = false
  AND action.tenant_id = 1
  AND action.code = 'orientedShoot'
  AND jsonb_typeof(action.param_slots_json->'methods') = 'array';

UPDATE ent_equipment_t1 equipment
SET custom_fields = jsonb_set(
      equipment.custom_fields,
      '{host_sop_param_pack,entries}',
      (
        SELECT COALESCE(jsonb_agg(
          CASE
            WHEN jsonb_typeof(entry->'paramsByNode') <> 'object' THEN entry
            ELSE jsonb_set(
              entry,
              '{paramsByNode}',
              (
                SELECT COALESCE(jsonb_object_agg(
                  node.key,
                  CASE
                    WHEN jsonb_typeof(node.value->'byCameraModel') <> 'object' THEN node.value
                    ELSE jsonb_set(
                      node.value,
                      '{byCameraModel}',
                      (
                        SELECT COALESCE(jsonb_object_agg(slice.key, slice.value), '{}'::jsonb)
                        FROM jsonb_each(node.value->'byCameraModel') slice
                        WHERE slice.key NOT IN (SELECT camera_id FROM dropped_camera)
                      )
                    )
                  END
                ), '{}'::jsonb)
                FROM jsonb_each(entry->'paramsByNode') node
              )
            )
          END
        ), '[]'::jsonb)
        FROM jsonb_array_elements(equipment.custom_fields->'host_sop_param_pack'->'entries') entry
      ),
      true
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE equipment.deleted = false
  AND jsonb_typeof(equipment.custom_fields->'host_sop_param_pack'->'entries') = 'array'
  AND (equipment.custom_fields->'host_sop_param_pack'->'entries')::text LIKE '%' || (SELECT camera_id FROM dropped_camera) || '%';

COMMIT;
