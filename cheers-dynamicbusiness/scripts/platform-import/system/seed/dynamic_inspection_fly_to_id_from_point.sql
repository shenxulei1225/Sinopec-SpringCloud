-- 选了位置之后，把点位编号写入飞向目标点编号。
-- 负责：已经写在检查参数里的位置，抄成同一格的点位编号；飞向目标点、返回充电这两条动作里，这一格改为系统写入，检查表单不再让人填。
-- 不负责：没选位置时编一个编号；改最大速度、悬停时间、拍摄角度；在大疆上另建点。
-- 再次执行会按当前点位编号重写这一格。位置空着则清掉。没有位置这一格的步骤不动。

SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

CREATE OR REPLACE FUNCTION pg_temp.point_id_text(loc jsonb) RETURNS text
LANGUAGE plpgsql AS $$
DECLARE
  nested jsonb;
BEGIN
  IF loc IS NULL OR jsonb_typeof(loc) = 'null' THEN
    RETURN NULL;
  END IF;
  IF jsonb_typeof(loc) = 'number' THEN
    RETURN loc::text;
  END IF;
  IF jsonb_typeof(loc) = 'string' THEN
    IF btrim(loc #>> '{}') = '' THEN
      RETURN NULL;
    END IF;
    RETURN btrim(loc #>> '{}');
  END IF;
  IF jsonb_typeof(loc) = 'object' THEN
    nested := loc->'id';
    IF nested IS NULL THEN
      nested := loc->'location_ref';
    END IF;
    RETURN pg_temp.point_id_text(nested);
  END IF;
  RETURN NULL;
END $$;

CREATE OR REPLACE FUNCTION pg_temp.sync_fly_to_bag(bag jsonb) RETURNS jsonb
LANGUAGE plpgsql AS $$
DECLARE
  loc jsonb;
  point_text text;
  model_key text;
  slice jsonb;
  nested jsonb := '{}'::jsonb;
BEGIN
  IF jsonb_typeof(bag) <> 'object' THEN
    RETURN bag;
  END IF;
  IF bag ? 'F-e49f76bdca3e4e1393e8e2f1cc0e7867' OR bag ? 'location_ref' THEN
    IF bag ? 'F-e49f76bdca3e4e1393e8e2f1cc0e7867' THEN
      loc := bag->'F-e49f76bdca3e4e1393e8e2f1cc0e7867';
    ELSE
      loc := bag->'location_ref';
    END IF;
    point_text := pg_temp.point_id_text(loc);
    IF point_text IS NOT NULL THEN
      bag := bag || jsonb_build_object('fly_to_id', point_text);
    ELSE
      bag := bag - 'fly_to_id';
    END IF;
  END IF;
  IF jsonb_typeof(bag->'byEquipmentModel') = 'object' THEN
    FOR model_key, slice IN
      SELECT key, value FROM jsonb_each(bag->'byEquipmentModel')
    LOOP
      nested := nested || jsonb_build_object(model_key, pg_temp.sync_fly_to_bag(slice));
    END LOOP;
    bag := jsonb_set(bag, '{byEquipmentModel}', nested);
  END IF;
  RETURN bag;
END $$;

UPDATE ent_equipment_t1 AS equipment
SET custom_fields = jsonb_set(
      equipment.custom_fields,
      '{host_sop_param_pack}',
      rewritten.pack
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT source.id,
         jsonb_set(
           source.pack,
           '{entries}',
           (
             SELECT COALESCE(jsonb_agg(synced.entry ORDER BY synced.ordinal), '[]'::jsonb)
             FROM (
               SELECT entry.ordinal,
                      CASE
                        WHEN jsonb_typeof(entry.value->'paramsByNode') = 'object' THEN jsonb_set(
                          entry.value,
                          '{paramsByNode}',
                          (
                            SELECT COALESCE(jsonb_object_agg(node.key, pg_temp.sync_fly_to_bag(node.value)), '{}'::jsonb)
                            FROM jsonb_each(entry.value->'paramsByNode') AS node(key, value)
                          )
                        )
                        ELSE entry.value
                      END AS entry
               FROM jsonb_array_elements(source.pack->'entries') WITH ORDINALITY AS entry(value, ordinal)
             ) AS synced
           )
         ) AS pack
  FROM (
    SELECT id, custom_fields->'host_sop_param_pack' AS pack
    FROM ent_equipment_t1
    WHERE deleted = false
      AND tenant_id = 1
      AND jsonb_typeof(custom_fields->'host_sop_param_pack') = 'object'
      AND jsonb_typeof(custom_fields->'host_sop_param_pack'->'entries') = 'array'
  ) AS source
) AS rewritten
WHERE equipment.id = rewritten.id
  AND equipment.custom_fields->'host_sop_param_pack' IS DISTINCT FROM rewritten.pack;

UPDATE ent_action_t1 AS action
SET param_slots_json = jsonb_set(
      action.param_slots_json,
      '{methods}',
      (
        SELECT COALESCE(jsonb_agg(
          CASE
            WHEN jsonb_typeof(method.value->'fields') = 'array' THEN jsonb_set(
              method.value,
              '{fields}',
              (
                SELECT COALESCE(jsonb_agg(
                  CASE
                    WHEN field.value->>'fieldCode' = 'fly_to_id'
                      THEN jsonb_set(field.value, '{valueSource}', '"SYSTEM"'::jsonb)
                    ELSE field.value
                  END
                  ORDER BY field.ordinal
                ), '[]'::jsonb)
                FROM jsonb_array_elements(method.value->'fields') WITH ORDINALITY AS field(value, ordinal)
              )
            )
            ELSE method.value
          END
          ORDER BY method.ordinal
        ), '[]'::jsonb)
        FROM jsonb_array_elements(action.param_slots_json->'methods') WITH ORDINALITY AS method(value, ordinal)
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE action.deleted = false
  AND action.tenant_id = 1
  AND jsonb_typeof(action.param_slots_json->'methods') = 'array'
  AND (
    action.param_slots_json::text LIKE '%"fieldCode": "fly_to_id"%'
    OR action.param_slots_json::text LIKE '%"fieldCode":"fly_to_id"%'
  );

UPDATE dynamic_field
SET description = '已选位置的点位编号。选了位置后写入，用来对上这次飞行的回执。不用另行填写。',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'fly_to_id';

COMMIT;
