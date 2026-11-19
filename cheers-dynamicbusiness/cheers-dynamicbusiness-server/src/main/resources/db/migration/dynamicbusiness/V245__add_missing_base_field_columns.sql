-- 基础字段已经登记，专用表上却没有对应的列。
-- 列表按已登记字段选列，缺列时整栏变成系统异常。
-- 应急预案的「需要现场抢修」是其中一列。上级部门走核心列 parent_id，这里不加。
SET search_path TO dynamicbusiness;

DO $$
DECLARE
    rec record;
    tbl text;
    col text;
    pgtype text;
BEGIN
    FOR rec IN
        SELECT b.field_code,
               upper(b.data_type) AS data_type,
               COALESCE(NULLIF(t.dedicated_table_name, ''),
                        'ent_' || b.entity_type_code || '_t' || b.tenant_id::text) AS table_name
        FROM dynamic_entity_type_base_field b
        JOIN dynamic_entity_type t
          ON t.code = b.entity_type_code
         AND t.tenant_id = b.tenant_id
         AND t.deleted = false
        WHERE b.deleted = false
          AND b.status = 1
          AND lower(b.field_code) <> 'parentid'
          AND upper(b.data_type) NOT IN ('REF_MULTI', 'ENTITY_REF_MULTI', 'BATCH_ENTITY_REF', 'ENTITY_SELF_REF')
    LOOP
        tbl := rec.table_name;
        col := lower(replace(rec.field_code, '-', '_'));
        IF col !~ '^[a-z][a-z0-9_]*$' THEN
            CONTINUE;
        END IF;
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.tables
            WHERE table_schema = 'dynamicbusiness' AND table_name = tbl
        ) THEN
            CONTINUE;
        END IF;
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = 'dynamicbusiness' AND table_name = tbl AND column_name = col
        ) THEN
            CONTINUE;
        END IF;
        pgtype := CASE rec.data_type
            WHEN 'LONG_TEXT' THEN 'TEXT'
            WHEN 'NUMBER' THEN 'NUMERIC(18,4)'
            WHEN 'INTEGER' THEN 'BIGINT'
            WHEN 'DATE' THEN 'DATE'
            WHEN 'DATETIME' THEN 'TIMESTAMP'
            WHEN 'BOOLEAN' THEN 'BOOLEAN'
            WHEN 'ENUM' THEN 'VARCHAR(100)'
            WHEN 'JSON' THEN 'JSONB'
            WHEN 'REF' THEN 'BIGINT'
            WHEN 'ENTITY_REF' THEN 'BIGINT'
            ELSE 'VARCHAR(255)'
        END;
        EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN %I %s', tbl, col, pgtype);
    END LOOP;
END $$;
