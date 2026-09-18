-- ============================================================================
-- recipes/inspection · 21 定点软删：错位「检查项→SOP」方法绑定（破坏性，手工执行）
--
-- 目标（巡检试点 P2）：
--   软删由配方写入的 inspection_item→SOP 方法绑定（不再作为怎么查权威）。
--
-- 范围（必须同时满足）：
--   - subject_type = 'inspection_item'
--   - creator IN ('recipe-inspection-bulk', 'recipe-inspection', 'recipe-inspection-demo')
--   - deleted = false
--
-- 明确不做：
--   - 不跑 11（不清 SOP 主表）
--   - 不改 host_sop_param_pack（键语义另案）
--   - 不删其它 creator 的方法绑定
--
-- 兼容表名：dynamic_sop_method_binding* 与 dynamic_relation_method_binding*
-- updater 标记：recipe-inspection-p2-clear-misplaced-bindings
-- ============================================================================

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  r record;
  v_rows bigint;
  v_total bigint := 0;
BEGIN
  FOR r IN
    SELECT c.relname AS tbl
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'dynamicbusiness'
      AND c.relkind = 'r'
      AND (
        c.relname ~ '^dynamic_sop_method_binding(_t[0-9]+)?$'
        OR c.relname ~ '^dynamic_relation_method_binding(_t[0-9]+)?$'
      )
    ORDER BY c.relname
  LOOP
    EXECUTE format($q$
      UPDATE dynamicbusiness.%I
      SET deleted = TRUE,
          updater = 'recipe-inspection-p2-clear-misplaced-bindings',
          update_time = CURRENT_TIMESTAMP
      WHERE deleted = FALSE
        AND subject_type = 'inspection_item'
        AND creator IN (
          'recipe-inspection-bulk',
          'recipe-inspection',
          'recipe-inspection-demo'
        )
    $q$, r.tbl);
    GET DIAGNOSTICS v_rows = ROW_COUNT;
    v_total := v_total + v_rows;
    RAISE NOTICE 'cleared misplaced item→sop bindings: table=% rows=%', r.tbl, v_rows;
  END LOOP;

  RAISE NOTICE 'P2 clear done. total_rows=%', v_total;
END $$;
