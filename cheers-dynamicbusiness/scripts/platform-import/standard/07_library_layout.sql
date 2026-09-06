-- ============================================================================
-- standard · 07 统一库布局：SINGLE · 分类 | 标准条目 | 详情
-- 根因：界面创建的布局只有分类+型号，中间「对象」栏查型号，看不到实体。
-- 口径（对齐 SOP 现网）：软删型号栏；Who=实体；分类→实体 filter；实体→分类 write；实体→详情 filter
-- 权威：栏在 dm_data_tab_layout；展示 props 在 pr_component_props；禁止前端猜 SINGLE。
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
  v_layout_id bigint;
  v_entity_props_id bigint;
  v_cat_props_id bigint;
  v_donor bigint;
  v_component_id bigint;
  v_cat_tab text;
  v_cat_key text;
  v_cat_identity text;
  v_entity_identity text;
  v_detail_identity text;
  v_ds jsonb;
  v_props jsonb;
  v_list_ui text := $p${"displayContent":["standard_no","name"],"emptyText":"暂无标准条目","search":{"showSearch":true,"searchPlaceholder":"搜索标准编号或名称…","searchScopeSelectedOptions":["standard_no","name"]}}$p$;
BEGIN
  UPDATE dynamic_entity_type
  SET work_scope = 'NETWORK',
      model_workbench_mode = 'SINGLE',
      updater = 'seed-standard-07',
      update_time = CURRENT_TIMESTAMP
  WHERE code = 'standard'
    AND tenant_id = v_tenant
    AND deleted = false;

  SELECT data_layout_id INTO v_layout_id
  FROM dynamic_entity_type
  WHERE code = 'standard' AND tenant_id = v_tenant AND deleted = false
  LIMIT 1;

  IF v_layout_id IS NULL THEN
    RAISE NOTICE 'skip 07: standard.data_layout_id 为空';
    RETURN;
  END IF;

  -- 1) SINGLE：软删型号栏（标准条目是实体行）
  UPDATE dm_data_tab_layout
  SET deleted = true,
      enabled = false,
      updater = 'seed-standard-07',
      update_time = CURRENT_TIMESTAMP
  WHERE layout_id = v_layout_id
    AND column_kind = 'MODEL'
    AND deleted = false;

  -- 2) 分类栏：筛选区；稳定 tab / columnKey / 标签
  UPDATE dm_data_tab_layout
  SET tab_id = 'standard-1',
      column_meta = COALESCE(column_meta, '{}'::jsonb)
        || jsonb_build_object(
          'label', '标准分类',
          'categoryTypeCode', 'standard',
          'columnKey', 'standard',
          'columnSection', 'filter'
        ),
      enabled = true,
      updater = 'seed-standard-07',
      update_time = CURRENT_TIMESTAMP
  WHERE layout_id = v_layout_id
    AND column_kind = 'CATEGORY'
    AND deleted = false;

  -- 分类树 props：纠正误写的设备分类指纹
  SELECT props_id INTO v_cat_props_id
  FROM dm_data_tab_layout
  WHERE layout_id = v_layout_id
    AND column_kind = 'CATEGORY'
    AND deleted = false
  LIMIT 1;

  IF v_cat_props_id IS NOT NULL THEN
    UPDATE platformresource.pr_component_props
    SET data_source = '{"businessCategory":"category","entityTypeCode":"standard","dataKind":"entity"}',
        name = 'dm-tree:standard:standard:标准分类',
        updater = 'seed-standard-07',
        update_time = CURRENT_TIMESTAMP
    WHERE id = v_cat_props_id AND deleted = false;
  END IF;

  -- 3) 实体栏：无则插入，有则对齐 Who
  IF NOT EXISTS (
    SELECT 1 FROM dm_data_tab_layout
    WHERE layout_id = v_layout_id AND column_kind = 'ENTITY' AND deleted = false
  ) THEN
    -- 复用已有 standard 实体 list 模板，否则从同类克隆
    SELECT p.id INTO v_entity_props_id
    FROM platformresource.pr_component_props p
    WHERE p.deleted = false
      AND p.component_code = 'list'
      AND p.is_template = true
      AND COALESCE(p.data_source::jsonb->>'businessCategory', '') = 'dynamic'
      AND COALESCE(p.data_source::jsonb->>'entityTypeCode', '') = 'standard'
      AND COALESCE(p.data_source::jsonb->>'dataKind', '') = 'entity'
    ORDER BY p.id DESC
    LIMIT 1;

    IF v_entity_props_id IS NULL THEN
      SELECT p.id INTO v_donor
      FROM platformresource.pr_component_props p
      WHERE p.deleted = false
        AND p.component_code = 'list'
        AND p.is_template = true
        AND COALESCE(p.data_source::jsonb->>'businessCategory', '') = 'dynamic'
        AND COALESCE(p.data_source::jsonb->>'dataKind', '') = 'entity'
      ORDER BY p.id DESC
      LIMIT 1;

      IF v_donor IS NULL THEN
        SELECT component_id INTO v_component_id
        FROM platformresource.pr_component_props
        WHERE component_code = 'list' AND deleted = false
        ORDER BY id LIMIT 1;
        IF v_component_id IS NULL THEN
          RAISE EXCEPTION '07 ENTITY: 库内无 list 组件，无法建 props';
        END IF;
        INSERT INTO platformresource.pr_component_props (
          is_template, component_id, component_code, data_source, schema_version,
          props, name, status, sort, creator, deleted, tenant_id
        ) VALUES (
          true, v_component_id, 'list',
          '{"businessCategory":"dynamic","entityTypeCode":"standard","dataKind":"entity"}',
          'list@1', v_list_ui, 'standard-ENTITY-' || v_layout_id,
          1, 0, 'seed-standard-07', false, 0
        ) RETURNING id INTO v_entity_props_id;
      ELSE
        INSERT INTO platformresource.pr_component_props (
          is_template, component_id, component_code, data_source, schema_version,
          props, name, status, sort, description, creator, deleted, tenant_id
        )
        SELECT
          true, component_id, 'list',
          '{"businessCategory":"dynamic","entityTypeCode":"standard","dataKind":"entity"}',
          schema_version,
          COALESCE(NULLIF(trim(props), ''), v_list_ui),
          'standard-ENTITY-' || v_layout_id,
          status, sort, description, 'seed-standard-07', false, 0
        FROM platformresource.pr_component_props
        WHERE id = v_donor
        RETURNING id INTO v_entity_props_id;
      END IF;
    END IF;

    INSERT INTO dm_data_tab_layout (
      tenant_id, layout_id, entity_type_code, column_kind, tab_id, props_id,
      enabled, column_meta, creator, deleted
    ) VALUES (
      v_tenant, v_layout_id, 'standard', 'ENTITY', 'standard', v_entity_props_id,
      true,
      jsonb_build_object(
        'label', '标准条目',
        'columnSection', 'who',
        'entityEntityTypeCode', 'standard',
        'columnOrder', 0
      ),
      'seed-standard-07', false
    );
  ELSE
    UPDATE dm_data_tab_layout
    SET tab_id = 'standard',
        column_meta = COALESCE(column_meta, '{}'::jsonb)
          || jsonb_build_object(
            'label', '标准条目',
            'columnSection', 'who',
            'entityEntityTypeCode', 'standard'
          ),
        enabled = true,
        updater = 'seed-standard-07',
        update_time = CURRENT_TIMESTAMP
    WHERE layout_id = v_layout_id
      AND column_kind = 'ENTITY'
      AND deleted = false;
  END IF;

  -- 实体 props：契约三字段 + 列表默认展示（标准编号优先，不用系统短码当主列）
  SELECT props_id INTO v_entity_props_id
  FROM dm_data_tab_layout
  WHERE layout_id = v_layout_id
    AND column_kind = 'ENTITY'
    AND deleted = false
  LIMIT 1;

  IF v_entity_props_id IS NOT NULL THEN
    SELECT COALESCE(data_source::jsonb, '{}'::jsonb), COALESCE(props::jsonb, '{}'::jsonb)
    INTO v_ds, v_props
    FROM platformresource.pr_component_props
    WHERE id = v_entity_props_id AND deleted = false;

    IF v_ds IS NOT NULL THEN
      v_ds := jsonb_set(
        jsonb_set(
          jsonb_set(v_ds, '{businessCategory}', '"dynamic"', true),
          '{entityTypeCode}', '"standard"', true
        ),
        '{dataKind}', '"entity"', true
      );
      v_ds := v_ds - 'queryContext';

      -- 权威：展示列 / 搜索范围写进 props；禁止只改 data_source 留下 name+code
      v_props := COALESCE(v_props, '{}'::jsonb)
        || jsonb_build_object(
          'displayContent', jsonb_build_array('standard_no', 'name'),
          'emptyText', '暂无标准条目',
          'search', jsonb_build_object(
            'showSearch', true,
            'searchPlaceholder', '搜索标准编号或名称…',
            'searchScopeSelectedOptions', jsonb_build_array('standard_no', 'name')
          )
        );

      UPDATE platformresource.pr_component_props
      SET data_source = v_ds::text,
          props = v_props::text,
          updater = 'seed-standard-07',
          update_time = CURRENT_TIMESTAMP
      WHERE id = v_entity_props_id AND deleted = false;
    END IF;
  END IF;

  -- 4) 详情栏开启
  IF NOT EXISTS (
    SELECT 1 FROM dm_data_tab_layout
    WHERE layout_id = v_layout_id AND column_kind = 'DETAIL' AND deleted = false
  ) THEN
    INSERT INTO dm_data_tab_layout (
      tenant_id, layout_id, entity_type_code, column_kind, tab_id, props_id,
      enabled, column_meta, creator, deleted
    ) VALUES (
      v_tenant, v_layout_id, 'standard', 'DETAIL', 'detail', NULL,
      true,
      jsonb_build_object('label', '详情', 'columnSection', 'what'),
      'seed-standard-07', false
    );
  ELSE
    UPDATE dm_data_tab_layout
    SET enabled = true,
        tab_id = COALESCE(NULLIF(trim(tab_id), ''), 'detail'),
        column_meta = COALESCE(column_meta, '{}'::jsonb)
          || jsonb_build_object('label', '详情', 'columnSection', 'what'),
        updater = 'seed-standard-07',
        update_time = CURRENT_TIMESTAMP
    WHERE layout_id = v_layout_id
      AND column_kind = 'DETAIL'
      AND deleted = false;
  END IF;

  -- 5) 栏身份
  SELECT
    COALESCE(NULLIF(trim(tab_id), ''), 'standard-1'),
    COALESCE(NULLIF(column_meta->>'columnKey', ''), 'standard')
  INTO v_cat_tab, v_cat_key
  FROM dm_data_tab_layout
  WHERE layout_id = v_layout_id
    AND column_kind = 'CATEGORY'
    AND deleted = false
  LIMIT 1;

  v_cat_identity := 'CATEGORY:' || v_cat_key || ':' || v_cat_tab;
  v_entity_identity := 'ENTITY:standard';
  v_detail_identity := 'DETAIL:detail';

  -- 6) 清旧边，写 CE filter / write + 实体→详情
  UPDATE dm_data_tab_column_relation
  SET deleted = true,
      updater = 'seed-standard-07',
      update_time = CURRENT_TIMESTAMP
  WHERE layout_id = v_layout_id
    AND deleted = false;

  DELETE FROM dm_data_tab_column_relation
  WHERE layout_id = v_layout_id AND deleted = true;

  INSERT INTO dm_data_tab_column_relation (
    tenant_id, layout_id, entity_type_code, edge_id,
    from_column_identity, to_column_identity, relation_kind,
    from_type_code, to_type_code, relation_meta, creator, deleted
  ) VALUES
    (
      v_tenant, v_layout_id, 'standard', 'edge-standard-ce-filter',
      v_cat_identity, v_entity_identity, 'CATEGORY_ENTITY',
      'standard', 'standard',
      jsonb_build_object('edgeAction', 'filter', 'enabledInteractions', '[]'::jsonb),
      'seed-standard-07', false
    ),
    (
      v_tenant, v_layout_id, 'standard', 'edge-standard-ce-write',
      v_entity_identity, v_cat_identity, 'CATEGORY_ENTITY',
      'standard', 'standard',
      jsonb_build_object(
        'edgeAction', 'write',
        'enabledInteractions', jsonb_build_array('dragAssociate')
      ),
      'seed-standard-07', false
    ),
    (
      v_tenant, v_layout_id, 'standard', 'edge-standard-ed-filter',
      v_entity_identity, v_detail_identity, 'ENTITY_DETAIL',
      'standard', 'standard',
      jsonb_build_object('edgeAction', 'filter', 'enabledInteractions', '[]'::jsonb),
      'seed-standard-07', false
    );

  RAISE NOTICE 'standard layout % → CATEGORY+ENTITY+DETAIL (SINGLE)', v_layout_id;
END $$;
