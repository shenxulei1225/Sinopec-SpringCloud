-- V80: 通用 SOP 方法选用 + 实例绑定表；迁出检查专用表后删除
-- 设计：docs/superpowers/specs/2026-08-29-action-library-sop-task-tree-design.md
-- 数据搬家字面量（仅本迁移）：subject_type=inspection_item / host_type=equipment / dimension_key=execution_means
-- Java/TS 业务代码不得写死上述类型码

SET search_path TO dynamicbusiness, public;

-- ---------------------------------------------------------------------------
-- 1) 方法选用：对象实体 + 维度 → SOP 模板
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS dynamic_sop_method_binding (
  id                  BIGSERIAL PRIMARY KEY,
  tenant_id           BIGINT       NOT NULL,
  subject_type        VARCHAR(64)  NOT NULL,
  subject_id          BIGINT       NOT NULL,
  dimension_key       VARCHAR(64)  NOT NULL,
  dimension_value     VARCHAR(64)  NOT NULL,
  sop_template_id     BIGINT       NOT NULL,
  creator             VARCHAR(64),
  create_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  updater             VARCHAR(64),
  update_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  deleted             BOOLEAN      DEFAULT FALSE
);

COMMENT ON TABLE dynamic_sop_method_binding IS
  'SOP 方法选用：subject + 维度 → SOP 模板 id（is_template=true）';
COMMENT ON COLUMN dynamic_sop_method_binding.subject_type IS '对象实体类型码（请求入参；非代码写死）';
COMMENT ON COLUMN dynamic_sop_method_binding.subject_id IS '对象实体 id';
COMMENT ON COLUMN dynamic_sop_method_binding.dimension_key IS '维度字段名（如 execution_means）';
COMMENT ON COLUMN dynamic_sop_method_binding.dimension_value IS '维度取值';
COMMENT ON COLUMN dynamic_sop_method_binding.sop_template_id IS 'SOP 模板实体 id';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_sop_method_binding_identity
  ON dynamic_sop_method_binding (
    tenant_id, subject_type, subject_id, dimension_key, dimension_value
  )
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_sop_method_binding_subject
  ON dynamic_sop_method_binding (subject_type, subject_id, tenant_id)
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_sop_method_binding_template
  ON dynamic_sop_method_binding (sop_template_id, tenant_id)
  WHERE deleted = FALSE;

-- ---------------------------------------------------------------------------
-- 2) 实例绑定：宿主 + 对象 + 维度 → 独占 SOP 实例
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS dynamic_sop_instance_binding (
  id                  BIGSERIAL PRIMARY KEY,
  tenant_id           BIGINT       NOT NULL,
  host_type           VARCHAR(64)  NOT NULL,
  host_id             BIGINT       NOT NULL,
  subject_type        VARCHAR(64)  NOT NULL,
  subject_id          BIGINT       NOT NULL,
  dimension_key       VARCHAR(64)  NOT NULL,
  dimension_value     VARCHAR(64)  NOT NULL,
  sop_instance_id     BIGINT       NOT NULL,
  creator             VARCHAR(64),
  create_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  updater             VARCHAR(64),
  update_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  deleted             BOOLEAN      DEFAULT FALSE
);

COMMENT ON TABLE dynamic_sop_instance_binding IS
  'SOP 实例绑定：宿主 + 对象 + 维度 → 独占 SOP 实例 id（is_template=false）';
COMMENT ON COLUMN dynamic_sop_instance_binding.host_type IS '宿主实体类型码（请求入参）';
COMMENT ON COLUMN dynamic_sop_instance_binding.host_id IS '宿主实体 id';
COMMENT ON COLUMN dynamic_sop_instance_binding.subject_type IS '对象实体类型码';
COMMENT ON COLUMN dynamic_sop_instance_binding.subject_id IS '对象实体 id';
COMMENT ON COLUMN dynamic_sop_instance_binding.dimension_key IS '维度字段名';
COMMENT ON COLUMN dynamic_sop_instance_binding.dimension_value IS '维度取值';
COMMENT ON COLUMN dynamic_sop_instance_binding.sop_instance_id IS 'SOP 实例实体 id';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_sop_instance_binding_identity
  ON dynamic_sop_instance_binding (
    tenant_id, host_type, host_id, subject_type, subject_id, dimension_key, dimension_value
  )
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_sop_instance_binding_host
  ON dynamic_sop_instance_binding (host_type, host_id, tenant_id)
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_sop_instance_binding_subject
  ON dynamic_sop_instance_binding (subject_type, subject_id, tenant_id)
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_sop_instance_binding_instance
  ON dynamic_sop_instance_binding (sop_instance_id, tenant_id)
  WHERE deleted = FALSE;

-- ---------------------------------------------------------------------------
-- 3) 租户物理表拆分（与 V44/V75 同模式）
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION dynamicbusiness._v80_table_exists(p_name text)
RETURNS boolean
LANGUAGE sql
STABLE
AS $$
  SELECT EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness' AND table_name = lower(p_name)
  );
$$;

CREATE OR REPLACE FUNCTION dynamicbusiness._v80_ensure_id_seq(p_table text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  seq_name text := format('%s_id_seq', p_table);
  max_id bigint;
  has_rows boolean;
BEGIN
  IF NOT dynamicbusiness._v80_table_exists(p_table) THEN
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

CREATE OR REPLACE FUNCTION dynamicbusiness._v80_split_one(p_base text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  tid bigint;
  physical text;
  tenants bigint[];
BEGIN
  IF NOT dynamicbusiness._v80_table_exists(p_base) THEN
    RETURN;
  END IF;
  IF p_base ~ '_t[0-9]+$' THEN
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
    IF NOT dynamicbusiness._v80_table_exists(physical) THEN
      EXECUTE format(
        'CREATE TABLE dynamicbusiness.%I (LIKE dynamicbusiness.%I INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES)',
        physical, p_base);
      PERFORM dynamicbusiness._v80_ensure_id_seq(physical);
    END IF;
  END LOOP;

  EXECUTE format('TRUNCATE TABLE dynamicbusiness.%I', p_base);
  PERFORM dynamicbusiness._v80_ensure_id_seq(p_base);
END;
$$;

SELECT dynamicbusiness._v80_split_one('dynamic_sop_method_binding');
SELECT dynamicbusiness._v80_split_one('dynamic_sop_instance_binding');

-- ---------------------------------------------------------------------------
-- 4) 从检查专用表搬家（字面量仅本迁移）
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION dynamicbusiness._v80_migrate_method_from(p_src text, p_dst text)
RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
  IF NOT dynamicbusiness._v80_table_exists(p_src) THEN
    RETURN;
  END IF;
  IF NOT dynamicbusiness._v80_table_exists(p_dst) THEN
    RETURN;
  END IF;
  EXECUTE format($q$
    INSERT INTO dynamicbusiness.%I (
      tenant_id, subject_type, subject_id, dimension_key, dimension_value,
      sop_template_id, creator, create_time, updater, update_time, deleted
    )
    SELECT
      s.tenant_id,
      'inspection_item',
      s.inspection_item_id,
      'execution_means',
      upper(trim(s.execution_means)),
      s.sop_id,
      s.creator,
      s.create_time,
      s.updater,
      s.update_time,
      COALESCE(s.deleted, false)
    FROM (
      SELECT DISTINCT ON (tenant_id, inspection_item_id, upper(trim(execution_means)))
        *
      FROM dynamicbusiness.%I
      WHERE deleted = false
        AND execution_means IS NOT NULL
        AND trim(execution_means) <> ''
      ORDER BY tenant_id, inspection_item_id, upper(trim(execution_means)), id DESC
    ) s
  $q$, p_dst, p_src);
  PERFORM dynamicbusiness._v80_ensure_id_seq(p_dst);
END;
$$;

CREATE OR REPLACE FUNCTION dynamicbusiness._v80_migrate_instance_from(p_src text, p_dst text)
RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
  IF NOT dynamicbusiness._v80_table_exists(p_src) THEN
    RETURN;
  END IF;
  IF NOT dynamicbusiness._v80_table_exists(p_dst) THEN
    RETURN;
  END IF;
  EXECUTE format($q$
    INSERT INTO dynamicbusiness.%I (
      tenant_id, host_type, host_id, subject_type, subject_id,
      dimension_key, dimension_value, sop_instance_id,
      creator, create_time, updater, update_time, deleted
    )
    SELECT
      s.tenant_id,
      'equipment',
      s.equipment_id,
      'inspection_item',
      s.inspection_item_id,
      'execution_means',
      upper(trim(s.execution_means)),
      s.sop_instance_id,
      s.creator,
      s.create_time,
      s.updater,
      s.update_time,
      COALESCE(s.deleted, false)
    FROM (
      SELECT DISTINCT ON (
        tenant_id, equipment_id, inspection_item_id, upper(trim(execution_means))
      )
        *
      FROM dynamicbusiness.%I
      WHERE deleted = false
        AND execution_means IS NOT NULL
        AND trim(execution_means) <> ''
      ORDER BY tenant_id, equipment_id, inspection_item_id, upper(trim(execution_means)), id DESC
    ) s
  $q$, p_dst, p_src);
  PERFORM dynamicbusiness._v80_ensure_id_seq(p_dst);
END;
$$;

DO $$
DECLARE
  r record;
  dst text;
BEGIN
  -- 方法：dynamic_inspection_item_sop(+_t*)
  FOR r IN
    SELECT c.relname AS tbl
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'dynamicbusiness'
      AND c.relkind = 'r'
      AND (
        c.relname = 'dynamic_inspection_item_sop'
        OR c.relname LIKE 'dynamic_inspection_item_sop\_t%' ESCAPE '\'
      )
    ORDER BY c.relname
  LOOP
    IF r.tbl = 'dynamic_inspection_item_sop' THEN
      dst := 'dynamic_sop_method_binding';
    ELSE
      dst := replace(r.tbl, 'dynamic_inspection_item_sop', 'dynamic_sop_method_binding');
    END IF;
    IF NOT dynamicbusiness._v80_table_exists(dst) AND r.tbl ~ '_t[0-9]+$' THEN
      EXECUTE format(
        'CREATE TABLE dynamicbusiness.%I (LIKE dynamicbusiness.dynamic_sop_method_binding INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES)',
        dst);
      PERFORM dynamicbusiness._v80_ensure_id_seq(dst);
    END IF;
    PERFORM dynamicbusiness._v80_migrate_method_from(r.tbl, dst);
  END LOOP;

  -- 实例：dynamic_equipment_inspection_sop_binding(+_t*)
  FOR r IN
    SELECT c.relname AS tbl
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'dynamicbusiness'
      AND c.relkind = 'r'
      AND (
        c.relname = 'dynamic_equipment_inspection_sop_binding'
        OR c.relname LIKE 'dynamic_equipment_inspection_sop_binding\_t%' ESCAPE '\'
      )
    ORDER BY c.relname
  LOOP
    IF r.tbl = 'dynamic_equipment_inspection_sop_binding' THEN
      dst := 'dynamic_sop_instance_binding';
    ELSE
      dst := replace(r.tbl, 'dynamic_equipment_inspection_sop_binding', 'dynamic_sop_instance_binding');
    END IF;
    IF NOT dynamicbusiness._v80_table_exists(dst) AND r.tbl ~ '_t[0-9]+$' THEN
      EXECUTE format(
        'CREATE TABLE dynamicbusiness.%I (LIKE dynamicbusiness.dynamic_sop_instance_binding INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES)',
        dst);
      PERFORM dynamicbusiness._v80_ensure_id_seq(dst);
    END IF;
    PERFORM dynamicbusiness._v80_migrate_instance_from(r.tbl, dst);
  END LOOP;
END $$;

-- ---------------------------------------------------------------------------
-- 5) 删除旧表（含租户物理表），禁止双轨读
-- ---------------------------------------------------------------------------
DO $$
DECLARE
  r record;
BEGIN
  FOR r IN
    SELECT c.relname AS tbl
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'dynamicbusiness'
      AND c.relkind = 'r'
      AND (
        c.relname = 'dynamic_inspection_item_sop'
        OR c.relname LIKE 'dynamic_inspection_item_sop\_t%' ESCAPE '\'
        OR c.relname = 'dynamic_equipment_inspection_sop_binding'
        OR c.relname LIKE 'dynamic_equipment_inspection_sop_binding\_t%' ESCAPE '\'
      )
    ORDER BY c.relname
  LOOP
    EXECUTE format('DROP TABLE IF EXISTS dynamicbusiness.%I CASCADE', r.tbl);
  END LOOP;
END $$;

DROP FUNCTION IF EXISTS dynamicbusiness._v80_migrate_method_from(text, text);
DROP FUNCTION IF EXISTS dynamicbusiness._v80_migrate_instance_from(text, text);
DROP FUNCTION IF EXISTS dynamicbusiness._v80_split_one(text);
DROP FUNCTION IF EXISTS dynamicbusiness._v80_ensure_id_seq(text);
DROP FUNCTION IF EXISTS dynamicbusiness._v80_table_exists(text);
