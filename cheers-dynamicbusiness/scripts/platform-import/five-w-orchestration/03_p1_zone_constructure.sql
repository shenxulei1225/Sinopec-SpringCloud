-- ============================================================================
-- five-w-orchestration · 03 P1 zone + Constructure（§5/§6）
-- zone：分类 + 实体（无型号）；Constructure：分类 + 型号 + 实体
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
    WHERE entity_type_code = 'zone' AND tenant_id = v_tenant;

    IF NOT FOUND THEN
      INSERT INTO dm_five_w_orchestration (
        entity_type_code, enabled, selection_level, what_mode, what_config,
        how_mode, how_config, tenant_id, creator, deleted
      ) VALUES (
        'zone', true, 'ENTITY', 'VIEW_DETAIL', '{"bindLayer":"ENTITY"}'::jsonb,
        'NONE', '{}'::jsonb, v_tenant, 'seed', false
      );
    END IF;

    UPDATE dm_five_w_who_layout
    SET deleted = true, updater = 'seed-replace', update_time = CURRENT_TIMESTAMP
    WHERE entity_type_code = 'zone' AND tenant_id = v_tenant AND deleted = false;

    PERFORM _seed_five_w_who_slot(v_tenant, 'zone', 'CATEGORY', 'zone-category', NULL,
      true, '["categoryId"]'::jsonb, NULL,
      '{"label":"站场分区","categoryTypeCode":"zone"}'::jsonb);
    PERFORM _seed_five_w_who_slot(v_tenant, 'zone', 'ENTITY', 'zone-entity', NULL,
      true, '["entityId"]'::jsonb, 'rowSelection', NULL);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'Constructure' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip P1 Constructure: dynamic_entity_type 不存在';
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
    WHERE entity_type_code = 'Constructure' AND tenant_id = v_tenant;

    IF NOT FOUND THEN
      INSERT INTO dm_five_w_orchestration (
        entity_type_code, enabled, selection_level, what_mode, what_config,
        how_mode, how_config, tenant_id, creator, deleted
      ) VALUES (
        'Constructure', true, 'ENTITY', 'VIEW_DETAIL', '{"bindLayer":"ENTITY"}'::jsonb,
        'NONE', '{}'::jsonb, v_tenant, 'seed', false
      );
    END IF;

    UPDATE dm_five_w_who_layout
    SET deleted = true, updater = 'seed-replace', update_time = CURRENT_TIMESTAMP
    WHERE entity_type_code = 'Constructure' AND tenant_id = v_tenant AND deleted = false;

    PERFORM _seed_five_w_who_slot(v_tenant, 'Constructure', 'CATEGORY', 'Constructure-category', NULL,
      true, '["categoryId"]'::jsonb, NULL,
      '{"label":"构筑物分类","categoryTypeCode":"Constructure"}'::jsonb);
    PERFORM _seed_five_w_who_slot(v_tenant, 'Constructure', 'MODEL', 'Constructure-model', NULL,
      true, '["modelId"]'::jsonb, NULL, NULL);
    PERFORM _seed_five_w_who_slot(v_tenant, 'Constructure', 'ENTITY', 'Constructure-entity', NULL,
      true, '["entityId"]'::jsonb, 'rowSelection', NULL);
  END IF;
END $$;
