-- 云台相机和机身相机的官网光学规格。
-- 负责：把大疆官网规格页上的焦距、分辨率、快门、俯仰范围写进对应型号，并补进还空着的设备。
-- 出处：
--   禅思 H30 系列 https://enterprise.dji.com/zenmuse-h30-series/specs
--   禅思 H20 系列 https://enterprise.dji.com/zenmuse-h20-series/specs
--   Matrice 400 机身相机 https://enterprise.dji.com/matrice-400/specs
-- 不负责：官网这一页没有写的项。不改已经填过的设备。

SET search_path TO dynamicbusiness;

BEGIN;

INSERT INTO dynamic_field (
  code, name, type, description, source, status, index_strategy, options, tenant_id, creator
) VALUES
  ('shutter_speed', '快门', 'TEXT', '官网规格表上的快门。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('fpv_resolution', '分辨率', 'TEXT', '官网规格表上机身相机的分辨率。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('fpv_night_vision', '夜视', 'TEXT', '官网规格表上机身相机的夜视能力。', 'USER', 1, 'BTREE', NULL, 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

CREATE TEMP TABLE spec_row (
  model_code text NOT NULL,
  field_code text NOT NULL,
  default_value text NOT NULL,
  sort_no int NOT NULL,
  PRIMARY KEY (model_code, field_code)
) ON COMMIT DROP;

INSERT INTO spec_row (model_code, field_code, default_value, sort_no) VALUES
  ('dji-pl-h30', 'wide_sensor', '1/1.3 英寸 CMOS，有效像素 4800 万', 90),
  ('dji-pl-h30', 'wide_focal_length', '{"min":24,"max":24,"text":"24 mm"}', 91),
  ('dji-pl-h30', 'wide_aperture', '{"min":1.7,"max":1.7,"text":"f/1.7"}', 92),
  ('dji-pl-h30', 'wide_photo_size', '8064×6048', 93),
  ('dji-pl-h30', 'zoom_focal_length', '{"min":33.4,"max":809.3,"text":"33.4-809.3 mm"}', 94),
  ('dji-pl-h30', 'zoom_aperture', '{"min":1.6,"max":5.2,"text":"f/1.6-f/5.2"}', 95),
  ('dji-pl-h30', 'zoom_photo_size', '7328×5496', 96),
  ('dji-pl-h30', 'shutter_speed', '{"min":0.000125,"max":2,"text":"电子快门 1/8000-2 s"}', 97),
  ('dji-pl-h30', 'iso_normal', '{"min":100,"max":25600,"text":"100-25600"}', 98),
  ('dji-pl-h30', 'gimbal_pitch_min', '-120', 99),
  ('dji-pl-h30', 'gimbal_pitch_max', '60', 100),

  ('dji-pl-h30t', 'wide_sensor', '1/1.3 英寸 CMOS，有效像素 4800 万', 90),
  ('dji-pl-h30t', 'wide_focal_length', '{"min":24,"max":24,"text":"24 mm"}', 91),
  ('dji-pl-h30t', 'wide_aperture', '{"min":1.7,"max":1.7,"text":"f/1.7"}', 92),
  ('dji-pl-h30t', 'wide_photo_size', '8064×6048', 93),
  ('dji-pl-h30t', 'zoom_focal_length', '{"min":33.4,"max":809.3,"text":"33.4-809.3 mm"}', 94),
  ('dji-pl-h30t', 'zoom_aperture', '{"min":1.6,"max":5.2,"text":"f/1.6-f/5.2"}', 95),
  ('dji-pl-h30t', 'zoom_photo_size', '7328×5496', 96),
  ('dji-pl-h30t', 'thermal_focal_length', '52 mm', 101),
  ('dji-pl-h30t', 'thermal_resolution', '1280×1024', 102),
  ('dji-pl-h30t', 'shutter_speed', '{"min":0.000125,"max":2,"text":"电子快门 1/8000-2 s"}', 97),
  ('dji-pl-h30t', 'iso_normal', '{"min":100,"max":25600,"text":"100-25600"}', 98),
  ('dji-pl-h30t', 'gimbal_pitch_min', '-120', 99),
  ('dji-pl-h30t', 'gimbal_pitch_max', '60', 100),

  ('dji-pl-h20', 'wide_sensor', '1/2.3 英寸 CMOS，1200 万像素', 90),
  ('dji-pl-h20', 'wide_focal_length', '{"min":24,"max":24,"text":"24 mm"}', 91),
  ('dji-pl-h20', 'wide_aperture', '{"min":2.8,"max":2.8,"text":"f/2.8"}', 92),
  ('dji-pl-h20', 'wide_photo_size', '4056×3040', 93),
  ('dji-pl-h20', 'zoom_focal_length', '{"min":31.7,"max":556.2,"text":"31.7-556.2 mm"}', 94),
  ('dji-pl-h20', 'zoom_aperture', '{"min":2.8,"max":11,"text":"f/2.8-f/11"}', 95),
  ('dji-pl-h20', 'zoom_photo_size', '5184×3888', 96),
  ('dji-pl-h20', 'shutter_speed', '{"min":0.000125,"max":1,"text":"1-1/8000 s"}', 97),
  ('dji-pl-h20', 'iso_normal', '{"min":100,"max":25600,"text":"100-25600"}', 98),
  ('dji-pl-h20', 'gimbal_pitch_min', '-120', 99),
  ('dji-pl-h20', 'gimbal_pitch_max', '30', 100),

  ('dji-pl-h20t', 'wide_sensor', '1/2.3 英寸 CMOS，1200 万像素', 90),
  ('dji-pl-h20t', 'wide_focal_length', '{"min":24,"max":24,"text":"24 mm"}', 91),
  ('dji-pl-h20t', 'wide_aperture', '{"min":2.8,"max":2.8,"text":"f/2.8"}', 92),
  ('dji-pl-h20t', 'wide_photo_size', '4056×3040', 93),
  ('dji-pl-h20t', 'zoom_focal_length', '{"min":31.7,"max":556.2,"text":"31.7-556.2 mm"}', 94),
  ('dji-pl-h20t', 'zoom_aperture', '{"min":2.8,"max":11,"text":"f/2.8-f/11"}', 95),
  ('dji-pl-h20t', 'zoom_photo_size', '5184×3888', 96),
  ('dji-pl-h20t', 'thermal_focal_length', '58 mm', 101),
  ('dji-pl-h20t', 'thermal_resolution', '640×512', 102),
  ('dji-pl-h20t', 'shutter_speed', '{"min":0.000125,"max":1,"text":"1-1/8000 s"}', 97),
  ('dji-pl-h20t', 'iso_normal', '{"min":100,"max":25600,"text":"100-25600"}', 98),
  ('dji-pl-h20t', 'gimbal_pitch_min', '-120', 99),
  ('dji-pl-h20t', 'gimbal_pitch_max', '30', 100),

  ('dji-m400', 'fpv_resolution', '1080p', 110),
  ('dji-m400', 'fpv_night_vision', '星光级', 111);

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, tenant_id, creator
)
SELECT
  m.id, m.code, f.id, f.code,
  false,
  f.type = 'TEXT',
  f.type = 'NUMBER' OR f.type = 'ENUM',
  f.type = 'NUMBER',
  spec.sort_no,
  spec.default_value,
  1,
  'seed'
FROM spec_row spec
JOIN dynamic_model m ON m.code = spec.model_code AND m.deleted = false AND m.tenant_id = 1
JOIN dynamic_field f ON f.code = spec.field_code AND f.tenant_id = 1 AND f.deleted = false
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  default_value = EXCLUDED.default_value,
  sort = EXCLUDED.sort,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE ent_equipment_t1 e
SET custom_fields = COALESCE(e.custom_fields, '{}'::jsonb) || patch.values,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT e2.id,
    jsonb_object_agg(
      a.field_code,
      CASE
        WHEN left(btrim(a.default_value), 1) = '{' THEN a.default_value::jsonb
        WHEN f.type = 'NUMBER' THEN to_jsonb(a.default_value::numeric)
        ELSE to_jsonb(a.default_value)
      END
    ) AS values
  FROM ent_equipment_t1 e2
  JOIN dynamic_model_field_assignment a
    ON a.model_id = e2.model_id AND a.deleted = false AND a.tenant_id = 1
  JOIN dynamic_field f ON f.id = a.field_id AND f.deleted = false
  JOIN spec_row spec ON spec.model_code = a.model_code AND spec.field_code = a.field_code
  WHERE e2.deleted = false
    AND e2.tenant_id = 1
    AND NULLIF(btrim(e2.custom_fields ->> a.field_code), '') IS NULL
  GROUP BY e2.id
) patch
WHERE e.id = patch.id;

COMMIT;
