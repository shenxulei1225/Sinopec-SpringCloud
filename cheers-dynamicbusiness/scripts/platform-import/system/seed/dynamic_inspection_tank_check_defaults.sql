-- 储罐检查：动作库补上已有依据的默认值，再把每台储罐的罐顶位置写进各型号。
-- 负责：拍摄张数 3、偏航 0、俯仰 0、焦距 30、是否驻留否、大疆飞向目标点最大速度 5、
--       旋转云台默认不转、定点拍照云台角 0、定点拍照耗时 10。这些和固定摄像机拍照、
--       大疆 M30 最大速度、定点拍照已有耗时是同一批已经写下的数。
--       到达位置用点位台账里该储罐的罐顶点，写进这一型号；和默认值不同的角度留在储罐上。
-- 不负责：悬停秒数、曝光、对焦框、光圈、快门、感光度、环境亮度、执行飞机上的摄像机。
--         不把最大速度写到东古或机器人。不把焦距 0 写进大疆变焦焦距。
-- 可重复执行：已有默认值不覆盖；储罐上某一型号已经写下的格子不覆盖。

SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

CREATE OR REPLACE FUNCTION pg_temp.fill_default(action_code text, field jsonb)
RETURNS jsonb
LANGUAGE plpgsql AS $$
DECLARE
  code text := field->>'fieldCode';
  cur text := COALESCE(field->>'defaultValue', '');
BEGIN
  IF cur <> '' THEN
    RETURN field;
  END IF;
  IF code = 'shot_count' THEN
    RETURN field || jsonb_build_object('defaultValue', '3');
  ELSIF code IN ('yaw', 'pitch') THEN
    RETURN field || jsonb_build_object('defaultValue', '0');
  ELSIF code = 'focal_length' THEN
    RETURN field || jsonb_build_object('defaultValue', '30');
  ELSIF code = 'max_speed' THEN
    RETURN field || jsonb_build_object('defaultValue', '5');
  ELSIF code = 'F-2b8fa8bda0b94663979c112af85325dc' THEN
    RETURN field || jsonb_build_object('defaultValue', false);
  ELSIF code IN (
    'gimbalPitchRotateEnable',
    'gimbalYawRotateEnable',
    'gimbalRollRotateEnable',
    'gimbalRotateTimeEnable'
  ) THEN
    RETURN field || jsonb_build_object('defaultValue', '0');
  ELSIF code = 'gimbalHeadingYawBase' THEN
    RETURN field || jsonb_build_object('defaultValue', 'north');
  ELSIF code = 'gimbalRotateMode' THEN
    RETURN field || jsonb_build_object('defaultValue', 'absoluteAngle');
  ELSIF code IN ('gimbalPitchRotateAngle', 'gimbalYawRotateAngle') THEN
    RETURN field || jsonb_build_object('defaultValue', '0');
  ELSIF code = 'action_duration' AND action_code IN ('orientedShoot', 'act-robot-shoot') THEN
    RETURN field || jsonb_build_object('defaultValue', '10');
  END IF;
  RETURN field;
END $$;

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT COALESCE(jsonb_agg(
          CASE
            WHEN m.value->>'executionMeans' IN ('UAV', 'ROBOT') THEN
              jsonb_set(
                m.value,
                '{fields}',
                (
                  SELECT COALESCE(
                    jsonb_agg(pg_temp.fill_default(a.code, f.value) ORDER BY f.ord),
                    '[]'::jsonb
                  )
                  FROM jsonb_array_elements(COALESCE(m.value->'fields', '[]'::jsonb))
                    WITH ORDINALITY AS f(value, ord)
                )
              )
            ELSE m.value
          END
          ORDER BY m.ord
        ), '[]'::jsonb)
        FROM jsonb_array_elements(a.param_slots_json->'methods') WITH ORDINALITY AS m(value, ord)
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.code IN (
    'orientedShoot',
    'act-robot-shoot',
    'act-robot-hold',
    'gimbalRotate',
    'action-065340437bcb46788b7dc261fab81c63'
  );

CREATE TEMP TABLE tank_top (
  equipment_id bigint PRIMARY KEY,
  point_id bigint NOT NULL
) ON COMMIT DROP;

INSERT INTO tank_top (equipment_id, point_id) VALUES
  (900104, 70),
  (900105, 71),
  (900107, 75),
  (900108, 76),
  (900109, 77),
  (900110, 72),
  (900111, 73),
  (900112, 74),
  (900113, 78),
  (900114, 79),
  (900115, 80);

CREATE TEMP TABLE field_default (
  code text PRIMARY KEY,
  val jsonb NOT NULL
) ON COMMIT DROP;

INSERT INTO field_default (code, val) VALUES
  ('shot_count', '"3"'::jsonb),
  ('yaw', '"0"'::jsonb),
  ('pitch', '"0"'::jsonb),
  ('focal_length', '"30"'::jsonb),
  ('max_speed', '"5"'::jsonb),
  ('F-2b8fa8bda0b94663979c112af85325dc', 'false'::jsonb),
  ('gimbalPitchRotateAngle', '"0"'::jsonb),
  ('gimbalYawRotateAngle', '"0"'::jsonb),
  ('gimbalPitchRotateEnable', '"0"'::jsonb),
  ('gimbalYawRotateEnable', '"0"'::jsonb),
  ('gimbalRollRotateEnable', '"0"'::jsonb),
  ('gimbalRotateTimeEnable', '"0"'::jsonb);

CREATE OR REPLACE FUNCTION pg_temp.same_as_default(val jsonb, def jsonb)
RETURNS boolean
LANGUAGE plpgsql AS $$
BEGIN
  IF val IS NULL OR jsonb_typeof(val) = 'null' OR def IS NULL THEN
    RETURN false;
  END IF;
  IF val = def THEN
    RETURN true;
  END IF;
  RETURN trim(both '"' from val::text) = trim(both '"' from def::text);
END $$;

CREATE OR REPLACE FUNCTION pg_temp.unwrap_pack(raw jsonb)
RETURNS jsonb
LANGUAGE plpgsql AS $$
DECLARE
  pack jsonb := raw;
  n int := 0;
BEGIN
  WHILE pack IS NOT NULL AND jsonb_typeof(pack) = 'string' AND n < 3 LOOP
    pack := (pack #>> '{}')::jsonb;
    n := n + 1;
  END LOOP;
  IF pack IS NULL OR jsonb_typeof(pack) <> 'object' THEN
    pack := '{"version":1,"entries":[]}'::jsonb;
  END IF;
  IF pack->'version' IS NULL THEN
    pack := pack || '{"version":1}'::jsonb;
  END IF;
  IF jsonb_typeof(pack->'entries') <> 'array' THEN
    pack := jsonb_set(pack, '{entries}', '[]'::jsonb);
  END IF;
  RETURN pack;
END $$;

-- 把这一步已有的、和默认值不同的数，写进该型号自己的格子。罐顶位置每个相关型号都写。
CREATE OR REPLACE FUNCTION pg_temp.nest_model_slice(
  node jsonb,
  action_code text,
  means text,
  point_id bigint
) RETURNS jsonb
LANGUAGE plpgsql AS $$
DECLARE
  model record;
  field_code text;
  flat jsonb;
  nested jsonb;
  slice jsonb;
  loc_key constant text := 'F-e49f76bdca3e4e1393e8e2f1cc0e7867';
  is_arrive boolean;
  flat_val jsonb;
  yaw_val jsonb;
  pitch_val jsonb;
  focal_val jsonb;
  changed boolean;
BEGIN
  IF node IS NULL OR jsonb_typeof(node) <> 'object' THEN
    node := '{}'::jsonb;
  END IF;
  is_arrive := action_code IN (
    'act-robot-arrive',
    'action-065340437bcb46788b7dc261fab81c63'
  );
  flat := node - 'byEquipmentModel';
  nested := node->'byEquipmentModel';
  IF nested IS NULL OR jsonb_typeof(nested) <> 'object' THEN
    nested := '{}'::jsonb;
  END IF;
  yaw_val := flat->'yaw';
  pitch_val := flat->'pitch';
  focal_val := flat->'focal_length';

  FOR model IN
    SELECT m->>'equipmentModelId' AS model_id,
           COALESCE(m->'fields', '[]'::jsonb) AS fields
    FROM ent_action_t1 a
    JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
    WHERE a.deleted = false
      AND a.code = action_code
      AND upper(m->>'executionMeans') = upper(means)
      AND COALESCE(m->>'equipmentModelId', '') <> ''
      AND jsonb_array_length(COALESCE(m->'fields', '[]'::jsonb)) > 0
  LOOP
    slice := nested->model.model_id;
    IF slice IS NULL OR jsonb_typeof(slice) <> 'object' THEN
      slice := '{}'::jsonb;
    END IF;
    changed := false;

    FOR field_code IN
      SELECT f->>'fieldCode'
      FROM jsonb_array_elements(model.fields) f
      WHERE COALESCE(f->>'fieldCode', '') <> ''
    LOOP
      IF slice ? field_code THEN
        CONTINUE;
      END IF;

      IF is_arrive AND field_code = loc_key THEN
        slice := slice || jsonb_build_object(field_code, to_jsonb(point_id));
        changed := true;
      ELSIF is_arrive AND field_code = 'fly_to_id' THEN
        slice := slice || jsonb_build_object(field_code, to_jsonb(point_id::text));
        changed := true;
      ELSIF flat ? field_code THEN
        flat_val := flat->field_code;
        IF flat_val IS NOT NULL
           AND jsonb_typeof(flat_val) <> 'null'
           AND NOT pg_temp.same_as_default(flat_val, (SELECT val FROM field_default WHERE code = field_code)) THEN
          slice := slice || jsonb_build_object(field_code, flat_val);
          changed := true;
        END IF;
      ELSIF field_code = 'gimbalYawRotateAngle'
            AND yaw_val IS NOT NULL
            AND NOT pg_temp.same_as_default(yaw_val, '"0"'::jsonb) THEN
        slice := slice || jsonb_build_object(field_code, yaw_val);
        changed := true;
      ELSIF field_code = 'gimbalPitchRotateAngle'
            AND pitch_val IS NOT NULL
            AND NOT pg_temp.same_as_default(pitch_val, '"0"'::jsonb) THEN
        slice := slice || jsonb_build_object(field_code, pitch_val);
        changed := true;
      ELSIF field_code = 'focalLength'
            AND focal_val IS NOT NULL
            AND jsonb_typeof(focal_val) <> 'null'
            AND COALESCE(focal_val #>> '{}', '') <> '' THEN
        slice := slice || jsonb_build_object(field_code, focal_val);
        changed := true;
      ELSIF field_code = 'gimbalYawRotateEnable'
            AND yaw_val IS NOT NULL
            AND NOT pg_temp.same_as_default(yaw_val, '"0"'::jsonb) THEN
        slice := slice || jsonb_build_object(field_code, '"1"'::jsonb);
        changed := true;
      ELSIF field_code = 'gimbalPitchRotateEnable'
            AND pitch_val IS NOT NULL
            AND NOT pg_temp.same_as_default(pitch_val, '"0"'::jsonb) THEN
        slice := slice || jsonb_build_object(field_code, '"1"'::jsonb);
        changed := true;
      END IF;
    END LOOP;

    IF changed OR (nested ? model.model_id) THEN
      nested := nested || jsonb_build_object(model.model_id, slice);
    END IF;
  END LOOP;

  IF nested = '{}'::jsonb THEN
    RETURN node;
  END IF;
  RETURN jsonb_set(node, '{byEquipmentModel}', nested);
END $$;

DO $$
DECLARE
  tank record;
  walk record;
  pack jsonb;
  entries jsonb;
  found int;
  i int;
  entry jsonb;
  nodes jsonb;
  node jsonb;
  node_key text;
  loc_key constant text := 'F-e49f76bdca3e4e1393e8e2f1cc0e7867';
BEGIN
  FOR tank IN
    SELECT t.equipment_id, t.point_id, e.custom_fields
    FROM tank_top t
    JOIN ent_equipment_t1 e
      ON e.id = t.equipment_id AND e.deleted = false AND e.facility_id = 44
  LOOP
    pack := pg_temp.unwrap_pack(tank.custom_fields->'host_sop_param_pack');
    entries := pack->'entries';

    FOR walk IN
      SELECT item.id AS item_id,
             upper(method.value->>'methodKey') AS means,
             st.value->>'nodeKey' AS node_key,
             st.value->>'actionId' AS action_id
      FROM ent_inspection_item_t1 item
      CROSS JOIN LATERAL jsonb_array_elements(item.step_tree_json->'methods') method(value)
      CROSS JOIN LATERAL jsonb_array_elements(method.value->'stepTree') st(value)
      WHERE item.deleted = false
        AND item.tenant_id = 1
        AND item.id IN (2017, 2354, 2355, 2357, 3146)
        AND upper(method.value->>'methodKey') IN ('UAV', 'ROBOT')
        AND COALESCE(st.value->>'nodeKey', '') <> ''
        AND COALESCE(st.value->>'actionId', '') <> ''
    LOOP
      found := NULL;
      FOR i IN 0 .. jsonb_array_length(entries) - 1 LOOP
        entry := entries->i;
        IF COALESCE(entry->>'subjectId', '') <> ''
           AND (entry->>'subjectId')::bigint = walk.item_id
           AND upper(COALESCE(entry->>'dimensionValue', '')) = walk.means
           AND COALESCE(entry->>'dimensionKey', 'execution_means') = 'execution_means' THEN
          found := i;
          EXIT;
        END IF;
      END LOOP;

      IF found IS NULL THEN
        entry := jsonb_build_object(
          'enabled', true,
          'subjectType', 'inspection_item',
          'subjectId', walk.item_id,
          'targetType', 'inspection_item',
          'targetId', walk.item_id,
          'dimensionKey', 'execution_means',
          'dimensionValue', walk.means,
          'paramsByNode', '{}'::jsonb
        );
        entries := entries || jsonb_build_array(entry);
        found := jsonb_array_length(entries) - 1;
      END IF;

      nodes := entry->'paramsByNode';
      IF nodes IS NULL OR jsonb_typeof(nodes) <> 'object' THEN
        nodes := '{}'::jsonb;
      END IF;
      node := nodes->walk.node_key;
      IF node IS NULL OR jsonb_typeof(node) <> 'object' THEN
        node := '{}'::jsonb;
      END IF;

      IF walk.action_id IN ('act-robot-arrive', 'action-065340437bcb46788b7dc261fab81c63') THEN
        node := node || jsonb_build_object(
          loc_key, to_jsonb(tank.point_id),
          'fly_to_id', to_jsonb(tank.point_id::text)
        );
      END IF;

      node := pg_temp.nest_model_slice(node, walk.action_id, walk.means, tank.point_id);
      nodes := nodes || jsonb_build_object(walk.node_key, node);
      entry := jsonb_set(entry, '{paramsByNode}', nodes);
      entries := jsonb_set(entries, ARRAY[found::text], entry);
    END LOOP;

    pack := jsonb_set(pack, '{entries}', entries);
    UPDATE ent_equipment_t1
    SET custom_fields = jsonb_set(
          COALESCE(custom_fields, '{}'::jsonb),
          '{host_sop_param_pack}',
          pack
        ),
        updater = 'seed',
        update_time = CURRENT_TIMESTAMP
    WHERE id = tank.equipment_id;
  END LOOP;
END $$;

DO $$
DECLARE
  bad text;
BEGIN
  SELECT string_agg(e.id::text, ',')
    INTO bad
  FROM ent_equipment_t1 e
  JOIN tank_top t ON t.equipment_id = e.id
  WHERE jsonb_typeof(e.custom_fields->'host_sop_param_pack') <> 'object';
  IF bad IS NOT NULL THEN
    RAISE EXCEPTION '参数包仍不是对象：%', bad;
  END IF;

  SELECT string_agg(t.equipment_id::text || '/' || model.model_id, ',')
    INTO bad
  FROM tank_top t
  JOIN ent_equipment_t1 e ON e.id = t.equipment_id
  CROSS JOIN (VALUES ('1867'), ('1913'), ('1772')) AS model(model_id)
  LEFT JOIN LATERAL (
    SELECT entry->'paramsByNode'->'n1'->'byEquipmentModel'->model.model_id
             ->'F-e49f76bdca3e4e1393e8e2f1cc0e7867' AS loc
    FROM jsonb_array_elements(e.custom_fields->'host_sop_param_pack'->'entries') entry
    WHERE (entry->>'subjectId')::bigint = 2354
      AND upper(entry->>'dimensionValue') = CASE WHEN model.model_id = '1772' THEN 'ROBOT' ELSE 'UAV' END
    LIMIT 1
  ) got ON true
  WHERE got.loc IS DISTINCT FROM to_jsonb(t.point_id);
  IF bad IS NOT NULL THEN
    RAISE EXCEPTION '罐顶没有写进型号：%', bad;
  END IF;

  IF EXISTS (
    SELECT 1
    FROM ent_equipment_t1 e
    JOIN LATERAL jsonb_array_elements(e.custom_fields->'host_sop_param_pack'->'entries') entry ON true
    WHERE e.id = 900105
      AND (entry->>'subjectId')::bigint = 2354
      AND upper(entry->>'dimensionValue') = 'UAV'
      AND entry->'paramsByNode'->'n1'->'byEquipmentModel'->'1913' ? 'max_speed'
  ) THEN
    RAISE EXCEPTION '东古飞向目标点写上了最大速度';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM ent_action_t1 a
    JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
    JOIN LATERAL jsonb_array_elements(m->'fields') f ON true
    WHERE a.code = 'orientedShoot'
      AND m->>'equipmentModelId' = '1913'
      AND f->>'fieldCode' = 'shot_count'
      AND f->>'defaultValue' IS DISTINCT FROM '3'
  ) THEN
    RAISE EXCEPTION '东古定点拍照拍摄张数默认值不是 3';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM ent_action_t1 a
    JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
    JOIN LATERAL jsonb_array_elements(m->'fields') f ON true
    WHERE a.code = 'action-065340437bcb46788b7dc261fab81c63'
      AND m->>'equipmentModelId' = '1875'
      AND f->>'fieldCode' = 'max_speed'
      AND f->>'defaultValue' IS DISTINCT FROM '5'
  ) THEN
    RAISE EXCEPTION '大疆飞向目标点最大速度默认值不是 5';
  END IF;
END $$;

COMMIT;
