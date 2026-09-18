-- ============================================================================
-- recipes/inspection · 22 审计：宿主参数包 targetType=sop 错位键（只读）
--
-- 背景（P2b）：检查方法权威在检查项；宿主包条目 target 应指向 inspection_item 自身。
-- 脚本 14 曾写入 targetType=sop。本审计只统计，不改库。
-- ============================================================================

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  r record;
  v_sop bigint;
  v_item bigint;
  v_other bigint;
BEGIN
  RAISE NOTICE '=== P2b audit: host_sop_param_pack targetType ===';

  FOR r IN
    SELECT c.relname AS tbl
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'dynamicbusiness'
      AND c.relkind = 'r'
      AND c.relname ~ '^ent_equipment(_t[0-9]+)?$'
    ORDER BY c.relname
  LOOP
    EXECUTE format($q$
      SELECT
        COUNT(*) FILTER (WHERE e ->> 'targetType' = 'sop'),
        COUNT(*) FILTER (WHERE e ->> 'targetType' = 'inspection_item'),
        COUNT(*) FILTER (
          WHERE COALESCE(e ->> 'targetType', '') NOT IN ('sop', 'inspection_item', '')
        )
      FROM dynamicbusiness.%I eq
      CROSS JOIN LATERAL jsonb_array_elements(
        CASE
          WHEN jsonb_typeof(eq.host_sop_param_pack -> 'entries') = 'array'
            THEN eq.host_sop_param_pack -> 'entries'
          ELSE '[]'::jsonb
        END
      ) e
      WHERE eq.deleted = FALSE
        AND eq.host_sop_param_pack IS NOT NULL
    $q$, r.tbl)
    INTO v_sop, v_item, v_other;

    RAISE NOTICE 'table=% target_sop=% target_inspection_item=% other=%',
      r.tbl, v_sop, v_item, v_other;
  END LOOP;
END $$;
