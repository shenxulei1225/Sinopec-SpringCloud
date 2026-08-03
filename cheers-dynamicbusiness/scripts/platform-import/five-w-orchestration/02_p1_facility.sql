-- ============================================================================
-- five-w-orchestration · 02 P1 facility（§38/§42）
-- Who：运营区域(region 外部分类) + 站场/管廊(facility 实体栏)；无型号栏
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

  UPDATE dm_five_w_orchestration
  SET enabled = true,
      selection_level = 'ENTITY',
      what_mode = 'VIEW_DETAIL',
      what_config = '{"bindLayer":"ENTITY"}'::jsonb,
      how_mode = 'NONE',
      how_config = '{}'::jsonb,
      deleted = false,
      updater = 'seed',
      update_time = CURRENT_TIMESTAMP
  WHERE entity_type_code = 'facility' AND tenant_id = v_tenant;

  IF NOT FOUND THEN
    INSERT INTO dm_five_w_orchestration (
      entity_type_code, enabled, selection_level, what_mode, what_config,
      how_mode, how_config, tenant_id, creator, deleted
    ) VALUES (
      'facility', true, 'ENTITY', 'VIEW_DETAIL', '{"bindLayer":"ENTITY"}'::jsonb,
      'NONE', '{}'::jsonb, v_tenant, 'seed', false
    );
  END IF;

  UPDATE dm_five_w_who_layout
  SET deleted = true, updater = 'seed-replace', update_time = CURRENT_TIMESTAMP
  WHERE entity_type_code = 'facility' AND tenant_id = v_tenant AND deleted = false;

  PERFORM _seed_five_w_who_slot(v_tenant, 'facility', 'CATEGORY', 'region-filter', NULL,
    true, '["categoryId"]'::jsonb, NULL,
    '{"label":"运营区域","categoryTypeCode":"region"}'::jsonb);
  PERFORM _seed_five_w_who_slot(v_tenant, 'facility', 'ENTITY', 'facility-entity', NULL,
    true, '["entityId"]'::jsonb, 'rowSelection', NULL);
END $$;
