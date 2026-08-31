-- ============================================================================
-- action · 06 挂载数据页布局（data_layout_id）
-- 权威：与创建类型时 DmWorkbenchLayoutService.ensureCatalogDataLayout 同口径——
--   从「通用台账（分类|型号|实体）」模版实例化，写回 dynamic_entity_type.data_layout_id。
-- 禁止：已有 data_layout_id 且栏行非空时覆盖；不编造异类型边。
-- 幂等：可重复执行。
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant   BIGINT := 1;
  v_tpl      BIGINT;
  v_layout   BIGINT;
  v_existing BIGINT;
  v_row_cnt  INT;
  v_name     TEXT := '数据页签·action';
  v_tab      TEXT := 'action-default';
BEGIN
  SELECT data_layout_id INTO v_existing
  FROM dynamic_entity_type
  WHERE code = 'action' AND deleted = FALSE AND tenant_id = v_tenant
  LIMIT 1;

  IF v_existing IS NOT NULL THEN
    SELECT COUNT(*) INTO v_row_cnt
    FROM dm_data_tab_layout
    WHERE layout_id = v_existing AND deleted = FALSE;
    IF v_row_cnt > 0 THEN
      RAISE NOTICE 'skip 06: action 已挂 data_layout_id=%（% 栏行）', v_existing, v_row_cnt;
      RETURN;
    END IF;
  END IF;

  SELECT id INTO v_tpl
  FROM dm_workbench_layout
  WHERE tenant_id = v_tenant
    AND deleted = FALSE
    AND is_template = TRUE
    AND name = '通用台账（分类|型号|实体）'
  ORDER BY id
  LIMIT 1;

  IF v_tpl IS NULL THEN
    RAISE EXCEPTION '缺少通用台账布局模版，无法为 action 挂载 data_layout_id';
  END IF;

  INSERT INTO dm_workbench_layout (
    name, is_template, source_template_id, creator, deleted, tenant_id, settings_json
  )
  SELECT
    v_name,
    FALSE,
    v_tpl,
    'seed-action-06',
    FALSE,
    v_tenant,
    COALESCE(settings_json, '{}'::jsonb)
  FROM dm_workbench_layout
  WHERE id = v_tpl
  RETURNING id INTO v_layout;

  -- 分类栏（FILTER）
  INSERT INTO dm_data_tab_layout (
    tenant_id, entity_type_code, column_kind, tab_id, props_id, enabled, column_meta,
    creator, deleted, layout_id
  ) VALUES (
    v_tenant, 'action', 'CATEGORY', v_tab, NULL, TRUE,
    jsonb_build_object(
      'label', '动作库',
      'columnKey', v_tab,
      'columnSection', 'FILTER',
      'categoryTypeCode', 'action',
      'widthPx', 240,
      'sectionWidthPx', 240
    ),
    'seed-action-06', FALSE, v_layout
  );

  -- 型号栏（OBJECT）
  INSERT INTO dm_data_tab_layout (
    tenant_id, entity_type_code, column_kind, tab_id, props_id, enabled, column_meta,
    creator, deleted, layout_id
  ) VALUES (
    v_tenant, 'action', 'MODEL', NULL, NULL, TRUE,
    jsonb_build_object(
      'label', '动作库',
      'columnSection', 'OBJECT',
      'modelEntityTypeCode', 'action',
      'widthPx', 192,
      'sectionWidthPx', 400
    ),
    'seed-action-06', FALSE, v_layout
  );

  -- 实体栏（OBJECT）
  INSERT INTO dm_data_tab_layout (
    tenant_id, entity_type_code, column_kind, tab_id, props_id, enabled, column_meta,
    creator, deleted, layout_id
  ) VALUES (
    v_tenant, 'action', 'ENTITY', NULL, NULL, TRUE,
    jsonb_build_object(
      'label', '动作库',
      'columnSection', 'OBJECT',
      'entityEntityTypeCode', 'action',
      'widthPx', 240
    ),
    'seed-action-06', FALSE, v_layout
  );

  -- 详情栏（模版默认关闭）
  INSERT INTO dm_data_tab_layout (
    tenant_id, entity_type_code, column_kind, tab_id, props_id, enabled, column_meta,
    creator, deleted, layout_id
  ) VALUES (
    v_tenant, 'action', 'DETAIL', NULL, NULL, FALSE,
    '{}'::jsonb,
    'seed-action-06', FALSE, v_layout
  );

  -- 同类型默认筛选边：分类→型号、型号→实体（与 applyInitialDefaultRelations 一致）
  INSERT INTO dm_data_tab_column_relation (
    entity_type_code, edge_id, from_column_identity, to_column_identity,
    relation_kind, from_type_code, to_type_code, relation_meta,
    creator, deleted, tenant_id, layout_id
  ) VALUES
  (
    'action',
    'edge-action-cm-filter',
    'CATEGORY:' || v_tab || ':' || v_tab,
    'MODEL:default',
    'CATEGORY_MODEL', 'action', 'action',
    '{"edgeRole":"filter","enabledInteractions":[]}'::jsonb,
    'seed-action-06', FALSE, v_tenant, v_layout
  ),
  (
    'action',
    'edge-action-me-filter',
    'MODEL:default',
    'ENTITY:default',
    'MODEL_ENTITY', 'action', 'action',
    '{"edgeRole":"filter","enabledInteractions":[]}'::jsonb,
    'seed-action-06', FALSE, v_tenant, v_layout
  );

  UPDATE dynamic_entity_type
  SET data_layout_id = v_layout,
      updater = 'seed-action-06',
      update_time = CURRENT_TIMESTAMP
  WHERE code = 'action'
    AND deleted = FALSE
    AND tenant_id = v_tenant;

  RAISE NOTICE 'action data_layout_id → %', v_layout;
END $$;
