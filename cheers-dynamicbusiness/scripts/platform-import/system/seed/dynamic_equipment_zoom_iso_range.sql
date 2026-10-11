SET search_path TO dynamicbusiness;

BEGIN;

CREATE OR REPLACE FUNCTION pg_temp.to_range_json(raw text)
RETURNS text
LANGUAGE plpgsql
AS $$
DECLARE
  body jsonb;
  nums text[];
BEGIN
  IF raw IS NULL OR btrim(raw) = '' THEN
    RETURN raw;
  END IF;
  IF left(btrim(raw), 1) = '{' THEN
    BEGIN
      body := btrim(raw)::jsonb;
    EXCEPTION WHEN others THEN
      RETURN raw;
    END;
    IF body ? 'min' AND body ? 'max' THEN
      RETURN jsonb_build_object('min', (body->>'min')::numeric, 'max', (body->>'max')::numeric)::text;
    END IF;
    RETURN raw;
  END IF;
  nums := regexp_match(btrim(raw), '(-?\d+(?:\.\d+)?)\D+(-?\d+(?:\.\d+)?)');
  IF nums IS NULL THEN
    RETURN raw;
  END IF;
  RETURN jsonb_build_object('min', nums[1]::numeric, 'max', nums[2]::numeric)::text;
END;
$$;

UPDATE dynamic_field
SET type = 'RANGE',
    unit = 'mm',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'zoom_focal_length'
  AND deleted = false;

UPDATE dynamic_field
SET type = 'RANGE',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'iso_normal'
  AND deleted = false;

UPDATE dynamic_model_field_assignment
SET default_value = pg_temp.to_range_json(default_value),
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND field_code IN ('zoom_focal_length', 'iso_normal')
  AND default_value IS NOT NULL
  AND btrim(default_value) <> ''
  AND default_value IS DISTINCT FROM pg_temp.to_range_json(default_value);

UPDATE ent_equipment
SET custom_fields = jsonb_set(custom_fields, '{zoom_focal_length}', pg_temp.to_range_json(custom_fields->>'zoom_focal_length')::jsonb)
WHERE deleted = false
  AND jsonb_typeof(custom_fields->'zoom_focal_length') = 'string'
  AND custom_fields->>'zoom_focal_length' IS DISTINCT FROM pg_temp.to_range_json(custom_fields->>'zoom_focal_length');

UPDATE ent_equipment
SET custom_fields = jsonb_set(custom_fields, '{iso_normal}', pg_temp.to_range_json(custom_fields->>'iso_normal')::jsonb)
WHERE deleted = false
  AND jsonb_typeof(custom_fields->'iso_normal') = 'string'
  AND custom_fields->>'iso_normal' IS DISTINCT FROM pg_temp.to_range_json(custom_fields->>'iso_normal');

UPDATE ent_equipment_t1
SET custom_fields = jsonb_set(custom_fields, '{zoom_focal_length}', pg_temp.to_range_json(custom_fields->>'zoom_focal_length')::jsonb)
WHERE deleted = false
  AND jsonb_typeof(custom_fields->'zoom_focal_length') = 'string'
  AND custom_fields->>'zoom_focal_length' IS DISTINCT FROM pg_temp.to_range_json(custom_fields->>'zoom_focal_length');

UPDATE ent_equipment_t1
SET custom_fields = jsonb_set(custom_fields, '{iso_normal}', pg_temp.to_range_json(custom_fields->>'iso_normal')::jsonb)
WHERE deleted = false
  AND jsonb_typeof(custom_fields->'iso_normal') = 'string'
  AND custom_fields->>'iso_normal' IS DISTINCT FROM pg_temp.to_range_json(custom_fields->>'iso_normal');

UPDATE ent_equipment_t2
SET custom_fields = jsonb_set(custom_fields, '{zoom_focal_length}', pg_temp.to_range_json(custom_fields->>'zoom_focal_length')::jsonb)
WHERE deleted = false
  AND jsonb_typeof(custom_fields->'zoom_focal_length') = 'string'
  AND custom_fields->>'zoom_focal_length' IS DISTINCT FROM pg_temp.to_range_json(custom_fields->>'zoom_focal_length');

UPDATE ent_equipment_t2
SET custom_fields = jsonb_set(custom_fields, '{iso_normal}', pg_temp.to_range_json(custom_fields->>'iso_normal')::jsonb)
WHERE deleted = false
  AND jsonb_typeof(custom_fields->'iso_normal') = 'string'
  AND custom_fields->>'iso_normal' IS DISTINCT FROM pg_temp.to_range_json(custom_fields->>'iso_normal');

UPDATE model_crud_form_definition AS form
SET crud_form_fields = jsonb_set(
      form.crud_form_fields,
      '{customFieldDefs}',
      (
        SELECT jsonb_agg(patched ORDER BY ord)
        FROM (
          SELECT ord,
            CASE
              WHEN elem->>'fieldCode' = 'zoom_focal_length' THEN
                elem || '{"fieldType":"RANGE","renderAs":"number-range","unit":"mm"}'::jsonb
              WHEN elem->>'fieldCode' = 'iso_normal' THEN
                (elem - 'unit') || '{"fieldType":"RANGE","renderAs":"number-range"}'::jsonb
              ELSE elem
            END AS patched
          FROM jsonb_array_elements(form.crud_form_fields->'customFieldDefs') WITH ORDINALITY AS listed(elem, ord)
        ) rewritten
      ),
      true
    ),
    update_time = CURRENT_TIMESTAMP
WHERE form.deleted = false
  AND jsonb_typeof(form.crud_form_fields->'customFieldDefs') = 'array'
  AND EXISTS (
    SELECT 1
    FROM jsonb_array_elements(form.crud_form_fields->'customFieldDefs') elem
    WHERE elem->>'fieldCode' IN ('zoom_focal_length', 'iso_normal')
      AND elem->>'fieldType' IS DISTINCT FROM 'RANGE'
  );

COMMIT;
