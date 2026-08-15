-- ============================================================================
-- five-w-orchestration · 01 P0 编排 seed
-- 配方：equipment / region / inspection_item / inspection_content
-- 前置：Flyway V51；dynamic_entity_type 已存在对应 code（通常 system seed 已导入）
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
    WHERE entity_type_code = 'equipment' AND tenant_id = v_tenant;

    IF NOT FOUND THEN
      INSERT INTO dm_five_w_orchestration (
        entity_type_code, enabled, selection_level, what_mode, what_config,
        how_mode, how_config, tenant_id, creator, deleted
      ) VALUES (
        'equipment', true, 'ENTITY', 'VIEW_DETAIL', '{"bindLayer":"ENTITY"}'::jsonb,
        'NONE', '{}'::jsonb, v_tenant, 'seed', false
      );
    END IF;

    PERFORM _seed_five_w_filter_slot(v_tenant, 'equipment', 'equipment-category', NULL,
      true, NULL,
      '{"label":"设备","categoryTypeCode":"equipment"}'::jsonb);
    PERFORM _seed_five_w_who_slot(v_tenant, 'equipment', 'MODEL', 'equipment-model', NULL,
      true, '["modelId"]'::jsonb, NULL, NULL);
    PERFORM _seed_five_w_who_slot(v_tenant, 'equipment', 'ENTITY', 'equipment-entity', NULL,
      true, '["entityId"]'::jsonb, 'rowSelection', NULL);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'region' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip P0 region: dynamic_entity_type 不存在';
  ELSE
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
    WHERE entity_type_code = 'region' AND tenant_id = v_tenant;

    IF NOT FOUND THEN
      INSERT INTO dm_five_w_orchestration (
        entity_type_code, enabled, selection_level, what_mode, what_config,
        how_mode, how_config, tenant_id, creator, deleted
      ) VALUES (
        'region', true, 'ENTITY', 'VIEW_DETAIL', '{"bindLayer":"ENTITY"}'::jsonb,
        'NONE', '{}'::jsonb, v_tenant, 'seed', false
      );
    END IF;

    UPDATE dm_five_w_who_layout
    SET deleted = true, updater = 'seed-replace', update_time = CURRENT_TIMESTAMP
    WHERE entity_type_code = 'region' AND tenant_id = v_tenant AND deleted = false;
    UPDATE dm_five_w_filter_layout
    SET deleted = true, updater = 'seed-replace', update_time = CURRENT_TIMESTAMP
    WHERE entity_type_code = 'region' AND tenant_id = v_tenant AND deleted = false;

    PERFORM _seed_five_w_filter_slot(v_tenant, 'region', 'region-tree', NULL,
      true, 'categoryLinkedEntity',
      '{"label":"区域","categoryTypeCode":"region"}'::jsonb);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'inspection_item' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip P0 inspection_item: dynamic_entity_type 不存在';
  ELSE
    -- 标准检查库：本页目标=检查项；Who=检查项实体；视角=检查分类宿主+设备分类成员；What=看详情
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
    WHERE entity_type_code = 'inspection_item' AND tenant_id = v_tenant;

    IF NOT FOUND THEN
      INSERT INTO dm_five_w_orchestration (
        entity_type_code, enabled, selection_level, what_mode, what_config,
        how_mode, how_config, tenant_id, creator, deleted
      ) VALUES (
        'inspection_item', true, 'ENTITY', 'VIEW_DETAIL',
        '{"bindLayer":"ENTITY"}'::jsonb,
        'NONE', '{}'::jsonb, v_tenant, 'seed', false
      );
    END IF;

    UPDATE dm_five_w_who_layout
    SET deleted = true, updater = 'seed-replace', update_time = CURRENT_TIMESTAMP
    WHERE entity_type_code = 'inspection_item' AND tenant_id = v_tenant AND deleted = false;
    UPDATE dm_five_w_filter_layout
    SET deleted = true, updater = 'seed-replace', update_time = CURRENT_TIMESTAMP
    WHERE entity_type_code = 'inspection_item' AND tenant_id = v_tenant AND deleted = false;

    PERFORM _seed_five_w_filter_slot(v_tenant, 'inspection_item', 'inspection_item-host', 'inspection_item-host',
      true, NULL,
      '{"label":"检查分类","columnKey":"col-insp-host","columnOrder":0,"relationMode":"cascade","relationRole":"host","categoryTypeCode":"inspection_item"}'::jsonb);
    PERFORM _seed_five_w_filter_slot(v_tenant, 'inspection_item', 'inspection_item-equipment-member', 'inspection_item-equipment-member',
      true, NULL,
      '{"label":"设备分类","columnKey":"col-equip-member","columnOrder":1,"relationMode":"cascade","relationRole":"member","categoryTypeCode":"equipment"}'::jsonb);
    PERFORM _seed_five_w_who_slot(v_tenant, 'inspection_item', 'ENTITY', 'inspection_item-entity', NULL,
      true, '["entityId"]'::jsonb, 'rowSelection', NULL);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'inspection_content' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip P0 inspection_content: dynamic_entity_type 不存在';
  ELSE
    -- 检查内容：本页目标=检查项；Who=检查项实体；视角=设备分类/型号/设备；What=看详情（适用勾选另配，不把检查项划给 What 候选）
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
    WHERE entity_type_code = 'inspection_content' AND tenant_id = v_tenant;

    IF NOT FOUND THEN
      INSERT INTO dm_five_w_orchestration (
        entity_type_code, enabled, selection_level, what_mode, what_config,
        how_mode, how_config, tenant_id, creator, deleted
      ) VALUES (
        'inspection_content', true, 'ENTITY', 'VIEW_DETAIL',
        '{"bindLayer":"ENTITY"}'::jsonb,
        'NONE', '{}'::jsonb, v_tenant, 'seed', false
      );
    END IF;

    UPDATE dm_five_w_who_layout
    SET deleted = true, updater = 'seed-replace', update_time = CURRENT_TIMESTAMP
    WHERE entity_type_code = 'inspection_content' AND tenant_id = v_tenant AND deleted = false;
    UPDATE dm_five_w_filter_layout
    SET deleted = true, updater = 'seed-replace', update_time = CURRENT_TIMESTAMP
    WHERE entity_type_code = 'inspection_content' AND tenant_id = v_tenant AND deleted = false;

    PERFORM _seed_five_w_filter_slot(v_tenant, 'inspection_content', 'inspection_content-category', NULL,
      true, NULL,
      '{"label":"设备分类","categoryTypeCode":"equipment"}'::jsonb);
    PERFORM _seed_five_w_filter_slot(v_tenant, 'inspection_content', 'inspection_content-model', NULL,
      true, NULL, '{"label":"设备型号"}'::jsonb, 'MODEL', '["modelId"]'::jsonb);
    PERFORM _seed_five_w_filter_slot(v_tenant, 'inspection_content', 'inspection_content-entity', NULL,
      true, NULL, '{"label":"设备"}'::jsonb, 'ENTITY', '["entityId"]'::jsonb);
    PERFORM _seed_five_w_who_slot(v_tenant, 'inspection_content', 'ENTITY', 'inspection_content-item', NULL,
      true, '["entityId"]'::jsonb, 'rowSelection', NULL);
  END IF;
END $$;
