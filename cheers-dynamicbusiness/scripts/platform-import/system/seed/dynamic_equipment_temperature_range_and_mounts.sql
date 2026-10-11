SET search_path TO dynamicbusiness;

BEGIN;

UPDATE dynamic_field
SET type = 'RANGE',
    unit = '°C',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'spec_operating_temperature'
  AND tenant_id = 1
  AND deleted = false;

UPDATE dynamic_model_field_assignment
SET default_value = jsonb_build_object(
      'min', (regexp_match(default_value, '(-?\d+(?:\.\d+)?)'))[1]::numeric,
      'max', (regexp_match(default_value, '到\s*(-?\d+(?:\.\d+)?)'))[1]::numeric
    )::text,
    update_time = CURRENT_TIMESTAMP
WHERE field_code = 'spec_operating_temperature'
  AND deleted = false
  AND default_value ~ '到';

UPDATE ent_equipment
SET custom_fields = jsonb_set(
      custom_fields,
      '{spec_operating_temperature}',
      jsonb_build_object(
        'min', (regexp_match(custom_fields->>'spec_operating_temperature', '(-?\d+(?:\.\d+)?)'))[1]::numeric,
        'max', (regexp_match(custom_fields->>'spec_operating_temperature', '到\s*(-?\d+(?:\.\d+)?)'))[1]::numeric
      )
    )
WHERE deleted = false
  AND custom_fields->>'spec_operating_temperature' ~ '到';

UPDATE ent_equipment_t1
SET custom_fields = jsonb_set(
      custom_fields,
      '{spec_operating_temperature}',
      jsonb_build_object(
        'min', (regexp_match(custom_fields->>'spec_operating_temperature', '(-?\d+(?:\.\d+)?)'))[1]::numeric,
        'max', (regexp_match(custom_fields->>'spec_operating_temperature', '到\s*(-?\d+(?:\.\d+)?)'))[1]::numeric
      )
    )
WHERE deleted = false
  AND custom_fields->>'spec_operating_temperature' ~ '到';

UPDATE ent_equipment_t2
SET custom_fields = jsonb_set(
      custom_fields,
      '{spec_operating_temperature}',
      jsonb_build_object(
        'min', (regexp_match(custom_fields->>'spec_operating_temperature', '(-?\d+(?:\.\d+)?)'))[1]::numeric,
        'max', (regexp_match(custom_fields->>'spec_operating_temperature', '到\s*(-?\d+(?:\.\d+)?)'))[1]::numeric
      )
    )
WHERE deleted = false
  AND custom_fields->>'spec_operating_temperature' ~ '到';

UPDATE dynamic_model m
SET field_groups_config = (
      SELECT jsonb_set(
        m.field_groups_config::jsonb,
        '{groups}',
        (
          SELECT jsonb_agg(patched ORDER BY ord)
          FROM (
            SELECT ord,
              CASE
                WHEN g->>'name' = '设备指标'
                 AND NOT EXISTS (
                   SELECT 1
                   FROM jsonb_array_elements(COALESCE(g->'fields', '[]'::jsonb)) f
                   WHERE (f->>'fieldId')::bigint = mount.field_id
                 )
                THEN jsonb_set(
                  g,
                  '{fields}',
                  COALESCE(g->'fields', '[]'::jsonb) || jsonb_build_array(
                    jsonb_build_object('fieldId', mount.field_id, 'sort', 80, 'extra', '{}'::jsonb)
                  )
                )
                WHEN g->>'name' IS DISTINCT FROM '设备指标'
                THEN jsonb_set(
                  g,
                  '{fields}',
                  COALESCE(
                    (
                      SELECT jsonb_agg(f)
                      FROM jsonb_array_elements(COALESCE(g->'fields', '[]'::jsonb)) f
                      WHERE (f->>'fieldId')::bigint IS DISTINCT FROM mount.field_id
                    ),
                    '[]'::jsonb
                  )
                )
                ELSE g
              END AS patched
            FROM jsonb_array_elements(m.field_groups_config::jsonb->'groups') WITH ORDINALITY AS t(g, ord)
          ) rows
        )
      )::text
    ),
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT id AS field_id
  FROM dynamic_field
  WHERE code = 'payload_mounts'
    AND tenant_id = 1
    AND deleted = false
) mount
WHERE m.deleted = false
  AND m.field_groups_config IS NOT NULL
  AND btrim(m.field_groups_config) <> ''
  AND EXISTS (
    SELECT 1
    FROM dynamic_model_field_assignment a
    WHERE a.model_code = m.code
      AND a.tenant_id = 1
      AND a.deleted = false
      AND a.field_code = 'payload_mounts'
  )
  AND EXISTS (
    SELECT 1
    FROM jsonb_array_elements(m.field_groups_config::jsonb->'groups') g
    WHERE g->>'name' = '设备指标'
  );

COMMIT;
