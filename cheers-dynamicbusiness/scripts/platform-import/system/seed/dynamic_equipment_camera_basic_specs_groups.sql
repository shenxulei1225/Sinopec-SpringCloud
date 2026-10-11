SET search_path TO dynamicbusiness;

BEGIN;

UPDATE dynamic_model AS model
SET field_groups_config = (
    WITH groups AS (
        SELECT item AS group_item, ordinality
        FROM jsonb_array_elements(model.field_groups_config::jsonb -> 'groups')
            WITH ORDINALITY AS listed(item, ordinality)
    ),
    placed AS (
        SELECT (field_item ->> 'fieldId')::bigint AS field_id
        FROM groups
        CROSS JOIN LATERAL jsonb_array_elements(COALESCE(group_item -> 'fields', '[]'::jsonb)) AS field_item
    ),
    missing AS (
        SELECT assignment.field_id,
               row_number() OVER (ORDER BY assignment.field_code) AS row_no
        FROM dynamic_model_field_assignment AS assignment
        WHERE assignment.model_id = model.id
          AND assignment.deleted = false
          AND COALESCE(btrim(assignment.default_value), '') <> ''
          AND assignment.field_code IN (
              'wide_fov', 'zoom_fov', 'mid_tele_fov', 'tele_fov', 'thermal_fov',
              'wide_actual_focal_length', 'zoom_actual_focal_length', 'mid_tele_actual_focal_length',
              'tele_actual_focal_length', 'thermal_actual_focal_length',
              'zoom_sensor', 'mid_tele_sensor', 'tele_sensor', 'thermal_sensor', 'thermal_aperture',
              'wide_photo_format', 'zoom_photo_format', 'tele_photo_format', 'thermal_photo_format',
              'wide_video_resolution', 'zoom_video_resolution', 'mid_tele_video_resolution',
              'tele_video_resolution', 'thermal_video_resolution',
              'wide_video_format', 'zoom_video_format', 'thermal_video_format',
              'wide_focus_distance', 'zoom_focus_distance', 'mid_tele_focus_distance',
              'tele_focus_distance', 'thermal_focus_distance',
              'max_zoom_factor', 'gimbal_yaw_min', 'gimbal_yaw_max', 'gimbal_angular_vibration',
              'spec_weight_g', 'spec_dimensions', 'spec_operating_temperature', 'spec_storage_temperature',
              'spec_ingress_protection', 'thermal_measure_range', 'thermal_digital_zoom',
              'thermal_pixel_pitch', 'laser_measure_range'
          )
          AND assignment.field_id NOT IN (SELECT field_id FROM placed)
    ),
    extra AS (
        SELECT COALESCE(
            jsonb_agg(
                jsonb_build_object('fieldId', field_id, 'sort', 220 + row_no, 'extra', '{}'::jsonb)
                ORDER BY row_no
            ),
            '[]'::jsonb
        ) AS fields
        FROM missing
    )
    SELECT jsonb_set(
        model.field_groups_config::jsonb,
        '{groups}',
        (
            SELECT jsonb_agg(
                CASE
                    WHEN group_item ->> 'name' = '云台相机'
                        THEN jsonb_set(group_item, '{fields}', COALESCE(group_item -> 'fields', '[]'::jsonb) || extra.fields)
                    ELSE group_item
                END
                ORDER BY ordinality
            )
            FROM groups, extra
        )
    )::text
),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE model.deleted = false
  AND model.code LIKE 'dji-pl-%'
  AND EXISTS (
      SELECT 1
      FROM jsonb_array_elements(model.field_groups_config::jsonb -> 'groups') AS group_item
      WHERE group_item ->> 'name' = '云台相机'
  )
  AND EXISTS (
      SELECT 1
      FROM dynamic_model_field_assignment AS assignment
      WHERE assignment.model_id = model.id
        AND assignment.deleted = false
        AND assignment.field_code IN (
            'wide_fov', 'zoom_fov', 'zoom_actual_focal_length', 'spec_weight_g', 'thermal_fov', 'gimbal_yaw_min'
        )
        AND assignment.field_id NOT IN (
            SELECT (field_item ->> 'fieldId')::bigint
            FROM jsonb_array_elements(model.field_groups_config::jsonb -> 'groups') AS group_item
            CROSS JOIN LATERAL jsonb_array_elements(COALESCE(group_item -> 'fields', '[]'::jsonb)) AS field_item
        )
  );

COMMIT;
