-- ============================================================================
-- recipes/inspection · 02 标准检查库 How 挂 sopHow（纯数据）
-- 权威表：dm_five_w_orchestration（how_mode + how_config JSONB）
-- 类型码仅写在本配方；平台 How 运行时不写死 inspection_item
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
  v_how_config jsonb := jsonb_build_object(
    'capability', 'sopHow',
    'sopHow', jsonb_build_object(
      'subjectType', 'inspection_item',
      'hostType', 'equipment',
      'dimensionKey', 'execution_means',
      'dimensionOptions', jsonb_build_array(
        jsonb_build_object('value', 'MANUAL', 'label', '人工'),
        jsonb_build_object('value', 'UAV', 'label', '无人机'),
        jsonb_build_object('value', 'ROBOT', 'label', '机器人'),
        jsonb_build_object('value', 'FIXED_CAMERA', 'label', '固定摄像机')
      )
    )
  );
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'inspection_item' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip 02: dynamic_entity_type.inspection_item 不存在';
    RETURN;
  END IF;

  UPDATE dm_five_w_orchestration
  SET how_mode = 'FOLLOW_WHAT',
      how_config = v_how_config,
      enabled = true,
      deleted = false,
      updater = 'recipe-inspection',
      update_time = CURRENT_TIMESTAMP
  WHERE entity_type_code = 'inspection_item'
    AND tenant_id = v_tenant;

  IF NOT FOUND THEN
    INSERT INTO dm_five_w_orchestration (
      entity_type_code, enabled,
      what_mode, what_config,
      how_mode, how_config,
      object_pick_from,
      tenant_id, creator, deleted
    ) VALUES (
      'inspection_item', true,
      'VIEW_DETAIL', '{"bindLayer":"ENTITY"}'::jsonb,
      'FOLLOW_WHAT', v_how_config,
      'LIST_ROW',
      v_tenant, 'recipe-inspection', false
    );
    RAISE NOTICE 'recipes/inspection: 新建 inspection_item 编排头并挂 sopHow';
  ELSE
    RAISE NOTICE 'recipes/inspection: 已更新 inspection_item how_config → sopHow';
  END IF;
END $$;
