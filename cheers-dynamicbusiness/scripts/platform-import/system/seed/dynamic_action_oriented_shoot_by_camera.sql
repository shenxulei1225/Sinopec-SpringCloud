-- 定点拍照的参数名单改按相机类型存放，并按同一规则重写设备上的检查参数。
-- 负责：把已经按飞机型号写好的定点拍照名单，挂到编码能对上的相机类型；海康机载相机用东古那一份。
--       飞向目标点编号只留在飞向目标点这一步。到达作业位置去掉这个编号。
--       定点拍照里、能对上相机类型的数，改存到该相机类型下。
-- 不负责：在本文件里给辅助影像、FPV、机场相机、禅思云台编名单。补全见 dynamic_action_oriented_shoot_all_cameras.sql。
--         两份东古数不一样时，不挑一份写进海康相机。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

CREATE TEMP TABLE camera_of_aircraft (
  aircraft_id text PRIMARY KEY,
  camera_id text NOT NULL
) ON COMMIT DROP;

INSERT INTO camera_of_aircraft (aircraft_id, camera_id)
SELECT aircraft.id::text, camera.id::text
FROM dynamic_model aircraft
JOIN dynamic_model camera
  ON camera.deleted = false
 AND camera.tenant_id = 1
 AND camera.entity_type_code = 'equipment'
 AND camera.code = 'dji-pl-' || substring(aircraft.code FROM 5) || '-cam'
WHERE aircraft.deleted = false
  AND aircraft.tenant_id = 1
  AND aircraft.entity_type_code = 'equipment'
  AND aircraft.code LIKE 'dji-m%'
  AND aircraft.code <> 'dji-m3ta';

INSERT INTO camera_of_aircraft (aircraft_id, camera_id)
SELECT aircraft.id::text, camera.id::text
FROM dynamic_model aircraft
JOIN dynamic_model camera
  ON camera.deleted = false
 AND camera.tenant_id = 1
 AND camera.code = 'hik-uav-cam'
WHERE aircraft.deleted = false
  AND aircraft.tenant_id = 1
  AND aircraft.code IN ('donggu-uav-small', 'donggu-uav-large')
ON CONFLICT (aircraft_id) DO NOTHING;

CREATE OR REPLACE FUNCTION pg_temp.fields_without_instance_camera(fields jsonb)
RETURNS jsonb
LANGUAGE sql
AS $$
  SELECT COALESCE(jsonb_agg(field ORDER BY ord), '[]'::jsonb)
  FROM jsonb_array_elements(COALESCE(fields, '[]'::jsonb)) WITH ORDINALITY AS listed(field, ord)
  WHERE field->>'fieldCode' IS DISTINCT FROM 'selected_camera';
$$;

CREATE OR REPLACE FUNCTION pg_temp.point_text(value jsonb)
RETURNS text
LANGUAGE plpgsql
AS $$
BEGIN
  IF value IS NULL OR jsonb_typeof(value) = 'null' THEN
    RETURN NULL;
  END IF;
  IF jsonb_typeof(value) = 'string' THEN
    IF btrim(value #>> '{}') = '' THEN
      RETURN NULL;
    END IF;
    RETURN value #>> '{}';
  END IF;
  IF jsonb_typeof(value) = 'number' THEN
    RETURN value #>> '{}';
  END IF;
  IF jsonb_typeof(value) = 'object' AND value ? 'id' THEN
    RETURN pg_temp.point_text(value->'id');
  END IF;
  RETURN NULL;
END;
$$;

CREATE OR REPLACE FUNCTION pg_temp.location_value(bag jsonb)
RETURNS jsonb
LANGUAGE plpgsql
AS $$
BEGIN
  IF bag ? 'F-e49f76bdca3e4e1393e8e2f1cc0e7867' THEN
    RETURN bag->'F-e49f76bdca3e4e1393e8e2f1cc0e7867';
  END IF;
  IF bag ? 'location_ref' THEN
    RETURN bag->'location_ref';
  END IF;
  RETURN NULL;
END;
$$;

CREATE OR REPLACE FUNCTION pg_temp.has_location(bag jsonb)
RETURNS boolean
LANGUAGE sql
AS $$
  SELECT bag ? 'F-e49f76bdca3e4e1393e8e2f1cc0e7867' OR bag ? 'location_ref';
$$;

CREATE OR REPLACE FUNCTION pg_temp.with_fly_to_id(bag jsonb)
RETURNS jsonb
LANGUAGE plpgsql
AS $$
DECLARE
  point_id text;
BEGIN
  IF NOT pg_temp.has_location(bag) THEN
    RETURN bag - 'fly_to_id';
  END IF;
  point_id := pg_temp.point_text(pg_temp.location_value(bag));
  IF point_id IS NULL THEN
    RETURN bag - 'fly_to_id';
  END IF;
  RETURN bag || jsonb_build_object('fly_to_id', point_id);
END;
$$;

CREATE OR REPLACE FUNCTION pg_temp.map_model_slices(nested jsonb, mapper text)
RETURNS jsonb
LANGUAGE plpgsql
AS $$
DECLARE
  model_key text;
  slice jsonb;
  out_slices jsonb := '{}'::jsonb;
BEGIN
  IF jsonb_typeof(nested) <> 'object' THEN
    RETURN nested;
  END IF;
  FOR model_key, slice IN SELECT key, value FROM jsonb_each(nested) LOOP
    IF jsonb_typeof(slice) = 'object' AND mapper = 'fly' THEN
      slice := pg_temp.with_fly_to_id(slice);
    ELSIF jsonb_typeof(slice) = 'object' AND mapper = 'strip' THEN
      slice := slice - 'fly_to_id';
    END IF;
    out_slices := out_slices || jsonb_build_object(model_key, slice);
  END LOOP;
  RETURN out_slices;
END;
$$;

CREATE OR REPLACE FUNCTION pg_temp.photo_under_camera(node jsonb)
RETURNS jsonb
LANGUAGE plpgsql
AS $$
DECLARE
  cameras jsonb := '{}'::jsonb;
  aircraft_key text;
  slice jsonb;
  camera_id text;
  grouped jsonb := '{}'::jsonb;
  existing jsonb;
  bucket jsonb;
BEGIN
  IF jsonb_typeof(node->'byCameraModel') = 'object' THEN
    cameras := node->'byCameraModel';
  END IF;
  IF jsonb_typeof(node->'byEquipmentModel') = 'object' THEN
    FOR aircraft_key, slice IN SELECT key, value FROM jsonb_each(node->'byEquipmentModel') LOOP
      SELECT map.camera_id INTO camera_id
      FROM camera_of_aircraft map
      WHERE map.aircraft_id = aircraft_key;
      IF camera_id IS NULL OR jsonb_typeof(slice) <> 'object' THEN
        CONTINUE;
      END IF;
      slice := slice - 'fly_to_id';
      bucket := COALESCE(grouped->camera_id, '[]'::jsonb) || jsonb_build_array(slice);
      grouped := grouped || jsonb_build_object(camera_id, bucket);
    END LOOP;
  END IF;

  FOR camera_id, bucket IN SELECT key, value FROM jsonb_each(grouped) LOOP
    existing := cameras->camera_id;
    IF jsonb_typeof(existing) = 'object' THEN
      CONTINUE;
    END IF;
    IF jsonb_array_length(bucket) = 1 THEN
      cameras := cameras || jsonb_build_object(camera_id, bucket->0);
    ELSIF bucket->0 = bucket->1 THEN
      cameras := cameras || jsonb_build_object(camera_id, bucket->0);
    END IF;
  END LOOP;

  IF cameras = '{}'::jsonb THEN
    RETURN '{}'::jsonb;
  END IF;
  RETURN jsonb_build_object('byCameraModel', cameras);
END;
$$;

CREATE OR REPLACE FUNCTION pg_temp.rewrite_step(node jsonb, action_id text)
RETURNS jsonb
LANGUAGE plpgsql
AS $$
DECLARE
  fly_action constant text := 'action-065340437bcb46788b7dc261fab81c63';
  models jsonb;
BEGIN
  IF jsonb_typeof(node) <> 'object' THEN
    RETURN node;
  END IF;
  IF action_id = fly_action THEN
    node := pg_temp.with_fly_to_id(node);
    IF jsonb_typeof(node->'byEquipmentModel') = 'object' THEN
      models := pg_temp.map_model_slices(node->'byEquipmentModel', 'fly');
      node := jsonb_set(node, '{byEquipmentModel}', models);
    END IF;
    RETURN node;
  END IF;
  IF action_id = 'orientedShoot' THEN
    RETURN pg_temp.photo_under_camera(node);
  END IF;
  node := node - 'fly_to_id';
  IF jsonb_typeof(node->'byEquipmentModel') = 'object' THEN
    node := jsonb_set(node, '{byEquipmentModel}', pg_temp.map_model_slices(node->'byEquipmentModel', 'strip'));
  END IF;
  IF jsonb_typeof(node->'byCameraModel') = 'object' THEN
    node := jsonb_set(node, '{byCameraModel}', pg_temp.map_model_slices(node->'byCameraModel', 'strip'));
  END IF;
  RETURN node;
END;
$$;

UPDATE ent_action_t1 action
SET param_slots_json = jsonb_set(
      action.param_slots_json,
      '{methods}',
      (
        SELECT COALESCE(jsonb_agg(elem), '[]'::jsonb)
        FROM jsonb_array_elements(action.param_slots_json->'methods') elem
        WHERE NOT (
          elem->>'executionMeans' = 'UAV'
          AND elem ? 'equipmentModelId'
          AND NOT EXISTS (
            SELECT 1 FROM dynamic_model camera
            WHERE camera.deleted = false
              AND camera.id::text = elem->>'equipmentModelId'
              AND (camera.code LIKE 'dji-pl-%' OR camera.code = 'hik-uav-cam')
          )
        )
      ) || COALESCE(built.rows, '[]'::jsonb)
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT jsonb_agg(row_obj ORDER BY camera_code) AS rows
  FROM (
    SELECT DISTINCT ON (camera.id)
      camera.code AS camera_code,
      jsonb_build_object(
        'executionMeans', 'UAV',
        'equipmentModelId', camera.id,
        'fields', pg_temp.fields_without_instance_camera(elem->'fields')
      ) AS row_obj
    FROM ent_action_t1 source
    JOIN LATERAL jsonb_array_elements(source.param_slots_json->'methods') elem ON true
    JOIN camera_of_aircraft map ON map.aircraft_id = elem->>'equipmentModelId'
    JOIN dynamic_model camera ON camera.id::text = map.camera_id
    WHERE source.deleted = false
      AND source.tenant_id = 1
      AND source.code = 'orientedShoot'
      AND elem->>'executionMeans' = 'UAV'
    ORDER BY camera.id, map.aircraft_id
  ) spec_rows
) built
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
                  pg_temp.rewrite_step(
                    node.value,
                    (
                      SELECT step->>'actionId'
                      FROM ent_inspection_item_t1 item
                      JOIN LATERAL jsonb_array_elements(item.step_tree_json->'methods') method ON true
                      JOIN LATERAL jsonb_array_elements(method->'stepTree') step ON true
                      WHERE item.id = NULLIF(entry->>'subjectId', '')::bigint
                        AND method->>'methodKey' = entry->>'dimensionValue'
                        AND step->>'nodeKey' = node.key
                      LIMIT 1
                    )
                  )
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
  AND jsonb_typeof(equipment.custom_fields->'host_sop_param_pack'->'entries') = 'array';

COMMIT;
