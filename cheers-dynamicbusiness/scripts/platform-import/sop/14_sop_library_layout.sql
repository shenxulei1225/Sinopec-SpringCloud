-- ============================================================================
-- sop · 14 统一 SOP 库布局（单入口：分类 | 模板列表 | 详情）
-- 前置：V73–V75；类型 sop 已存在；data_layout_id 已挂工作台布局
-- 口径：列表只展示 is_template=true；SINGLE 型号栏软删；不另开门户
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
  v_layout_id bigint;
  v_entity_props_id bigint;
  v_ds jsonb;
BEGIN
  -- 1) 标准库：全网
  UPDATE dynamic_entity_type
  SET work_scope = 'NETWORK',
      model_workbench_mode = 'SINGLE',
      updater = 'seed-14',
      update_time = CURRENT_TIMESTAMP
  WHERE code = 'sop'
    AND tenant_id = v_tenant
    AND deleted = false
    AND (work_scope IS DISTINCT FROM 'NETWORK' OR model_workbench_mode IS DISTINCT FROM 'SINGLE');

  SELECT data_layout_id INTO v_layout_id
  FROM dynamic_entity_type
  WHERE code = 'sop' AND tenant_id = v_tenant AND deleted = false
  LIMIT 1;

  IF v_layout_id IS NULL THEN
    RAISE NOTICE 'skip 14: sop.data_layout_id 为空';
    RETURN;
  END IF;

  -- 2) SINGLE：软删型号栏（模板是实体行，不靠型号栏）
  UPDATE dm_data_tab_layout
  SET deleted = true,
      enabled = false,
      updater = 'seed-14',
      update_time = CURRENT_TIMESTAMP
  WHERE layout_id = v_layout_id
    AND column_kind = 'MODEL'
    AND deleted = false;

  -- 3) 分类栏：标签与类型码对齐（强制 tab_id，避免残留 field_work_standard-default）
  UPDATE dm_data_tab_layout
  SET tab_id = 'sop-1',
      column_meta = COALESCE(column_meta, '{}'::jsonb)
        || jsonb_build_object(
          'label', 'SOP分类',
          'categoryTypeCode', 'sop',
          'columnKey', 'sop',
          'columnSection', 'OBJECT'
        ),
      enabled = true,
      updater = 'seed-14',
      update_time = CURRENT_TIMESTAMP
  WHERE layout_id = v_layout_id
    AND column_kind = 'CATEGORY'
    AND deleted = false;

  -- 4) 实体栏：SOP 模板列表
  UPDATE dm_data_tab_layout
  SET column_meta = COALESCE(column_meta, '{}'::jsonb)
        || jsonb_build_object(
          'label', 'SOP模板',
          'columnSection', 'OBJECT',
          'entityEntityTypeCode', 'sop'
        ),
      enabled = true,
      updater = 'seed-14',
      update_time = CURRENT_TIMESTAMP
  WHERE layout_id = v_layout_id
    AND column_kind = 'ENTITY'
    AND deleted = false;

  -- 5) 详情栏保持开启
  UPDATE dm_data_tab_layout
  SET enabled = true,
      updater = 'seed-14',
      update_time = CURRENT_TIMESTAMP
  WHERE layout_id = v_layout_id
    AND column_kind = 'DETAIL'
    AND deleted = false;

  -- 6) 实体列表 props：只保留 data_source 三字段契约；is_template 过滤走 GET /dynamicbusiness/sop/templates
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
          updater = 'seed-14',
          update_time = CURRENT_TIMESTAMP
      WHERE id = v_entity_props_id AND deleted = false;
    END IF;
  END IF;

  -- 7) 旧 field_work_standard 分类种类软删（若仍有效）
  UPDATE dynamic_category_type
  SET deleted = true,
      updater = 'seed-14',
      update_time = CURRENT_TIMESTAMP
  WHERE category_type_code = 'field_work_standard'
    AND tenant_id = v_tenant
    AND deleted = false;

  UPDATE dynamic_category
  SET deleted = true,
      updater = 'seed-14',
      update_time = CURRENT_TIMESTAMP
  WHERE category_type_code = 'field_work_standard'
    AND tenant_id = v_tenant
    AND deleted = false;
END $$;
