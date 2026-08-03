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

    PERFORM _seed_five_w_who_slot(v_tenant, 'equipment', 'CATEGORY', 'equipment-category', NULL,
      true, '["categoryId"]'::jsonb, NULL,
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

    PERFORM _seed_five_w_who_slot(v_tenant, 'region', 'CATEGORY', 'region-tree', NULL,
      true, '["categoryId","entityId"]'::jsonb, 'categoryLinkedEntity',
      '{"label":"区域","categoryTypeCode":"region"}'::jsonb);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'inspection_item' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip P0 inspection_item: dynamic_entity_type 不存在';
  ELSE
    UPDATE dm_five_w_orchestration
    SET enabled = true,
        selection_level = 'MODEL',
        what_mode = 'PICK_ENTITY',
        what_config = '{"bindLayer":"MODEL","candidateEntityTypeCode":"inspection_item","relationKind":"MODEL_ENTITY"}'::jsonb,
        how_mode = 'AFTER_WHAT_ITEM',
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
        'inspection_item', true, 'MODEL', 'PICK_ENTITY',
        '{"bindLayer":"MODEL","candidateEntityTypeCode":"inspection_item","relationKind":"MODEL_ENTITY"}'::jsonb,
        'AFTER_WHAT_ITEM', '{}'::jsonb, v_tenant, 'seed', false
      );
    END IF;

    UPDATE dm_five_w_who_layout
    SET deleted = true, updater = 'seed-replace', update_time = CURRENT_TIMESTAMP
    WHERE entity_type_code = 'inspection_item' AND tenant_id = v_tenant AND deleted = false;

    PERFORM _seed_five_w_who_slot(v_tenant, 'inspection_item', 'CATEGORY', 'inspection_item-category', NULL,
      true, '["categoryId"]'::jsonb, NULL,
      '{"label":"设备分类","categoryTypeCode":"equipment"}'::jsonb);
    PERFORM _seed_five_w_who_slot(v_tenant, 'inspection_item', 'MODEL', 'inspection_item-model', NULL,
      true, '["modelId"]'::jsonb, NULL, NULL);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'inspection_content' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip P0 inspection_content: dynamic_entity_type 不存在';
  ELSE
    UPDATE dm_five_w_orchestration
    SET enabled = true,
        selection_level = 'ENTITY',
        what_mode = 'SITE_PREP',
        what_config = '{"bindLayer":"ENTITY","candidateEntityTypeCode":"inspection_item"}'::jsonb,
        how_mode = 'AFTER_WHAT_ITEM',
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
        'inspection_content', true, 'ENTITY', 'SITE_PREP',
        '{"bindLayer":"ENTITY","candidateEntityTypeCode":"inspection_item"}'::jsonb,
        'AFTER_WHAT_ITEM', '{}'::jsonb, v_tenant, 'seed', false
      );
    END IF;

    UPDATE dm_five_w_who_layout
    SET deleted = true, updater = 'seed-replace', update_time = CURRENT_TIMESTAMP
    WHERE entity_type_code = 'inspection_content' AND tenant_id = v_tenant AND deleted = false;

    PERFORM _seed_five_w_who_slot(v_tenant, 'inspection_content', 'CATEGORY', 'inspection_content-category', NULL,
      true, '["categoryId"]'::jsonb, NULL,
      '{"label":"设备分类","categoryTypeCode":"equipment"}'::jsonb);
    PERFORM _seed_five_w_who_slot(v_tenant, 'inspection_content', 'MODEL', 'inspection_content-model', NULL,
      true, '["modelId"]'::jsonb, NULL, NULL);
    PERFORM _seed_five_w_who_slot(v_tenant, 'inspection_content', 'ENTITY', 'inspection_content-entity', NULL,
      true, '["entityId"]'::jsonb, 'rowSelection', NULL);
  END IF;
END $$;
