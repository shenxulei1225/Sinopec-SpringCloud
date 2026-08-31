-- ============================================================================
-- action · 11 模型管理 Tab：型号列表展示配置（与建类型后 ensureModelTabListPropsId 同口径）
-- 权威：展示配置在 platformresource.pr_component_props（名「模型管理列表-action」）；
--       关联写在 dm_model_tab_category.model_list_props_id。
-- 禁止：打开页面读路径再补；不覆盖已有非空 model_list_props_id。
-- 幂等：已有同名模板则复用；仅当 model_list_props_id 为空时挂上。
-- 前置：08 已写数据页栏 props（可复用 list 克隆源）；类型 code=action 已存在。
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant      CONSTANT bigint := 1;
  v_props       bigint;
  v_donor       bigint;
  v_component_id bigint;
  v_model_ui    text := $p${"displayContent":["name"],"emptyText":"暂无模型"}$p$;
BEGIN
  -- ---------- 1. 确保「模型管理列表-action」模板行 ----------
  SELECT p.id INTO v_props
  FROM platformresource.pr_component_props p
  WHERE p.deleted = false
    AND p.component_code = 'list'
    AND p.name = '模型管理列表-action'
  ORDER BY p.id DESC
  LIMIT 1;

  IF v_props IS NULL THEN
    -- 优先复用数据页 MODEL 栏同源模板（dataKind=model）
    SELECT p.id INTO v_donor
    FROM platformresource.pr_component_props p
    WHERE p.deleted = false
      AND p.component_code = 'list'
      AND p.is_template = true
      AND COALESCE(p.data_source::jsonb->>'businessCategory', '') = 'dynamic'
      AND COALESCE(p.data_source::jsonb->>'entityTypeCode', '') = 'action'
      AND COALESCE(p.data_source::jsonb->>'dataKind', '') = 'model'
    ORDER BY p.id DESC
    LIMIT 1;

    IF v_donor IS NULL THEN
      SELECT p.id INTO v_donor
      FROM platformresource.pr_component_props p
      WHERE p.deleted = false
        AND p.component_code = 'list'
        AND p.is_template = true
        AND COALESCE(p.data_source::jsonb->>'businessCategory', '') = 'dynamic'
        AND COALESCE(p.data_source::jsonb->>'dataKind', '') = 'model'
      ORDER BY p.id DESC
      LIMIT 1;
    END IF;

    IF v_donor IS NULL THEN
      SELECT component_id INTO v_component_id
      FROM platformresource.pr_component_props
      WHERE component_code = 'list' AND deleted = false
      ORDER BY id LIMIT 1;
      IF v_component_id IS NULL THEN
        RAISE EXCEPTION '11: 库内无 list 组件配置，无法为 action 建模型管理列表 props';
      END IF;
      INSERT INTO platformresource.pr_component_props (
        is_template, component_id, component_code, data_source, schema_version,
        props, name, status, sort, creator, deleted, tenant_id
      ) VALUES (
        true, v_component_id, 'list',
        '{"businessCategory":"dynamic","entityTypeCode":"action","dataKind":"model"}',
        'list@1', v_model_ui, '模型管理列表-action',
        1, 0, 'seed-action-11', false, 0
      ) RETURNING id INTO v_props;
    ELSE
      INSERT INTO platformresource.pr_component_props (
        is_template, component_id, component_code, data_source, schema_version,
        props, name, status, sort, description, creator, deleted, tenant_id
      )
      SELECT
        true, component_id, 'list',
        '{"businessCategory":"dynamic","entityTypeCode":"action","dataKind":"model"}',
        schema_version,
        COALESCE(NULLIF(props, ''), v_model_ui),
        '模型管理列表-action',
        status, sort, description, 'seed-action-11', false, 0
      FROM platformresource.pr_component_props
      WHERE id = v_donor
      RETURNING id INTO v_props;
    END IF;

    RAISE NOTICE 'action 模型管理列表 props 新建 → %', v_props;
  ELSE
    RAISE NOTICE 'skip 11 props: 已有 模型管理列表-action id=%', v_props;
  END IF;

  -- ---------- 2. 挂到 dm_model_tab_category.model_list_props_id ----------
  INSERT INTO dm_model_tab_category (
    tenant_id, entity_type_code, enabled, label, category_type_code,
    props_id, model_list_props_id, creator
  )
  VALUES (
    v_tenant, 'action', false, NULL, 'action',
    NULL, v_props, 'seed-action-11'
  )
  ON CONFLICT (tenant_id, entity_type_code) WHERE deleted = false
  DO UPDATE SET
    model_list_props_id = COALESCE(
      dm_model_tab_category.model_list_props_id,
      EXCLUDED.model_list_props_id
    ),
    category_type_code = COALESCE(
      NULLIF(trim(dm_model_tab_category.category_type_code), ''),
      EXCLUDED.category_type_code
    ),
    deleted = false,
    updater = 'seed-action-11',
    update_time = CURRENT_TIMESTAMP;

  RAISE NOTICE 'action dm_model_tab_category.model_list_props_id → %',
    (SELECT model_list_props_id FROM dm_model_tab_category
     WHERE entity_type_code = 'action' AND deleted = false AND tenant_id = v_tenant
     LIMIT 1);
END $$;
