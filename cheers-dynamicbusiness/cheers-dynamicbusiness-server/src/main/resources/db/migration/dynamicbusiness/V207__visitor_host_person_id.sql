-- 被访人从手写文字改为组织架构里的人。这一列只存这个人的编号。列里还没有内容。
SET search_path TO dynamicbusiness;

DO $$
DECLARE
  rec record;
BEGIN
  FOR rec IN
    SELECT c.table_name, c.column_name
    FROM information_schema.columns c
    JOIN dynamic_entity_type_base_field b
      ON lower(replace(b.field_code, '-', '_')) = c.column_name
     AND b.deleted = false
     AND b.field_name = '被访人'
     AND b.entity_type_code = 'visitor'
    WHERE c.table_schema = 'dynamicbusiness'
      AND c.table_name IN ('ent_visitor_t1', 'ent_visitor_t123')
      AND c.data_type = 'character varying'
  LOOP
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ALTER COLUMN %I TYPE bigint USING NULLIF(btrim(%I), '''')::bigint',
      rec.table_name, rec.column_name, rec.column_name
    );
  END LOOP;
END $$;
