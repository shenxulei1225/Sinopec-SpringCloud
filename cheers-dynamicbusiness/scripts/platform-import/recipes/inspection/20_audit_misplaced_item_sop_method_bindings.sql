-- ============================================================================
-- recipes/inspection · 20 审计：错位「检查项→SOP」方法绑定（只读）
--
-- 背景（巡检试点 P2）：
--   脚本 14（creator=recipe-inspection-bulk）把「怎么查」写成了项→SOP 方法绑定。
--   定稿权威：怎么查 = 检查项 action_tree_json；本审计只统计废数据，不改库。
--
-- 兼容：
--   - 未跑 V109：dynamic_sop_method_binding(_t*)
--   - 已跑 V109：dynamic_relation_method_binding(_t*)
-- ============================================================================

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  r record;
  v_live bigint;
  v_bulk bigint;
  v_other bigint;
BEGIN
  RAISE NOTICE '=== P2 audit: misplaced inspection_item method bindings ===';

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
      SELECT
        COUNT(*) FILTER (WHERE deleted = FALSE AND subject_type = 'inspection_item'),
        COUNT(*) FILTER (
          WHERE deleted = FALSE
            AND subject_type = 'inspection_item'
            AND creator IN ('recipe-inspection-bulk', 'recipe-inspection', 'recipe-inspection-demo')
        ),
        COUNT(*) FILTER (
          WHERE deleted = FALSE
            AND subject_type = 'inspection_item'
            AND creator NOT IN ('recipe-inspection-bulk', 'recipe-inspection', 'recipe-inspection-demo')
        )
      FROM dynamicbusiness.%I
    $q$, r.tbl)
    INTO v_live, v_bulk, v_other;

    RAISE NOTICE 'table=% live_item_bindings=% recipe_creators=% other_creators=%',
      r.tbl, v_live, v_bulk, v_other;
  END LOOP;

  RAISE NOTICE 'host_sop_param_pack(targetType=sop) 本轮不自动清；开跑可回退同 subject 条目。需重键请另开方案。';
END $$;
