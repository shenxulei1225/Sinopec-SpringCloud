-- ============================================================================
-- recipes/inspection · 02 确保检查项编排头存在（只写点列表行即对象）
-- 作业指导不再挂在编排 How 槽上；详情跟谁认布局栏 + 关系图连线。
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'inspection_item' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip 02: dynamic_entity_type.inspection_item 不存在';
    RETURN;
  END IF;

  UPDATE dm_catalog_orchestration
  SET enabled = true,
      object_pick_from = COALESCE(NULLIF(object_pick_from, ''), 'LIST_ROW'),
      deleted = false,
      updater = 'recipe-inspection',
      update_time = CURRENT_TIMESTAMP
  WHERE entity_type_code = 'inspection_item'
    AND tenant_id = v_tenant;

  IF NOT FOUND THEN
    INSERT INTO dm_catalog_orchestration (
      entity_type_code, enabled, object_pick_from,
      tenant_id, creator, deleted
    ) VALUES (
      'inspection_item', true, 'LIST_ROW',
      v_tenant, 'recipe-inspection', false
    );
    RAISE NOTICE 'recipes/inspection: 新建 inspection_item 编排头';
  ELSE
    RAISE NOTICE 'recipes/inspection: 已确认 inspection_item 编排头';
  END IF;
END $$;
