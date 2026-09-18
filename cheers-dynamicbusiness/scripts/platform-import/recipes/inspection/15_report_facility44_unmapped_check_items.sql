-- ============================================================================
-- recipes/inspection · 15 facility=44 未映射检查项清单（检查依据）
--
-- 用途：
-- 1) 输出当前 facility=44 范围内，“设备型号已挂接但未进入关键词映射”的检查项；
-- 2) 给出每条检查项的建议 SOP（基于同型号已映射项投票）与置信说明；
-- 3) 作为后续人工补映射的核对清单，复跑即可对账。
-- ============================================================================

SET search_path TO dynamicbusiness;

WITH eq AS (
  SELECT id AS equipment_id, model_id
  FROM ent_equipment_t1
  WHERE tenant_id = 1
    AND deleted = false
    AND facility_id = 44
    AND model_id IS NOT NULL
    AND model_id > 0
),
model_eq AS (
  SELECT model_id, COUNT(*) AS eq_count
  FROM eq
  GROUP BY model_id
),
rel AS (
  SELECT r.model_id, r.entity_id AS inspection_item_id
  FROM dynamic_model_entity_relation_t1 r
  WHERE r.tenant_id = 1
    AND r.deleted = false
    AND r.entity_type_code = 'inspection_item'
),
scope_pairs AS (
  SELECT DISTINCT eq.model_id, rel.inspection_item_id
  FROM eq JOIN rel USING (model_id)
),
mapped_by_name AS (
  SELECT
    i.id AS inspection_item_id,
    CASE
      WHEN i.name LIKE '%储罐%' OR i.name LIKE '%液位%' OR i.name LIKE '%泄漏%' THEN 'SOP-STD-TANK-INSPECTION'
      WHEN i.name LIKE '%工艺%' OR i.name LIKE '%阀%' OR i.name LIKE '%管线%' THEN 'SOP-STD-PROCESS-INSPECTION'
      WHEN i.name LIKE '%安防%' OR i.name LIKE '%报警%' OR i.name LIKE '%摄像%' OR i.name LIKE '%门禁%' THEN 'SOP-STD-SECURITY-INSPECTION'
      ELSE NULL
    END AS sop_code
  FROM ent_inspection_item_t1 i
  WHERE i.tenant_id = 1
    AND i.deleted = false
),
mapped_model_majority AS (
  SELECT
    sp.model_id,
    mn.sop_code,
    COUNT(*) AS item_count,
    ROW_NUMBER() OVER (
      PARTITION BY sp.model_id
      ORDER BY COUNT(*) DESC, mn.sop_code
    ) AS rn
  FROM scope_pairs sp
  JOIN mapped_by_name mn
    ON mn.inspection_item_id = sp.inspection_item_id
  WHERE mn.sop_code IS NOT NULL
  GROUP BY sp.model_id, mn.sop_code
),
model_primary_sop AS (
  SELECT model_id, sop_code, item_count
  FROM mapped_model_majority
  WHERE rn = 1
),
unmapped_pairs AS (
  SELECT sp.model_id, sp.inspection_item_id
  FROM scope_pairs sp
  LEFT JOIN mapped_by_name mn
    ON mn.inspection_item_id = sp.inspection_item_id
  WHERE mn.sop_code IS NULL
),
pair_suggestions AS (
  SELECT
    up.inspection_item_id,
    up.model_id,
    me.eq_count,
    mps.sop_code AS suggested_sop
  FROM unmapped_pairs up
  JOIN model_eq me ON me.model_id = up.model_id
  LEFT JOIN model_primary_sop mps ON mps.model_id = up.model_id
),
item_votes AS (
  SELECT
    inspection_item_id,
    suggested_sop,
    SUM(eq_count) AS vote_eq_count
  FROM pair_suggestions
  WHERE suggested_sop IS NOT NULL
  GROUP BY inspection_item_id, suggested_sop
),
item_best AS (
  SELECT
    inspection_item_id,
    suggested_sop,
    vote_eq_count,
    ROW_NUMBER() OVER (
      PARTITION BY inspection_item_id
      ORDER BY vote_eq_count DESC, suggested_sop
    ) AS rn,
    SUM(vote_eq_count) OVER (PARTITION BY inspection_item_id) AS total_vote_eq
  FROM item_votes
),
item_models AS (
  SELECT
    inspection_item_id,
    STRING_AGG(DISTINCT model_id::text, ',' ORDER BY model_id::text) AS model_ids,
    SUM(eq_count) AS total_eq
  FROM pair_suggestions
  GROUP BY inspection_item_id
),
detail AS (
  SELECT
    i.id AS inspection_item_id,
    i.code AS inspection_item_code,
    i.name AS inspection_item_name,
    im.model_ids,
    im.total_eq AS affected_equipment_count,
    COALESCE(ib.suggested_sop, 'MANUAL_REVIEW') AS suggested_sop,
    CASE
      WHEN ib.suggested_sop IS NULL THEN '模型内无可参考已映射项'
      WHEN ib.total_vote_eq IS NULL OR ib.total_vote_eq = 0 THEN '模型内无可参考已映射项'
      WHEN ib.vote_eq_count::numeric / ib.total_vote_eq::numeric >= 0.8 THEN '模型主导映射（高置信）'
      WHEN ib.vote_eq_count::numeric / ib.total_vote_eq::numeric >= 0.5 THEN '模型主导映射（中置信）'
      ELSE '模型映射分歧（低置信）'
    END AS basis
  FROM ent_inspection_item_t1 i
  JOIN item_models im ON im.inspection_item_id = i.id
  LEFT JOIN item_best ib
    ON ib.inspection_item_id = i.id
   AND ib.rn = 1
  WHERE i.tenant_id = 1
    AND i.deleted = false
)
SELECT
  'summary' AS section,
  suggested_sop,
  COUNT(*)::bigint AS item_count
FROM detail
GROUP BY suggested_sop
ORDER BY item_count DESC;

WITH eq AS (
  SELECT id AS equipment_id, model_id
  FROM ent_equipment_t1
  WHERE tenant_id = 1
    AND deleted = false
    AND facility_id = 44
    AND model_id IS NOT NULL
    AND model_id > 0
),
model_eq AS (
  SELECT model_id, COUNT(*) AS eq_count
  FROM eq
  GROUP BY model_id
),
rel AS (
  SELECT r.model_id, r.entity_id AS inspection_item_id
  FROM dynamic_model_entity_relation_t1 r
  WHERE r.tenant_id = 1
    AND r.deleted = false
    AND r.entity_type_code = 'inspection_item'
),
scope_pairs AS (
  SELECT DISTINCT eq.model_id, rel.inspection_item_id
  FROM eq JOIN rel USING (model_id)
),
mapped_by_name AS (
  SELECT
    i.id AS inspection_item_id,
    CASE
      WHEN i.name LIKE '%储罐%' OR i.name LIKE '%液位%' OR i.name LIKE '%泄漏%' THEN 'SOP-STD-TANK-INSPECTION'
      WHEN i.name LIKE '%工艺%' OR i.name LIKE '%阀%' OR i.name LIKE '%管线%' THEN 'SOP-STD-PROCESS-INSPECTION'
      WHEN i.name LIKE '%安防%' OR i.name LIKE '%报警%' OR i.name LIKE '%摄像%' OR i.name LIKE '%门禁%' THEN 'SOP-STD-SECURITY-INSPECTION'
      ELSE NULL
    END AS sop_code
  FROM ent_inspection_item_t1 i
  WHERE i.tenant_id = 1
    AND i.deleted = false
),
mapped_model_majority AS (
  SELECT
    sp.model_id,
    mn.sop_code,
    COUNT(*) AS item_count,
    ROW_NUMBER() OVER (
      PARTITION BY sp.model_id
      ORDER BY COUNT(*) DESC, mn.sop_code
    ) AS rn
  FROM scope_pairs sp
  JOIN mapped_by_name mn
    ON mn.inspection_item_id = sp.inspection_item_id
  WHERE mn.sop_code IS NOT NULL
  GROUP BY sp.model_id, mn.sop_code
),
model_primary_sop AS (
  SELECT model_id, sop_code, item_count
  FROM mapped_model_majority
  WHERE rn = 1
),
unmapped_pairs AS (
  SELECT sp.model_id, sp.inspection_item_id
  FROM scope_pairs sp
  LEFT JOIN mapped_by_name mn
    ON mn.inspection_item_id = sp.inspection_item_id
  WHERE mn.sop_code IS NULL
),
pair_suggestions AS (
  SELECT
    up.inspection_item_id,
    up.model_id,
    me.eq_count,
    mps.sop_code AS suggested_sop
  FROM unmapped_pairs up
  JOIN model_eq me ON me.model_id = up.model_id
  LEFT JOIN model_primary_sop mps ON mps.model_id = up.model_id
),
item_votes AS (
  SELECT
    inspection_item_id,
    suggested_sop,
    SUM(eq_count) AS vote_eq_count
  FROM pair_suggestions
  WHERE suggested_sop IS NOT NULL
  GROUP BY inspection_item_id, suggested_sop
),
item_best AS (
  SELECT
    inspection_item_id,
    suggested_sop,
    vote_eq_count,
    ROW_NUMBER() OVER (
      PARTITION BY inspection_item_id
      ORDER BY vote_eq_count DESC, suggested_sop
    ) AS rn,
    SUM(vote_eq_count) OVER (PARTITION BY inspection_item_id) AS total_vote_eq
  FROM item_votes
),
item_models AS (
  SELECT
    inspection_item_id,
    STRING_AGG(DISTINCT model_id::text, ',' ORDER BY model_id::text) AS model_ids,
    SUM(eq_count) AS total_eq
  FROM pair_suggestions
  GROUP BY inspection_item_id
)
SELECT
  i.id AS inspection_item_id,
  i.code AS inspection_item_code,
  i.name AS inspection_item_name,
  im.model_ids,
  im.total_eq AS affected_equipment_count,
  COALESCE(ib.suggested_sop, 'MANUAL_REVIEW') AS suggested_sop,
  CASE
    WHEN ib.suggested_sop IS NULL THEN '模型内无可参考已映射项'
    WHEN ib.total_vote_eq IS NULL OR ib.total_vote_eq = 0 THEN '模型内无可参考已映射项'
    WHEN ib.vote_eq_count::numeric / ib.total_vote_eq::numeric >= 0.8 THEN '模型主导映射（高置信）'
    WHEN ib.vote_eq_count::numeric / ib.total_vote_eq::numeric >= 0.5 THEN '模型主导映射（中置信）'
    ELSE '模型映射分歧（低置信）'
  END AS basis
FROM ent_inspection_item_t1 i
JOIN item_models im ON im.inspection_item_id = i.id
LEFT JOIN item_best ib
  ON ib.inspection_item_id = i.id
 AND ib.rn = 1
WHERE i.tenant_id = 1
  AND i.deleted = false
ORDER BY i.id;
