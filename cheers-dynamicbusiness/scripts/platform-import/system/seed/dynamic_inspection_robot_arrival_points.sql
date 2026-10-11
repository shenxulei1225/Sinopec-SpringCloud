-- 机器人到达位置改用地面机器人的点。罐顶是无人机的点，不能留给机器人。
-- 负责：机器人这一档里，选择位置若指向某座罐的罐顶，改成同名罐的罐底。
-- 不负责：无人机飞向目标点、人工到达位置；通道停点、阀门停点。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

CREATE TEMP TABLE robot_point_for_uav_top (
  uav_point text PRIMARY KEY,
  robot_point text NOT NULL
) ON COMMIT DROP;

INSERT INTO robot_point_for_uav_top (uav_point, robot_point) VALUES
  ('70', '94'),
  ('71', '95'),
  ('72', '96'),
  ('73', '97'),
  ('74', '98'),
  ('75', '99'),
  ('76', '100'),
  ('77', '101'),
  ('78', '102'),
  ('79', '103'),
  ('80', '104');

CREATE OR REPLACE FUNCTION pg_temp.robot_location_value(value jsonb)
RETURNS jsonb
LANGUAGE plpgsql
AS $$
DECLARE
  point_id text;
  robot_point text;
BEGIN
  IF jsonb_typeof(value) = 'number' THEN
    point_id := value #>> '{}';
  ELSIF jsonb_typeof(value) = 'string' THEN
    point_id := btrim(value #>> '{}');
  ELSIF jsonb_typeof(value) = 'object' AND value ? 'id' THEN
    RETURN jsonb_set(value, '{id}', pg_temp.robot_location_value(value->'id'));
  ELSE
    RETURN value;
  END IF;
  SELECT map.robot_point INTO robot_point
  FROM robot_point_for_uav_top map
  WHERE map.uav_point = point_id;
  IF robot_point IS NULL THEN
    RETURN value;
  END IF;
  IF jsonb_typeof(value) = 'string' THEN
    RETURN to_jsonb(robot_point);
  END IF;
  RETURN to_jsonb(robot_point::bigint);
END;
$$;

CREATE OR REPLACE FUNCTION pg_temp.rewrite_robot_locations(node jsonb)
RETURNS jsonb
LANGUAGE plpgsql
AS $$
DECLARE
  location_code constant text := 'F-e49f76bdca3e4e1393e8e2f1cc0e7867';
  nest text;
  model_key text;
  slice jsonb;
  models jsonb;
BEGIN
  IF jsonb_typeof(node) <> 'object' THEN
    RETURN node;
  END IF;
  IF node ? location_code THEN
    node := jsonb_set(node, ARRAY[location_code], pg_temp.robot_location_value(node->location_code), false);
  END IF;
  IF node ? 'location_ref' THEN
    node := jsonb_set(node, '{location_ref}', pg_temp.robot_location_value(node->'location_ref'), false);
  END IF;
  FOREACH nest IN ARRAY ARRAY['byEquipmentModel', 'byCameraModel'] LOOP
    IF jsonb_typeof(node->nest) <> 'object' THEN
      CONTINUE;
    END IF;
    models := '{}'::jsonb;
    FOR model_key, slice IN SELECT entry.key, entry.value FROM jsonb_each(node->nest) entry LOOP
      models := models || jsonb_build_object(model_key, pg_temp.rewrite_robot_locations(slice));
    END LOOP;
    node := jsonb_set(node, ARRAY[nest], models, false);
  END LOOP;
  RETURN node;
END;
$$;

UPDATE ent_equipment_t1 equipment
SET custom_fields = jsonb_set(
      equipment.custom_fields,
      '{host_sop_param_pack,entries}',
      (
        SELECT COALESCE(jsonb_agg(
          CASE
            WHEN entry->>'dimensionValue' IS DISTINCT FROM 'ROBOT'
              OR jsonb_typeof(entry->'paramsByNode') <> 'object' THEN entry
            ELSE jsonb_set(
              entry,
              '{paramsByNode}',
              (
                SELECT COALESCE(jsonb_object_agg(
                  node.key,
                  pg_temp.rewrite_robot_locations(node.value)
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
