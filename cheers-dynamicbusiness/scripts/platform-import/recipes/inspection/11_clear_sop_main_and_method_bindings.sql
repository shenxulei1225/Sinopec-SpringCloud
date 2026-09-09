-- ============================================================================
-- recipes/inspection · 11 清空 SOP 主表与旧方法绑定（手工执行，破坏性）
--
-- 目标：
--   1) 清空 SOP 主表（ent_sop / ent_sop_t*）现有可见数据；
--   2) 清空旧“检查项 -> SOP”方法绑定（dynamic_sop_method_binding / _t*）。
--
-- 说明：
--   - 采用软删（deleted=true），保留回溯线索；
--   - 不删除表结构；
--   - 不触碰 dynamic_sop_scope_rule / dynamic_sop_item_pack（如需一并清空可另加脚本）。
-- ============================================================================

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  r record;
  v_rows bigint;
BEGIN
  -- 1) 清空 SOP 主表（遍历 ent_sop 与 ent_sop_t*）
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
          updater = 'recipe-inspection-clear-sop-main',
          update_time = CURRENT_TIMESTAMP
      WHERE deleted = FALSE
    $q$, r.tbl);
    GET DIAGNOSTICS v_rows = ROW_COUNT;
    RAISE NOTICE 'clear sop main: table=% rows=%', r.tbl, v_rows;
  END LOOP;

  -- 2) 清空旧方法绑定（遍历 dynamic_sop_method_binding 与 _t*）
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
    ORDER BY c.relname
  LOOP
    EXECUTE format($q$
      UPDATE dynamicbusiness.%I
      SET deleted = TRUE,
          updater = 'recipe-inspection-clear-sop-main',
          update_time = CURRENT_TIMESTAMP
      WHERE deleted = FALSE
    $q$, r.tbl);
    GET DIAGNOSTICS v_rows = ROW_COUNT;
    RAISE NOTICE 'clear method bindings: table=% rows=%', r.tbl, v_rows;
  END LOOP;
END $$;
