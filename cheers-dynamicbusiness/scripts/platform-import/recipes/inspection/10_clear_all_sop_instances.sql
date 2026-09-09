-- ============================================================================
-- recipes/inspection · 10 清理现有 SOP 实例（手工执行，破坏性）
--
-- 背景：当前存量 SOP 实例主要按“检查项 × 手段”派生，不符合后续“大范围标准 SOP”口径。
-- 目标：清空全部 SOP 实例与实例绑定，保留 SOP 基础能力，便于按新口径重建。
--
-- 清理范围：
--   1) ent_sop(_t*) 中实例行（is_template=false 或 sop_template_id 非空）
--   2) dynamic_sop_instance_binding(_t*) 全量绑定行
--
-- 不清理：
--   - dynamic_sop_method_binding（方法选用）；
--   - SOP 标准包表（dynamic_sop_scope_rule / dynamic_sop_item_pack）。
-- ============================================================================

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  r record;
  v_rows bigint;
BEGIN
  -- 1) 软删全部 SOP 实例行（遍历 ent_sop 与 ent_sop_t*）
  FOR r IN
    SELECT c.relname AS tbl
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'dynamicbusiness'
      AND c.relkind = 'r'
      AND c.relname ~ '^ent_sop(_t[0-9]+)?$'
    ORDER BY c.relname
  LOOP
    EXECUTE format($q$
      UPDATE dynamicbusiness.%I
      SET deleted = TRUE,
          updater = 'recipe-inspection-clear-instances',
          update_time = CURRENT_TIMESTAMP
      WHERE deleted = FALSE
        AND (
          COALESCE(is_template, FALSE) = FALSE
          OR sop_template_id IS NOT NULL
        )
    $q$, r.tbl);
    GET DIAGNOSTICS v_rows = ROW_COUNT;
    RAISE NOTICE 'clear sop instances: table=% rows=%', r.tbl, v_rows;
  END LOOP;

  -- 2) 软删全部实例绑定（遍历 dynamic_sop_instance_binding 与 _t*）
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
    ORDER BY c.relname
  LOOP
    EXECUTE format($q$
      UPDATE dynamicbusiness.%I
      SET deleted = TRUE,
          updater = 'recipe-inspection-clear-instances',
          update_time = CURRENT_TIMESTAMP
      WHERE deleted = FALSE
    $q$, r.tbl);
    GET DIAGNOSTICS v_rows = ROW_COUNT;
    RAISE NOTICE 'clear instance bindings: table=% rows=%', r.tbl, v_rows;
  END LOOP;
END $$;
