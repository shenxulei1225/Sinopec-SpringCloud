-- ============================================================================
-- catalog-orchestration · 05 P2 批量回填
-- 为尚未有 dm_catalog_orchestration 行的目录补默认编排头（幂等；不覆盖已有行）
-- 分类目录：点树即当前对象；其余：点列表这一行。
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
  r RECORD;
  v_pick text;
BEGIN
  FOR r IN
    SELECT et.code, et.entry_kind
    FROM dynamic_entity_type et
    WHERE et.tenant_id = v_tenant
      AND et.deleted = false
      AND NOT EXISTS (
        SELECT 1 FROM dm_catalog_orchestration o
        WHERE o.entity_type_code = et.code
          AND o.tenant_id = v_tenant
          AND o.deleted = false
      )
    ORDER BY et.code
  LOOP
    v_pick := CASE
      WHEN r.entry_kind = 'CATEGORY' THEN 'CATEGORY_NODE'
      ELSE 'LIST_ROW'
    END;
    RAISE NOTICE 'backfill catalog orchestration: % (pick=%)', r.code, v_pick;
    PERFORM dynamicbusiness._seed_catalog_orchestration(v_tenant, r.code, v_pick);
  END LOOP;
END $$;
