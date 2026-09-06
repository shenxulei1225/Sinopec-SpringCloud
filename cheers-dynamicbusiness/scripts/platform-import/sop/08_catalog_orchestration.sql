-- ============================================================================
-- sop · 08 目录编排头
-- 点列表这一行是当前对象；分类 / 型号 / 实体三栏认布局
-- 前置：类型 sop 已存在；库内已有 _seed_catalog_orchestration
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
    RAISE NOTICE 'skip sop catalog-orchestration: dynamic_entity_type 不存在';
    RETURN;
  END IF;

  -- 含软删行一并恢复（WHERE 不按 deleted 过滤）
  PERFORM _seed_catalog_orchestration(v_tenant, 'sop', 'LIST_ROW');

  -- 数据 Tab 布局：V56 只改了 entity_type_code，列扩展里可能仍写 field_work_standard
  UPDATE dm_data_tab_layout
  SET
    column_meta = jsonb_set(
      jsonb_set(
        COALESCE(column_meta, '{}'::jsonb),
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
      column_meta IS NULL
      OR column_meta->>'categoryTypeCode' IS DISTINCT FROM 'sop'
      OR column_meta->>'categoryTypeCode' = 'field_work_standard'
    );
END $$;
