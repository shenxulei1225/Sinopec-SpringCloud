-- V37: 实体专用表 + 运行时关联表按租户物理隔离（_t{tenantId})
-- 见 docs/动态业务/多租户物理隔离定稿.md
-- 无后缀基表搬数后 TRUNCATE，保留为空壳模板供 CREATE TABLE … LIKE

SET search_path TO dynamicbusiness, public;

CREATE OR REPLACE FUNCTION dynamicbusiness._v37_table_exists(p_name text)
RETURNS boolean
LANGUAGE sql
STABLE
AS $$
  SELECT EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness' AND table_name = lower(p_name)
  );
$$;

CREATE OR REPLACE FUNCTION dynamicbusiness._v37_ensure_id_seq(p_table text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  seq_name text := format('%s_id_seq', p_table);
  max_id bigint;
  has_rows boolean;
BEGIN
  IF NOT dynamicbusiness._v37_table_exists(p_table) THEN
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

CREATE OR REPLACE FUNCTION dynamicbusiness._v37_split_one(p_base text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  tid bigint;
  physical text;
  src_cnt bigint;
  dst_cnt bigint;
  has_tenant boolean;
  tenants bigint[];
BEGIN
  IF NOT dynamicbusiness._v37_table_exists(p_base) THEN
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
    RAISE NOTICE 'V37 skip % (no tenant_id)', p_base;
    RETURN;
  END IF;

  EXECUTE format(
    $f$
    SELECT ARRAY(
      SELECT DISTINCT tenant_id FROM (
        SELECT tenant_id FROM dynamicbusiness.%I
         WHERE tenant_id IS NOT NULL AND tenant_id > 0
        UNION
        -- 凡有业务类型的租户都建空壳分表（含无数据的关联表）
        SELECT tenant_id FROM dynamicbusiness.dynamic_entity_type
         WHERE deleted = false AND tenant_id IS NOT NULL AND tenant_id > 0
        UNION
        SELECT tenant_id FROM dynamicbusiness.dynamic_dynamic_table
         WHERE deleted = false AND tenant_id IS NOT NULL AND tenant_id > 0
           AND (table_name = %L OR table_name LIKE (%L || '_t%%'))
      ) u
      ORDER BY 1
    )
    $f$,
    p_base, p_base, p_base)
    INTO tenants;

  IF tenants IS NULL OR coalesce(array_length(tenants, 1), 0) = 0 THEN
    RAISE NOTICE 'V37 skip % (no tenants)', p_base;
    RETURN;
  END IF;

  FOREACH tid IN ARRAY tenants LOOP
    physical := p_base || '_t' || tid::text;
    IF NOT dynamicbusiness._v37_table_exists(physical) THEN
      EXECUTE format(
        'CREATE TABLE dynamicbusiness.%I (LIKE dynamicbusiness.%I INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES)',
        physical, p_base);
      RAISE NOTICE 'V37 created %', physical;
    END IF;

    EXECUTE format(
      'INSERT INTO dynamicbusiness.%I
       SELECT s.* FROM dynamicbusiness.%I s
       WHERE s.tenant_id = %s
         AND NOT EXISTS (SELECT 1 FROM dynamicbusiness.%I d WHERE d.id = s.id)',
      physical, p_base, tid, physical);

    EXECUTE format('SELECT COUNT(*) FROM dynamicbusiness.%I WHERE tenant_id = %s', p_base, tid)
      INTO src_cnt;
    EXECUTE format('SELECT COUNT(*) FROM dynamicbusiness.%I WHERE tenant_id = %s', physical, tid)
      INTO dst_cnt;
    IF src_cnt <> dst_cnt THEN
      RAISE EXCEPTION 'V37 row count mismatch: % tenant=% src=% dst=%', p_base, tid, src_cnt, dst_cnt;
    END IF;
    PERFORM dynamicbusiness._v37_ensure_id_seq(physical);
  END LOOP;

  EXECUTE format('TRUNCATE TABLE dynamicbusiness.%I', p_base);
  PERFORM dynamicbusiness._v37_ensure_id_seq(p_base);
  RAISE NOTICE 'V37 truncated template %', p_base;
END;
$$;

-- 1) 拆分全部 ent_* 共享表
DO $$
DECLARE
  r record;
BEGIN
  FOR r IN
    SELECT table_name
    FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness'
      AND table_type = 'BASE TABLE'
      AND table_name LIKE 'ent\_%' ESCAPE '\'
      AND table_name !~ '_t[0-9]+$'
    ORDER BY table_name
  LOOP
    PERFORM dynamicbusiness._v37_split_one(r.table_name);
  END LOOP;
END $$;

-- 2) 拆分运行时关联表
SELECT dynamicbusiness._v37_split_one('dynamic_entity_relation');
SELECT dynamicbusiness._v37_split_one('dynamic_entity_category_relation');
SELECT dynamicbusiness._v37_split_one('dynamic_category_entity_link');
SELECT dynamicbusiness._v37_split_one('dynamic_entity_field_index');
SELECT dynamicbusiness._v37_split_one('dynamic_model_category_relation');
SELECT dynamicbusiness._v37_split_one('dynamic_entity_access_permission');
SELECT dynamicbusiness._v37_split_one('dynamic_entity_field_permission');
SELECT dynamicbusiness._v37_split_one('dynamic_entity_operation_permission');
SELECT dynamicbusiness._v37_split_one('dynamic_precomputed_value');
SELECT dynamicbusiness._v37_split_one('dynamic_category_user_relation');
SELECT dynamicbusiness._v37_split_one('dynamic_category_permission');

-- 3) 元数据补租户后缀
UPDATE dynamicbusiness.dynamic_entity_type et
SET dedicated_table_name = dedicated_table_name || '_t' || et.tenant_id::text
WHERE et.deleted = false
  AND et.dedicated_table_name IS NOT NULL
  AND btrim(et.dedicated_table_name) <> ''
  AND et.dedicated_table_name !~ '_t[0-9]+$'
  AND et.tenant_id IS NOT NULL
  AND et.tenant_id > 0;

UPDATE dynamicbusiness.dynamic_dynamic_table dt
SET table_name = table_name || '_t' || dt.tenant_id::text
WHERE dt.deleted = false
  AND dt.table_name IS NOT NULL
  AND btrim(dt.table_name) <> ''
  AND dt.table_name !~ '_t[0-9]+$'
  AND dt.tenant_id IS NOT NULL
  AND dt.tenant_id > 0;

-- 4) 清理辅助函数
DROP FUNCTION IF EXISTS dynamicbusiness._v37_split_one(text);
DROP FUNCTION IF EXISTS dynamicbusiness._v37_ensure_id_seq(text);
DROP FUNCTION IF EXISTS dynamicbusiness._v37_table_exists(text);
