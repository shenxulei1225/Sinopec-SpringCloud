-- ============================================================================
-- inspection · 24 标准检查库适用勾选边（幂等，只动 inspection_item.data_layout_id）
-- 口径：
--   点检查内容分类 → 检查项列表看该类全部项
--   勾选/取消只走 write（设备↔检查项），不按已勾选裁列表
--   禁止：设备实体→检查项 filter；设备分类→检查项 filter
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
  v_layout_id bigint;
  v_item_entity text;
  v_item_category text;
  v_equip_entity text;
  v_equip_category text;
  v_edge_id text;
BEGIN
  SELECT data_layout_id INTO v_layout_id
  FROM dynamic_entity_type
  WHERE code = 'inspection_item' AND tenant_id = v_tenant AND deleted = false
  LIMIT 1;

  IF v_layout_id IS NULL THEN
    RAISE NOTICE 'skip 24: inspection_item.data_layout_id 为空';
    RETURN;
  END IF;

  SELECT 'ENTITY:' || tab_id INTO v_item_entity
  FROM dm_data_tab_layout
  WHERE layout_id = v_layout_id AND deleted = false AND column_kind = 'ENTITY'
    AND COALESCE(column_meta ->> 'entityEntityTypeCode', '') = 'inspection_item'
  LIMIT 1;

  SELECT 'CATEGORY:' || (column_meta ->> 'columnKey') || ':' || tab_id
    INTO v_item_category
  FROM dm_data_tab_layout
  WHERE layout_id = v_layout_id AND deleted = false AND column_kind = 'CATEGORY'
    AND COALESCE(column_meta ->> 'categoryTypeCode', '') = 'inspection_item'
  LIMIT 1;

  SELECT 'ENTITY:' || tab_id INTO v_equip_entity
  FROM dm_data_tab_layout
  WHERE layout_id = v_layout_id AND deleted = false AND column_kind = 'ENTITY'
    AND COALESCE(column_meta ->> 'entityEntityTypeCode', '') = 'equipment'
  LIMIT 1;

  SELECT 'CATEGORY:' || (column_meta ->> 'columnKey') || ':' || tab_id
    INTO v_equip_category
  FROM dm_data_tab_layout
  WHERE layout_id = v_layout_id AND deleted = false AND column_kind = 'CATEGORY'
    AND COALESCE(column_meta ->> 'categoryTypeCode', '') = 'equipment'
  LIMIT 1;

  IF v_item_entity IS NULL THEN
    RAISE NOTICE 'skip 24: 标准检查库数据页没有检查项对象栏';
    RETURN;
  END IF;

  IF v_equip_entity IS NOT NULL THEN
    UPDATE dm_data_tab_column_relation
    SET deleted = true, updater = 'seed-24', update_time = CURRENT_TIMESTAMP
    WHERE layout_id = v_layout_id AND deleted = false
      AND relation_kind = 'ENTITY_ENTITY'
      AND COALESCE(relation_meta ->> 'edgeAction', '') = 'filter'
      AND from_column_identity = v_equip_entity
      AND to_column_identity = v_item_entity;
  END IF;

  IF v_equip_category IS NOT NULL THEN
    UPDATE dm_data_tab_column_relation
    SET deleted = true, updater = 'seed-24', update_time = CURRENT_TIMESTAMP
    WHERE layout_id = v_layout_id AND deleted = false
      AND relation_kind = 'CATEGORY_ENTITY'
      AND COALESCE(relation_meta ->> 'edgeAction', '') = 'filter'
      AND from_column_identity = v_equip_category
      AND to_column_identity = v_item_entity;
  END IF;

  IF v_item_category IS NOT NULL AND NOT EXISTS (
    SELECT 1
    FROM dm_data_tab_column_relation
    WHERE layout_id = v_layout_id AND deleted = false
      AND relation_kind = 'CATEGORY_ENTITY'
      AND COALESCE(relation_meta ->> 'edgeAction', '') = 'filter'
      AND from_column_identity = v_item_category
      AND to_column_identity = v_item_entity
  ) THEN
    v_edge_id := 'edge-ce-' || substr(md5(v_layout_id::text || v_item_category || v_item_entity), 1, 8);
    INSERT INTO dm_data_tab_column_relation (
      entity_type_code, edge_id, from_column_identity, to_column_identity,
      relation_kind, from_type_code, to_type_code, relation_meta,
      creator, create_time, updater, update_time, deleted, tenant_id, layout_id
    ) VALUES (
      'inspection_item', v_edge_id, v_item_category, v_item_entity,
      'CATEGORY_ENTITY', 'inspection_item', 'inspection_item',
      '{"edgeAction": "filter", "enabledInteractions": ["filterEmptySkip"]}'::jsonb,
      'seed-24', CURRENT_TIMESTAMP, 'seed-24', CURRENT_TIMESTAMP, false, v_tenant, v_layout_id
    );
  END IF;
END $$;
