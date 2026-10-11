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
               row_number() OVER (ORDER BY assignment.field_id) AS row_no
        FROM dynamic_model_field_assignment AS assignment
        WHERE assignment.model_id = model.id
          AND assignment.deleted = false
          AND assignment.field_code IN (
              'wide_sensor', 'wide_focal_length', 'wide_aperture', 'wide_photo_size',
              'zoom_focal_length', 'zoom_aperture', 'zoom_photo_size',
              'mid_tele_focal_length', 'mid_tele_aperture', 'mid_tele_photo_size',
              'tele_focal_length', 'tele_aperture', 'tele_photo_size',
              'thermal_resolution', 'thermal_focal_length',
              'iso_normal', 'shutter_speed', 'gimbal_pitch_min', 'gimbal_pitch_max'
          )
          AND assignment.field_id NOT IN (SELECT field_id FROM placed)
    ),
    extra AS (
        SELECT COALESCE(
            jsonb_agg(
                jsonb_build_object(
                    'fieldId', field_id,
                    'sort', 200 + row_no,
                    'extra', '{}'::jsonb
                )
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
                        THEN jsonb_set(
                            group_item,
                            '{fields}',
                            COALESCE(group_item -> 'fields', '[]'::jsonb) || extra.fields
                        )
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
        AND assignment.field_code IN ('shutter_speed', 'gimbal_pitch_min', 'gimbal_pitch_max', 'tele_photo_size')
        AND assignment.field_id NOT IN (
            SELECT (field_item ->> 'fieldId')::bigint
            FROM jsonb_array_elements(model.field_groups_config::jsonb -> 'groups') AS group_item
            CROSS JOIN LATERAL jsonb_array_elements(COALESCE(group_item -> 'fields', '[]'::jsonb)) AS field_item
        )
  );

UPDATE dynamic_model AS model
SET field_groups_config = jsonb_set(
    model.field_groups_config::jsonb,
    '{groups}',
    COALESCE(model.field_groups_config::jsonb -> 'groups', '[]'::jsonb) || jsonb_build_array(
        jsonb_build_object(
            'id', 'group-gimbal-camera',
            'name', '云台相机',
            'color', '#409eff',
            'sort', 300,
            'fields', (
                SELECT jsonb_agg(
                    jsonb_build_object(
                        'fieldId', listed.field_id,
                        'sort', 100 + listed.row_no,
                        'extra', '{}'::jsonb
                    )
                    ORDER BY listed.row_no
                )
                FROM (
                    SELECT assignment.field_id,
                           row_number() OVER (ORDER BY assignment.field_id) AS row_no
                    FROM dynamic_model_field_assignment AS assignment
                    WHERE assignment.model_id = model.id
                      AND assignment.deleted = false
                      AND assignment.field_code IN (
                          'wide_sensor', 'wide_focal_length', 'wide_aperture', 'wide_photo_size',
                          'zoom_focal_length', 'zoom_aperture', 'zoom_photo_size',
                          'thermal_resolution', 'thermal_focal_length',
                          'iso_normal', 'shutter_speed', 'gimbal_pitch_min', 'gimbal_pitch_max'
                      )
                      AND COALESCE(btrim(assignment.default_value), '') <> ''
                ) AS listed
            )
        )
    )
)::text,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE model.deleted = false
  AND model.code IN ('dji-pl-h20n', 'dji-pl-z30', 'dji-pl-xts', 'dji-pl-xt2')
  AND NOT EXISTS (
      SELECT 1
      FROM jsonb_array_elements(model.field_groups_config::jsonb -> 'groups') AS group_item
      WHERE group_item ->> 'name' = '云台相机'
  );

COMMIT;
