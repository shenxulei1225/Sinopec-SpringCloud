-- 探照灯、点云录制、喊话不再把摄像机或协议里的负载序号当作要人填的参数。
-- 负责：探照灯动作改选探照灯；点云录制改选激光雷达；喊话改选喊话器。
-- 不负责：改协议报文；给还没有的探照灯、雷达、喊话器编一台设备；改拍照、录像、云台、全景、建文件夹上的摄像机。
-- 负载挂载位置已经不在动作参数里，这里不再写回。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

INSERT INTO dynamic_field (
  code, name, type, description, source, status, creator, updater, tenant_id, governance_status
)
SELECT v.code, v.name, 'STRING', v.description, 'LIBRARY', 1, 'seed', 'seed', 1, 'COMPANY'
FROM (
  VALUES
    (
      'selected_searchlight',
      '探照灯',
      '选择这次使用的探照灯。记下的是这盏灯的负载编号，不是手填挂载位置。'
    ),
    (
      'selected_lidar',
      '激光雷达',
      '选择这次使用的激光雷达。记下的是这台雷达的负载编号，不是手填挂载位置。'
    ),
    (
      'selected_speaker',
      '喊话器',
      '选择这次使用的喊话器。记下的是这只喊话器的负载编号，不是手填负载序号。'
    )
) AS v(code, name, description)
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_field f WHERE f.code = v.code
);

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(replaced.method ORDER BY replaced.ord)
        FROM (
          SELECT
            method.ord,
            jsonb_set(
              method.elem,
              '{fields}',
              (
                SELECT COALESCE(jsonb_agg(
                  CASE
                    WHEN f.elem->>'fieldCode' = 'selected_camera'
                     AND a.code IN (
                       'searchlightOn', 'searchlightStrobe', 'searchlightOff'
                     )
                    THEN jsonb_set(f.elem, '{fieldCode}', to_jsonb('selected_searchlight'::text))
                    WHEN f.elem->>'fieldCode' = 'selected_camera'
                     AND a.code IN (
                       'recordPointCloudStart', 'recordPointCloudPause',
                       'recordPointCloudResume', 'recordPointCloudStop'
                     )
                    THEN jsonb_set(f.elem, '{fieldCode}', to_jsonb('selected_lidar'::text))
                    WHEN f.elem->>'fieldCode' = 'psdk_index'
                     AND a.code = 'megaphone'
                    THEN jsonb_set(f.elem, '{fieldCode}', to_jsonb('selected_speaker'::text))
                    ELSE f.elem
                  END
                  ORDER BY f.ord
                ), '[]'::jsonb)
                FROM jsonb_array_elements(COALESCE(method.elem->'fields', '[]'::jsonb))
                  WITH ORDINALITY AS f(elem, ord)
              )
            ) AS method
          FROM jsonb_array_elements(a.param_slots_json->'methods')
            WITH ORDINALITY AS method(elem, ord)
        ) replaced
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code IN (
    'searchlightOn', 'searchlightStrobe', 'searchlightOff',
    'recordPointCloudStart', 'recordPointCloudPause',
    'recordPointCloudResume', 'recordPointCloudStop',
    'megaphone'
  )
  AND jsonb_typeof(a.param_slots_json->'methods') = 'array';

COMMIT;

SELECT a.code, a.name,
       string_agg(DISTINCT f.elem->>'fieldCode', ', ' ORDER BY f.elem->>'fieldCode') AS fields
FROM ent_action_t1 a
CROSS JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') method
CROSS JOIN LATERAL jsonb_array_elements(COALESCE(method->'fields', '[]'::jsonb)) f(elem)
WHERE a.deleted = false
  AND a.code IN (
    'searchlightOn', 'searchlightStrobe', 'searchlightOff',
    'recordPointCloudStart', 'recordPointCloudPause',
    'recordPointCloudResume', 'recordPointCloudStop',
    'megaphone', 'customDirName', 'gimbalRotate', 'panoShot'
  )
GROUP BY a.code, a.name
ORDER BY a.name;
