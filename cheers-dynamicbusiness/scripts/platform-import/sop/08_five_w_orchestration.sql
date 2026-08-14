-- ============================================================================
-- sop · 08 五维编排（数据 Tab Who/What）
-- 配方对齐设备台账：选实体看详情；分类 / 型号 / 实体三栏
-- 修复：V56 软删空壳编排头后，可能留下「有 Who、无语义块」→ getBundle 500
-- 前置：类型 sop 已存在；库内已有 _seed_five_w_* 辅助函数（Flyway five-w）
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'sop' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip sop five-w: dynamic_entity_type 不存在';
    RETURN;
  END IF;

  -- 含软删行一并恢复（WHERE 不按 deleted 过滤）
  PERFORM _seed_five_w_semantic(
    v_tenant,
    'sop',
    'ENTITY',
    'VIEW_DETAIL',
    '{"bindLayer":"ENTITY"}'::jsonb,
    'NONE',
    '{}'::jsonb
  );

  PERFORM _seed_five_w_replace_who_slots(v_tenant, 'sop');

  PERFORM _seed_five_w_who_slot(
    v_tenant, 'sop', 'CATEGORY', 'sop-category', NULL,
    true, '["categoryId"]'::jsonb, NULL,
    '{"label":"标准作业流程SOP","categoryTypeCode":"sop"}'::jsonb);
  PERFORM _seed_five_w_who_slot(
    v_tenant, 'sop', 'MODEL', 'sop-model', NULL,
    true, '["modelId"]'::jsonb, NULL, NULL);
  PERFORM _seed_five_w_who_slot(
    v_tenant, 'sop', 'ENTITY', 'sop-entity', NULL,
    true, '["entityId"]'::jsonb, 'rowSelection', NULL);

  -- 数据 Tab 布局：V56 只改了 entity_type_code，category_column 里可能仍写 field_work_standard
  UPDATE dm_data_tab_layout
  SET
    category_column = jsonb_set(
      jsonb_set(
        COALESCE(category_column, '{}'::jsonb),
        '{categoryTypeCode}',
        '"sop"'::jsonb,
        true
      ),
      '{label}',
      '"标准作业流程SOP"'::jsonb,
      true
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
  WHERE entity_type_code = 'sop'
    AND tenant_id = v_tenant
    AND deleted = false
    AND column_kind = 'CATEGORY'
    AND (
      category_column IS NULL
      OR category_column->>'categoryTypeCode' IS DISTINCT FROM 'sop'
      OR category_column->>'categoryTypeCode' = 'field_work_standard'
    );
END $$;
