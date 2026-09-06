-- ============================================================================
-- action · 07 默认目录编排头（与建类型 EntityTypeOrchestrationBootstrapService 同口径）
-- 权威：dm_catalog_orchestration；开哪些栏仍认 dm_data_tab_layout。
-- 禁止：覆盖已有编排行。
-- 幂等：已有 action 行则跳过。
-- 前置：类型 action 已存在；建议已跑 catalog-orchestration/00_helpers.sql。
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'action' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip 07: action 类型不存在';
    RETURN;
  END IF;

  IF EXISTS (
    SELECT 1 FROM dm_catalog_orchestration
    WHERE entity_type_code = 'action' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip 07: action 编排头已存在';
    RETURN;
  END IF;

  PERFORM dynamicbusiness._seed_catalog_orchestration(
    v_tenant,
    'action',
    'LIST_ROW'
  );

  RAISE NOTICE 'action 编排头已写入（点列表这一行）';
END $$;
