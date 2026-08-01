-- V42: 实体通用列 code 租户内唯一
-- 1) 重复 code：保留最小 id 行原值，其余改为 {table}-{id}-{uuid} 保证可建唯一索引
-- 2) 为所有带 code 列的 ent_*（含模板与 _t{n}）补条件唯一索引
-- 口径与既有 facility/zone/point：UNIQUE (code, tenant_id) WHERE deleted=false AND code IS NOT NULL

SET search_path TO dynamicbusiness, public;

CREATE OR REPLACE FUNCTION dynamicbusiness._v42_dedupe_ent_codes(p_table text)
RETURNS integer
LANGUAGE plpgsql
AS $$
DECLARE
  updated integer := 0;
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness' AND table_name = lower(p_table)
  ) THEN
    RETURN 0;
  END IF;
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness' AND table_name = lower(p_table) AND column_name = 'code'
  ) THEN
    RETURN 0;
  END IF;

  EXECUTE format(
    $sql$
    UPDATE dynamicbusiness.%I e
    SET code = lower(%L) || '-' || e.id::text || '-' || replace(gen_random_uuid()::text, '-', '')
    WHERE e.deleted = false
      AND e.code IS NOT NULL
      AND btrim(e.code) <> ''
      AND EXISTS (
        SELECT 1
        FROM dynamicbusiness.%I o
        WHERE o.deleted = false
          AND o.code IS NOT NULL
          AND o.code = e.code
          AND o.id < e.id
      )
    $sql$,
    p_table, p_table, p_table
  );
  GET DIAGNOSTICS updated = ROW_COUNT;
  RETURN updated;
END;
$$;

CREATE OR REPLACE FUNCTION dynamicbusiness._v42_ensure_code_unique(p_table text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  idx_name text;
  has_tenant boolean;
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness' AND table_name = lower(p_table)
  ) THEN
    RETURN;
  END IF;
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness' AND table_name = lower(p_table) AND column_name = 'code'
  ) THEN
    RETURN;
  END IF;

  SELECT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness' AND table_name = lower(p_table) AND column_name = 'tenant_id'
  ) INTO has_tenant;

  idx_name := 'uk_' || lower(p_table) || '_code_tenant';

  IF EXISTS (
    SELECT 1 FROM pg_indexes
    WHERE schemaname = 'dynamicbusiness'
      AND tablename = lower(p_table)
      AND (
        indexname = idx_name
        OR indexdef ILIKE '%%UNIQUE%%(code, tenant_id)%%'
        OR indexdef ILIKE '%%UNIQUE%%(code)%%'
      )
  ) THEN
    RETURN;
  END IF;

  IF has_tenant THEN
    EXECUTE format(
      'CREATE UNIQUE INDEX IF NOT EXISTS %I ON dynamicbusiness.%I USING btree (code, tenant_id) WHERE deleted = false AND code IS NOT NULL',
      idx_name, p_table
    );
  ELSE
    EXECUTE format(
      'CREATE UNIQUE INDEX IF NOT EXISTS %I ON dynamicbusiness.%I USING btree (code) WHERE deleted = false AND code IS NOT NULL',
      idx_name, p_table
    );
  END IF;
END;
$$;

DO $$
DECLARE
  r record;
  n integer;
BEGIN
  FOR r IN
    SELECT c.table_name
    FROM information_schema.columns c
    WHERE c.table_schema = 'dynamicbusiness'
      AND c.table_name LIKE 'ent\_%' ESCAPE '\'
      AND c.column_name = 'code'
    ORDER BY c.table_name
  LOOP
    n := dynamicbusiness._v42_dedupe_ent_codes(r.table_name);
    IF n > 0 THEN
      RAISE NOTICE 'V42 deduped % rows on %', n, r.table_name;
    END IF;
    PERFORM dynamicbusiness._v42_ensure_code_unique(r.table_name);
  END LOOP;
END
$$;

DROP FUNCTION IF EXISTS dynamicbusiness._v42_dedupe_ent_codes(text);
DROP FUNCTION IF EXISTS dynamicbusiness._v42_ensure_code_unique(text);
