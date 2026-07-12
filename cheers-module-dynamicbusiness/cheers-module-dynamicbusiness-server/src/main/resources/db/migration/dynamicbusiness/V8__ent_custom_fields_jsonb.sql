-- 将 ent_* 业务表的 custom_fields 从 TEXT 统一为 JSONB，与 EntityDO / JsonbMapTypeHandler 写入方式一致。

DO $$
DECLARE
    tbl text;
BEGIN
    FOR tbl IN
        SELECT c.table_name
        FROM information_schema.columns c
        WHERE c.table_schema = 'dynamicbusiness'
          AND c.column_name = 'custom_fields'
          AND c.data_type = 'text'
          AND c.table_name LIKE 'ent\_%'
    LOOP
        EXECUTE format(
            'ALTER TABLE dynamicbusiness.%I ALTER COLUMN custom_fields TYPE jsonb USING '
            || 'CASE WHEN custom_fields IS NULL OR btrim(custom_fields) = '''' THEN ''{}''::jsonb '
            || 'ELSE custom_fields::jsonb END',
            tbl
        );
        EXECUTE format(
            'ALTER TABLE dynamicbusiness.%I ALTER COLUMN custom_fields SET DEFAULT ''{}''::jsonb',
            tbl
        );
        EXECUTE format(
            'COMMENT ON COLUMN dynamicbusiness.%I.custom_fields IS ''自定义字段（JSONB）''',
            tbl
        );
    END LOOP;
END $$;
