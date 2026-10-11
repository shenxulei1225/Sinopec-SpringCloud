-- 任务执行中、实时画面上的负载操作，名称改为「手动」加原名。
-- 巡检开头的开始录像、停止录像不改。
-- 画面专用的开始录像、停止录像恢复显示，名称是手动开始录像、手动停止录像。
SET search_path TO dynamicbusiness;
BEGIN;

UPDATE ent_action_t1
SET name = '手动' || name,
    deleted = false
WHERE code IN (
  'zoom', 'focus', 'frameZoom', 'screenDrag', 'cameraAim', 'focusValue', 'focusMode',
  'cameraMode', 'photoTake', 'photoStop', 'recordStart', 'recordStop', 'gimbalReset',
  'screenSplit', 'exposureMode', 'exposureSet', 'aperture', 'shutter', 'iso',
  'photoStorage', 'videoStorage', 'irMeteringMode', 'irMeteringPoint', 'irMeteringArea'
)
  AND name NOT LIKE '手动%';

UPDATE ent_protocol_mapping_t1 m
SET name = '手动' || m.name,
    deleted = false
FROM ent_action_t1 a
WHERE a.id = ((m.custom_fields->'action_ref'->>'id')::bigint)
  AND a.code IN (
    'zoom', 'focus', 'frameZoom', 'screenDrag', 'cameraAim', 'focusValue', 'focusMode',
    'cameraMode', 'photoTake', 'photoStop', 'recordStart', 'recordStop', 'gimbalReset',
    'screenSplit', 'exposureMode', 'exposureSet', 'aperture', 'shutter', 'iso',
    'photoStorage', 'videoStorage', 'irMeteringMode', 'irMeteringPoint', 'irMeteringArea'
  )
  AND m.name NOT LIKE '手动%';

UPDATE ent_action_t1 a
SET name = v.name
FROM (VALUES
  ('zoom', '手动设置变焦'),
  ('focusMode', '手动设置对焦模式'),
  ('focusValue', '手动设置对焦值'),
  ('screenSplit', '手动设置分屏'),
  ('iso', '手动设置感光度'),
  ('aperture', '手动设置光圈'),
  ('shutter', '手动设置快门'),
  ('exposureSet', '手动设置曝光补偿'),
  ('exposureMode', '手动设置曝光模式'),
  ('photoStorage', '手动设置照片存储'),
  ('videoStorage', '手动设置视频存储'),
  ('irMeteringMode', '手动设置红外测温模式'),
  ('irMeteringPoint', '手动设置红外测温点'),
  ('irMeteringArea', '手动设置红外测温区域')
) AS v(code, name)
WHERE a.code = v.code
  AND a.deleted = false;

UPDATE ent_protocol_mapping_t1 m
SET name = v.name || substring(m.name from position(' · ' in m.name))
FROM ent_action_t1 a
JOIN (VALUES
  ('zoom', '手动设置变焦'),
  ('focusMode', '手动设置对焦模式'),
  ('focusValue', '手动设置对焦值'),
  ('screenSplit', '手动设置分屏'),
  ('iso', '手动设置感光度'),
  ('aperture', '手动设置光圈'),
  ('shutter', '手动设置快门'),
  ('exposureSet', '手动设置曝光补偿'),
  ('exposureMode', '手动设置曝光模式'),
  ('photoStorage', '手动设置照片存储'),
  ('videoStorage', '手动设置视频存储'),
  ('irMeteringMode', '手动设置红外测温模式'),
  ('irMeteringPoint', '手动设置红外测温点'),
  ('irMeteringArea', '手动设置红外测温区域')
) AS v(code, name) ON a.code = v.code
WHERE a.id = ((m.custom_fields->'action_ref'->>'id')::bigint)
  AND position(' · ' in m.name) > 0;

COMMIT;
