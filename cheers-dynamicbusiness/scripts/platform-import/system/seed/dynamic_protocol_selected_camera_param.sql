-- 选择摄像机是检查项里填写的动作参数（ACTION_PARAM，不是登录用户）。
-- 负责：变焦、拍照、开始录像、停止录像、点对焦的无人机手段加上这一项。
-- 不负责：把编号写成默认值；改航线动作的负载挂载位置；下发时回读设备。
SET search_path TO dynamicbusiness;

BEGIN;

INSERT INTO dynamic_field (code, name, type, description, source, status, tenant_id, creator)
VALUES (
  'selected_camera',
  '摄像机',
  'STRING',
  '选择这次使用的摄像机。记下的是类型-子类型-负载位置，例如选中 FPV 得到 39-0-7。不是手填编号。',
  'LIBRARY',
  1,
  1,
  'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          CASE
            WHEN upper(elem->>'executionMeans') = 'UAV'
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
  AND a.code IN ('zoom', 'takePhoto', 'startRecord', 'stopRecord', 'focus')
  AND jsonb_typeof(a.param_slots_json->'methods') = 'array';

COMMIT;
