-- V38: 型号—实体多对多关联（跨类型挂靠，如设备型号 ↔ 检查内容）
-- 物理隔离：无后缀基表为空模板；现有租户拆为 *_t{tenantId}（与 V37 同口径）
-- 见 docs/动态业务/多租户物理隔离定稿.md、场景 8（型号对应检查内容等）

SET search_path TO dynamicbusiness, public;

CREATE TABLE IF NOT EXISTS dynamicbusiness.dynamic_model_entity_relation (
    id                      BIGSERIAL PRIMARY KEY,
    model_id                BIGINT       NOT NULL,
    model_entity_type_code  VARCHAR(64)  NOT NULL,
    entity_id               BIGINT       NOT NULL,
    entity_type_code        VARCHAR(64)  NOT NULL,
    domain                  VARCHAR(128),
    sort                    INTEGER      DEFAULT 0,
    creator                 VARCHAR(64),
    create_time             TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updater                 VARCHAR(64),
    update_time             TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted                 BOOLEAN      DEFAULT FALSE,
    tenant_id               BIGINT       NOT NULL DEFAULT 0
);

COMMENT ON TABLE dynamicbusiness.dynamic_model_entity_relation IS
    '型号—实体多对多关联（跨类型）；基表为租户物理表模板，运行时写 *_t{tenantId}';
COMMENT ON COLUMN dynamicbusiness.dynamic_model_entity_relation.model_id IS '型号 ID';
COMMENT ON COLUMN dynamicbusiness.dynamic_model_entity_relation.model_entity_type_code IS
    '型号所属实际存储类型（如 equipment）';
COMMENT ON COLUMN dynamicbusiness.dynamic_model_entity_relation.entity_id IS '挂靠实体 ID';
COMMENT ON COLUMN dynamicbusiness.dynamic_model_entity_relation.entity_type_code IS
    '挂靠实体实际存储类型（如 inspection_item）';
COMMENT ON COLUMN dynamicbusiness.dynamic_model_entity_relation.domain IS
    '实体业务域镜像（可选查询维，不参与唯一键）';
COMMENT ON COLUMN dynamicbusiness.dynamic_model_entity_relation.sort IS '同一型号上下文内排序';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_model_entity_relation_identity
    ON dynamicbusiness.dynamic_model_entity_relation (
        tenant_id, model_entity_type_code, model_id, entity_type_code, entity_id
    );

CREATE INDEX IF NOT EXISTS idx_dm_mer_model_entity_type
    ON dynamicbusiness.dynamic_model_entity_relation (
        model_id, model_entity_type_code, entity_type_code
    )
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dm_mer_entity
    ON dynamicbusiness.dynamic_model_entity_relation (entity_id, entity_type_code)
    WHERE deleted = FALSE;

-- 按现有租户拆物理表（空基表保留作 LIKE 模板）
CREATE OR REPLACE FUNCTION dynamicbusiness._v38_table_exists(p_name text)
RETURNS boolean
LANGUAGE sql
STABLE
AS $$
  SELECT EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness' AND table_name = lower(p_name)
  );
$$;

CREATE OR REPLACE FUNCTION dynamicbusiness._v38_ensure_id_seq(p_table text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  seq_name text := format('%s_id_seq', p_table);
  max_id bigint;
  has_rows boolean;
BEGIN
  IF NOT dynamicbusiness._v38_table_exists(p_table) THEN
    RETURN;
  END IF;
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness' AND table_name = p_table AND column_name = 'id'
  ) THEN
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

CREATE OR REPLACE FUNCTION dynamicbusiness._v38_split_one(p_base text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  tid bigint;
  physical text;
  has_tenant boolean;
  tenants bigint[];
BEGIN
  IF NOT dynamicbusiness._v38_table_exists(p_base) THEN
    RETURN;
  END IF;
  IF p_base ~ '_t[0-9]+$' THEN
    RETURN;
  END IF;

  SELECT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness' AND table_name = p_base AND column_name = 'tenant_id'
  ) INTO has_tenant;
  IF NOT has_tenant THEN
    RAISE NOTICE 'V38 skip % (no tenant_id)', p_base;
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
    IF NOT dynamicbusiness._v38_table_exists(physical) THEN
      EXECUTE format(
        'CREATE TABLE dynamicbusiness.%I (LIKE dynamicbusiness.%I INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES)',
        physical, p_base);
      PERFORM dynamicbusiness._v38_ensure_id_seq(physical);
      RAISE NOTICE 'V38 created %', physical;
    END IF;
  END LOOP;

  -- 基表保持空模板（若误有行则清空，避免与物理表双写）
  EXECUTE format('TRUNCATE TABLE dynamicbusiness.%I', p_base);
  PERFORM dynamicbusiness._v38_ensure_id_seq(p_base);
END;
$$;

SELECT dynamicbusiness._v38_split_one('dynamic_model_entity_relation');

DROP FUNCTION IF EXISTS dynamicbusiness._v38_split_one(text);
DROP FUNCTION IF EXISTS dynamicbusiness._v38_ensure_id_seq(text);
DROP FUNCTION IF EXISTS dynamicbusiness._v38_table_exists(text);
