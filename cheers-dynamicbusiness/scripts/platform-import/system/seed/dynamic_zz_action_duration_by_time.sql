-- 动作耗时只留在会占住任务时长的动作上。
-- 负责：观察、等待、采样、录像、全景、喊话、转动保留或补上动作耗时；人工的核对、记录结论、填写表单只在人工这一档写动作耗时。开关和点云暂停、继续、停止不写动作耗时。
-- 不负责：编一个默认秒数；改路径耗时；给单次快门、曝光、直播开关加耗时。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

CREATE TEMP TABLE drop_duration (code text PRIMARY KEY) ON COMMIT DROP;
INSERT INTO drop_duration (code) VALUES
  ('action-262f1fa49d004e2bb48e9877037125c3'),
  ('action-24c9d55205ed426b994bb89bdeb124ad'),
  ('action-6772dfad7e2f4afc988558e1646f958a'),
  ('action-fc74e442ec0d42cf90d80ac5ba15d351'),
  ('act-mig-3d20dc855a6e0308'),
  ('action-c1748f516dfc4cd5934b373062740cd3'),
  ('act-mig-d39aa7c41200081a'),
  ('action-fa4e3366ba554a73a7cd50234aaf78d8'),
  ('act-uav-leak-inspect'),
  ('action-59d4ad5dff49463592f5b79227213a82'),
  ('action-990b11dac60e489d89813bae8c8c80da'),
  ('action-ab595a61ca164a2c9db74e1311e747a5'),
  ('recordPointCloudPause'),
  ('recordPointCloudResume'),
  ('recordPointCloudStop'),
  ('act-robot-self-check');

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT COALESCE(jsonb_agg(cleaned ORDER BY ord), '[]'::jsonb)
        FROM (
          SELECT
            CASE
              WHEN jsonb_array_length(fields) = 0 AND elem ? 'equipmentModelId'
                THEN NULL
              ELSE jsonb_set(elem, '{fields}', fields)
            END AS cleaned,
            ord
          FROM jsonb_array_elements(a.param_slots_json->'methods') WITH ORDINALITY AS t(elem, ord)
          CROSS JOIN LATERAL (
            SELECT COALESCE(jsonb_agg(f ORDER BY f_ord), '[]'::jsonb) AS fields
            FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) WITH ORDINALITY AS u(f, f_ord)
            WHERE f->>'fieldCode' IS DISTINCT FROM 'action_duration'
          ) kept
        ) s
        WHERE cleaned IS NOT NULL
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code IN (SELECT code FROM drop_duration);

CREATE TEMP TABLE duration_slot ON COMMIT DROP AS
SELECT jsonb_build_object(
  'fieldCode', 'action_duration',
  'required', false,
  'valueSource', 'ACTION_PARAM',
  'defaultValue', NULL
) AS slot;

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          CASE
            WHEN elem->>'executionMeans' = 'UAV'
             AND NOT EXISTS (
               SELECT 1
               FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) f
               WHERE f->>'fieldCode' = 'action_duration'
             )
            THEN jsonb_set(
              elem,
              '{fields}',
              COALESCE(elem->'fields', '[]'::jsonb) || (SELECT slot FROM duration_slot)
            )
            ELSE elem
          END
          ORDER BY ord
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') WITH ORDINALITY AS t(elem, ord)
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code IN ('panoShot', 'megaphone', 'startRecord', 'recordStart');

-- 人工核对、记录结论、填写表单要占一段时间。只写在人工这一档，不写进无人机和机器人。
UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          CASE
            WHEN elem->>'executionMeans' = 'MANUAL'
             AND NOT (elem ? 'equipmentModelId')
             AND NOT EXISTS (
               SELECT 1
               FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) f
               WHERE f->>'fieldCode' = 'action_duration'
             )
            THEN jsonb_set(
              elem,
              '{fields}',
              COALESCE(elem->'fields', '[]'::jsonb) || (SELECT slot FROM duration_slot)
            )
            WHEN elem->>'executionMeans' IS DISTINCT FROM 'MANUAL'
             AND EXISTS (
               SELECT 1
               FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) f
               WHERE f->>'fieldCode' = 'action_duration'
             )
            THEN jsonb_set(
              elem,
              '{fields}',
              COALESCE((
                SELECT jsonb_agg(f ORDER BY f_ord)
                FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) WITH ORDINALITY AS u(f, f_ord)
                WHERE f->>'fieldCode' IS DISTINCT FROM 'action_duration'
              ), '[]'::jsonb)
            )
            ELSE elem
          END
          ORDER BY ord
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') WITH ORDINALITY AS t(elem, ord)
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code IN (
    'action-cf214375fd8b441fb918c84902f8c0e6',
    'act-mig-1ab9b09572c83094',
    'action-0bc9f1eea1bb444d8fbb89c111727028'
  );

COMMIT;
