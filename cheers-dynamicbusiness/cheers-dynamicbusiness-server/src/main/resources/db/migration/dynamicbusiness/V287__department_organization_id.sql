-- 部门表补「所属组织」列。挂在场站上的部门这一列留空，只写所属场站。
-- 组织树按这一列查直接挂在组织上的部门。列不存在时整段查询失败，部门栏报错。
SET search_path TO dynamicbusiness;

DO $$
DECLARE
    rel text;
BEGIN
    FOR rel IN
        SELECT c.relname
        FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE n.nspname = 'dynamicbusiness'
          AND c.relkind = 'r'
          AND c.relname LIKE 'ent_department%'
          AND NOT EXISTS (
              SELECT 1
              FROM information_schema.columns col
              WHERE col.table_schema = 'dynamicbusiness'
                AND col.table_name = c.relname
                AND col.column_name = 'organization_id'
          )
    LOOP
        EXECUTE format(
            'ALTER TABLE dynamicbusiness.%I ADD COLUMN organization_id bigint',
            rel
        );
    END LOOP;
END $$;
