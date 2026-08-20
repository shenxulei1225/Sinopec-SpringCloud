-- ============================================================================
-- five-w-orchestration · 02 P1 facility（§38/§42）
-- 编排头：点列表这一行是当前对象。开区域筛选栏 / 站场实体栏认布局。
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'facility' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip P1 facility: dynamic_entity_type 不存在';
    RETURN;
  END IF;

  PERFORM _seed_five_w_semantic(
    v_tenant, 'facility',
    'VIEW_DETAIL', '{"bindLayer":"ENTITY"}'::jsonb,
    'NONE', '{}'::jsonb, 'LIST_ROW');
END $$;
