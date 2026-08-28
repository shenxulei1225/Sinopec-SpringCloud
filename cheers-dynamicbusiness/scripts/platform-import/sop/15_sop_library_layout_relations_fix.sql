-- ============================================================================
-- sop · 15 修复统一库布局与栏间关系（幂等）
-- 口径（与 14 一致并补齐边）：
--   SINGLE：软删型号栏；筛选=SOP分类；Who=SOP模板实体；详情开启
--   栏间：分类→实体 filter + write（关联配置）；清掉挂在型号上的旧边
--   实体列表 props：is_template=true
--   未挂分类的模板：挂到「检查」（保证点分类有数；已挂接的不改）
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
  v_layout_id bigint;
  v_entity_props_id bigint;
  v_cat_tab text;
  v_cat_key text;
  v_cat_identity text;
  v_entity_identity text;
  v_ds jsonb;
  v_inspection_cat_id bigint;
  v_model_id bigint;
BEGIN
  SELECT data_layout_id INTO v_layout_id
  FROM dynamic_entity_type
  WHERE code = 'sop' AND tenant_id = v_tenant AND deleted = false
  LIMIT 1;

  IF v_layout_id IS NULL THEN
    RAISE NOTICE 'skip 15: sop.data_layout_id 为空';
    RETURN;
  END IF;

  UPDATE dynamic_entity_type
  SET work_scope = 'NETWORK',
      model_workbench_mode = 'SINGLE',
      updater = 'seed-15',
      update_time = CURRENT_TIMESTAMP
  WHERE code = 'sop' AND tenant_id = v_tenant AND deleted = false;

  -- 1) 软删型号栏（SINGLE：模板是实体行）
  UPDATE dm_data_tab_layout
  SET deleted = true,
      enabled = false,
      updater = 'seed-15',
      update_time = CURRENT_TIMESTAMP
  WHERE layout_id = v_layout_id
    AND column_kind = 'MODEL'
    AND deleted = false;

  -- 2) 分类栏：筛选区；稳定 tab / columnKey
  UPDATE dm_data_tab_layout
  SET tab_id = COALESCE(NULLIF(trim(tab_id), ''), 'sop-default'),
      column_meta = COALESCE(column_meta, '{}'::jsonb)
        || jsonb_build_object(
          'label', 'SOP分类',
          'categoryTypeCode', 'sop',
          'columnKey', COALESCE(NULLIF(column_meta->>'columnKey', ''), 'sop-default'),
          'columnSection', 'FILTER'
        ),
      enabled = true,
      updater = 'seed-15',
      update_time = CURRENT_TIMESTAMP
  WHERE layout_id = v_layout_id
    AND column_kind = 'CATEGORY'
    AND deleted = false;

  -- 3) 实体栏：Who · SOP模板；空 tabId 保持 null（身份 = ENTITY:default）
  UPDATE dm_data_tab_layout
  SET tab_id = NULL,
      column_meta = COALESCE(column_meta, '{}'::jsonb)
        || jsonb_build_object(
          'label', 'SOP模板',
          'columnSection', 'OBJECT',
          'entityEntityTypeCode', 'sop'
        ),
      enabled = true,
      updater = 'seed-15',
      update_time = CURRENT_TIMESTAMP
  WHERE layout_id = v_layout_id
    AND column_kind = 'ENTITY'
    AND deleted = false;

  -- 4) 详情栏：无则插，有则开
  IF NOT EXISTS (
    SELECT 1 FROM dm_data_tab_layout
    WHERE layout_id = v_layout_id AND column_kind = 'DETAIL' AND deleted = false
  ) THEN
    INSERT INTO dm_data_tab_layout (
      tenant_id, layout_id, entity_type_code, column_kind, tab_id, props_id,
      enabled, column_meta, creator, deleted
    ) VALUES (
      v_tenant, v_layout_id, 'sop', 'DETAIL', NULL, NULL,
      true, '{}'::jsonb, 'seed-15', false
    );
  ELSE
    UPDATE dm_data_tab_layout
    SET enabled = true,
        updater = 'seed-15',
        update_time = CURRENT_TIMESTAMP
    WHERE layout_id = v_layout_id
      AND column_kind = 'DETAIL'
      AND deleted = false;
  END IF;

  -- 5) 实体列表 props：契约三字段；is_template 过滤走前端 sopLibraryEntityFieldFilters
  SELECT props_id INTO v_entity_props_id
  FROM dm_data_tab_layout
  WHERE layout_id = v_layout_id
    AND column_kind = 'ENTITY'
    AND deleted = false
  LIMIT 1;

  IF v_entity_props_id IS NOT NULL THEN
    SELECT COALESCE(data_source::jsonb, '{}'::jsonb) INTO v_ds
    FROM platformresource.pr_component_props
    WHERE id = v_entity_props_id AND deleted = false;

    IF v_ds IS NOT NULL THEN
      v_ds := jsonb_set(
        jsonb_set(
          jsonb_set(v_ds, '{businessCategory}', '"dynamic"', true),
          '{entityTypeCode}', '"sop"', true
        ),
        '{dataKind}', '"entity"', true
      );
      v_ds := v_ds - 'queryContext';

      UPDATE platformresource.pr_component_props
      SET data_source = v_ds::text,
          updater = 'seed-15',
          update_time = CURRENT_TIMESTAMP
      WHERE id = v_entity_props_id AND deleted = false;
    END IF;
  END IF;

  -- 6) 算当前分类 / 实体列身份
  SELECT
    COALESCE(NULLIF(trim(tab_id), ''), 'sop-default'),
    COALESCE(NULLIF(column_meta->>'columnKey', ''), 'sop-default')
  INTO v_cat_tab, v_cat_key
  FROM dm_data_tab_layout
  WHERE layout_id = v_layout_id
    AND column_kind = 'CATEGORY'
    AND deleted = false
  LIMIT 1;

  v_cat_identity := 'CATEGORY:' || v_cat_key || ':' || v_cat_tab;
  v_entity_identity := 'ENTITY:default';

  -- 7) 清掉本布局全部旧边（含型号端点），再写标准 CE filter + write
  UPDATE dm_data_tab_column_relation
  SET deleted = true,
      updater = 'seed-15',
      update_time = CURRENT_TIMESTAMP
  WHERE layout_id = v_layout_id
    AND deleted = false;

  DELETE FROM dm_data_tab_column_relation
  WHERE layout_id = v_layout_id AND deleted = true;

  IF v_cat_identity IS NOT NULL THEN
    INSERT INTO dm_data_tab_column_relation (
      tenant_id, layout_id, entity_type_code, edge_id,
      from_column_identity, to_column_identity, relation_kind,
      from_type_code, to_type_code, relation_meta, creator, deleted
    ) VALUES
      (
        v_tenant, v_layout_id, 'sop', 'edge-sop-ce-filter',
        v_cat_identity, v_entity_identity, 'CATEGORY_ENTITY',
        'sop', 'sop',
        jsonb_build_object('edgeRole', 'filter', 'enabledInteractions', '[]'::jsonb),
        'seed-15', false
      ),
      (
        v_tenant, v_layout_id, 'sop', 'edge-sop-ce-write',
        v_cat_identity, v_entity_identity, 'CATEGORY_ENTITY',
        'sop', 'sop',
        jsonb_build_object(
          'edgeRole', 'write',
          'enabledInteractions', jsonb_build_array('dragAssociate', 'unbindChecked')
        ),
        'seed-15', false
      );
  END IF;

  -- 8) 未挂任何 SOP 分类的模板 → 挂「检查」（已挂接跳过）
  SELECT id INTO v_inspection_cat_id
  FROM dynamic_category
  WHERE deleted = false AND tenant_id = v_tenant AND code = 'cat-sop-inspection'
  LIMIT 1;

  SELECT id INTO v_model_id
  FROM dynamic_model
  WHERE deleted = false AND tenant_id = v_tenant AND code = 'sop'
  LIMIT 1;

  IF v_inspection_cat_id IS NOT NULL AND v_model_id IS NOT NULL THEN
    INSERT INTO dynamic_category_entity_link_t1 (
      tenant_id, category_id, entity_type_code, entity_id, entity_model_id, creator, deleted
    )
    SELECT
      v_tenant,
      v_inspection_cat_id,
      'sop',
      s.id,
      v_model_id,
      'seed-15',
      false
    FROM ent_sop_t1 s
    WHERE s.deleted = false
      AND s.tenant_id = v_tenant
      AND s.is_template = true
      AND NOT EXISTS (
        SELECT 1
        FROM dynamic_category_entity_link_t1 l
        JOIN dynamic_category c ON c.id = l.category_id AND c.deleted = false
        WHERE l.deleted = false
          AND l.tenant_id = v_tenant
          AND l.entity_type_code = 'sop'
          AND l.entity_id = s.id
          AND c.category_type_code = 'sop'
      );
  END IF;
END $$;
