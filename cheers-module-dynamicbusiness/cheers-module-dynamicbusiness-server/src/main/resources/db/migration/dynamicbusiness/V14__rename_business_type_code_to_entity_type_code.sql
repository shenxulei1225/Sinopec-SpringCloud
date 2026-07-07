-- 存量库：business_type_code → entity_type_code（及 source/target 变体）
-- 新库已由 V1 使用 entity_type_code，此处仅对升级环境生效

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  r RECORD;
BEGIN
  FOR r IN
    SELECT table_schema, table_name, column_name
    FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness'
      AND column_name IN ('business_type_code', 'source_business_type_code', 'target_business_type_code')
  LOOP
    EXECUTE format(
      'ALTER TABLE %I.%I RENAME COLUMN %I TO %s',
      r.table_schema,
      r.table_name,
      r.column_name,
      CASE r.column_name
        WHEN 'business_type_code' THEN 'entity_type_code'
        WHEN 'source_business_type_code' THEN 'source_entity_type_code'
        WHEN 'target_business_type_code' THEN 'target_entity_type_code'
      END
    );
  END LOOP;
END $$;

-- association_fields JSON 键名（refEntityTypeCode → refEntityTypeCode）
UPDATE dynamicbusiness.dynamic_entity_type
SET association_fields = replace(association_fields::text, 'refEntityTypeCode', 'refEntityTypeCode')::jsonb
WHERE association_fields IS NOT NULL
  AND association_fields::text LIKE '%refEntityTypeCode%';
