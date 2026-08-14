-- V55: 移除 inspection_method / method_template_id 遗留
-- 顺序：补齐 SOP 实体表 → 从旧 method 模板迁移 SOP 实例 → 删除旧表与列

SET search_path TO dynamicbusiness, public;

-- 0) 补齐 SOP 实体表（从旧 V43 升级的库）
CREATE SEQUENCE IF NOT EXISTS ent_field_work_standard_id_seq;

CREATE TABLE IF NOT EXISTS ent_field_work_standard (
  id                   BIGINT PRIMARY KEY DEFAULT nextval('ent_field_work_standard_id_seq'::regclass),
  tenant_id            BIGINT NOT NULL DEFAULT 0,
  entity_type_code     VARCHAR(64) DEFAULT 'field_work_standard',
  model_id             BIGINT NOT NULL,
  domain               VARCHAR(128),
  name                 VARCHAR(255) NOT NULL,
  code                 VARCHAR(100),
  parent_id            BIGINT DEFAULT 0,
  tree_path            VARCHAR(500),
  sort                 INTEGER DEFAULT 0,
  status               INTEGER DEFAULT 1,
  attrs                JSONB DEFAULT '{}'::jsonb,
  custom_fields        JSONB DEFAULT '{}'::jsonb,
  version_no           INTEGER NOT NULL DEFAULT 1,
  publish_status       VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  steps_json           JSONB NOT NULL DEFAULT '[]'::jsonb,
  creator              VARCHAR(64),
  create_time          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updater              VARCHAR(64),
  update_time          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted              BOOLEAN DEFAULT FALSE
);

DO $$
DECLARE
  tid bigint;
  physical text;
  tenants bigint[];
BEGIN
  tenants := ARRAY(
    SELECT DISTINCT tenant_id
    FROM dynamicbusiness.dynamic_entity_type
    WHERE deleted = false AND tenant_id IS NOT NULL AND tenant_id > 0
    ORDER BY 1
  );
  IF tenants IS NULL OR array_length(tenants, 1) IS NULL THEN
    tenants := ARRAY[1]::bigint[];
  END IF;

  FOREACH tid IN ARRAY tenants
  LOOP
    physical := 'ent_field_work_standard_t' || tid::text;
    IF NOT EXISTS (
      SELECT 1 FROM information_schema.tables
      WHERE table_schema = 'dynamicbusiness' AND table_name = physical
    ) AND EXISTS (
      SELECT 1 FROM information_schema.tables
      WHERE table_schema = 'dynamicbusiness' AND table_name = 'ent_field_work_standard'
    ) THEN
      EXECUTE format(
        'CREATE TABLE dynamicbusiness.%I (LIKE dynamicbusiness.ent_field_work_standard INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES)',
        physical);
    END IF;
  END LOOP;
END $$;

-- 0b) 旧 method 模板 → SOP 实体（检查项—SOP 关联走矩阵 entity_relation，不在此迁移）
DO $$
DECLARE
  sop_model_id bigint;
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness' AND table_name = 'ent_inspection_method_t1'
  ) THEN
    RETURN;
  END IF;

  SELECT m.id INTO sop_model_id
  FROM dynamicbusiness.dynamic_model m
  WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'field_work_standard'
  LIMIT 1;

  IF sop_model_id IS NULL THEN
    RAISE NOTICE 'V55 skip method→SOP data copy: field_work_standard model not seeded yet';
    RETURN;
  END IF;

  INSERT INTO dynamicbusiness.ent_field_work_standard_t1 (
    tenant_id, entity_type_code, model_id, name, code, status,
    version_no, publish_status, steps_json, creator, deleted
  )
  SELECT
    im.tenant_id,
    'field_work_standard',
    sop_model_id,
    regexp_replace(im.name, '模板$', ''),
    'SOP-' || im.code,
    im.status,
    1,
    'PUBLISHED',
    jsonb_build_array(
      jsonb_build_object('code', 'step-1', 'order', 1, 'title', '执行检查', 'description', im.name)
    ),
    'v55-migrate',
    false
  FROM dynamicbusiness.ent_inspection_method_t1 im
  WHERE im.deleted = false
    AND im.is_template = true
    AND im.tenant_id = 1
    AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.ent_field_work_standard_t1 s
      WHERE s.deleted = false AND s.tenant_id = 1 AND s.code = 'SOP-' || im.code
    );

END $$;

-- 1) 检查内容表去掉 method_template_id
DO $$
DECLARE
  tbl text;
BEGIN
  FOR tbl IN
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_type = 'BASE TABLE'
      AND (
        t.table_name = 'ent_inspection_item'
        OR t.table_name LIKE 'ent\_inspection\_item\_t%' ESCAPE '\'
      )
    ORDER BY 1
  LOOP
    IF EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = 'dynamicbusiness'
        AND c.table_name = tbl
        AND c.column_name = 'method_template_id'
    ) THEN
      EXECUTE format('DROP INDEX IF EXISTS dynamicbusiness.idx_%s_method_template_id', tbl);
      EXECUTE format(
        'ALTER TABLE dynamicbusiness.%I DROP COLUMN IF EXISTS method_template_id',
        tbl
      );
    END IF;
  END LOOP;
END $$;

-- 2) 删除 inspection_method 专用表（模板表 + 租户分表）
DO $$
DECLARE
  r record;
BEGIN
  FOR r IN
    SELECT tablename
    FROM pg_tables
    WHERE schemaname = 'dynamicbusiness'
      AND (
        tablename = 'ent_inspection_method'
        OR tablename LIKE 'ent\_inspection\_method\_t%' ESCAPE '\'
      )
  LOOP
    EXECUTE format('DROP TABLE IF EXISTS dynamicbusiness.%I CASCADE', r.tablename);
  END LOOP;
END $$;

DROP SEQUENCE IF EXISTS dynamicbusiness.ent_inspection_method_id_seq;

DO $$
DECLARE
  r record;
BEGIN
  FOR r IN
    SELECT sequence_name
    FROM information_schema.sequences
    WHERE sequence_schema = 'dynamicbusiness'
      AND sequence_name LIKE 'ent\_inspection\_method\_t%\_id\_seq' ESCAPE '\'
  LOOP
    EXECUTE format('DROP SEQUENCE IF EXISTS dynamicbusiness.%I', r.sequence_name);
  END LOOP;
END $$;
