-- ============================================================================
-- recipes/inspection · 09 从旧方法绑定迁移 SOP 标准检查项包（写入）
-- 使用前提：
--   1) 先执行 08 审计并人工确认迁移名单；
--   2) 已执行 Flyway V104（dynamic_sop_item_pack / dynamic_sop_scope_rule）。
-- 注意：本脚本只迁“标准检查项包”，不自动生成 MODEL 范围规则。
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
BEGIN
  IF to_regclass('dynamicbusiness.dynamic_sop_method_binding') IS NULL THEN
    RAISE EXCEPTION 'dynamic_sop_method_binding 不存在，请先确认 V80';
  END IF;
  IF to_regclass('dynamicbusiness.dynamic_sop_item_pack') IS NULL THEN
    RAISE EXCEPTION 'dynamic_sop_item_pack 不存在，请先执行 Flyway V104';
  END IF;
  IF to_regclass('dynamicbusiness.ent_sop_t1') IS NULL THEN
    RAISE EXCEPTION 'ent_sop_t1 不存在，无法校验 SOP 有效性';
  END IF;
  IF to_regclass('dynamicbusiness.ent_inspection_item_t1') IS NULL THEN
    RAISE EXCEPTION 'ent_inspection_item_t1 不存在，无法校验检查项有效性';
  END IF;

  -- A. 覆盖重建标准检查项包（仅迁 inspection_item 旧绑定）。
  --    保留 SOP 内顺序：按检查项 id 升序生成 sort_no（10,20,30...）。
  DELETE FROM dynamic_sop_item_pack
  WHERE tenant_id = v_tenant
    AND deleted = FALSE
    AND sop_id IN (
      SELECT DISTINCT b.sop_template_id
      FROM dynamic_sop_method_binding b
      WHERE b.deleted = FALSE
        AND b.tenant_id = v_tenant
        AND b.subject_type = 'inspection_item'
    );

  INSERT INTO dynamic_sop_item_pack (
    tenant_id,
    sop_id,
    inspection_item_id,
    required,
    sort_no,
    note,
    creator,
    create_time,
    updater,
    update_time,
    deleted
  )
  SELECT
    v_tenant,
    x.sop_template_id,
    x.inspection_item_id,
    TRUE,
    x.sort_no,
    'migrated-from-method-binding',
    'recipe-sop-standard-pack',
    NOW(),
    'recipe-sop-standard-pack',
    NOW(),
    FALSE
  FROM (
    SELECT
      b.sop_template_id,
      b.subject_id AS inspection_item_id,
      ROW_NUMBER() OVER (
        PARTITION BY b.sop_template_id
        ORDER BY b.subject_id
      ) * 10 AS sort_no
    FROM dynamic_sop_method_binding b
    JOIN ent_sop_t1 s
      ON s.id = b.sop_template_id
     AND s.deleted = FALSE
    JOIN ent_inspection_item_t1 i
      ON i.id = b.subject_id
     AND i.deleted = FALSE
    WHERE b.deleted = FALSE
      AND b.tenant_id = v_tenant
      AND b.subject_type = 'inspection_item'
    GROUP BY b.sop_template_id, b.subject_id
  ) x
  WHERE NOT EXISTS (
    SELECT 1
    FROM dynamic_sop_item_pack dup
    WHERE dup.deleted = FALSE
      AND dup.tenant_id = v_tenant
      AND dup.sop_id = x.sop_template_id
      AND dup.inspection_item_id = x.inspection_item_id
  );

  RAISE NOTICE 'SOP 标准检查项包迁移完成：tenant=%', v_tenant;
END $$;
