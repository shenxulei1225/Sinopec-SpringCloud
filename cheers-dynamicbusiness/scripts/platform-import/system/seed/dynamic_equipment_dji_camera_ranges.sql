-- 把已经写成句子的焦距、光圈、快门、感光度改成最小和最大。
-- 详情仍用 text 里的原文。实时画面和航线读 min、max。
-- 不改俯仰。俯仰本来就是最小、最大两个数。

SET search_path TO dynamicbusiness;

BEGIN;

CREATE TEMP TABLE range_row (
  model_code text NOT NULL,
  field_code text NOT NULL,
  default_value text NOT NULL,
  PRIMARY KEY (model_code, field_code)
) ON COMMIT DROP;

INSERT INTO range_row (model_code, field_code, default_value) VALUES
  ('dji-pl-h30', 'wide_focal_length', '{"min":24,"max":24,"text":"24 mm"}'),
  ('dji-pl-h30', 'zoom_focal_length', '{"min":33.4,"max":809.3,"text":"33.4-809.3 mm"}'),
  ('dji-pl-h30', 'wide_aperture', '{"min":1.7,"max":1.7,"text":"f/1.7"}'),
  ('dji-pl-h30', 'zoom_aperture', '{"min":1.6,"max":5.2,"text":"f/1.6-f/5.2"}'),
  ('dji-pl-h30', 'shutter_speed', '{"min":0.000125,"max":2,"text":"电子快门 1/8000-2 s"}'),
  ('dji-pl-h30', 'iso_normal', '{"min":100,"max":25600,"text":"100-25600"}'),
  ('dji-pl-h30t', 'wide_focal_length', '{"min":24,"max":24,"text":"24 mm"}'),
  ('dji-pl-h30t', 'zoom_focal_length', '{"min":33.4,"max":809.3,"text":"33.4-809.3 mm"}'),
  ('dji-pl-h30t', 'wide_aperture', '{"min":1.7,"max":1.7,"text":"f/1.7"}'),
  ('dji-pl-h30t', 'zoom_aperture', '{"min":1.6,"max":5.2,"text":"f/1.6-f/5.2"}'),
  ('dji-pl-h30t', 'shutter_speed', '{"min":0.000125,"max":2,"text":"电子快门 1/8000-2 s"}'),
  ('dji-pl-h30t', 'iso_normal', '{"min":100,"max":25600,"text":"100-25600"}'),
  ('dji-pl-h20', 'wide_focal_length', '{"min":24,"max":24,"text":"24 mm"}'),
  ('dji-pl-h20', 'zoom_focal_length', '{"min":31.7,"max":556.2,"text":"31.7-556.2 mm"}'),
  ('dji-pl-h20', 'wide_aperture', '{"min":2.8,"max":2.8,"text":"f/2.8"}'),
  ('dji-pl-h20', 'zoom_aperture', '{"min":2.8,"max":11,"text":"f/2.8-f/11"}'),
  ('dji-pl-h20', 'shutter_speed', '{"min":0.000125,"max":1,"text":"1-1/8000 s"}'),
  ('dji-pl-h20', 'iso_normal', '{"min":100,"max":25600,"text":"100-25600"}'),
  ('dji-pl-h20t', 'wide_focal_length', '{"min":24,"max":24,"text":"24 mm"}'),
  ('dji-pl-h20t', 'zoom_focal_length', '{"min":31.7,"max":556.2,"text":"31.7-556.2 mm"}'),
  ('dji-pl-h20t', 'wide_aperture', '{"min":2.8,"max":2.8,"text":"f/2.8"}'),
  ('dji-pl-h20t', 'zoom_aperture', '{"min":2.8,"max":11,"text":"f/2.8-f/11"}'),
  ('dji-pl-h20t', 'shutter_speed', '{"min":0.000125,"max":1,"text":"1-1/8000 s"}'),
  ('dji-pl-h20t', 'iso_normal', '{"min":100,"max":25600,"text":"100-25600"}');

UPDATE dynamic_model_field_assignment a
SET default_value = range_row.default_value,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM range_row
WHERE a.model_code = range_row.model_code
  AND a.field_code = range_row.field_code
  AND a.tenant_id = 1
  AND a.deleted = false;

UPDATE ent_equipment_t1 e
SET custom_fields = COALESCE(e.custom_fields, '{}'::jsonb) || patch.values,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT e2.id,
    jsonb_object_agg(range_row.field_code, range_row.default_value::jsonb) AS values
  FROM ent_equipment_t1 e2
  JOIN dynamic_model m ON m.id = e2.model_id AND m.deleted = false
  JOIN range_row ON range_row.model_code = m.code
  WHERE e2.deleted = false
    AND e2.tenant_id = 1
  GROUP BY e2.id
) patch
WHERE e.id = patch.id;

COMMIT;
