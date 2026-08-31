-- ============================================================================
-- recipes/inspection · 移除本配方写入（平台能力保留）
-- 1) inspection_item How 还原为 NONE
-- 2) 软删 creator=recipe-inspection 的方法选用行
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
BEGIN
  UPDATE dm_five_w_orchestration
  SET how_mode = 'NONE',
      how_config = '{}'::jsonb,
      updater = 'recipe-inspection-remove',
      update_time = CURRENT_TIMESTAMP
  WHERE entity_type_code = 'inspection_item'
    AND tenant_id = v_tenant
    AND deleted = false;

  IF to_regclass('dynamicbusiness.dynamic_sop_method_binding') IS NOT NULL THEN
    UPDATE dynamic_sop_method_binding
    SET deleted = true,
        updater = 'recipe-inspection-remove',
        update_time = CURRENT_TIMESTAMP
    WHERE tenant_id = v_tenant
      AND deleted = false
      AND creator = 'recipe-inspection';
  END IF;

  IF to_regclass('dynamicbusiness.dynamic_sop_method_binding_t1') IS NOT NULL THEN
    UPDATE dynamic_sop_method_binding_t1
    SET deleted = true,
        updater = 'recipe-inspection-remove',
        update_time = CURRENT_TIMESTAMP
    WHERE tenant_id = v_tenant
      AND deleted = false
      AND creator = 'recipe-inspection-demo';
  END IF;

  IF to_regclass('dynamicbusiness.dynamic_sop_instance_binding_t1') IS NOT NULL THEN
    UPDATE dynamic_sop_instance_binding_t1
    SET deleted = true,
        updater = 'recipe-inspection-remove',
        update_time = CURRENT_TIMESTAMP
    WHERE tenant_id = v_tenant
      AND deleted = false
      AND creator = 'recipe-inspection-demo';
  END IF;

  IF to_regclass('dynamicbusiness.ent_sop_t1') IS NOT NULL THEN
    UPDATE ent_sop_t1
    SET deleted = true,
        updater = 'recipe-inspection-remove',
        update_time = CURRENT_TIMESTAMP
    WHERE tenant_id = v_tenant
      AND deleted = false
      AND creator = 'recipe-inspection-demo';
  END IF;

  UPDATE inspection_task.inspection_object_station_binding
  SET deleted = true,
      updater = 'recipe-inspection-remove',
      update_time = CURRENT_TIMESTAMP
  WHERE deleted = false
    AND creator = 'recipe-inspection-demo';

  RAISE NOTICE 'recipes/inspection: 已移除 How sopHow 挂载与配方样例方法/实例/停靠点绑定';
END $$;
