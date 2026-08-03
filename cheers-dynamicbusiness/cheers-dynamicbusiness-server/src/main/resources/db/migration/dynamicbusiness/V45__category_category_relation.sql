-- V45: 分类—分类跨种类关联（如学习主题 → 设备分类可见枝）
-- 同种类层级仍用 dynamic_category.parent_id，禁止写入本表
-- 物理隔离：无后缀基表为空模板；现有租户拆为 *_t{tenantId}（与 V38 同口径）

SET search_path TO dynamicbusiness, public;

CREATE TABLE IF NOT EXISTS dynamicbusiness.dynamic_category_category_relation (
    id                         BIGSERIAL PRIMARY KEY,
    host_category_id           BIGINT       NOT NULL,
    host_category_type_code    VARCHAR(64)  NOT NULL,
    member_category_id         BIGINT       NOT NULL,
    member_category_type_code  VARCHAR(64)  NOT NULL,
    sort                       INTEGER      DEFAULT 0,
    creator                    VARCHAR(64),
    create_time                TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updater                    VARCHAR(64),
    update_time                TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted                    BOOLEAN      DEFAULT FALSE,
    tenant_id                  BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT ck_dccr_cross_type CHECK (host_category_type_code <> member_category_type_code)
);

COMMENT ON TABLE dynamicbusiness.dynamic_category_category_relation IS
    '分类—分类跨种类关联（宿主看成员）；同种类禁止，须用 parent_id；基表为租户物理表模板';
COMMENT ON COLUMN dynamicbusiness.dynamic_category_category_relation.host_category_id IS
    '宿主分类节点 ID（如学习主题）';
COMMENT ON COLUMN dynamicbusiness.dynamic_category_category_relation.host_category_type_code IS
    '宿主分类种类编码（如 inspection_item）';
COMMENT ON COLUMN dynamicbusiness.dynamic_category_category_relation.member_category_id IS
    '成员分类节点 ID（如允许展示的设备分类）';
COMMENT ON COLUMN dynamicbusiness.dynamic_category_category_relation.member_category_type_code IS
    '成员分类种类编码（如 equipment）；必须与宿主种类不同';
COMMENT ON COLUMN dynamicbusiness.dynamic_category_category_relation.sort IS
    '同一宿主下成员排序';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_category_category_relation_identity
    ON dynamicbusiness.dynamic_category_category_relation (
        tenant_id,
        host_category_type_code,
        host_category_id,
        member_category_type_code,
        member_category_id
    );

CREATE INDEX IF NOT EXISTS idx_dccr_host
    ON dynamicbusiness.dynamic_category_category_relation (
        host_category_id, host_category_type_code, member_category_type_code
    )
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dccr_member
    ON dynamicbusiness.dynamic_category_category_relation (
        member_category_id, member_category_type_code
    )
    WHERE deleted = FALSE;

CREATE OR REPLACE FUNCTION dynamicbusiness._v45_table_exists(p_name text)
RETURNS boolean
LANGUAGE sql
STABLE
AS $$
  SELECT EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness' AND table_name = lower(p_name)
  );
$$;

CREATE OR REPLACE FUNCTION dynamicbusiness._v45_ensure_id_seq(p_table text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  seq_name text := format('%s_id_seq', p_table);
  max_id bigint;
  has_rows boolean;
BEGIN
  IF NOT dynamicbusiness._v45_table_exists(p_table) THEN
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

CREATE OR REPLACE FUNCTION dynamicbusiness._v45_split_one(p_base text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  tid bigint;
  physical text;
  has_tenant boolean;
  tenants bigint[];
BEGIN
  IF NOT dynamicbusiness._v45_table_exists(p_base) THEN
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
    RAISE NOTICE 'V45 skip % (no tenant_id)', p_base;
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
    IF NOT dynamicbusiness._v45_table_exists(physical) THEN
      EXECUTE format(
        'CREATE TABLE dynamicbusiness.%I (LIKE dynamicbusiness.%I INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES)',
        physical, p_base);
      PERFORM dynamicbusiness._v45_ensure_id_seq(physical);
      RAISE NOTICE 'V45 created %', physical;
    END IF;
  END LOOP;

  EXECUTE format('TRUNCATE TABLE dynamicbusiness.%I', p_base);
  PERFORM dynamicbusiness._v45_ensure_id_seq(p_base);
END;
$$;

SELECT dynamicbusiness._v45_split_one('dynamic_category_category_relation');

DROP FUNCTION IF EXISTS dynamicbusiness._v45_split_one(text);
DROP FUNCTION IF EXISTS dynamicbusiness._v45_ensure_id_seq(text);
DROP FUNCTION IF EXISTS dynamicbusiness._v45_table_exists(text);
