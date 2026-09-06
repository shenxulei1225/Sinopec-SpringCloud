-- ============================================================================
-- recipes/inspection · 移除本配方写入（平台能力保留）
-- 软删 creator=recipe-inspection 的方法选用行与历史工作面 props
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
BEGIN
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

  UPDATE platformresource.pr_component_props
  SET deleted = true,
      updater = 'recipe-inspection-remove',
      update_time = CURRENT_TIMESTAMP
  WHERE component_code = 'work-face'
    AND creator = 'recipe-inspection'
    AND deleted = false;

  RAISE NOTICE 'recipes/inspection: 已移除 How sopHow 挂载、What 工作面 props 与配方样例方法/实例/停靠点绑定';
END $$;
