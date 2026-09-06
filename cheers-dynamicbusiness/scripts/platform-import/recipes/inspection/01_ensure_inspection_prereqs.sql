-- ============================================================================
-- recipes/inspection · 01 前置自检（不建整套检查平台）
-- 类型 / 目录 / 样例实体：请先跑 inspection-method + system seed + catalog-orchestration
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'inspection_item' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE
      'recipes/inspection: 缺少类型 inspection_item — 请先跑 system seed / inspection-method，再跑本配方';
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE code = 'equipment' AND tenant_id = v_tenant AND deleted = false
  ) THEN
    RAISE NOTICE
      'recipes/inspection: 缺少类型 equipment — SOP 实例绑定宿主需要设备类型；请先导入设备台账 seed';
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM dm_catalog_orchestration
    WHERE entity_type_code = 'inspection_item'
      AND tenant_id = v_tenant
      AND deleted = false
  ) THEN
    RAISE NOTICE
      'recipes/inspection: 缺少 inspection_item 编排头 — 请先跑 ../catalog-orchestration/import.sh，再跑 02 补丁';
  END IF;

  IF to_regclass('dynamicbusiness.dynamic_sop_method_binding') IS NULL THEN
    RAISE NOTICE
      'recipes/inspection: 无表 dynamic_sop_method_binding — 请先 Flyway V80；03 样例绑定将跳过';
  END IF;
END $$;
