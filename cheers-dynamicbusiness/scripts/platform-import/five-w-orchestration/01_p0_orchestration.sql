-- ============================================================================
-- five-w-orchestration · 01 P0 编排 seed
-- 配方：equipment / region / inspection_item / inspection_content
-- 前置：Flyway V63；dynamic_entity_type 已存在对应 code（通常 system seed 已导入）
-- 只写编排头。开哪些栏认 dm_data_tab_layout。
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'equipment' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip P0 equipment: dynamic_entity_type 不存在';
  ELSE
    PERFORM _seed_five_w_semantic(
      v_tenant, 'equipment',
      'VIEW_DETAIL', '{"bindLayer":"ENTITY"}'::jsonb,
      'NONE', '{}'::jsonb, 'LIST_ROW');
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'region' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip P0 region: dynamic_entity_type 不存在';
  ELSE
    PERFORM _seed_five_w_semantic(
      v_tenant, 'region',
      'VIEW_DETAIL', '{"bindLayer":"ENTITY"}'::jsonb,
      'NONE', '{}'::jsonb, 'CATEGORY_NODE');
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'inspection_item' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip P0 inspection_item: dynamic_entity_type 不存在';
  ELSE
    -- 标准检查库：本页目标=检查项；点列表这一行是当前对象；What=看详情
    PERFORM _seed_five_w_semantic(
      v_tenant, 'inspection_item',
      'VIEW_DETAIL', '{"bindLayer":"ENTITY"}'::jsonb,
      'NONE', '{}'::jsonb, 'LIST_ROW');
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'inspection_content' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip P0 inspection_content: dynamic_entity_type 不存在';
  ELSE
    PERFORM _seed_five_w_semantic(
      v_tenant, 'inspection_content',
      'VIEW_DETAIL', '{"bindLayer":"ENTITY"}'::jsonb,
      'NONE', '{}'::jsonb, 'LIST_ROW');
  END IF;
END $$;
