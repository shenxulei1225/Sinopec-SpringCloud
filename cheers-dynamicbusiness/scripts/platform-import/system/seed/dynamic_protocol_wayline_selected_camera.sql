-- 真正操作摄像机的航线动作，加上选择摄像机。
-- 负责：检查项里点摄像机，记下类型-子类型-负载位置。生成步骤时另取最后一段写入挂载序号。
-- 不负责：探照灯、点云录制、定点拍照、喊话。这些动作不因为协议里有负载挂载位置就选摄像机。
-- 不负责：悬停和飞行器偏航；把序号写成 0。
-- 已有摄像机这一格的不再追加，避免重复执行时叠出第二格。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          CASE
            WHEN (
                upper(elem->>'executionMeans') = 'UAV'
                OR a.code = 'gimbalRotate'
              )
             AND NOT EXISTS (
               SELECT 1
               FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) f
               WHERE f->>'fieldCode' = 'selected_camera'
             )
            THEN jsonb_set(
              elem,
              '{fields}',
              jsonb_build_array(
                jsonb_build_object(
                  'fieldCode', 'selected_camera',
                  'required', true,
                  'valueSource', 'ACTION_PARAM',
                  'defaultValue', NULL
                )
              ) || COALESCE(elem->'fields', '[]'::jsonb)
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
    'customDirName', 'gimbalEvenlyRotate', 'gimbalRotate', 'panoShot'
  )
  AND jsonb_typeof(a.param_slots_json->'methods') = 'array';

COMMIT;
