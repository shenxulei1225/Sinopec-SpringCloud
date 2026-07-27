-- 业务域（Domain）统一为列名 domain：
-- 1) 入口表、型号表：重命名既有业务域列为 domain（RENAME 须写清原列名）
-- 2) 各专用表 ent_*：增加 domain，并从型号抄写回填

SET search_path TO dynamicbusiness;

-- ---------- 1. 入口与型号列统一为 domain ----------
ALTER TABLE dynamicbusiness.dynamic_entity_type
    RENAME COLUMN data_scope TO domain;

COMMENT ON COLUMN dynamicbusiness.dynamic_entity_type.domain IS
    '业务域（Domain）；子数据类型入口必填，其余入口为空';

ALTER TABLE dynamicbusiness.dynamic_model
    RENAME COLUMN data_scope TO domain;

COMMENT ON COLUMN dynamicbusiness.dynamic_model.domain IS
    '业务域（Domain）；创建时由当前入口写入，无业务域时为空';

DROP INDEX IF EXISTS dynamicbusiness.idx_dynamic_model_entity_type_data_scope;
CREATE INDEX IF NOT EXISTS idx_dynamic_model_entity_type_domain
    ON dynamicbusiness.dynamic_model (entity_type_code, domain)
    WHERE deleted = false;

DROP INDEX IF EXISTS dynamicbusiness.idx_dynamic_entity_type_base_scope;
CREATE INDEX IF NOT EXISTS idx_dynamic_entity_type_base_domain
    ON dynamicbusiness.dynamic_entity_type (base_entity_type_code, domain)
    WHERE deleted = false AND entry_kind = 'SCOPED';

-- ---------- 2. 实体专用表增加 domain ----------
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
            'COMMENT ON COLUMN dynamicbusiness.%I.domain IS ''业务域（Domain）；创建或更换型号时从型号抄写；无业务域时为空''',
            tbl
        );
        EXECUTE format(
            'CREATE INDEX IF NOT EXISTS idx_%s_domain ON dynamicbusiness.%I (domain) WHERE deleted = false',
            tbl,
            tbl
        );
    END LOOP;
END $$;

-- ---------- 3. 存量回填：实体.domain ← 型号.domain ----------
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
            'UPDATE dynamicbusiness.%I e '
            || 'SET domain = m.domain '
            || 'FROM dynamicbusiness.dynamic_model m '
            || 'WHERE e.model_id = m.id '
            || 'AND COALESCE(e.deleted, false) = false '
            || 'AND (e.domain IS NULL OR btrim(e.domain) = '''') '
            || 'AND m.domain IS NOT NULL AND btrim(m.domain) <> ''''',
            tbl
        );
    END LOOP;
END $$;
