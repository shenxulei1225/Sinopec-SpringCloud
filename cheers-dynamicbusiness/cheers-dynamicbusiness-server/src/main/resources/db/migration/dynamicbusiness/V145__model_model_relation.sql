-- V145: 模型—模型多对多。同类型、异类型分表。
-- 基表是空模板；现有租户拆成 *_t{tenantId}。运行时由关联表确保服务按模板再建。
-- 不写实体自定义字段，不代替实体上级。

SET search_path TO dynamicbusiness, public;

CREATE TABLE IF NOT EXISTS dynamicbusiness.dynamic_model_model_relation (
    id                 BIGSERIAL PRIMARY KEY,
    entity_type_code   VARCHAR(64)  NOT NULL,
    source_model_id    BIGINT       NOT NULL,
    target_model_id    BIGINT       NOT NULL,
    sort               INTEGER      DEFAULT 0,
    creator            VARCHAR(64),
    create_time        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updater            VARCHAR(64),
    update_time        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted            BOOLEAN      DEFAULT FALSE,
    tenant_id          BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT ck_model_model_relation_not_self
        CHECK (source_model_id <> target_model_id)
);

COMMENT ON TABLE dynamicbusiness.dynamic_model_model_relation IS
    '模型—模型多对多（同类型）。一行一个来源模型、一个目标模型。基表为租户物理表模板。';
COMMENT ON COLUMN dynamicbusiness.dynamic_model_model_relation.entity_type_code IS
    '两端所属的同一存储类型';
COMMENT ON COLUMN dynamicbusiness.dynamic_model_model_relation.source_model_id IS '来源模型';
COMMENT ON COLUMN dynamicbusiness.dynamic_model_model_relation.target_model_id IS '目标模型';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_model_model_relation_identity
    ON dynamicbusiness.dynamic_model_model_relation (
        tenant_id, entity_type_code, source_model_id, target_model_id
    );

CREATE INDEX IF NOT EXISTS idx_mmr_source
    ON dynamicbusiness.dynamic_model_model_relation (source_model_id, entity_type_code)
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_mmr_target
    ON dynamicbusiness.dynamic_model_model_relation (target_model_id, entity_type_code)
    WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamicbusiness.dynamic_model_model_cross_relation (
    id                        BIGSERIAL PRIMARY KEY,
    source_entity_type_code   VARCHAR(64)  NOT NULL,
    source_model_id           BIGINT       NOT NULL,
    target_entity_type_code   VARCHAR(64)  NOT NULL,
    target_model_id           BIGINT       NOT NULL,
    sort                      INTEGER      DEFAULT 0,
    creator                   VARCHAR(64),
    create_time               TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updater                   VARCHAR(64),
    update_time               TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted                   BOOLEAN      DEFAULT FALSE,
    tenant_id                 BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT ck_model_model_cross_relation_types
        CHECK (source_entity_type_code <> target_entity_type_code),
    CONSTRAINT ck_model_model_cross_relation_not_self
        CHECK (source_model_id <> target_model_id)
);

COMMENT ON TABLE dynamicbusiness.dynamic_model_model_cross_relation IS
    '模型—模型多对多（异类型）。与同类型表分开。基表为租户物理表模板。';
COMMENT ON COLUMN dynamicbusiness.dynamic_model_model_cross_relation.source_entity_type_code IS
    '来源模型所属存储类型';
COMMENT ON COLUMN dynamicbusiness.dynamic_model_model_cross_relation.target_entity_type_code IS
    '目标模型所属存储类型';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_model_model_cross_relation_identity
    ON dynamicbusiness.dynamic_model_model_cross_relation (
        tenant_id, source_entity_type_code, source_model_id,
        target_entity_type_code, target_model_id
    );

CREATE INDEX IF NOT EXISTS idx_mmcr_source
    ON dynamicbusiness.dynamic_model_model_cross_relation (
        source_model_id, source_entity_type_code, target_entity_type_code
    )
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_mmcr_target
    ON dynamicbusiness.dynamic_model_model_cross_relation (
        target_model_id, target_entity_type_code, source_entity_type_code
    )
    WHERE deleted = FALSE;

CREATE OR REPLACE FUNCTION dynamicbusiness._v145_table_exists(p_name text)
RETURNS boolean
LANGUAGE sql
STABLE
AS $$
  SELECT EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness' AND table_name = lower(p_name)
  );
$$;

CREATE OR REPLACE FUNCTION dynamicbusiness._v145_ensure_id_seq(p_table text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  seq_name text := format('%s_id_seq', p_table);
  max_id bigint;
  has_rows boolean;
BEGIN
  IF NOT dynamicbusiness._v145_table_exists(p_table) THEN
    RETURN;
  END IF;
  EXECUTE format('CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.%I', seq_name);
  EXECUTE format(
    'ALTER TABLE dynamicbusiness.%I ALTER COLUMN id SET DEFAULT nextval(%L::regclass)',
    p_table, 'dynamicbusiness.' || seq_name);
  BEGIN
    EXECUTE format(
      'ALTER SEQUENCE dynamicbusiness.%I OWNED BY dynamicbusiness.%I.id',
      seq_name, p_table);
  EXCEPTION WHEN OTHERS THEN
    NULL;
  END;
  EXECUTE format(
    'SELECT COALESCE(MAX(id), 1), EXISTS (SELECT 1 FROM dynamicbusiness.%I LIMIT 1) FROM dynamicbusiness.%I',
    p_table, p_table)
    INTO max_id, has_rows;
  PERFORM setval(('dynamicbusiness.' || seq_name)::regclass, max_id, has_rows);
END;
$$;

CREATE OR REPLACE FUNCTION dynamicbusiness._v145_split_one(p_base text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  tid bigint;
  physical text;
  tenants bigint[];
BEGIN
  IF NOT dynamicbusiness._v145_table_exists(p_base) THEN
    RETURN;
  END IF;
  SELECT ARRAY(
    SELECT DISTINCT tenant_id
    FROM dynamicbusiness.dynamic_entity_type
    WHERE deleted = false AND tenant_id IS NOT NULL AND tenant_id > 0
    ORDER BY tenant_id
  ) INTO tenants;
  IF tenants IS NULL OR array_length(tenants, 1) IS NULL THEN
    tenants := ARRAY[1]::bigint[];
  END IF;
  FOREACH tid IN ARRAY tenants
  LOOP
    physical := p_base || '_t' || tid::text;
    IF NOT dynamicbusiness._v145_table_exists(physical) THEN
      EXECUTE format(
        'CREATE TABLE dynamicbusiness.%I (LIKE dynamicbusiness.%I INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES)',
        physical, p_base);
      PERFORM dynamicbusiness._v145_ensure_id_seq(physical);
    END IF;
  END LOOP;
  EXECUTE format('TRUNCATE TABLE dynamicbusiness.%I', p_base);
  PERFORM dynamicbusiness._v145_ensure_id_seq(p_base);
END;
$$;

SELECT dynamicbusiness._v145_split_one('dynamic_model_model_relation');
SELECT dynamicbusiness._v145_split_one('dynamic_model_model_cross_relation');

DROP FUNCTION IF EXISTS dynamicbusiness._v145_split_one(text);
DROP FUNCTION IF EXISTS dynamicbusiness._v145_ensure_id_seq(text);
DROP FUNCTION IF EXISTS dynamicbusiness._v145_table_exists(text);
