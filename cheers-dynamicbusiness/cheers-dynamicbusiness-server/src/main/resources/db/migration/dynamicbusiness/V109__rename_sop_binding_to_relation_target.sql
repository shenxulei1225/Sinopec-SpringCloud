-- V109: 绑定模型去业务语义：subject+dimension(+host) -> targetType+targetId
-- 目标：去掉 SOP 专属物理名（表名/列名），保留既有数据并补 target_type。

SET search_path TO dynamicbusiness, public;

CREATE OR REPLACE FUNCTION dynamicbusiness._v109_table_exists(p_name text)
RETURNS boolean
LANGUAGE sql
STABLE
AS $$
  SELECT EXISTS (
    SELECT 1
    FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness'
      AND table_name = lower(p_name)
  );
$$;

DO $$
DECLARE
  r record;
  new_tbl text;
BEGIN
  -- 1) 先改方法绑定表名（含租户物理表）
  FOR r IN
    SELECT c.relname AS tbl
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'dynamicbusiness'
      AND c.relkind = 'r'
      AND (
        c.relname = 'dynamic_sop_method_binding'
        OR c.relname LIKE 'dynamic_sop_method_binding\_t%' ESCAPE '\'
      )
  LOOP
    new_tbl := replace(r.tbl, 'dynamic_sop_method_binding', 'dynamic_relation_method_binding');
    IF NOT dynamicbusiness._v109_table_exists(new_tbl) THEN
      EXECUTE format('ALTER TABLE dynamicbusiness.%I RENAME TO %I', r.tbl, new_tbl);
    END IF;
  END LOOP;

  -- 2) 再改实例绑定表名（含租户物理表）
  FOR r IN
    SELECT c.relname AS tbl
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'dynamicbusiness'
      AND c.relkind = 'r'
      AND (
        c.relname = 'dynamic_sop_instance_binding'
        OR c.relname LIKE 'dynamic_sop_instance_binding\_t%' ESCAPE '\'
      )
  LOOP
    new_tbl := replace(r.tbl, 'dynamic_sop_instance_binding', 'dynamic_relation_instance_binding');
    IF NOT dynamicbusiness._v109_table_exists(new_tbl) THEN
      EXECUTE format('ALTER TABLE dynamicbusiness.%I RENAME TO %I', r.tbl, new_tbl);
    END IF;
  END LOOP;
END $$;

DO $$
DECLARE
  r record;
  idx_name text;
BEGIN
  -- 3) 方法绑定：sop_template_id -> target_id，并补 target_type
  FOR r IN
    SELECT c.relname AS tbl
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'dynamicbusiness'
      AND c.relkind = 'r'
      AND (
        c.relname = 'dynamic_relation_method_binding'
        OR c.relname LIKE 'dynamic_relation_method_binding\_t%' ESCAPE '\'
      )
    ORDER BY c.relname
  LOOP
    IF EXISTS (
      SELECT 1
      FROM information_schema.columns
      WHERE table_schema = 'dynamicbusiness'
        AND table_name = r.tbl
        AND column_name = 'sop_template_id'
    ) THEN
      EXECUTE format('ALTER TABLE dynamicbusiness.%I RENAME COLUMN sop_template_id TO target_id', r.tbl);
    END IF;
    EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS target_type VARCHAR(64)', r.tbl);
    EXECUTE format('UPDATE dynamicbusiness.%I SET target_type = %L WHERE target_type IS NULL OR trim(target_type) = %L', r.tbl, 'sop', '');
    EXECUTE format('ALTER TABLE dynamicbusiness.%I ALTER COLUMN target_type SET NOT NULL', r.tbl);

    idx_name := format('idx_%s_target', r.tbl);
    EXECUTE format(
      'CREATE INDEX IF NOT EXISTS %I ON dynamicbusiness.%I (target_type, target_id, tenant_id) WHERE deleted = FALSE',
      idx_name, r.tbl
    );
  END LOOP;

  -- 4) 实例绑定：sop_instance_id -> target_id，并补 target_type
  FOR r IN
    SELECT c.relname AS tbl
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'dynamicbusiness'
      AND c.relkind = 'r'
      AND (
        c.relname = 'dynamic_relation_instance_binding'
        OR c.relname LIKE 'dynamic_relation_instance_binding\_t%' ESCAPE '\'
      )
    ORDER BY c.relname
  LOOP
    IF EXISTS (
      SELECT 1
      FROM information_schema.columns
      WHERE table_schema = 'dynamicbusiness'
        AND table_name = r.tbl
        AND column_name = 'sop_instance_id'
    ) THEN
      EXECUTE format('ALTER TABLE dynamicbusiness.%I RENAME COLUMN sop_instance_id TO target_id', r.tbl);
    END IF;
    EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS target_type VARCHAR(64)', r.tbl);
    EXECUTE format('UPDATE dynamicbusiness.%I SET target_type = %L WHERE target_type IS NULL OR trim(target_type) = %L', r.tbl, 'sop', '');
    EXECUTE format('ALTER TABLE dynamicbusiness.%I ALTER COLUMN target_type SET NOT NULL', r.tbl);

    idx_name := format('idx_%s_target', r.tbl);
    EXECUTE format(
      'CREATE INDEX IF NOT EXISTS %I ON dynamicbusiness.%I (target_type, target_id, tenant_id) WHERE deleted = FALSE',
      idx_name, r.tbl
    );
  END LOOP;
END $$;

COMMENT ON TABLE dynamicbusiness.dynamic_relation_method_binding IS
  '通用方法绑定：subject + 维度 -> targetType + targetId';
COMMENT ON TABLE dynamicbusiness.dynamic_relation_instance_binding IS
  '通用宿主绑定：host + subject + 维度 -> targetType + targetId（目标独占宿主）';

DROP FUNCTION IF EXISTS dynamicbusiness._v109_table_exists(text);
