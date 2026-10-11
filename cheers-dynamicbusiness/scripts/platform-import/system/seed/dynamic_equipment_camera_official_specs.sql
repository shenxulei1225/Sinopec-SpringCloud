SET search_path TO dynamicbusiness;

BEGIN;

CREATE TEMP TABLE cam_spec (
    model_code text NOT NULL,
    field_code text NOT NULL,
    default_value text NOT NULL
) ON COMMIT DROP;

INSERT INTO cam_spec (model_code, field_code, default_value) VALUES
    ('dji-pl-m30-cam', 'gimbal_pitch_min', '-120'),
    ('dji-pl-m30-cam', 'gimbal_pitch_max', '45'),
    ('dji-pl-m30-cam', 'shutter_speed', '{"min":0.000125,"max":8,"text":"1/8000-8 s"}'),
    ('dji-pl-m30t-cam', 'gimbal_pitch_min', '-120'),
    ('dji-pl-m30t-cam', 'gimbal_pitch_max', '45'),
    ('dji-pl-m30t-cam', 'shutter_speed', '{"min":0.000125,"max":8,"text":"1/8000-8 s"}'),
    ('dji-pl-m3e-cam', 'gimbal_pitch_min', '-90'),
    ('dji-pl-m3e-cam', 'gimbal_pitch_max', '35'),
    ('dji-pl-m3e-cam', 'shutter_speed', '{"min":0.000125,"max":8,"text":"1/8000-8 s"}'),
    ('dji-pl-m3d-cam', 'gimbal_pitch_min', '-90'),
    ('dji-pl-m3d-cam', 'gimbal_pitch_max', '35'),
    ('dji-pl-m3d-cam', 'shutter_speed', '{"min":0.000125,"max":8,"text":"1/8000-8 s"}'),
    ('dji-pl-m3d-cam', 'tele_photo_size', '4000×3000'),
    ('dji-pl-m3t-cam', 'gimbal_pitch_min', '-90'),
    ('dji-pl-m3t-cam', 'gimbal_pitch_max', '35'),
    ('dji-pl-m3t-cam', 'shutter_speed', '{"min":0.000125,"max":8,"text":"1/8000-8 s"}'),
    ('dji-pl-m3ta-cam', 'gimbal_pitch_min', '-90'),
    ('dji-pl-m3ta-cam', 'gimbal_pitch_max', '35'),
    ('dji-pl-m3ta-cam', 'shutter_speed', '{"min":0.000125,"max":8,"text":"1/8000-8 s"}'),
    ('dji-pl-m3td-cam', 'gimbal_pitch_min', '-90'),
    ('dji-pl-m3td-cam', 'gimbal_pitch_max', '35'),
    ('dji-pl-m3td-cam', 'shutter_speed', '{"min":0.000125,"max":8,"text":"1/8000-8 s"}'),
    ('dji-pl-m3td-cam', 'tele_photo_size', '4000×3000'),
    ('dji-pl-m4e-cam', 'gimbal_pitch_min', '-90'),
    ('dji-pl-m4e-cam', 'gimbal_pitch_max', '70'),
    ('dji-pl-m4e-cam', 'shutter_speed', '{"min":0.000125,"max":2,"text":"1/8000-2 s"}'),
    ('dji-pl-m4d-cam', 'gimbal_pitch_min', '-90'),
    ('dji-pl-m4d-cam', 'gimbal_pitch_max', '70'),
    ('dji-pl-m4d-cam', 'shutter_speed', '{"min":0.000125,"max":2,"text":"1/8000-2 s"}'),
    ('dji-pl-m4t-cam', 'gimbal_pitch_min', '-90'),
    ('dji-pl-m4t-cam', 'gimbal_pitch_max', '70'),
    ('dji-pl-m4t-cam', 'shutter_speed', '{"min":0.000125,"max":2,"text":"1/8000-2 s"}'),
    ('dji-pl-m4td-cam', 'gimbal_pitch_min', '-90'),
    ('dji-pl-m4td-cam', 'gimbal_pitch_max', '70'),
    ('dji-pl-m4td-cam', 'shutter_speed', '{"min":0.000125,"max":2,"text":"1/8000-2 s"}'),
    ('dji-pl-h20n', 'wide_sensor', '1/2.7 英寸 CMOS，有效像素 200 万'),
    ('dji-pl-h20n', 'wide_focal_length', '{"min":29,"max":29,"text":"29 mm"}'),
    ('dji-pl-h20n', 'wide_aperture', '{"min":2.8,"max":2.8,"text":"f/2.8"}'),
    ('dji-pl-h20n', 'zoom_focal_length', '{"min":32.7,"max":574.5}'),
    ('dji-pl-h20n', 'zoom_aperture', '{"min":1.6,"max":11,"text":"f/1.6-f/11"}'),
    ('dji-pl-h20n', 'iso_normal', '{"min":100,"max":102400}'),
    ('dji-pl-h20n', 'shutter_speed', '{"min":0.000125,"max":8,"text":"1/8000-8 s"}'),
    ('dji-pl-h20n', 'thermal_resolution', '640×512'),
    ('dji-pl-h20n', 'thermal_focal_length', '广角等效 53 mm，长焦等效 196 mm'),
    ('dji-pl-h20n', 'gimbal_pitch_min', '-120'),
    ('dji-pl-h20n', 'gimbal_pitch_max', '30'),
    ('dji-pl-z30', 'zoom_focal_length', '{"min":4.3,"max":129}'),
    ('dji-pl-z30', 'zoom_aperture', '{"min":1.6,"max":4.7,"text":"f/1.6-f/4.7"}'),
    ('dji-pl-z30', 'wide_sensor', '1/2.8 英寸 CMOS，有效像素 213 万'),
    ('dji-pl-xts', 'thermal_resolution', '640×512'),
    ('dji-pl-xts', 'thermal_focal_length', '{"min":19,"max":19,"text":"19 mm"}'),
    ('dji-pl-xt2', 'thermal_resolution', '640×512 或 336×256');

INSERT INTO dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable,
    default_value, sort, creator, updater, deleted, tenant_id, model_code, field_code
)
SELECT m.id, f.id, false, false, false, false,
       spec.default_value, 100, 'seed', 'seed', false, 1, m.code, f.code
FROM cam_spec spec
JOIN dynamic_model m ON m.code = spec.model_code AND m.deleted = false
JOIN dynamic_field f ON f.code = spec.field_code AND f.tenant_id = 1 AND f.deleted = false
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamic_model_field_assignment existing
    WHERE existing.model_id = m.id
      AND existing.field_id = f.id
      AND existing.deleted = false
);

UPDATE dynamic_model_field_assignment AS assignment
SET default_value = spec.default_value,
    update_time = CURRENT_TIMESTAMP
FROM cam_spec spec
JOIN dynamic_model m ON m.code = spec.model_code AND m.deleted = false
WHERE assignment.model_id = m.id
  AND assignment.field_code = spec.field_code
  AND assignment.deleted = false
  AND (assignment.default_value IS NULL OR btrim(assignment.default_value) = '');

UPDATE dynamic_model_field_assignment
SET default_value = jsonb_build_object(
        'min', (regexp_match(btrim(default_value), '^(\d+(?:\.\d+)?)\s*mm$'))[1]::numeric,
        'max', (regexp_match(btrim(default_value), '^(\d+(?:\.\d+)?)\s*mm$'))[1]::numeric,
        'text', btrim(default_value)
    )::text,
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND model_code LIKE 'dji-pl-%'
  AND field_code IN ('wide_focal_length', 'tele_focal_length', 'mid_tele_focal_length', 'thermal_focal_length')
  AND btrim(default_value) ~ '^\d+(?:\.\d+)?\s*mm$';

UPDATE dynamic_model_field_assignment
SET default_value = jsonb_build_object(
        'min', (regexp_match(btrim(default_value), '^f/(\d+(?:\.\d+)?)$'))[1]::numeric,
        'max', (regexp_match(btrim(default_value), '^f/(\d+(?:\.\d+)?)$'))[1]::numeric,
        'text', btrim(default_value)
    )::text,
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND model_code LIKE 'dji-pl-%'
  AND field_code IN ('wide_aperture', 'zoom_aperture', 'tele_aperture', 'mid_tele_aperture')
  AND btrim(default_value) ~ '^f/\d+(?:\.\d+)?$';

UPDATE dynamic_model_field_assignment
SET default_value = jsonb_build_object(
        'min', (regexp_match(btrim(default_value), '^f/(\d+(?:\.\d+)?)'))[1]::numeric,
        'max', (regexp_match(btrim(default_value), 'f/(\d+(?:\.\d+)?)$'))[1]::numeric,
        'text', btrim(default_value)
    )::text,
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND model_code LIKE 'dji-pl-%'
  AND field_code IN ('wide_aperture', 'zoom_aperture', 'tele_aperture', 'mid_tele_aperture')
  AND btrim(default_value) ~ '^f/\d+(?:\.\d+)?\s*[-–]\s*f/\d+(?:\.\d+)?$';

UPDATE dynamic_model dst
SET auto_param_defaults = src.auto_param_defaults,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model src
WHERE dst.code = 'dji-pl-m3ta-cam'
  AND src.code = 'dji-pl-m3t-cam'
  AND dst.deleted = false
  AND src.deleted = false
  AND COALESCE(dst.auto_param_defaults, '') = '';

COMMIT;
