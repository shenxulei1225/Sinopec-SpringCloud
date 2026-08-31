-- ============================================================================
-- action · 08 数据页各栏 propsId（与创建类型后 writeDefaultDataTabLayouts 同口径）
-- 权威：栏身份在 dm_data_tab_layout；展示配置在 platformresource.pr_component_props。
-- 禁止：读路径猜 props；不覆盖已有非空 props_id。
-- 幂等：按 data_source 指纹复用已有模板行；缺则从同类模板克隆并改写 data_source。
-- 前置：06 已挂 data_layout_id；库内至少有一份 tree/list 模板可作克隆源（system/sop 等 seed）。
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant      CONSTANT bigint := 1;
  v_layout      bigint;
  v_props       bigint;
  v_donor       bigint;
  v_component_id bigint;
  v_tree_ui     text := $p${
    "selectMode":"single",
    "categoryAssociation":"MULTI",
    "search":{"showSearch":true,"searchPlaceholder":"搜索分类","searchScopeSelectedOptions":[]},
    "autoCrud":true,
    "defaultExpandAll":{"expandAll":false,"expandLevel":1},
    "autoLoad":true,
    "emptyText":"暂无分类",
    "displayContent":["name"]
  }$p$;
  v_list_ui     text := $p${"displayContent":["name"],"emptyText":"暂无实体"}$p$;
  v_model_ui    text := $p${"displayContent":["name"],"emptyText":"暂无模型"}$p$;
BEGIN
  SELECT data_layout_id INTO v_layout
  FROM dynamic_entity_type
  WHERE code = 'action' AND deleted = false AND tenant_id = v_tenant
  LIMIT 1;

  IF v_layout IS NULL THEN
    RAISE NOTICE 'skip 08: action 尚未挂 data_layout_id（先跑 06）';
    RETURN;
  END IF;

  -- ---------- CATEGORY：分类树 ----------
  SELECT props_id INTO v_props
  FROM dm_data_tab_layout
  WHERE layout_id = v_layout AND column_kind = 'CATEGORY' AND deleted = false
  LIMIT 1;

  IF v_props IS NULL THEN
    SELECT p.id INTO v_props
    FROM platformresource.pr_component_props p
    WHERE p.deleted = false
      AND p.component_code = 'tree'
      AND p.is_template = true
      AND COALESCE(p.data_source::jsonb->>'businessCategory', '') = 'category'
      AND COALESCE(p.data_source::jsonb->>'entityTypeCode', '') = 'action'
    ORDER BY p.id DESC
    LIMIT 1;

    IF v_props IS NULL THEN
      SELECT p.id, p.component_id INTO v_donor, v_component_id
      FROM platformresource.pr_component_props p
      WHERE p.deleted = false
        AND p.component_code = 'tree'
        AND p.is_template = true
        AND COALESCE(p.data_source::jsonb->>'businessCategory', '') = 'category'
      ORDER BY p.id DESC
      LIMIT 1;

      IF v_donor IS NULL THEN
        -- 无克隆源：写入与 buildCategoryTreeUiDefaults 同口径的最小模板
        SELECT component_id INTO v_component_id
        FROM platformresource.pr_component_props
        WHERE component_code = 'tree' AND deleted = false
        ORDER BY id LIMIT 1;
        IF v_component_id IS NULL THEN
          RAISE EXCEPTION '08 CATEGORY: 库内无 tree 组件配置，无法为 action 建 props';
        END IF;
        INSERT INTO platformresource.pr_component_props (
          is_template, component_id, component_code, data_source, schema_version,
          props, name, status, sort, creator, deleted, tenant_id
        ) VALUES (
          true, v_component_id, 'tree',
          '{"businessCategory":"category","entityTypeCode":"action","dataKind":"entity"}',
          'tree@1', v_tree_ui, 'dm-tree:action:action:动作库',
          1, 0, 'seed-action-08', false, 0
        ) RETURNING id INTO v_props;
      ELSE
        INSERT INTO platformresource.pr_component_props (
          is_template, component_id, component_code, data_source, schema_version,
          props, name, status, sort, description, creator, deleted, tenant_id
        )
        SELECT
          true, component_id, 'tree',
          '{"businessCategory":"category","entityTypeCode":"action","dataKind":"entity"}',
          schema_version, props, 'dm-tree:action:action:动作库',
          status, sort, description, 'seed-action-08', false, 0
        FROM platformresource.pr_component_props
        WHERE id = v_donor
        RETURNING id INTO v_props;
      END IF;
    END IF;

    UPDATE dm_data_tab_layout
    SET props_id = v_props,
        updater = 'seed-action-08',
        update_time = CURRENT_TIMESTAMP
    WHERE layout_id = v_layout
      AND column_kind = 'CATEGORY'
      AND deleted = false
      AND props_id IS NULL;

    RAISE NOTICE 'action CATEGORY props_id → %', v_props;
  ELSE
    RAISE NOTICE 'skip 08 CATEGORY: 已有 props_id=%', v_props;
  END IF;

  -- ---------- ENTITY：实体列表 ----------
  v_props := NULL;
  SELECT props_id INTO v_props
  FROM dm_data_tab_layout
  WHERE layout_id = v_layout AND column_kind = 'ENTITY' AND deleted = false
  LIMIT 1;

  IF v_props IS NULL THEN
    SELECT p.id INTO v_props
    FROM platformresource.pr_component_props p
    WHERE p.deleted = false
      AND p.component_code = 'list'
      AND p.is_template = true
      AND COALESCE(p.data_source::jsonb->>'businessCategory', '') = 'dynamic'
      AND COALESCE(p.data_source::jsonb->>'entityTypeCode', '') = 'action'
      AND COALESCE(p.data_source::jsonb->>'dataKind', '') = 'entity'
    ORDER BY p.id DESC
    LIMIT 1;

    IF v_props IS NULL THEN
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
          RAISE EXCEPTION '08 ENTITY: 库内无 list 组件配置，无法为 action 建 props';
        END IF;
        INSERT INTO platformresource.pr_component_props (
          is_template, component_id, component_code, data_source, schema_version,
          props, name, status, sort, creator, deleted, tenant_id
        ) VALUES (
          true, v_component_id, 'list',
          '{"businessCategory":"dynamic","entityTypeCode":"action","dataKind":"entity"}',
          'list@1', v_list_ui, 'action-ENTITY-' || v_layout,
          1, 0, 'seed-action-08', false, 0
        ) RETURNING id INTO v_props;
      ELSE
        INSERT INTO platformresource.pr_component_props (
          is_template, component_id, component_code, data_source, schema_version,
          props, name, status, sort, description, creator, deleted, tenant_id
        )
        SELECT
          true, component_id, 'list',
          '{"businessCategory":"dynamic","entityTypeCode":"action","dataKind":"entity"}',
          schema_version, props, 'action-ENTITY-' || v_layout,
          status, sort, description, 'seed-action-08', false, 0
        FROM platformresource.pr_component_props
        WHERE id = v_donor
        RETURNING id INTO v_props;
      END IF;
    END IF;

    UPDATE dm_data_tab_layout
    SET props_id = v_props,
        updater = 'seed-action-08',
        update_time = CURRENT_TIMESTAMP
    WHERE layout_id = v_layout
      AND column_kind = 'ENTITY'
      AND deleted = false
      AND props_id IS NULL;

    RAISE NOTICE 'action ENTITY props_id → %', v_props;
  ELSE
    RAISE NOTICE 'skip 08 ENTITY: 已有 props_id=%', v_props;
  END IF;

  -- ---------- MODEL：型号列表 ----------
  v_props := NULL;
  SELECT props_id INTO v_props
  FROM dm_data_tab_layout
  WHERE layout_id = v_layout AND column_kind = 'MODEL' AND deleted = false
  LIMIT 1;

  IF v_props IS NULL THEN
    SELECT p.id INTO v_props
    FROM platformresource.pr_component_props p
    WHERE p.deleted = false
      AND p.component_code = 'list'
      AND p.is_template = true
      AND COALESCE(p.data_source::jsonb->>'businessCategory', '') = 'dynamic'
      AND COALESCE(p.data_source::jsonb->>'entityTypeCode', '') = 'action'
      AND COALESCE(p.data_source::jsonb->>'dataKind', '') = 'model'
    ORDER BY p.id DESC
    LIMIT 1;

    IF v_props IS NULL THEN
      SELECT p.id INTO v_donor
      FROM platformresource.pr_component_props p
      WHERE p.deleted = false
        AND p.component_code = 'list'
        AND p.is_template = true
        AND COALESCE(p.data_source::jsonb->>'businessCategory', '') = 'dynamic'
        AND COALESCE(p.data_source::jsonb->>'dataKind', '') = 'model'
      ORDER BY p.id DESC
      LIMIT 1;

      IF v_donor IS NULL THEN
        SELECT component_id INTO v_component_id
        FROM platformresource.pr_component_props
        WHERE component_code = 'list' AND deleted = false
        ORDER BY id LIMIT 1;
        IF v_component_id IS NULL THEN
          RAISE EXCEPTION '08 MODEL: 库内无 list 组件配置，无法为 action 建 props';
        END IF;
        INSERT INTO platformresource.pr_component_props (
          is_template, component_id, component_code, data_source, schema_version,
          props, name, status, sort, creator, deleted, tenant_id
        ) VALUES (
          true, v_component_id, 'list',
          '{"businessCategory":"dynamic","entityTypeCode":"action","dataKind":"model"}',
          'list@1', v_model_ui, 'action-MODEL-' || v_layout,
          1, 0, 'seed-action-08', false, 0
        ) RETURNING id INTO v_props;
      ELSE
        INSERT INTO platformresource.pr_component_props (
          is_template, component_id, component_code, data_source, schema_version,
          props, name, status, sort, description, creator, deleted, tenant_id
        )
        SELECT
          true, component_id, 'list',
          '{"businessCategory":"dynamic","entityTypeCode":"action","dataKind":"model"}',
          schema_version, props, 'action-MODEL-' || v_layout,
          status, sort, description, 'seed-action-08', false, 0
        FROM platformresource.pr_component_props
        WHERE id = v_donor
        RETURNING id INTO v_props;
      END IF;
    END IF;

    UPDATE dm_data_tab_layout
    SET props_id = v_props,
        updater = 'seed-action-08',
        update_time = CURRENT_TIMESTAMP
    WHERE layout_id = v_layout
      AND column_kind = 'MODEL'
      AND deleted = false
      AND props_id IS NULL;

    RAISE NOTICE 'action MODEL props_id → %', v_props;
  ELSE
    RAISE NOTICE 'skip 08 MODEL: 已有 props_id=%', v_props;
  END IF;
END $$;
