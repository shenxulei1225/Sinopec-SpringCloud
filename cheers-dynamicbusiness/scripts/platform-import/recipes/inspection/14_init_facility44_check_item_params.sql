-- ============================================================================
-- recipes/inspection · 14 facility=44 检查项参数初始化
--
-- ⛔ P2 停用（2026-09-11）：禁止再执行本脚本。
--   本脚本把「怎么查」写成 inspection_item→SOP 方法绑定，与定稿权威冲突：
--   怎么查 = 检查项 action_tree_json；SOP = 做什么（内容清单）。
--   错位数据请用 20 审计 + 21 定点软删收口；不要用 11 全表清 SOP。
--
-- 历史目标（已废弃，仅留档）：
-- 1) 为“已挂到型号的检查项”批量补方法选用（inspection_item -> sop）；
-- 2) 为 facility=44 的设备批量补宿主 SOP 参数包骨架（host_sop_param_pack）；
-- 3) 巡检方式覆盖 MANUAL / UAV / ROBOT / FIXED_CAMERA；
-- 4) SOP 映射按关键词分三类：储罐 / 工艺 / 安防。
--
-- 说明：
-- - 仅补“缺失项”，不删除、不覆盖已有配置；
-- - 宿主参数包尽量带标准 SOP 默认 paramsByNode（当前三条标准 SOP 多数为 {}）；
-- - 未命中关键词的检查项不会写入本次映射，需后续补规则。
-- ============================================================================

DO $$
BEGIN
  RAISE EXCEPTION
    'FORBIDDEN: recipes/inspection/14 is retired (P2). Use 20_audit + 21_clear for misplaced bindings; configure check methods on inspection_item.action_tree_json.';
END $$;

/* ---- 以下为历史正文（不再执行；保留供对照） ----

SET search_path TO dynamicbusiness, public;


DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
  v_facility CONSTANT bigint := 44;
  v_tank_sop_id bigint;
  v_process_sop_id bigint;
  v_security_sop_id bigint;
  v_method_inserted bigint := 0;
  v_equipment_updated bigint := 0;
BEGIN
  -- 0) 标准 SOP 存在性校验
  SELECT id INTO v_tank_sop_id
  FROM ent_sop_t1
  WHERE tenant_id = v_tenant
    AND deleted = false
    AND code = 'SOP-STD-TANK-INSPECTION'
  LIMIT 1;

  SELECT id INTO v_process_sop_id
  FROM ent_sop_t1
  WHERE tenant_id = v_tenant
    AND deleted = false
    AND code = 'SOP-STD-PROCESS-INSPECTION'
  LIMIT 1;

  SELECT id INTO v_security_sop_id
  FROM ent_sop_t1
  WHERE tenant_id = v_tenant
    AND deleted = false
    AND code = 'SOP-STD-SECURITY-INSPECTION'
  LIMIT 1;

  IF v_tank_sop_id IS NULL OR v_process_sop_id IS NULL OR v_security_sop_id IS NULL THEN
    RAISE EXCEPTION
      '缺少标准 SOP（储罐/工艺/安防）。请先执行 recipes/inspection/12_seed_macro_scope_sops.sql';
  END IF;

  -- 1) 批量补方法选用（仅模型已挂接的检查项，且命中关键词）
  WITH mapped_items AS (
    SELECT
      i.id AS inspection_item_id,
      CASE
        WHEN i.name LIKE '%储罐%' OR i.name LIKE '%液位%' OR i.name LIKE '%泄漏%' THEN v_tank_sop_id
        WHEN i.name LIKE '%工艺%' OR i.name LIKE '%阀%' OR i.name LIKE '%管线%' THEN v_process_sop_id
        WHEN i.name LIKE '%安防%' OR i.name LIKE '%报警%' OR i.name LIKE '%摄像%' OR i.name LIKE '%门禁%' THEN v_security_sop_id
        ELSE NULL
      END AS sop_id
    FROM ent_inspection_item_t1 i
    WHERE i.tenant_id = v_tenant
      AND i.deleted = false
      AND EXISTS (
        SELECT 1
        FROM dynamic_model_entity_relation_t1 r
        WHERE r.tenant_id = v_tenant
          AND r.deleted = false
          AND r.entity_type_code = 'inspection_item'
          AND r.entity_id = i.id
      )
  ),
  method_rows AS (
    SELECT
      mi.inspection_item_id,
      m.means AS dimension_value,
      mi.sop_id
    FROM mapped_items mi
    CROSS JOIN (VALUES ('MANUAL'), ('UAV'), ('ROBOT'), ('FIXED_CAMERA')) m(means)
    WHERE mi.sop_id IS NOT NULL
  )
  INSERT INTO dynamic_sop_method_binding_t1 (
    tenant_id, subject_type, subject_id,
    dimension_key, dimension_value, sop_template_id,
    creator, create_time, updater, update_time, deleted
  )
  SELECT
    v_tenant,
    'inspection_item',
    mr.inspection_item_id,
    'execution_means',
    mr.dimension_value,
    mr.sop_id,
    'recipe-inspection-bulk',
    CURRENT_TIMESTAMP,
    'recipe-inspection-bulk',
    CURRENT_TIMESTAMP,
    false
  FROM method_rows mr
  WHERE NOT EXISTS (
    SELECT 1
    FROM dynamic_sop_method_binding_t1 b
    WHERE b.tenant_id = v_tenant
      AND b.deleted = false
      AND b.subject_type = 'inspection_item'
      AND b.subject_id = mr.inspection_item_id
      AND b.dimension_key = 'execution_means'
      AND b.dimension_value = mr.dimension_value
  );

  GET DIAGNOSTICS v_method_inserted = ROW_COUNT;

  -- 2) facility=44 按“设备型号适用检查项”补宿主参数包（仅补缺失键）
  WITH equipment_scope AS (
    SELECT
      e.id AS equipment_id,
      e.host_sop_param_pack AS old_pack,
      e.model_id
    FROM ent_equipment_t1 e
    WHERE e.tenant_id = v_tenant
      AND e.deleted = false
      AND e.facility_id = v_facility
      AND e.model_id IS NOT NULL
      AND e.model_id > 0
  ),
  applicable_items AS (
    SELECT
      es.equipment_id,
      r.entity_id AS inspection_item_id
    FROM equipment_scope es
    JOIN dynamic_model_entity_relation_t1 r
      ON r.tenant_id = v_tenant
     AND r.deleted = false
     AND r.model_id = es.model_id
     AND r.entity_type_code = 'inspection_item'
  ),
  mapped_pairs AS (
    SELECT
      ai.equipment_id,
      ai.inspection_item_id,
      CASE
        WHEN i.name LIKE '%储罐%' OR i.name LIKE '%液位%' OR i.name LIKE '%泄漏%' THEN v_tank_sop_id
        WHEN i.name LIKE '%工艺%' OR i.name LIKE '%阀%' OR i.name LIKE '%管线%' THEN v_process_sop_id
        WHEN i.name LIKE '%安防%' OR i.name LIKE '%报警%' OR i.name LIKE '%摄像%' OR i.name LIKE '%门禁%' THEN v_security_sop_id
        ELSE NULL
      END AS sop_id
    FROM applicable_items ai
    JOIN ent_inspection_item_t1 i
      ON i.tenant_id = v_tenant
     AND i.deleted = false
     AND i.id = ai.inspection_item_id
  ),
  base_entries AS (
    SELECT
      mp.equipment_id,
      mp.inspection_item_id,
      m.means AS dimension_value,
      mp.sop_id,
      COALESCE(s.default_params_by_node_json, '{}'::jsonb) AS default_params
    FROM mapped_pairs mp
    CROSS JOIN (VALUES ('MANUAL'), ('UAV'), ('ROBOT'), ('FIXED_CAMERA')) m(means)
    JOIN ent_sop_t1 s
      ON s.tenant_id = v_tenant
     AND s.deleted = false
     AND s.id = mp.sop_id
    WHERE mp.sop_id IS NOT NULL
  ),
  candidate_entries AS (
    SELECT
      b.equipment_id,
      jsonb_build_object(
        'subjectType', 'inspection_item',
        'subjectId', b.inspection_item_id,
        'targetType', 'sop',
        'targetId', b.sop_id,
        'dimensionKey', 'execution_means',
        'dimensionValue', b.dimension_value,
        'paramsByNode', b.default_params
      ) AS entry
    FROM base_entries b
  ),
  existing_entries AS (
    SELECT
      es.equipment_id,
      COALESCE(es.old_pack -> 'entries', '[]'::jsonb) AS entries
    FROM equipment_scope es
  ),
  filtered_new_entries AS (
    SELECT
      ce.equipment_id,
      jsonb_agg(ce.entry ORDER BY ce.entry ->> 'subjectId', ce.entry ->> 'dimensionValue') AS entries
    FROM candidate_entries ce
    JOIN existing_entries ee
      ON ee.equipment_id = ce.equipment_id
    WHERE NOT EXISTS (
      SELECT 1
      FROM jsonb_array_elements(ee.entries) ex(entry)
      WHERE ex.entry ->> 'subjectType' = ce.entry ->> 'subjectType'
        AND ex.entry ->> 'subjectId' = ce.entry ->> 'subjectId'
        AND ex.entry ->> 'targetType' = ce.entry ->> 'targetType'
        AND ex.entry ->> 'targetId' = ce.entry ->> 'targetId'
        AND ex.entry ->> 'dimensionKey' = ce.entry ->> 'dimensionKey'
        AND ex.entry ->> 'dimensionValue' = ce.entry ->> 'dimensionValue'
    )
    GROUP BY ce.equipment_id
  )
  UPDATE ent_equipment_t1 e
  SET host_sop_param_pack = jsonb_build_object(
        'version', 1,
        'entries',
        COALESCE(e.host_sop_param_pack -> 'entries', '[]'::jsonb)
        || COALESCE(fne.entries, '[]'::jsonb)
      ),
      updater = 'recipe-inspection-bulk',
      update_time = CURRENT_TIMESTAMP
  FROM filtered_new_entries fne
  WHERE e.id = fne.equipment_id
    AND e.tenant_id = v_tenant
    AND e.deleted = false;

  GET DIAGNOSTICS v_equipment_updated = ROW_COUNT;

  RAISE NOTICE 'done: method_inserted=%, equipment_host_pack_updated=%', v_method_inserted, v_equipment_updated;
END $$;

-- 3) 结果快照
WITH mapped AS (
  SELECT
    i.id,
    CASE
      WHEN i.name LIKE '%储罐%' OR i.name LIKE '%液位%' OR i.name LIKE '%泄漏%' THEN 'SOP-STD-TANK-INSPECTION'
      WHEN i.name LIKE '%工艺%' OR i.name LIKE '%阀%' OR i.name LIKE '%管线%' THEN 'SOP-STD-PROCESS-INSPECTION'
      WHEN i.name LIKE '%安防%' OR i.name LIKE '%报警%' OR i.name LIKE '%摄像%' OR i.name LIKE '%门禁%' THEN 'SOP-STD-SECURITY-INSPECTION'
      ELSE NULL
    END AS sop_code
  FROM ent_inspection_item_t1 i
  WHERE i.tenant_id = 1
    AND i.deleted = false
    AND EXISTS (
      SELECT 1
      FROM dynamic_model_entity_relation_t1 r
      WHERE r.tenant_id = 1
        AND r.deleted = false
        AND r.entity_type_code = 'inspection_item'
        AND r.entity_id = i.id
    )
)
SELECT
  COUNT(*) AS model_attached_items,
  COUNT(*) FILTER (WHERE sop_code IS NOT NULL) AS mapped_items,
  COUNT(*) FILTER (WHERE sop_code IS NULL) AS unmapped_items
FROM mapped;

---- 历史正文结束 ---- */
