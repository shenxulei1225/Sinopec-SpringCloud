-- 除定点拍照以外，把无人机动作的参数按型号写上。
-- 负责：每个已经有参数的无人机动作，只给官网支持列里的机型各写一份。参数沿用该动作上已经核对过的那一份。偏航角范围、偏航转动模式、全景元素、探照灯亮度按官网在型号之间分开。东古飞向目标点只留选择位置，对上无人机对接协议的到达指令。东古录像参数写在东古小型和东古大型上。
-- 不负责：重写定点拍照已按型号写好的行；给按体型登记的小型无人机和大型无人机、机场、M400、M3TA 建行。拍照、录像、变焦、点对焦在大疆型号上保留实时指令字段，并补上航线文件里多出来的元素。
-- 重跑时先去掉这些动作上已按型号拆开的无人机行，再按当前未分型号的那一份重写。未分型号的那一行留下。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

CREATE OR REPLACE FUNCTION pg_temp.scoped_fields(src jsonb, mode text, model_code text)
RETURNS jsonb
LANGUAGE sql
AS $$
  SELECT COALESCE(jsonb_agg(patched ORDER BY ord), '[]'::jsonb)
  FROM (
    SELECT
      CASE
        WHEN f->>'fieldCode' = 'aircraftHeading'
          THEN f || jsonb_build_object('min', -180, 'max', 180)
        WHEN f->>'fieldCode' = 'searchlightBrightness'
          THEN f || jsonb_build_object('min', 0, 'max', 100)
        ELSE f
      END AS patched,
      ord
    FROM jsonb_array_elements(COALESCE($1, '[]'::jsonb)) WITH ORDINALITY AS t(f, ord)
    WHERE NOT (
        f->>'fieldCode' = 'aircraftPathMode'
        AND $3 NOT IN ('dji-m300-rtk', 'dji-m350-rtk')
      )
      AND (
        $2 IS DISTINCT FROM 'pano-sub'
        OR f->>'fieldCode' IN ('panoShotSubMode', 'action_duration')
      )
      AND (
        $2 IS DISTINCT FROM 'donggu-arrive'
        OR f->>'fieldCode' = 'F-e49f76bdca3e4e1393e8e2f1cc0e7867'
      )
  ) s;
$$;

CREATE TEMP TABLE grp (
  grp text NOT NULL,
  model_code text NOT NULL,
  PRIMARY KEY (grp, model_code)
) ON COMMIT DROP;

INSERT INTO grp (grp, model_code) VALUES
  ('broad', 'dji-m300-rtk'),
  ('broad', 'dji-m350-rtk'),
  ('broad', 'dji-m30'),
  ('broad', 'dji-m30t'),
  ('broad', 'dji-m3e'),
  ('broad', 'dji-m3t'),
  ('broad', 'dji-m3d'),
  ('broad', 'dji-m3td'),
  ('broad', 'dji-m4d'),
  ('broad', 'dji-m4td'),
  ('broad', 'dji-m4e'),
  ('broad', 'dji-m4t'),
  ('cloud', 'dji-m30'),
  ('cloud', 'dji-m30t'),
  ('cloud', 'dji-m3e'),
  ('cloud', 'dji-m3t'),
  ('cloud', 'dji-m3d'),
  ('cloud', 'dji-m3td'),
  ('cloud', 'dji-m4d'),
  ('cloud', 'dji-m4td'),
  ('cloud', 'dji-m4e'),
  ('cloud', 'dji-m4t'),
  ('pano-full', 'dji-m30'),
  ('pano-full', 'dji-m30t'),
  ('pano-sub', 'dji-m3d'),
  ('pano-sub', 'dji-m3td'),
  ('pano-sub', 'dji-m4d'),
  ('pano-sub', 'dji-m4td'),
  ('pano-sub', 'dji-m4e'),
  ('pano-sub', 'dji-m4t'),
  ('point', 'dji-m300-rtk'),
  ('point', 'dji-m350-rtk'),
  ('m4', 'dji-m4d'),
  ('m4', 'dji-m4td'),
  ('dock', 'dji-m30'),
  ('dock', 'dji-m30t'),
  ('dock', 'dji-m3d'),
  ('dock', 'dji-m3td'),
  ('dock', 'dji-m4d'),
  ('dock', 'dji-m4td'),
  ('donggu', 'donggu-uav-large'),
  ('donggu', 'donggu-uav-small');

INSERT INTO grp (grp, model_code)
SELECT 'fleet', model_code FROM grp WHERE grp = 'cloud'
UNION ALL
SELECT 'fleet', 'donggu-uav-large'
UNION ALL
SELECT 'fleet', 'donggu-uav-small';

CREATE TEMP TABLE action_grp (
  action_code text NOT NULL,
  grp text NOT NULL,
  mode text NOT NULL
) ON COMMIT DROP;

INSERT INTO action_grp (action_code, grp, mode) VALUES
  ('hover', 'broad', 'all'),
  ('customDirName', 'broad', 'all'),
  ('gimbalEvenlyRotate', 'broad', 'all'),
  ('gimbalRotate', 'broad', 'all'),
  ('rotateYaw', 'broad', 'all'),
  ('zoom', 'broad', 'all'),
  ('focus', 'broad', 'all'),
  ('takePhoto', 'broad', 'all'),
  ('startRecord', 'broad', 'all'),
  ('stopRecord', 'broad', 'all'),
  ('photoTake', 'broad', 'all'),
  ('photoStop', 'broad', 'all'),
  ('recordStart', 'broad', 'all'),
  ('recordStop', 'broad', 'all'),
  ('panoShot', 'pano-full', 'all'),
  ('panoShot', 'pano-sub', 'pano-sub'),
  ('recordPointCloudStart', 'point', 'all'),
  ('recordPointCloudPause', 'point', 'all'),
  ('recordPointCloudResume', 'point', 'all'),
  ('recordPointCloudStop', 'point', 'all'),
  ('searchlightOn', 'm4', 'all'),
  ('searchlightOff', 'm4', 'all'),
  ('searchlightStrobe', 'm4', 'all'),
  ('megaphone', 'm4', 'all'),
  ('aperture', 'cloud', 'all'),
  ('iso', 'cloud', 'all'),
  ('shutter', 'cloud', 'all'),
  ('exposureSet', 'cloud', 'all'),
  ('exposureMode', 'cloud', 'all'),
  ('cameraMode', 'cloud', 'all'),
  ('cameraAim', 'cloud', 'all'),
  ('focusMode', 'cloud', 'all'),
  ('focusValue', 'cloud', 'all'),
  ('frameZoom', 'cloud', 'all'),
  ('screenDrag', 'cloud', 'all'),
  ('screenSplit', 'cloud', 'all'),
  ('gimbalReset', 'cloud', 'all'),
  ('irMeteringPoint', 'cloud', 'all'),
  ('irMeteringMode', 'cloud', 'all'),
  ('irMeteringArea', 'cloud', 'all'),
  ('photoStorage', 'cloud', 'all'),
  ('videoStorage', 'cloud', 'all'),
  ('action-a2bf6d76c8d04602bab0d18aa62f6958', 'cloud', 'all'),
  ('action-a2f8ee6c1ab5495fa565742c82d97413', 'cloud', 'all'),
  ('action-065340437bcb46788b7dc261fab81c63', 'dock', 'all'),
  ('action-065340437bcb46788b7dc261fab81c63', 'donggu', 'donggu-arrive'),
  ('action-2b44a1f882f54438b6a1864e3636c889', 'dock', 'all'),
  ('act-uav-wayline-prepare', 'broad', 'all'),
  ('action-24c9d55205ed426b994bb89bdeb124ad', 'fleet', 'all'),
  ('action-6772dfad7e2f4afc988558e1646f958a', 'fleet', 'all'),
  ('action-fc74e442ec0d42cf90d80ac5ba15d351', 'fleet', 'all'),
  ('act-mig-3d20dc855a6e0308', 'fleet', 'all'),
  ('action-c1748f516dfc4cd5934b373062740cd3', 'fleet', 'all'),
  ('act-mig-d39aa7c41200081a', 'fleet', 'all'),
  ('action-fa4e3366ba554a73a7cd50234aaf78d8', 'fleet', 'all'),
  ('act-uav-leak-inspect', 'fleet', 'all'),
  ('action-59d4ad5dff49463592f5b79227213a82', 'fleet', 'all'),
  ('action-990b11dac60e489d89813bae8c8c80da', 'fleet', 'all'),
  ('action-647f9f172c3b489ea862a29c0e8396a2', 'fleet', 'all'),
  ('action-ab595a61ca164a2c9db74e1311e747a5', 'fleet', 'all'),
  ('act-robot-shoot', 'donggu', 'all'),
  ('startRecord', 'donggu', 'donggu-record'),
  ('stopRecord', 'donggu', 'donggu-stop');

CREATE TEMP TABLE donggu_packet (
  mode text PRIMARY KEY,
  fields jsonb NOT NULL
) ON COMMIT DROP;

INSERT INTO donggu_packet (mode, fields) VALUES
  ('donggu-record', jsonb_build_array(
    jsonb_build_object('fieldCode', 'yaw', 'required', true, 'valueSource', 'ACTION_PARAM'),
    jsonb_build_object('fieldCode', 'pitch', 'required', true, 'valueSource', 'ACTION_PARAM'),
    jsonb_build_object('fieldCode', 'focal_length', 'required', true, 'valueSource', 'ACTION_PARAM'),
    jsonb_build_object('fieldCode', 'F-2b8fa8bda0b94663979c112af85325dc', 'required', false, 'valueSource', 'ACTION_PARAM'),
    jsonb_build_object('fieldCode', 'stream_url', 'required', true, 'valueSource', 'SYSTEM'),
    jsonb_build_object('fieldCode', 'record_rotate', 'required', false, 'valueSource', 'ACTION_PARAM'),
    jsonb_build_object('fieldCode', 'action_duration', 'required', false, 'valueSource', 'ACTION_PARAM')
  )),
  ('donggu-stop', jsonb_build_array(
    jsonb_build_object('fieldCode', 'F-2b8fa8bda0b94663979c112af85325dc', 'required', false, 'valueSource', 'ACTION_PARAM'),
    jsonb_build_object('fieldCode', 'action_duration', 'required', false, 'valueSource', 'ACTION_PARAM')
  ));

CREATE TEMP TABLE action_base ON COMMIT DROP AS
SELECT a.code AS action_code, m->'fields' AS fields
FROM ent_action_t1 a
JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND m->>'executionMeans' = 'UAV'
  AND NOT (m ? 'equipmentModelId');

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
        )
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code IN (SELECT action_code FROM action_grp)
  AND jsonb_typeof(a.param_slots_json->'methods') = 'array';

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (a.param_slots_json->'methods') || built.rows
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT
    spec.action_code,
    jsonb_agg(spec.row_obj ORDER BY spec.model_code) AS rows
  FROM (
    SELECT
      g.action_code,
      gm.model_code,
      jsonb_build_object(
        'executionMeans', 'UAV',
        'equipmentModelId', m.id,
        'fields',
          CASE
            WHEN g.mode IN ('donggu-record', 'donggu-stop')
              THEN (SELECT fields FROM donggu_packet p WHERE p.mode = g.mode)
            ELSE pg_temp.scoped_fields(b.fields, g.mode, gm.model_code)
          END
      ) AS row_obj
    FROM action_grp g
    JOIN grp gm ON gm.grp = g.grp
    JOIN action_base b ON b.action_code = g.action_code
    JOIN dynamic_model m
      ON m.deleted = false
     AND m.tenant_id = 1
     AND m.entity_type_code = 'equipment'
     AND m.code = gm.model_code
  ) spec
  GROUP BY spec.action_code
) built
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code = built.action_code
  AND built.rows IS NOT NULL;

-- 大疆型号上保留原来的实时指令字段，再补官网航线文件多出来的元素。东古型号不写这些元素。
UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          CASE
            WHEN elem->>'executionMeans' = 'UAV'
             AND elem ? 'equipmentModelId'
             AND dm.code LIKE 'dji-m%'
             AND extras.fields <> '[]'::jsonb
            THEN jsonb_set(elem, '{fields}', COALESCE(elem->'fields', '[]'::jsonb) || extras.fields)
            ELSE elem
          END
          ORDER BY ord
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') WITH ORDINALITY AS t(elem, ord)
        LEFT JOIN dynamic_model dm
          ON dm.id = NULLIF(elem->>'equipmentModelId', '')::bigint
         AND dm.deleted = false
        LEFT JOIN LATERAL (
          SELECT COALESCE(jsonb_agg(slot ORDER BY slot_ord), '[]'::jsonb) AS fields
          FROM (
            SELECT slot, slot_ord
            FROM (
              VALUES
                (1, jsonb_build_object('fieldCode', 'payloadPositionIndex', 'required', true, 'valueSource', 'EXECUTOR_MOUNT')),
                (2, jsonb_build_object('fieldCode', 'fileSuffix', 'required', true, 'valueSource', 'ACTION_PARAM')),
                (3, jsonb_build_object('fieldCode', 'useGlobalPayloadLensIndex', 'required', true, 'valueSource', 'ACTION_PARAM')),
                (4, jsonb_build_object('fieldCode', 'payloadLensIndex', 'required', true, 'valueSource', 'ACTION_PARAM')),
                (5, jsonb_build_object('fieldCode', 'focalLength', 'required', true, 'valueSource', 'ACTION_PARAM')),
                (6, jsonb_build_object('fieldCode', 'isPointFocus', 'required', true, 'valueSource', 'ACTION_PARAM')),
                (7, jsonb_build_object('fieldCode', 'isInfiniteFocus', 'required', true, 'valueSource', 'ACTION_PARAM'))
            ) AS candidate(slot_ord, slot)
            WHERE CASE a.code
              WHEN 'takePhoto' THEN slot->>'fieldCode' IN ('payloadPositionIndex', 'fileSuffix', 'useGlobalPayloadLensIndex')
                OR (slot->>'fieldCode' = 'payloadLensIndex' AND dm.code IN ('dji-m30','dji-m30t','dji-m3d','dji-m3td','dji-m4d','dji-m4td','dji-m4e','dji-m4t'))
              WHEN 'photoTake' THEN slot->>'fieldCode' IN ('payloadPositionIndex', 'fileSuffix', 'useGlobalPayloadLensIndex')
                OR (slot->>'fieldCode' = 'payloadLensIndex' AND dm.code IN ('dji-m30','dji-m30t','dji-m3d','dji-m3td','dji-m4d','dji-m4td','dji-m4e','dji-m4t'))
              WHEN 'startRecord' THEN slot->>'fieldCode' IN ('payloadPositionIndex', 'fileSuffix', 'useGlobalPayloadLensIndex')
                OR (slot->>'fieldCode' = 'payloadLensIndex' AND dm.code IN ('dji-m30','dji-m30t','dji-m3d','dji-m3td','dji-m4d','dji-m4td','dji-m4e','dji-m4t'))
              WHEN 'recordStart' THEN slot->>'fieldCode' IN ('payloadPositionIndex', 'fileSuffix', 'useGlobalPayloadLensIndex')
                OR (slot->>'fieldCode' = 'payloadLensIndex' AND dm.code IN ('dji-m30','dji-m30t','dji-m3d','dji-m3td','dji-m4d','dji-m4td','dji-m4e','dji-m4t'))
              WHEN 'stopRecord' THEN slot->>'fieldCode' = 'payloadPositionIndex'
                OR (slot->>'fieldCode' = 'payloadLensIndex' AND dm.code IN ('dji-m30','dji-m30t'))
              WHEN 'recordStop' THEN slot->>'fieldCode' = 'payloadPositionIndex'
                OR (slot->>'fieldCode' = 'payloadLensIndex' AND dm.code IN ('dji-m30','dji-m30t'))
              WHEN 'zoom' THEN slot->>'fieldCode' IN ('payloadPositionIndex', 'focalLength')
              WHEN 'focus' THEN slot->>'fieldCode' IN ('payloadPositionIndex', 'isPointFocus')
                OR (slot->>'fieldCode' = 'isInfiniteFocus' AND dm.code IN ('dji-m3e','dji-m3t','dji-m3d','dji-m3td','dji-m4d','dji-m4td','dji-m4e','dji-m4t'))
              ELSE false
            END
            AND NOT EXISTS (
              SELECT 1
              FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) existing
              WHERE existing->>'fieldCode' = slot->>'fieldCode'
            )
          ) missing
        ) extras ON true
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code IN (
    'takePhoto', 'photoTake', 'startRecord', 'recordStart',
    'stopRecord', 'recordStop', 'zoom', 'focus'
  );

COMMIT;
