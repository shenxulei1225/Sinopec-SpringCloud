-- 每个该有这一动作的飞机型号都写上参数，必要格子补上协议里已经写明的默认值。
-- 负责：飞向目标点、一键起飞，以及已经写在部分大疆飞机上的动作，补到全部 dji-m* 飞机。
--       默认值只写协议说明里能对上的数：最大速度 5、悬停 5 秒、普通拍照、未框选、使用全局存储、
--       可见光 visable、主方向 0、画面中心 480×360、广角、区域对焦、云台不转、张数 3、偏航俯仰 0、焦距 30、不驻留。
-- 不负责：机场；把东古加进没有东古指令的动作（悬停、旋转云台）；探照灯、喊话器、点云、全景的型号范围；
--         曝光、光圈、快门、感光度、环境亮度、变焦焦距毫米、执行飞机上的摄像机、到达位置、起飞高度、直播地址。
-- 可重复执行：已有型号行不覆盖；已有默认值不覆盖。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

CREATE TEMP TABLE aircraft ON COMMIT DROP AS
SELECT id::text AS id, code
FROM dynamic_model
WHERE deleted = false
  AND tenant_id = 1
  AND code LIKE 'dji-m%';

CREATE TEMP TABLE keep_narrow (code text PRIMARY KEY) ON COMMIT DROP;
INSERT INTO keep_narrow (code) VALUES
  ('megaphone'),
  ('recordPointCloudPause'),
  ('recordPointCloudResume'),
  ('recordPointCloudStart'),
  ('recordPointCloudStop'),
  ('searchlightOff'),
  ('searchlightOn'),
  ('searchlightStrobe'),
  ('panoShot');

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (a.param_slots_json->'methods') || added.rows
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT a2.id AS action_id,
         jsonb_agg(
           jsonb_build_object(
             'executionMeans', 'UAV',
             'equipmentModelId', ac.id,
             'fields', src.fields
           )
         ) AS rows
  FROM ent_action_t1 a2
  JOIN aircraft ac ON true
  JOIN LATERAL (
    SELECT m->'fields' AS fields
    FROM jsonb_array_elements(a2.param_slots_json->'methods') m
    JOIN aircraft src_model ON src_model.id = m->>'equipmentModelId'
    WHERE m->>'executionMeans' = 'UAV'
      AND jsonb_array_length(COALESCE(m->'fields', '[]'::jsonb)) > 0
    ORDER BY jsonb_array_length(m->'fields') DESC, src_model.code
    LIMIT 1
  ) src ON true
  WHERE a2.deleted = false
    AND a2.tenant_id = 1
    AND a2.code NOT IN (SELECT code FROM keep_narrow)
    AND NOT EXISTS (
      SELECT 1
      FROM jsonb_array_elements(a2.param_slots_json->'methods') have
      WHERE have->>'executionMeans' = 'UAV'
        AND have->>'equipmentModelId' = ac.id
    )
  GROUP BY a2.id
) added
WHERE a.id = added.action_id;

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
  IF code IN (
    'orientedCameraApertue',
    'orientedCameraLuminance',
    'orientedCameraShutterTime',
    'orientedCameraISO',
    'focalLength',
    'selected_camera',
    'F-e49f76bdca3e4e1393e8e2f1cc0e7867'
  ) THEN
    RETURN field;
  END IF;
  IF code = 'max_speed' THEN
    RETURN field || jsonb_build_object('defaultValue', '5');
  ELSIF code = 'hoverTime' THEN
    RETURN field || jsonb_build_object('defaultValue', '5');
  ELSIF code = 'action_duration' AND action_code = 'hover' THEN
    RETURN field || jsonb_build_object('defaultValue', '5');
  ELSIF code = 'shot_count' THEN
    RETURN field || jsonb_build_object('defaultValue', '3');
  ELSIF code IN ('yaw', 'pitch', 'aircraftHeading', 'targetAngle',
                 'gimbalPitchRotateAngle', 'gimbalYawRotateAngle', 'gimbalRollRotateAngle',
                 'gimbalPitchRotateEnable', 'gimbalYawRotateEnable',
                 'gimbalRollRotateEnable', 'gimbalRotateTimeEnable',
                 'accurateFrameValid', 'isPointFocus', 'isInfiniteFocus') THEN
    RETURN field || jsonb_build_object('defaultValue', '0');
  ELSIF code = 'focal_length' THEN
    RETURN field || jsonb_build_object('defaultValue', '30');
  ELSIF code = 'F-2b8fa8bda0b94663979c112af85325dc' THEN
    RETURN field || jsonb_build_object('defaultValue', false);
  ELSIF code = 'gimbalHeadingYawBase' THEN
    RETURN field || jsonb_build_object('defaultValue', 'north');
  ELSIF code = 'gimbalRotateMode' THEN
    RETURN field || jsonb_build_object('defaultValue', 'absoluteAngle');
  ELSIF code = 'useGlobalPayloadLensIndex' THEN
    RETURN field || jsonb_build_object('defaultValue', '1');
  ELSIF code = 'orientedPhotoMode' THEN
    RETURN field || jsonb_build_object('defaultValue', 'normalPhoto');
  ELSIF code = 'payloadLensIndex' THEN
    RETURN field || jsonb_build_object('defaultValue', 'visable');
  ELSIF code = 'focusX' THEN
    RETURN field || jsonb_build_object('defaultValue', '480');
  ELSIF code = 'focusY' THEN
    RETURN field || jsonb_build_object('defaultValue', '360');
  ELSIF code = 'focusRegionWidth' THEN
    RETURN field || jsonb_build_object('defaultValue', '100');
  ELSIF code = 'focusRegionHeight' THEN
    RETURN field || jsonb_build_object('defaultValue', '100');
  ELSIF code IN ('camera_type', 'point_focus_camera_type') THEN
    RETURN field || jsonb_build_object('defaultValue', 'wide');
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
  AND a.tenant_id = 1
  AND jsonb_typeof(a.param_slots_json->'methods') = 'array';

DO $$
DECLARE
  fly_missing int;
  hover_donggu int;
  narrow int;
  exposure_filled int;
BEGIN
  SELECT count(*) INTO fly_missing
  FROM aircraft ac
  WHERE NOT EXISTS (
    SELECT 1
    FROM ent_action_t1 a
    JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
    WHERE a.code = 'action-065340437bcb46788b7dc261fab81c63'
      AND m->>'executionMeans' = 'UAV'
      AND m->>'equipmentModelId' = ac.id
      AND jsonb_array_length(COALESCE(m->'fields', '[]'::jsonb)) > 0
  );
  IF fly_missing <> 0 THEN
    RAISE EXCEPTION '飞向目标点仍有飞机没有参数：%', fly_missing;
  END IF;

  SELECT count(*) INTO hover_donggu
  FROM ent_action_t1 a
  JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
  JOIN dynamic_model dm ON dm.id::text = m->>'equipmentModelId'
  WHERE a.code = 'hover'
    AND dm.code IN ('donggu-uav-small', 'donggu-uav-large');
  IF hover_donggu <> 0 THEN
    RAISE EXCEPTION '悬停等待写上了东古型号';
  END IF;

  SELECT count(DISTINCT m->>'equipmentModelId') INTO narrow
  FROM ent_action_t1 a
  JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
  JOIN aircraft ac ON ac.id = m->>'equipmentModelId'
  WHERE a.code = 'searchlightOn';
  IF narrow <> 2 THEN
    RAISE EXCEPTION '探照灯型号被扩开了：%', narrow;
  END IF;

  SELECT count(*) INTO exposure_filled
  FROM ent_action_t1 a
  JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
  JOIN LATERAL jsonb_array_elements(m->'fields') f ON true
  WHERE f->>'fieldCode' = 'orientedCameraISO'
    AND COALESCE(f->>'defaultValue', '') <> '';
  IF exposure_filled <> 0 THEN
    RAISE EXCEPTION '感光度被写上了默认值';
  END IF;
END $$;

COMMIT;
