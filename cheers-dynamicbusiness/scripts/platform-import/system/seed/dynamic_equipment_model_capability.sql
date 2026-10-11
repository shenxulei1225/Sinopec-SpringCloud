-- 设备型号上的拍摄能力。
-- 负责：在摄像机型号上写下支持的镜头和默认镜头；在飞机型号上写下允许的云台口和俯仰范围。
-- 值写在型号字段的默认值上。同一款设备共用一份，不在每一台设备上再填一遍。
-- 不负责：检查参数读取这些默认值；不改某一台摄像机已经挂上的云台口。
-- 相机类型号沿用型号上已有的主类型，不另做一行。
-- 拍照方式、对焦方式、曝光方式先挂到摄像机型号上。官方没有按型号写清支持哪些，默认值留空。
-- 俯仰范围来自航线说明：M300、M350、M30、M30T 是 -120 到 45；M3E、M3T 是 -90 到 35；M3D、M3TD、M4D、M4TD、M4E、M4T 是 -90 到 30。
-- M3TA、M400、东古没有这段范围，俯仰两行留空。
-- 云台口：0 主云台；1 右云台和 2 上方云台只写在 Matrice 300 RTK；7 是 FPV，写在已经有 FPV 挂接的机型上。
-- 镜头：有红外直播地址的型号带上红外；云台相机带上广角和变焦；禅思 Z30 只有变焦；禅思 XT2 是可见光和红外；禅思 XTS 只有红外；禅思 H20N 另有窄带。FPV、辅助影像、机场相机的镜头留空。

SET search_path TO dynamicbusiness;

BEGIN;

INSERT INTO dynamic_field (
  code, name, type, description, source, status, index_strategy, options, tenant_id, creator
) VALUES
  (
    'supported_lenses',
    '支持的镜头',
    'MULTI_SELECT',
    '这款摄像机能存储哪些镜头的照片。检查参数里的镜头选项只从这里勾过的项里出。没有勾的镜头不会出现。',
    'USER',
    1,
    'NONE',
    '[
      {"label":"广角","value":"wide"},
      {"label":"变焦","value":"zoom"},
      {"label":"红外","value":"ir"},
      {"label":"窄带","value":"narrow_band"},
      {"label":"可见光","value":"visable"}
    ]'::jsonb,
    1,
    'seed'
  ),
  (
    'default_lens',
    '默认镜头',
    'ENUM',
    '这款摄像机没人改存储镜头时用哪一个。必须是支持的镜头里的一项。',
    'USER',
    1,
    'NONE',
    '[
      {"label":"广角","value":"wide"},
      {"label":"变焦","value":"zoom"},
      {"label":"红外","value":"ir"},
      {"label":"窄带","value":"narrow_band"},
      {"label":"可见光","value":"visable"}
    ]'::jsonb,
    1,
    'seed'
  ),
  (
    'supported_photo_modes',
    '支持的拍照方式',
    'MULTI_SELECT',
    '这款摄像机支持哪些拍照方式。官方没有按型号写清时，这一行留空，不写成全部支持。',
    'USER',
    1,
    'NONE',
    '[
      {"label":"普通拍照","value":"normalPhoto"},
      {"label":"智能低光","value":"lowLightSmartShooting"}
    ]'::jsonb,
    1,
    'seed'
  ),
  (
    'supported_focus_modes',
    '支持的对焦方式',
    'MULTI_SELECT',
    '这款摄像机支持哪些对焦方式。官方没有按型号写清时，这一行留空。',
    'USER',
    1,
    'NONE',
    '[
      {"label":"点对焦","value":"point"},
      {"label":"区域对焦","value":"area"},
      {"label":"无穷远对焦","value":"infinite"}
    ]'::jsonb,
    1,
    'seed'
  ),
  (
    'supported_exposure_modes',
    '支持的曝光方式',
    'MULTI_SELECT',
    '这款摄像机支持哪些曝光方式。官方没有按型号写清时，这一行留空。',
    'USER',
    1,
    'NONE',
    '[
      {"label":"自动","value":"1"},
      {"label":"快门优先","value":"2"},
      {"label":"光圈优先","value":"3"},
      {"label":"手动","value":"4"}
    ]'::jsonb,
    1,
    'seed'
  ),
  (
    'allowed_gimbal_ports',
    '允许的云台口',
    'MULTI_SELECT',
    '这款飞机允许摄像机装在哪些云台口。某一台摄像机选云台口时，只列出这里勾过的口。',
    'USER',
    1,
    'NONE',
    '[
      {"label":"0 主云台","value":"0"},
      {"label":"1 右云台","value":"1"},
      {"label":"2 上方云台","value":"2"},
      {"label":"7 FPV","value":"7"}
    ]'::jsonb,
    1,
    'seed'
  ),
  (
    'gimbal_pitch_min',
    '俯仰最小角度',
    'NUMBER',
    '这款飞机云台俯仰能转到的最小角度，单位度。取景滑条的下端用这个数。官方没有写出范围时留空。',
    'USER',
    1,
    'NONE',
    NULL,
    1,
    'seed'
  ),
  (
    'gimbal_pitch_max',
    '俯仰最大角度',
    'NUMBER',
    '这款飞机云台俯仰能转到的最大角度，单位度。取景滑条的上端用这个数。官方没有写出范围时留空。',
    'USER',
    1,
    'NONE',
    NULL,
    1,
    'seed'
  )
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  source = EXCLUDED.source,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

CREATE TEMP TABLE camera_lens (
  model_code text PRIMARY KEY,
  lenses text,
  default_lens text
) ON COMMIT DROP;

INSERT INTO camera_lens (model_code, lenses, default_lens) VALUES
  ('dji-pl-z30', '["zoom"]', 'zoom'),
  ('dji-pl-xt2', '["visable","ir"]', 'visable'),
  ('dji-pl-xts', '["ir"]', 'ir'),
  ('dji-pl-h20', '["wide","zoom"]', 'wide'),
  ('dji-pl-h20t', '["wide","zoom","ir"]', 'wide'),
  ('dji-pl-h20n', '["wide","zoom","ir","narrow_band"]', 'wide'),
  ('dji-pl-h30', '["wide","zoom"]', 'wide'),
  ('dji-pl-h30t', '["wide","zoom","ir"]', 'wide'),
  ('dji-pl-m30-cam', '["wide","zoom"]', 'wide'),
  ('dji-pl-m30t-cam', '["wide","zoom","ir"]', 'wide'),
  ('dji-pl-m3e-cam', '["wide","zoom"]', 'wide'),
  ('dji-pl-m3t-cam', '["wide","zoom","ir"]', 'wide'),
  ('dji-pl-m3ta-cam', '["wide","zoom","ir"]', 'wide'),
  ('dji-pl-m3d-cam', '["wide","zoom"]', 'wide'),
  ('dji-pl-m3td-cam', '["wide","zoom","ir"]', 'wide'),
  ('dji-pl-m4e-cam', '["wide","zoom"]', 'wide'),
  ('dji-pl-m4t-cam', '["wide","zoom","ir"]', 'wide'),
  ('dji-pl-m4d-cam', '["wide","zoom"]', 'wide'),
  ('dji-pl-m4td-cam', '["wide","zoom","ir"]', 'wide');

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, tenant_id, creator
)
SELECT
  m.id, m.code, f.id, f.code,
  false, false, true, false, spec.sort_no,
  spec.default_value, 1, 'seed'
FROM dynamic_model m
JOIN (
  SELECT model_code, 'supported_lenses' AS field_code, 50 AS sort_no, lenses AS default_value FROM camera_lens
  UNION ALL
  SELECT model_code, 'default_lens', 51, default_lens FROM camera_lens
) AS spec ON spec.model_code = m.code
JOIN dynamic_field f ON f.code = spec.field_code AND f.tenant_id = 1 AND f.deleted = false
WHERE m.deleted = false AND m.tenant_id = 1
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  default_value = EXCLUDED.default_value,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, tenant_id, creator
)
SELECT
  m.id, m.code, f.id, f.code,
  false, false, true, false, spec.sort_no,
  NULL, 1, 'seed'
FROM dynamic_model m
JOIN (
  VALUES
    ('supported_lenses', 50),
    ('default_lens', 51),
    ('supported_photo_modes', 52),
    ('supported_focus_modes', 53),
    ('supported_exposure_modes', 54)
) AS spec(field_code, sort_no) ON true
JOIN dynamic_field f ON f.code = spec.field_code AND f.tenant_id = 1 AND f.deleted = false
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.code LIKE 'dji-pl-%'
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_model_field_assignment existing
    WHERE existing.model_code = m.code
      AND existing.field_code = f.code
      AND existing.tenant_id = 1
      AND existing.deleted = false
  );

CREATE TEMP TABLE aircraft_capability (
  model_code text PRIMARY KEY,
  ports text,
  pitch_min text,
  pitch_max text
) ON COMMIT DROP;

INSERT INTO aircraft_capability (model_code, ports, pitch_min, pitch_max) VALUES
  ('dji-m300-rtk', '["0","1","2","7"]', '-120', '45'),
  ('dji-m350-rtk', '["0","7"]', '-120', '45'),
  ('dji-m400', '["0","7"]', NULL, NULL),
  ('dji-m30', '["0","7"]', '-120', '45'),
  ('dji-m30t', '["0","7"]', '-120', '45'),
  ('dji-m3e', '["0"]', '-90', '35'),
  ('dji-m3t', '["0"]', '-90', '35'),
  ('dji-m3ta', '["0"]', NULL, NULL),
  ('dji-m3d', '["0"]', '-90', '30'),
  ('dji-m3td', '["0"]', '-90', '30'),
  ('dji-m4d', '["0"]', '-90', '30'),
  ('dji-m4td', '["0"]', '-90', '30'),
  ('dji-m4e', '["0"]', '-90', '30'),
  ('dji-m4t', '["0"]', '-90', '30'),
  ('donggu-uav-small', NULL, NULL, NULL),
  ('donggu-uav-large', NULL, NULL, NULL);

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, tenant_id, creator
)
SELECT
  m.id, m.code, f.id, f.code,
  false, false, true, false,
  spec.sort_no,
  spec.default_value,
  1,
  'seed'
FROM dynamic_model m
JOIN aircraft_capability cap ON cap.model_code = m.code
JOIN (
  SELECT model_code, 'allowed_gimbal_ports' AS field_code, 55 AS sort_no, ports AS default_value
  FROM aircraft_capability
  UNION ALL
  SELECT model_code, 'gimbal_pitch_min', 56, pitch_min FROM aircraft_capability
  UNION ALL
  SELECT model_code, 'gimbal_pitch_max', 57, pitch_max FROM aircraft_capability
) AS spec ON spec.model_code = m.code
JOIN dynamic_field f ON f.code = spec.field_code AND f.tenant_id = 1 AND f.deleted = false
WHERE m.deleted = false AND m.tenant_id = 1
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  default_value = EXCLUDED.default_value,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

COMMIT;
