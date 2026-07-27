-- 业务域入口（DOMAIN）+ 划分表去 Member 命名 + 分类–实体 link 存储类型列
SET search_path TO dynamicbusiness;

-- ---------- 1. 子数据类型入口 SCOPED → DOMAIN ----------
UPDATE dynamic_entity_type
SET entry_kind = 'DOMAIN'
WHERE entry_kind = 'SCOPED'
  AND deleted = false;

DROP INDEX IF EXISTS idx_dynamic_entity_type_base_domain;
CREATE INDEX IF NOT EXISTS idx_dynamic_entity_type_base_domain
    ON dynamic_entity_type (base_entity_type_code, domain)
    WHERE deleted = false AND entry_kind = 'DOMAIN';

-- ---------- 2. dynamic_entity_type_scope_member → dynamic_entity_type_scope ----------
DO $$
BEGIN
    IF to_regclass('dynamicbusiness.dynamic_entity_type_scope_member') IS NOT NULL THEN
        ALTER TABLE dynamic_entity_type_scope_member RENAME TO dynamic_entity_type_scope;
    END IF;
END $$;

ALTER SEQUENCE IF EXISTS dynamic_entity_type_scope_member_id_seq
    RENAME TO dynamic_entity_type_scope_id_seq;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'dynamicbusiness'
          AND table_name = 'dynamic_entity_type_scope'
          AND column_name = 'scope_entity_type_code'
    ) THEN
        ALTER TABLE dynamic_entity_type_scope
            RENAME COLUMN scope_entity_type_code TO entity_type_code;
    END IF;
END $$;

DO $$
BEGIN
    IF to_regclass('dynamicbusiness.dynamic_entity_type_scope') IS NOT NULL THEN
        ALTER TABLE dynamic_entity_type_scope
            ALTER COLUMN id SET DEFAULT nextval(
                'dynamicbusiness.dynamic_entity_type_scope_id_seq'::regclass);
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'dynamic_entity_type_scope_member_pkey'
          AND connamespace = 'dynamicbusiness'::regnamespace
    ) THEN
        ALTER TABLE dynamic_entity_type_scope
            RENAME CONSTRAINT dynamic_entity_type_scope_member_pkey
            TO dynamic_entity_type_scope_pkey;
    END IF;
END $$;

ALTER INDEX IF EXISTS uk_dynamic_entity_type_scope_member
    RENAME TO uk_dynamic_entity_type_scope;
ALTER INDEX IF EXISTS idx_dynamic_entity_type_scope_member_code
    RENAME TO idx_dynamic_entity_type_scope_code;
ALTER INDEX IF EXISTS idx_dynamic_entity_type_scope_member_entity
    RENAME TO idx_dynamic_entity_type_scope_entity;

COMMENT ON COLUMN dynamic_entity_type_scope.entity_type_code IS
    '划分数据入口编码（dynamic_entity_type.code，entry_kind=SCOPE）';

-- ---------- 3. 分类–实体 link：存储类型 + 业务域（查询索引） ----------
ALTER TABLE dynamic_category_entity_link
    ADD COLUMN IF NOT EXISTS storage_entity_type_code character varying(64);

ALTER TABLE dynamic_category_entity_link
    ADD COLUMN IF NOT EXISTS domain character varying(128);

COMMENT ON COLUMN dynamic_category_entity_link.storage_entity_type_code IS
    '实体所在存储类型编码；由 entity_model_id → dynamic_model.entity_type_code 回填';
COMMENT ON COLUMN dynamic_category_entity_link.domain IS
    '业务域（Domain）；由 entity_model_id → dynamic_model.domain 回填';

UPDATE dynamic_category_entity_link l
SET storage_entity_type_code = m.entity_type_code
FROM dynamic_model m
WHERE l.entity_model_id = m.id
  AND COALESCE(l.deleted, false) = false
  AND (l.storage_entity_type_code IS NULL OR btrim(l.storage_entity_type_code) = '')
  AND m.entity_type_code IS NOT NULL
  AND btrim(m.entity_type_code) <> '';

UPDATE dynamic_category_entity_link l
SET domain = m.domain
FROM dynamic_model m
WHERE l.entity_model_id = m.id
  AND COALESCE(l.deleted, false) = false
  AND (l.domain IS NULL OR btrim(l.domain) = '')
  AND m.domain IS NOT NULL
  AND btrim(m.domain) <> '';

CREATE INDEX IF NOT EXISTS idx_dynamic_category_entity_link_storage_entity
    ON dynamic_category_entity_link (storage_entity_type_code, entity_id)
    WHERE deleted = false;

CREATE INDEX IF NOT EXISTS idx_dynamic_category_entity_link_domain
    ON dynamic_category_entity_link (domain)
    WHERE deleted = false;

-- ---------- 4. 实体专用表 domain 列与索引：V21 已加，此处不重复 ----------
