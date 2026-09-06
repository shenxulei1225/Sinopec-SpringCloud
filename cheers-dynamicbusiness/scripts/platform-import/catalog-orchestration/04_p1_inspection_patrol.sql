-- ============================================================================
-- catalog-orchestration · 04 P1 巡检域专项
-- inspection_method · patrol_equipment · task_patrol · task_maintenance
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
BEGIN
  IF EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'inspection_method' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    PERFORM dynamicbusiness._seed_catalog_orchestration(
      v_tenant, 'inspection_method', 'LIST_ROW');
  ELSE
    RAISE NOTICE 'skip inspection_method: dynamic_entity_type 不存在';
  END IF;

  IF EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'patrol_equipment' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    PERFORM dynamicbusiness._seed_catalog_orchestration(
      v_tenant, 'patrol_equipment', 'LIST_ROW');
  ELSE
    RAISE NOTICE 'skip patrol_equipment: dynamic_entity_type 不存在';
  END IF;

  IF EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'task_patrol' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    PERFORM dynamicbusiness._seed_catalog_orchestration(
      v_tenant, 'task_patrol', 'LIST_ROW');
  ELSE
    RAISE NOTICE 'skip task_patrol: dynamic_entity_type 不存在';
  END IF;

  IF EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'task_maintenance' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    PERFORM dynamicbusiness._seed_catalog_orchestration(
      v_tenant, 'task_maintenance', 'LIST_ROW');
  ELSE
    RAISE NOTICE 'skip task_maintenance: dynamic_entity_type 不存在';
  END IF;
END $$;
