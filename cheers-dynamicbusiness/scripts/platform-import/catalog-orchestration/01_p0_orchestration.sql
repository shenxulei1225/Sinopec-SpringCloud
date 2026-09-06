-- ============================================================================
-- catalog-orchestration · 01 P0 编排 seed
-- 只写编排头：点树还是点列表行。开哪些栏认 dm_data_tab_layout。
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'equipment' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip P0 equipment: dynamic_entity_type 不存在';
  ELSE
    PERFORM _seed_catalog_orchestration(v_tenant, 'equipment', 'LIST_ROW');
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'region' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip P0 region: dynamic_entity_type 不存在';
  ELSE
    PERFORM _seed_catalog_orchestration(v_tenant, 'region', 'CATEGORY_NODE');
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'inspection_item' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip P0 inspection_item: dynamic_entity_type 不存在';
  ELSE
    PERFORM _seed_catalog_orchestration(v_tenant, 'inspection_item', 'LIST_ROW');
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'inspection_content' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip P0 inspection_content: dynamic_entity_type 不存在';
  ELSE
    PERFORM _seed_catalog_orchestration(v_tenant, 'inspection_content', 'LIST_ROW');
  END IF;
END $$;
