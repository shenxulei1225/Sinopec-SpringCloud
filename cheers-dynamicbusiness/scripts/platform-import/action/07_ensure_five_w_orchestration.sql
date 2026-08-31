-- ============================================================================
-- action · 07 默认五维编排（与建类型 EntityTypeOrchestrationBootstrapService · LEDGER_3COL 同口径）
-- 权威：dm_five_w_orchestration；开哪些栏仍认 dm_data_tab_layout。
-- 禁止：覆盖已有编排行的业务定制（how/what）；缺 helper 时直接写 semantic。
-- 幂等：已有 action 行则跳过。
-- 前置：类型 action 已存在；建议已跑 five-w-orchestration/00_helpers.sql（本脚本不依赖 ledger 重载函数）。
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
    SELECT 1 FROM dm_five_w_orchestration
    WHERE entity_type_code = 'action' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE 'skip 07: action 五维编排已存在';
    RETURN;
  END IF;

  -- 与 _seed_five_w_semantic / 建类型台账三栏默认一致：点列表这一行 → 看实体详情
  PERFORM dynamicbusiness._seed_five_w_semantic(
    v_tenant,
    'action',
    'VIEW_DETAIL',
    '{"bindLayer":"ENTITY"}'::jsonb,
    'NONE',
    '{}'::jsonb,
    'LIST_ROW'
  );

  RAISE NOTICE 'action 五维编排已写入（LEDGER_3COL 默认）';
END $$;
