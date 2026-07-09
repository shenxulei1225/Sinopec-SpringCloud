-- 组件 data_source JSON：businessTypeCode → entityTypeCode
-- 存量库列名 business_type_code → entity_type_code（若存在）

SET search_path TO platformresource;

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'platformresource'
      AND table_name = 'pr_component_props'
      AND column_name = 'business_type_code'
  ) THEN
    ALTER TABLE pr_component_props RENAME COLUMN business_type_code TO entity_type_code;
  END IF;
END $$;

UPDATE pr_component_props
SET data_source = replace(data_source, '"businessTypeCode"', '"entityTypeCode"')
WHERE data_source IS NOT NULL
  AND data_source LIKE '%businessTypeCode%';

UPDATE pr_component_props
SET props = replace(props, '"businessTypeCode"', '"entityTypeCode"')
WHERE props IS NOT NULL
  AND props LIKE '%businessTypeCode%';

UPDATE pr_component_props
SET props_override = replace(props_override, '"businessTypeCode"', '"entityTypeCode"')
WHERE props_override IS NOT NULL
  AND props_override LIKE '%businessTypeCode%';
