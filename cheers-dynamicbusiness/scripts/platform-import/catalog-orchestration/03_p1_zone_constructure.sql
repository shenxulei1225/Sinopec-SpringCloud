-- ============================================================================
-- catalog-orchestration · 03 P1 zone + Constructure
-- zone：分类即对象。Constructure：点列表行。开栏认布局。
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'zone' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip P1 zone: dynamic_entity_type 不存在';
  ELSE
    PERFORM _seed_catalog_orchestration(v_tenant, 'zone', 'CATEGORY_NODE');
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'Constructure' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip P1 Constructure: dynamic_entity_type 不存在';
  ELSE
    PERFORM _seed_catalog_orchestration(v_tenant, 'Constructure', 'LIST_ROW');
  END IF;
END $$;
