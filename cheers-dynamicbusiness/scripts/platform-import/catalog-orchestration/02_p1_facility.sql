-- ============================================================================
-- catalog-orchestration · 02 P1 facility
-- 编排头：分类即对象（点树节点即当前对象）。开区域筛选栏 / 站场分类栏认布局。
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'facility' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip P1 facility: dynamic_entity_type 不存在';
    RETURN;
  END IF;

  PERFORM _seed_catalog_orchestration(v_tenant, 'facility', 'CATEGORY_NODE');
END $$;
