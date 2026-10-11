-- 动作耗时的数字已经是秒。
-- 参数上已经选了秒或分的，显示单位不动。
-- 没选过单位的，当时按字段库的分来显示。把「分」记到这条参数上，避免改成按秒展示。
-- 不乘 60。已经是秒的数字保持不变。

SET search_path TO dynamicbusiness, public;

CREATE OR REPLACE FUNCTION dynamicbusiness.mark_unset_action_duration_display_as_minutes(doc jsonb)
RETURNS jsonb
LANGUAGE plpgsql
AS $$
DECLARE
  methods jsonb;
  method jsonb;
  fields jsonb;
  field jsonb;
  rebuilt_methods jsonb := '[]'::jsonb;
  rebuilt_fields jsonb;
  idx integer;
  field_idx integer;
  unit text;
BEGIN
  IF doc IS NULL OR jsonb_typeof(doc) <> 'object' THEN
    RETURN doc;
  END IF;

  fields := doc -> 'fields';
  IF jsonb_typeof(fields) = 'array' THEN
    rebuilt_fields := '[]'::jsonb;
    FOR field_idx IN 0 .. jsonb_array_length(fields) - 1 LOOP
      field := fields -> field_idx;
      unit := field ->> 'durationUnit';
      IF field ->> 'fieldCode' = 'action_duration'
         AND jsonb_typeof(field -> 'defaultValue') = 'number'
         AND (unit IS NULL OR btrim(unit) = '') THEN
        field := jsonb_set(field, '{durationUnit}', to_jsonb('min'::text), true);
      END IF;
      rebuilt_fields := rebuilt_fields || jsonb_build_array(field);
    END LOOP;
    doc := jsonb_set(doc, '{fields}', rebuilt_fields, false);
  END IF;

  methods := doc -> 'methods';
  IF jsonb_typeof(methods) = 'array' THEN
    FOR idx IN 0 .. jsonb_array_length(methods) - 1 LOOP
      method := methods -> idx;
      fields := method -> 'fields';
      rebuilt_fields := '[]'::jsonb;
      IF jsonb_typeof(fields) = 'array' THEN
        FOR field_idx IN 0 .. jsonb_array_length(fields) - 1 LOOP
          field := fields -> field_idx;
          unit := field ->> 'durationUnit';
          IF field ->> 'fieldCode' = 'action_duration'
             AND jsonb_typeof(field -> 'defaultValue') = 'number'
             AND (unit IS NULL OR btrim(unit) = '') THEN
            field := jsonb_set(field, '{durationUnit}', to_jsonb('min'::text), true);
          END IF;
          rebuilt_fields := rebuilt_fields || jsonb_build_array(field);
        END LOOP;
        method := jsonb_set(method, '{fields}', rebuilt_fields, false);
      END IF;
      rebuilt_methods := rebuilt_methods || jsonb_build_array(method);
    END LOOP;
    doc := jsonb_set(doc, '{methods}', rebuilt_methods, false);
  END IF;

  RETURN doc;
END;
$$;

DO $$
DECLARE
  target record;
BEGIN
  FOR target IN
    SELECT c.relname AS table_name
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'dynamicbusiness'
      AND c.relkind = 'r'
      AND c.relname LIKE 'ent_action%'
      AND EXISTS (
        SELECT 1
        FROM information_schema.columns col
        WHERE col.table_schema = 'dynamicbusiness'
          AND col.table_name = c.relname
          AND col.column_name = 'param_slots_json'
      )
  LOOP
    EXECUTE format(
      'UPDATE dynamicbusiness.%I
          SET param_slots_json = dynamicbusiness.mark_unset_action_duration_display_as_minutes(param_slots_json),
              updater = ''migration'',
              update_time = CURRENT_TIMESTAMP
        WHERE deleted = false
          AND param_slots_json::text LIKE ''%%action_duration%%''',
      target.table_name
    );
  END LOOP;
END $$;

DROP FUNCTION dynamicbusiness.mark_unset_action_duration_display_as_minutes(jsonb);
