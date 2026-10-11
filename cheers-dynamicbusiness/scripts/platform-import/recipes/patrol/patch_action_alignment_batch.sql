-- 批量补齐：动作 param_slots 与对照 USER 槽、空型号行、常用默认值（与种子 rest/refine 同源）。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

-- 1) 到达指定位置 / 返回充电：补 fly_to_id、max_speed（对照 data.fly_to_id）
UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          CASE WHEN elem->>'executionMeans' = 'UAV' THEN jsonb_set(
            elem,
            '{fields}',
            COALESCE(elem->'fields', '[]'::jsonb) || (
              SELECT COALESCE(jsonb_agg(add.field), '[]'::jsonb)
              FROM (VALUES
                ('{"fieldCode":"max_speed","required":true,"defaultValue":null}'::jsonb),
                ('{"fieldCode":"fly_to_id","required":true,"valueSource":"SYSTEM","defaultValue":null}'::jsonb)
              ) AS add(field)
              WHERE NOT EXISTS (
                SELECT 1 FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) have
                WHERE have->>'fieldCode' = add.field->>'fieldCode'
              )
            )
          ) ELSE elem END
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'patch-action-alignment',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1
  AND jsonb_typeof(a.param_slots_json->'methods') = 'array'
  AND a.code IN ('action-065340437bcb46788b7dc261fab81c63', 'act-uav-return-charge');

-- 2) 关气体等：补 action_duration（对照 request.time）
UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          CASE WHEN elem->>'executionMeans' = 'UAV' THEN jsonb_set(
            elem,
            '{fields}',
            COALESCE(elem->'fields', '[]'::jsonb) || '[{"fieldCode":"action_duration","required":false,"valueSource":"ACTION_PARAM"}]'::jsonb
          ) ELSE elem END
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'patch-action-alignment',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1
  AND a.code = 'action-ab595a61ca164a2c9db74e1311e747a5'
  AND NOT EXISTS (
    SELECT 1 FROM jsonb_array_elements(a.param_slots_json->'methods') m,
         jsonb_array_elements(COALESCE(m->'fields', '[]'::jsonb)) f
    WHERE f->>'fieldCode' = 'action_duration'
  );

-- 3) 喊话器：对照 slot psdk_index → 动作声明 payload_position_index（同义，试译读 param_slots）
UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          CASE WHEN elem->>'executionMeans' = 'UAV' THEN jsonb_set(
            elem,
            '{fields}',
            COALESCE(elem->'fields', '[]'::jsonb) || '[{"fieldCode":"psdk_index","required":true,"valueSource":"EXECUTOR_MOUNT"}]'::jsonb
          ) ELSE elem END
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'patch-action-alignment',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1
  AND a.code = 'megaphone'
  AND NOT EXISTS (
    SELECT 1 FROM jsonb_array_elements(a.param_slots_json->'methods') m,
         jsonb_array_elements(COALESCE(m->'fields', '[]'::jsonb)) f
    WHERE f->>'fieldCode' IN ('psdk_index', 'payload_position_index')
  );

-- 4) 空 UAV 型号行：从同动作已有完整行复制 fields（避免 declared 为空导致试译缺槽）
CREATE OR REPLACE FUNCTION pg_temp.copy_fields_if_empty(
  action_code text, means text, donor_model bigint
) RETURNS void LANGUAGE plpgsql AS $$
DECLARE
  donor jsonb;
BEGIN
  SELECT elem->'fields' INTO donor
  FROM ent_action_t1 a,
       jsonb_array_elements(a.param_slots_json->'methods') elem
  WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = action_code
    AND elem->>'executionMeans' = means
    AND (elem->>'equipmentModelId')::bigint = donor_model
  LIMIT 1;
  IF donor IS NULL OR jsonb_array_length(donor) = 0 THEN
    RETURN;
  END IF;
  UPDATE ent_action_t1 a
  SET param_slots_json = jsonb_set(
        a.param_slots_json,
        '{methods}',
        (
          SELECT jsonb_agg(
            CASE
              WHEN elem->>'executionMeans' = means
               AND jsonb_array_length(COALESCE(elem->'fields', '[]'::jsonb)) = 0
               AND elem->>'equipmentModelId' IS NOT NULL
              THEN jsonb_set(elem, '{fields}', donor)
              ELSE elem
            END
          )
          FROM jsonb_array_elements(a.param_slots_json->'methods') elem
        )
      ),
      updater = 'patch-action-alignment',
      update_time = CURRENT_TIMESTAMP
  WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = action_code;
END;
$$;

SELECT pg_temp.copy_fields_if_empty('gimbalRotate', 'UAV', 1867);
SELECT pg_temp.copy_fields_if_empty('orientedShoot', 'UAV', 1885);
SELECT pg_temp.copy_fields_if_empty('startRecord', 'UAV', 1867);

-- 5) 云台/悬停等：USER 角度与 enable 缺 defaultValue 时写 0（与 dynamic_action_model_defaults_complete 一致）
UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          jsonb_set(
            meth,
            '{fields}',
            (
              SELECT jsonb_agg(
                CASE
                  WHEN f->>'fieldCode' IN (
                    'gimbalPitchRotateAngle','gimbalYawRotateAngle','gimbalRollRotateAngle',
                    'gimbalRotateTime','aircraftHeading','targetAngle','yaw','pitch','hoverTime'
                  ) AND (f->>'defaultValue' IS NULL OR f->>'defaultValue' = '')
                  THEN f || '{"defaultValue":"0"}'::jsonb
                  WHEN f->>'fieldCode' IN (
                    'gimbalPitchRotateEnable','gimbalYawRotateEnable','gimbalRollRotateEnable','gimbalRotateTimeEnable'
                  ) AND (f->>'defaultValue' IS NULL OR f->>'defaultValue' = '')
                  THEN f || '{"defaultValue":"0"}'::jsonb
                  ELSE f
                END
              )
              FROM jsonb_array_elements(COALESCE(meth->'fields', '[]'::jsonb)) f
            )
          )
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') meth
      )
    ),
    updater = 'patch-action-alignment',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1
  AND a.code IN ('gimbalRotate', 'orientedShoot', 'hover', 'rotateYaw', 'gimbalEvenlyRotate');

COMMIT;
