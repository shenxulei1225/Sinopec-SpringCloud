-- V36: 补齐实体专用表核心列 domain / tree_path / sort（对齐 EntityDO）
-- 根因：运行时 buildCreateTableSql 旧模板缺这三列；Flyway 曾给当时已有 ent_* 补过，
-- 之后经接口新建的表（如 ent_standard）缺列，导致 query-by-scene 统一 SELECT 500。

SET search_path TO dynamicbusiness;

DO $$
DECLARE
    tbl text;
BEGIN
    FOR tbl IN
        SELECT t.table_name
        FROM information_schema.tables t
        WHERE t.table_schema = 'dynamicbusiness'
          AND t.table_type = 'BASE TABLE'
          AND t.table_name LIKE 'ent\_%' ESCAPE '\'
    LOOP
        EXECUTE format(
            'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS domain character varying(128)',
            tbl
        );
        EXECUTE format(
            'COMMENT ON COLUMN dynamicbusiness.%I.domain IS %L',
            tbl,
            '业务域（Domain）；创建或更换型号时从型号抄写；无业务域时为空'
        );
        EXECUTE format(
            'CREATE INDEX IF NOT EXISTS idx_%s_domain ON dynamicbusiness.%I (domain) WHERE deleted = false',
            tbl,
            tbl
        );

        EXECUTE format(
            'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS tree_path character varying(500)',
            tbl
        );
        EXECUTE format(
            'COMMENT ON COLUMN dynamicbusiness.%I.tree_path IS %L',
            tbl,
            '树路径（通用实体）'
        );
        EXECUTE format(
            'CREATE INDEX IF NOT EXISTS idx_%s_tree_path ON dynamicbusiness.%I (tree_path)',
            tbl,
            tbl
        );

        EXECUTE format(
            'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS sort integer DEFAULT 0',
            tbl
        );
        EXECUTE format(
            'COMMENT ON COLUMN dynamicbusiness.%I.sort IS %L',
            tbl,
            '同级排序'
        );
    END LOOP;
END $$;
