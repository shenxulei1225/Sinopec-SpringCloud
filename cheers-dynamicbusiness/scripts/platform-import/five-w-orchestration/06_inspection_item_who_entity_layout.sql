-- ============================================================================
-- five-w-orchestration · 06 标准检查库 Who 实体列对齐
-- 原则：编排语义在 dm_five_w_*；画面 Who 列当前仍读 dm_data_tab_layout。
-- 本页目标=检查项 → layout 须有开启的 ENTITY 行；不得用 MODEL 行冒充检查项。
-- 前置：01_p0 已把 inspection_item 选层/What 配成实体层+看详情。
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
  v_props bigint;
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'inspection_item' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip 06: inspection_item 类型不存在';
    RETURN;
  END IF;

  -- 误配：Who 型号 Tab 绑标准检查库（查的是型号，不是检查项）
  UPDATE dm_data_tab_layout
  SET deleted = true,
      enabled = false,
      updater = 'seed-06',
      update_time = CURRENT_TIMESTAMP
  WHERE entity_type_code = 'inspection_item'
    AND column_kind = 'MODEL'
    AND deleted = false;

  SELECT p.id INTO v_props
  FROM platformresource.pr_component_props p
  WHERE p.deleted = false
    AND p.component_code = 'list'
    AND p.data_source ILIKE '%"entityTypeCode":"inspection_item"%'
    AND p.data_source ILIKE '%"dataKind":"entity"%'
  ORDER BY p.id DESC
  LIMIT 1;

  IF v_props IS NULL THEN
    RAISE NOTICE 'skip 06 ENTITY insert: 未找到 inspection_item 实体列表 props';
  ELSIF NOT EXISTS (
    SELECT 1 FROM dm_data_tab_layout
    WHERE tenant_id = v_tenant
      AND entity_type_code = 'inspection_item'
      AND column_kind = 'ENTITY'
      AND deleted = false
  ) THEN
    INSERT INTO dm_data_tab_layout (
      tenant_id, entity_type_code, column_kind, perspective_id, props_id,
      enabled, category_column, creator, deleted
    ) VALUES (
      v_tenant,
      'inspection_item',
      'ENTITY',
      'entity-default',
      v_props,
      true,
      '{"label":"检查项","widthPx":320,"columnOrder":0,"workspaceBand":"WHO","entityEntityTypeCode":"inspection_item"}'::jsonb,
      'seed-06',
      false
    );
  ELSE
    UPDATE dm_data_tab_layout
    SET enabled = true,
        props_id = COALESCE(props_id, v_props),
        category_column = COALESCE(
          category_column,
          '{"label":"检查项","widthPx":320,"columnOrder":0,"workspaceBand":"WHO","entityEntityTypeCode":"inspection_item"}'::jsonb
        ),
        updater = 'seed-06',
        update_time = CURRENT_TIMESTAMP
    WHERE entity_type_code = 'inspection_item'
      AND column_kind = 'ENTITY'
      AND deleted = false
      AND tenant_id = v_tenant;
  END IF;
END $$;
