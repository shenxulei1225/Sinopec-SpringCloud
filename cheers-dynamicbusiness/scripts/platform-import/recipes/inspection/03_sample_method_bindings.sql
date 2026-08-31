-- ============================================================================
-- recipes/inspection · 03 可选样例方法选用（V80 通用表）
-- 仅当 dynamic_sop_method_binding 存在时写入；无表则跳过
-- 不强制写实例绑定（需真实设备 id；由管理端「从模板创建实例」产生）
-- creator = recipe-inspection，便于 remove.sql 识别
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
BEGIN
  IF to_regclass('dynamicbusiness.dynamic_sop_method_binding') IS NULL THEN
    RAISE NOTICE 'skip 03: dynamic_sop_method_binding 不存在（需 Flyway V80）';
    RETURN;
  END IF;

  IF to_regclass('dynamicbusiness.ent_inspection_item_t1') IS NULL THEN
    RAISE NOTICE 'skip 03: ent_inspection_item_t1 不存在';
    RETURN;
  END IF;

  IF to_regclass('dynamicbusiness.ent_sop_t1') IS NULL THEN
    RAISE NOTICE 'skip 03: ent_sop_t1 不存在';
    RETURN;
  END IF;

  INSERT INTO dynamic_sop_method_binding (
    tenant_id, subject_type, subject_id,
    dimension_key, dimension_value, sop_template_id,
    creator, create_time, updater, update_time, deleted
  )
  SELECT
    v_tenant,
    'inspection_item',
    i.id,
    'execution_means',
    v.means,
    s.id,
    'recipe-inspection',
    NOW(),
    'recipe-inspection',
    NOW(),
    false
  FROM ent_inspection_item_t1 i
  CROSS JOIN (
    VALUES
      ('SOP-TPL-MANUAL-LEAK', 'MANUAL'),
      ('SOP-TPL-UAV-LEAK', 'UAV')
  ) AS v(sop_code, means)
  JOIN ent_sop_t1 s
    ON s.tenant_id = v_tenant
   AND s.deleted = false
   AND s.is_template = true
   AND s.code = v.sop_code
  WHERE i.deleted = false
    AND i.tenant_id = v_tenant
    AND (
      i.code ILIKE '%leak%'
      OR i.name LIKE '%跑冒滴漏%'
      OR i.name LIKE '%泄漏%'
    )
    AND NOT EXISTS (
      SELECT 1
      FROM dynamic_sop_method_binding x
      WHERE x.tenant_id = v_tenant
        AND x.deleted = false
        AND x.subject_type = 'inspection_item'
        AND x.subject_id = i.id
        AND x.dimension_key = 'execution_means'
        AND x.dimension_value = v.means
    );

  RAISE NOTICE 'recipes/inspection: 样例方法选用已幂等写入（匹配泄漏类检查项 × MANUAL/UAV）';
END $$;
