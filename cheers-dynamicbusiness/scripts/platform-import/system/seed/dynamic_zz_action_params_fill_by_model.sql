-- 按型号补上还缺的动作参数名单。
-- 负责：机器人两个型号各写一份，名单用这一动作机器人这一档已有的参数；这一档是空的时，用东古无人机上已经写好、且不是大疆航线格子的那一份。
-- 多数大疆飞机已经有名单、只缺 M400 或 M3TA 的，用同动作上已有的一份补上。
-- 东古小型、东古大型缺行时，只补对接协议里已经对上的参数，并从本动作已有格子里抄定义。
-- 不负责：改已经写上的型号行；把悬停、云台、变焦、曝光这些大疆航线格子抄到东古或机器人；给机场型号写飞行动作参数。
-- 可重复执行：已有「手段 + 型号」的行不会再插一行。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

CREATE TEMP TABLE dji_only_code (code text PRIMARY KEY) ON COMMIT DROP;
INSERT INTO dji_only_code (code) VALUES
  ('hoverTime'),
  ('fly_to_id'),
  ('max_speed'),
  ('payloadPositionIndex'),
  ('payloadLensIndex'),
  ('focalLength'),
  ('aircraftHeading'),
  ('aircraftPathMode'),
  ('gimbalHeadingYawBase'),
  ('gimbalRotateMode'),
  ('gimbalPitchRotateEnable'),
  ('gimbalPitchRotateAngle'),
  ('gimbalRollRotateEnable'),
  ('gimbalRollRotateAngle'),
  ('gimbalYawRotateEnable'),
  ('gimbalYawRotateAngle'),
  ('gimbalRotateTimeEnable'),
  ('gimbalRotateTime'),
  ('focusX'),
  ('focusY'),
  ('focusRegionWidth'),
  ('focusRegionHeight'),
  ('fileSuffix'),
  ('useGlobalPayloadLensIndex'),
  ('isPointFocus'),
  ('isInfiniteFocus'),
  ('orientedCameraApertue'),
  ('orientedCameraLuminance'),
  ('orientedCameraShutterTime'),
  ('orientedCameraISO'),
  ('orientedPhotoMode'),
  ('panoShotSubMode'),
  ('searchlightBrightness');

CREATE TEMP TABLE add_row (
  action_id bigint NOT NULL,
  means text NOT NULL,
  model_id text NOT NULL,
  fields jsonb NOT NULL,
  PRIMARY KEY (action_id, means, model_id)
) ON COMMIT DROP;

-- 机器人：已有名单，或东古名单里没有大疆航线格子。
INSERT INTO add_row (action_id, means, model_id, fields)
SELECT src.action_id, 'ROBOT', rm.id::text, src.fields
FROM (
  SELECT a.id AS action_id,
         COALESCE(
           (
             SELECT m->'fields'
             FROM jsonb_array_elements(a.param_slots_json->'methods') m
             WHERE m->>'executionMeans' = 'ROBOT'
               AND NOT (m ? 'equipmentModelId')
               AND jsonb_array_length(COALESCE(m->'fields', '[]'::jsonb)) > 0
             LIMIT 1
           ),
           (
             SELECT m->'fields'
             FROM jsonb_array_elements(a.param_slots_json->'methods') m
             WHERE m->>'executionMeans' = 'UAV'
               AND m->>'equipmentModelId' IN ('1913', '1912')
               AND jsonb_array_length(COALESCE(m->'fields', '[]'::jsonb)) > 0
               AND NOT EXISTS (
                 SELECT 1
                 FROM jsonb_array_elements(m->'fields') f
                 JOIN dji_only_code d ON d.code = f->>'fieldCode'
               )
             ORDER BY m->>'equipmentModelId' DESC
             LIMIT 1
           )
         ) AS fields
  FROM ent_action_t1 a
  WHERE a.deleted = false
    AND a.tenant_id = 1
    AND jsonb_typeof(a.param_slots_json->'methods') = 'array'
    AND EXISTS (
      SELECT 1
      FROM jsonb_array_elements(a.param_slots_json->'methods') m
      WHERE m->>'executionMeans' = 'ROBOT'
    )
) src
JOIN dynamic_model rm
  ON rm.deleted = false
 AND rm.tenant_id = 1
 AND rm.code IN (
   'MODEL-4a8d42f9e6584c47b91de62062829c5c',
   'MODEL-446e168c800240b88ee995806ac85c0a'
 )
WHERE src.fields IS NOT NULL
  AND jsonb_array_length(src.fields) > 0;

-- 大疆：已经有至少 10 个飞机型号的动作，把缺的 M400、M3TA 用同动作里格子最多的那一份补上。
INSERT INTO add_row (action_id, means, model_id, fields)
SELECT covered.action_id, 'UAV', missing.id::text, covered.fields
FROM (
  SELECT a.id AS action_id,
         (
           SELECT m->'fields'
           FROM jsonb_array_elements(a.param_slots_json->'methods') m
           JOIN dynamic_model dm
             ON dm.deleted = false
            AND dm.tenant_id = 1
            AND dm.id = NULLIF(m->>'equipmentModelId', '')::bigint
            AND dm.code LIKE 'dji-m%'
           WHERE m->>'executionMeans' = 'UAV'
           ORDER BY jsonb_array_length(COALESCE(m->'fields', '[]'::jsonb)) DESC, dm.code
           LIMIT 1
         ) AS fields,
         (
           SELECT count(DISTINCT dm.id)
           FROM jsonb_array_elements(a.param_slots_json->'methods') m
           JOIN dynamic_model dm
             ON dm.deleted = false
            AND dm.tenant_id = 1
            AND dm.id = NULLIF(m->>'equipmentModelId', '')::bigint
            AND dm.code LIKE 'dji-m%'
           WHERE m->>'executionMeans' = 'UAV'
         ) AS model_count
  FROM ent_action_t1 a
  WHERE a.deleted = false
    AND a.tenant_id = 1
    AND jsonb_typeof(a.param_slots_json->'methods') = 'array'
) covered
JOIN dynamic_model missing
  ON missing.deleted = false
 AND missing.tenant_id = 1
 AND missing.code IN ('dji-m400', 'dji-m3ta')
WHERE covered.model_count >= 10
  AND covered.fields IS NOT NULL
  AND jsonb_array_length(covered.fields) > 0;

-- 东古：对接协议里点名的格子，且本动作已经有这个格子的定义。
INSERT INTO add_row (action_id, means, model_id, fields)
SELECT proto.action_id, 'UAV', dg.id::text, proto.fields
FROM (
  SELECT a.id AS action_id,
         (
           SELECT COALESCE(jsonb_agg(found.field ORDER BY found.ord), '[]'::jsonb)
           FROM (
             SELECT DISTINCT ON (slot.slot_code)
                    slot.ord,
                    existing.field
             FROM (
               SELECT s.elem->>'slot' AS slot_code,
                      s.ord
               FROM ent_protocol_mapping_t1 map
               JOIN LATERAL jsonb_array_elements(map.custom_fields->'param_slot_mapping_json')
                 WITH ORDINALITY AS s(elem, ord) ON true
               WHERE map.deleted = false
                 AND map.tenant_id = 1
                 AND map.custom_fields->>'mapped_protocol_version' = 'uav-ws'
                 AND map.custom_fields#>>'{action_ref,id}' = a.id::text
             ) slot
             JOIN LATERAL (
               SELECT f AS field
               FROM jsonb_array_elements(a.param_slots_json->'methods') method_row
               JOIN LATERAL jsonb_array_elements(COALESCE(method_row->'fields', '[]'::jsonb)) f ON true
               WHERE f->>'fieldCode' = slot.slot_code
               LIMIT 1
             ) existing ON true
             ORDER BY slot.slot_code, slot.ord
           ) found
         ) AS fields
  FROM ent_action_t1 a
  WHERE a.deleted = false
    AND a.tenant_id = 1
) proto
JOIN dynamic_model dg
  ON dg.deleted = false
 AND dg.tenant_id = 1
 AND dg.code IN ('donggu-uav-small', 'donggu-uav-large')
WHERE proto.fields IS NOT NULL
  AND jsonb_array_length(proto.fields) > 0;

DELETE FROM add_row ar
WHERE EXISTS (
  SELECT 1
  FROM ent_action_t1 a
  JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
  WHERE a.id = ar.action_id
    AND m->>'executionMeans' = ar.means
    AND m->>'equipmentModelId' = ar.model_id
);

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (a.param_slots_json->'methods') || built.rows
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT action_id,
         jsonb_agg(
           jsonb_build_object(
             'executionMeans', means,
             'equipmentModelId', model_id,
             'fields', fields
           )
           ORDER BY means, model_id
         ) AS rows
  FROM add_row
  GROUP BY action_id
) built
WHERE a.id = built.action_id;

COMMIT;
