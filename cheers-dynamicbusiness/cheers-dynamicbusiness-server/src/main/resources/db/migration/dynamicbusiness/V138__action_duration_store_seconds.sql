-- 动作耗时的数字按秒存。字段库不指定这一格的单位。
-- 配置时选的秒、分、小时只用于显示，不参与耗时计算。
-- 本迁移把已经按分写下的默认值换成整数秒，只跑一次。停留时长本来就是秒，不动。

SET search_path TO dynamicbusiness, public;

CREATE OR REPLACE FUNCTION dynamicbusiness.convert_action_duration_defaults_to_seconds(doc jsonb)
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
  minutes numeric;
BEGIN
  IF doc IS NULL OR jsonb_typeof(doc) <> 'object' THEN
    RETURN doc;
  END IF;

  fields := doc -> 'fields';
  IF jsonb_typeof(fields) = 'array' THEN
    rebuilt_fields := '[]'::jsonb;
    FOR field_idx IN 0 .. jsonb_array_length(fields) - 1 LOOP
      field := fields -> field_idx;
      IF field ->> 'fieldCode' = 'action_duration'
         AND jsonb_typeof(field -> 'defaultValue') = 'number' THEN
        minutes := (field ->> 'defaultValue')::numeric;
        field := jsonb_set(
          field,
          '{defaultValue}',
          to_jsonb(round(minutes * 60)::numeric),
          false
        );
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
          IF field ->> 'fieldCode' = 'action_duration'
             AND jsonb_typeof(field -> 'defaultValue') = 'number' THEN
            minutes := (field ->> 'defaultValue')::numeric;
            field := jsonb_set(
              field,
              '{defaultValue}',
              to_jsonb(round(minutes * 60)::numeric),
              false
            );
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
          SET param_slots_json = dynamicbusiness.convert_action_duration_defaults_to_seconds(param_slots_json),
              updater = ''migration'',
              update_time = CURRENT_TIMESTAMP
        WHERE deleted = false
          AND param_slots_json::text LIKE ''%%action_duration%%''',
      target.table_name
    );
  END LOOP;
END $$;

UPDATE dynamic_field
SET unit = NULL,
    description = '排程用的动作耗时。写下的数字是秒。秒、分、小时在配置这条参数时选择，只用于显示，不参与计算。',
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'action_duration'
  AND deleted = false;

DROP FUNCTION dynamicbusiness.convert_action_duration_defaults_to_seconds(jsonb);
