SET search_path TO platform;

DO $$
DECLARE
  r RECORD;
BEGIN
  FOR r IN
    SELECT table_name, column_name
    FROM information_schema.columns
    WHERE table_schema = 'platform'
      AND column_name = 'business_type_code'
  LOOP
    EXECUTE format('ALTER TABLE %I RENAME COLUMN %I TO entity_type_code', r.table_name, r.column_name);
  END LOOP;
END $$;
