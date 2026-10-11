-- 补还没按型号写上的动作参数。
-- 负责：东古小型无人机抄东古大型无人机上已有的同一份东古参数；要占任务时长的检查和停留观察、选摄像机按支持的产品型号写上已有字段。人工的核对、记录结论、填写表单在人工这一档写动作耗时。
-- 不负责：给返航、降落、电量、开机自检、起飞前检查、设备状态检查编参数（这些指令报文没有要填的项）；不把定向拍照的像素框坐标拿来当航线对焦的 0 到 1 坐标。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

INSERT INTO dynamic_field (
  code, name, type, description, source, status, index_strategy, options, tenant_id, creator
)
VALUES
  (
    'fileSuffix', '拍摄照片文件后缀', 'STRING',
    '为生成媒体文件命名时额外附带的后缀。',
    'LIBRARY', 1, 'NONE', NULL, 1, 'seed'
  ),
  (
    'isPointFocus', '是否点对焦', 'ENUM',
    '0 区域对焦，1 点对焦。',
    'LIBRARY', 1, 'NONE',
    '[{"label":"区域对焦","value":"0"},{"label":"点对焦","value":"1"}]'::jsonb,
    1, 'seed'
  ),
  (
    'isInfiniteFocus', '是否无穷远对焦', 'ENUM',
    '0 非无穷远对焦，1 无穷远对焦。官网支持 M3E 及更新的机型。',
    'LIBRARY', 1, 'NONE',
    '[{"label":"非无穷远对焦","value":"0"},{"label":"无穷远对焦","value":"1"}]'::jsonb,
    1, 'seed'
  )
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  source = 'LIBRARY',
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

CREATE TEMP TABLE duration_fields ON COMMIT DROP AS
SELECT m->'fields' AS fields
FROM ent_action_t1 a
JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code = 'action-6772dfad7e2f4afc988558e1646f958a'
  AND m->>'executionMeans' = 'UAV'
  AND NOT (m ? 'equipmentModelId');

CREATE TEMP TABLE camera_fields ON COMMIT DROP AS
SELECT m->'fields' AS fields
FROM ent_action_t1 a
JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code = 'takePhoto'
  AND m->>'executionMeans' = 'UAV'
  AND NOT (m ? 'equipmentModelId');

CREATE TEMP TABLE aircraft (
  code text PRIMARY KEY
) ON COMMIT DROP;

INSERT INTO aircraft (code)
SELECT m.code
FROM dynamic_model m
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.entity_type_code = 'equipment'
  AND (m.code LIKE 'dji-m%' OR m.code IN ('donggu-uav-small', 'donggu-uav-large'));

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          CASE
            WHEN elem->>'executionMeans' = 'UAV'
             AND NOT (elem ? 'equipmentModelId')
             AND jsonb_array_length(COALESCE(elem->'fields', '[]'::jsonb)) = 0
            THEN jsonb_set(
              elem,
              '{fields}',
              CASE
                WHEN a.code = 'action-262f1fa49d004e2bb48e9877037125c3'
                  THEN (SELECT fields FROM camera_fields)
                ELSE (SELECT fields FROM duration_fields)
              END
            )
            ELSE elem
          END
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code IN (
    'action-709af6187479468da55ff188c4270229',
    'action-1e89c5bda42b4f01a28c8df181fdf01e',
    'action-ecf43e29835c451cbf4498aedf20cfb9',
    'action-629b9a2c7a56425ab5596e1bfcd6d30a',
    'action-351582e9e7c142d5b6b841523aa205d3',
    'act-mig-788d41abf5bbd55c'
  );

CREATE TEMP TABLE fill_action (
  action_code text PRIMARY KEY,
  model_grp text NOT NULL
) ON COMMIT DROP;

INSERT INTO fill_action (action_code, model_grp) VALUES
  ('action-709af6187479468da55ff188c4270229', 'aircraft'),
  ('action-1e89c5bda42b4f01a28c8df181fdf01e', 'aircraft'),
  ('action-ecf43e29835c451cbf4498aedf20cfb9', 'aircraft'),
  ('action-629b9a2c7a56425ab5596e1bfcd6d30a', 'aircraft'),
  ('action-351582e9e7c142d5b6b841523aa205d3', 'aircraft'),
  ('act-mig-788d41abf5bbd55c', 'aircraft'),
  ('action-262f1fa49d004e2bb48e9877037125c3', 'aircraft'),
  ('act-robot-hold', 'donggu');

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
      f.action_code,
      m.code AS model_code,
      jsonb_build_object(
        'executionMeans', 'UAV',
        'equipmentModelId', m.id,
        'fields', src.fields
      ) AS row_obj
    FROM fill_action f
    JOIN ent_action_t1 src_action
      ON src_action.code = f.action_code
     AND src_action.deleted = false
     AND src_action.tenant_id = 1
    JOIN LATERAL (
      SELECT mth->'fields' AS fields
      FROM jsonb_array_elements(src_action.param_slots_json->'methods') mth
      WHERE mth->>'executionMeans' = 'UAV'
        AND NOT (mth ? 'equipmentModelId')
      LIMIT 1
    ) src ON jsonb_array_length(COALESCE(src.fields, '[]'::jsonb)) > 0
    JOIN dynamic_model m
      ON m.deleted = false
     AND m.tenant_id = 1
     AND m.entity_type_code = 'equipment'
     AND (
       (f.model_grp = 'aircraft' AND m.code IN (SELECT code FROM aircraft))
       OR (f.model_grp = 'donggu' AND m.code IN ('donggu-uav-small', 'donggu-uav-large'))
     )
    WHERE NOT EXISTS (
      SELECT 1
      FROM jsonb_array_elements(src_action.param_slots_json->'methods') existing
      WHERE existing->>'executionMeans' = 'UAV'
        AND existing->>'equipmentModelId' = m.id::text
    )
  ) spec
  GROUP BY spec.action_code
) built
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code = built.action_code
  AND built.rows IS NOT NULL;

-- 东古小型无人机与东古大型无人机走同一份东古协议，大型上已经有的参数名单抄到小型。
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
    spec.action_id,
    jsonb_agg(spec.row_obj) AS rows
  FROM (
    SELECT
      a2.id AS action_id,
      jsonb_build_object(
        'executionMeans', 'UAV',
        'equipmentModelId', small.id,
        'fields', large_row->'fields'
      ) AS row_obj
    FROM ent_action_t1 a2
    JOIN dynamic_model large
      ON large.code = 'donggu-uav-large' AND large.deleted = false AND large.tenant_id = 1
    JOIN dynamic_model small
      ON small.code = 'donggu-uav-small' AND small.deleted = false AND small.tenant_id = 1
    JOIN LATERAL jsonb_array_elements(a2.param_slots_json->'methods') large_row
      ON large_row->>'executionMeans' = 'UAV'
     AND large_row->>'equipmentModelId' = large.id::text
    WHERE a2.deleted = false
      AND a2.tenant_id = 1
      AND NOT EXISTS (
        SELECT 1
        FROM jsonb_array_elements(a2.param_slots_json->'methods') existing
        WHERE existing->>'executionMeans' = 'UAV'
          AND existing->>'equipmentModelId' = small.id::text
      )
  ) spec
  GROUP BY spec.action_id
) built
WHERE a.id = built.action_id
  AND built.rows IS NOT NULL;

COMMIT;
