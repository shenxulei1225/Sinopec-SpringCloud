-- 存量库：biz_* 专用实体表 → ent_*；dynamic_business_type* → dynamic_entity_type*
-- 新库 V1 已使用新名，本脚本仅对升级环境生效

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  r RECORD;
BEGIN
  FOR r IN
    SELECT tablename
    FROM pg_tables
    WHERE schemaname = 'dynamicbusiness'
      AND tablename LIKE 'biz\_%' ESCAPE '\'
  LOOP
    EXECUTE format('ALTER TABLE dynamicbusiness.%I RENAME TO %I',
      r.tablename, replace(r.tablename, 'biz_', 'ent_'));
  END LOOP;
END $$;

DO $$
DECLARE
  r RECORD;
  new_name text;
BEGIN
  FOR r IN
    SELECT table_name
    FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness'
      AND table_name LIKE 'dynamic_business_type%'
  LOOP
    new_name := replace(r.table_name, 'dynamic_business_type', 'dynamic_entity_type');
    EXECUTE format('ALTER TABLE dynamicbusiness.%I RENAME TO %I', r.table_name, new_name);
  END LOOP;
END $$;

DO $$
DECLARE
  r RECORD;
  new_name text;
BEGIN
  FOR r IN
    SELECT sequence_name
    FROM information_schema.sequences
    WHERE sequence_schema = 'dynamicbusiness'
      AND sequence_name LIKE 'dynamic_business_type%'
  LOOP
    new_name := replace(r.sequence_name, 'dynamic_business_type', 'dynamic_entity_type');
    EXECUTE format('ALTER SEQUENCE dynamicbusiness.%I RENAME TO %I', r.sequence_name, new_name);
  END LOOP;
END $$;

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness'
      AND table_name = 'dynamic_model_relation'
      AND column_name = 'business_type_relation_id'
  ) THEN
    ALTER TABLE dynamicbusiness.dynamic_model_relation
      RENAME COLUMN business_type_relation_id TO entity_type_relation_id;
  END IF;
END $$;

UPDATE dynamicbusiness.dynamic_entity_type_config
SET dedicated_table_name = replace(dedicated_table_name, 'biz_', 'ent_')
WHERE dedicated_table_name LIKE 'biz\_%' ESCAPE '\';

UPDATE dynamicbusiness.dynamic_entity_type
SET association_fields = replace(association_fields::text, '"businessType"', '"entityType"')::jsonb
WHERE association_fields IS NOT NULL
  AND association_fields::text LIKE '%"businessType"%';

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness'
      AND table_name = 'dynamic_page_config'
      AND column_name = 'config_json'
  ) THEN
    UPDATE dynamicbusiness.dynamic_page_config
    SET config_json = replace(config_json::text, '"businessType"', '"entityType"')::jsonb
    WHERE config_json IS NOT NULL
      AND config_json::text LIKE '%"businessType"%';
  ELSIF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness'
      AND table_name = 'dynamic_page_config'
      AND column_name = 'config'
  ) THEN
    UPDATE dynamicbusiness.dynamic_page_config
    SET config = replace(config::text, '"businessType"', '"entityType"')::jsonb
    WHERE config IS NOT NULL
      AND config::text LIKE '%"businessType"%';
  END IF;
END $$;
