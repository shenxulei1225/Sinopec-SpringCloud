-- 用型号上已有的字段分组，把设备指标、机身相机、云台相机建出来，并把已分配字段放进去。
-- 不新增分组字段，不改分组功能。一个型号没有的字段不会被加进分组。

SET search_path TO dynamicbusiness;

BEGIN;

UPDATE dynamic_model m
SET field_groups_config = (
  WITH source AS (
    SELECT CASE
      WHEN m.field_groups_config IS NULL OR btrim(m.field_groups_config) = '' THEN '{"groups":[]}'::jsonb
      ELSE m.field_groups_config::jsonb
    END AS doc
  ),
  assigned AS (
    SELECT f.id AS field_id, f.code
    FROM dynamic_model_field_assignment a
    JOIN dynamic_field f
      ON f.code = a.field_code
     AND f.tenant_id = 1
     AND f.deleted = false
    WHERE a.model_code = m.code
      AND a.tenant_id = 1
      AND a.deleted = false
      AND f.code IN (
        'spec_weight_g', 'spec_max_takeoff_weight_g', 'spec_max_payload_g',
        'spec_dimensions', 'spec_wheelbase_mm',
        'max_flight_time', 'battery_discharge_curve', 'max_hover_time',
        'max_horizontal_speed', 'max_ascent_speed', 'max_descent_speed', 'max_wind_resistance',
        'spec_max_altitude_m', 'spec_operating_temperature', 'spec_max_pitch_angle',
        'payload_mounts', 'body_camera_group',
        'fpv_resolution', 'fpv_night_vision',
        'gimbal_pitch_min', 'gimbal_pitch_max',
        'wide_sensor', 'wide_focal_length', 'wide_aperture', 'wide_photo_size',
        'zoom_focal_length', 'zoom_aperture', 'zoom_photo_size',
        'mid_tele_focal_length', 'mid_tele_aperture', 'mid_tele_photo_size',
        'tele_focal_length', 'tele_aperture', 'tele_photo_size',
        'thermal_resolution', 'thermal_focal_length',
        'shutter_speed', 'iso_normal'
      )
  ),
  pitch_target AS (
    SELECT CASE
      WHEN EXISTS (SELECT 1 FROM assigned WHERE code = 'fpv_resolution') THEN 'body'
      ELSE 'gimbal'
    END AS target
  ),
  placed AS (
    SELECT field_id, code,
      CASE
        WHEN code IN (
          'spec_weight_g', 'spec_max_takeoff_weight_g', 'spec_max_payload_g',
          'spec_dimensions', 'spec_wheelbase_mm',
          'max_flight_time', 'battery_discharge_curve', 'max_hover_time',
          'max_horizontal_speed', 'max_ascent_speed', 'max_descent_speed', 'max_wind_resistance',
          'spec_max_altitude_m', 'spec_operating_temperature', 'spec_max_pitch_angle',
          'payload_mounts', 'body_camera_group'
        ) THEN 'metrics'
        WHEN code IN ('fpv_resolution', 'fpv_night_vision') THEN 'body'
        WHEN code IN ('gimbal_pitch_min', 'gimbal_pitch_max') THEN (SELECT target FROM pitch_target)
        ELSE 'gimbal'
      END AS bucket,
      CASE code
        WHEN 'spec_weight_g' THEN 10
        WHEN 'spec_max_takeoff_weight_g' THEN 12
        WHEN 'spec_max_payload_g' THEN 14
        WHEN 'spec_dimensions' THEN 20
        WHEN 'spec_wheelbase_mm' THEN 22
        WHEN 'max_flight_time' THEN 30
        WHEN 'battery_discharge_curve' THEN 31
        WHEN 'max_hover_time' THEN 32
        WHEN 'max_horizontal_speed' THEN 40
        WHEN 'max_ascent_speed' THEN 42
        WHEN 'max_descent_speed' THEN 44
        WHEN 'max_wind_resistance' THEN 46
        WHEN 'spec_max_altitude_m' THEN 50
        WHEN 'spec_operating_temperature' THEN 52
        WHEN 'spec_max_pitch_angle' THEN 54
        WHEN 'payload_mounts' THEN 80
        WHEN 'body_camera_group' THEN 5
        WHEN 'fpv_resolution' THEN 10
        WHEN 'fpv_night_vision' THEN 20
        WHEN 'gimbal_pitch_min' THEN 30
        WHEN 'gimbal_pitch_max' THEN 40
        WHEN 'wide_sensor' THEN 10
        WHEN 'wide_focal_length' THEN 20
        WHEN 'wide_aperture' THEN 30
        WHEN 'wide_photo_size' THEN 40
        WHEN 'zoom_focal_length' THEN 50
        WHEN 'zoom_aperture' THEN 60
        WHEN 'zoom_photo_size' THEN 70
        WHEN 'mid_tele_focal_length' THEN 80
        WHEN 'mid_tele_aperture' THEN 90
        WHEN 'mid_tele_photo_size' THEN 100
        WHEN 'tele_focal_length' THEN 110
        WHEN 'tele_aperture' THEN 120
        WHEN 'tele_photo_size' THEN 130
        WHEN 'thermal_resolution' THEN 140
        WHEN 'thermal_focal_length' THEN 150
        WHEN 'shutter_speed' THEN 160
        WHEN 'iso_normal' THEN 170
        ELSE 900
      END AS sort
    FROM assigned
  ),
  kept AS (
    SELECT COALESCE(jsonb_agg(cleaned ORDER BY COALESCE((cleaned->>'sort')::int, 0)), '[]'::jsonb) AS groups
    FROM (
      SELECT jsonb_set(
        g,
        '{fields}',
        COALESCE(
          (
            SELECT jsonb_agg(f ORDER BY COALESCE((f->>'sort')::int, 0))
            FROM jsonb_array_elements(COALESCE(g->'fields', '[]'::jsonb)) f
            WHERE NOT EXISTS (
              SELECT 1 FROM placed p WHERE p.field_id = (f->>'fieldId')::bigint
            )
          ),
          '[]'::jsonb
        )
      ) AS cleaned
      FROM source, jsonb_array_elements(source.doc->'groups') g
      WHERE COALESCE(g->>'id', '') NOT IN (
          'spec-root', 'spec-body', 'spec-gimbal',
          'group-device-metrics', 'group-body-camera', 'group-gimbal-camera'
        )
        AND COALESCE(g->>'name', '') NOT IN ('规格', '设备指标', '机身相机', '云台相机')
    ) rows
  ),
  metrics_fields AS (
    SELECT COALESCE(
      jsonb_agg(jsonb_build_object('fieldId', field_id, 'sort', sort, 'extra', '{}'::jsonb) ORDER BY sort),
      '[]'::jsonb
    ) AS fields
    FROM placed WHERE bucket = 'metrics'
  ),
  body_fields AS (
    SELECT COALESCE(
      jsonb_agg(jsonb_build_object('fieldId', field_id, 'sort', sort, 'extra', '{}'::jsonb) ORDER BY sort),
      '[]'::jsonb
    ) AS fields
    FROM placed WHERE bucket = 'body'
  ),
  gimbal_fields AS (
    SELECT COALESCE(
      jsonb_agg(jsonb_build_object('fieldId', field_id, 'sort', sort, 'extra', '{}'::jsonb) ORDER BY sort),
      '[]'::jsonb
    ) AS fields
    FROM placed WHERE bucket = 'gimbal'
  )
  SELECT jsonb_build_object(
    'groups',
    kept.groups
      || CASE WHEN jsonb_array_length(metrics_fields.fields) > 0 THEN jsonb_build_array(jsonb_build_object(
           'id', 'group-device-metrics', 'name', '设备指标', 'color', '#409eff', 'sort', 100, 'fields', metrics_fields.fields
         )) ELSE '[]'::jsonb END
      || CASE WHEN jsonb_array_length(body_fields.fields) > 0 THEN jsonb_build_array(jsonb_build_object(
           'id', 'group-body-camera', 'name', '机身相机', 'color', '#409eff', 'sort', 200,
           'parentId', CASE WHEN jsonb_array_length(metrics_fields.fields) > 0 THEN 'group-device-metrics' ELSE NULL END,
           'fields', body_fields.fields
         )) ELSE '[]'::jsonb END
      || CASE WHEN jsonb_array_length(gimbal_fields.fields) > 0 THEN jsonb_build_array(jsonb_build_object(
           'id', 'group-gimbal-camera', 'name', '云台相机', 'color', '#409eff', 'sort', 300,
           'parentId', CASE WHEN jsonb_array_length(metrics_fields.fields) > 0 THEN 'group-device-metrics' ELSE NULL END,
           'fields', gimbal_fields.fields
         )) ELSE '[]'::jsonb END
      || CASE
           WHEN jsonb_array_length(body_fields.fields) > 0
            AND jsonb_array_length(gimbal_fields.fields) = 0
            AND jsonb_array_length(metrics_fields.fields) > 0
           THEN jsonb_build_array(jsonb_build_object(
             'id', 'group-gimbal-camera', 'name', '云台相机', 'color', '#409eff', 'sort', 300,
             'parentId', 'group-device-metrics', 'fields', '[]'::jsonb
           ))
           ELSE '[]'::jsonb
         END
  )::text
  FROM kept, metrics_fields, body_fields, gimbal_fields
),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE m.deleted = false
  AND EXISTS (
    SELECT 1
    FROM dynamic_model_field_assignment a
    WHERE a.model_code = m.code
      AND a.tenant_id = 1
      AND a.deleted = false
      AND a.field_code IN (
        'spec_weight_g', 'spec_max_takeoff_weight_g', 'spec_max_payload_g',
        'spec_dimensions', 'spec_wheelbase_mm',
        'max_flight_time', 'battery_discharge_curve', 'max_hover_time',
        'max_horizontal_speed', 'max_ascent_speed', 'max_descent_speed', 'max_wind_resistance',
        'spec_max_altitude_m', 'spec_operating_temperature', 'spec_max_pitch_angle',
        'payload_mounts', 'body_camera_group',
        'fpv_resolution', 'fpv_night_vision',
        'gimbal_pitch_min', 'gimbal_pitch_max',
        'wide_sensor', 'wide_focal_length', 'wide_aperture', 'wide_photo_size',
        'zoom_focal_length', 'zoom_aperture', 'zoom_photo_size',
        'mid_tele_focal_length', 'mid_tele_aperture', 'mid_tele_photo_size',
        'tele_focal_length', 'tele_aperture', 'tele_photo_size',
        'thermal_resolution', 'thermal_focal_length',
        'shutter_speed', 'iso_normal'
      )
  );

COMMIT;
