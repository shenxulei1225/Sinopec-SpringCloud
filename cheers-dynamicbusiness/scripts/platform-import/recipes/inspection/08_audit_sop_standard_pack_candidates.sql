-- ============================================================================
-- recipes/inspection · 08 SOP 标准包迁移前审计（只读）
-- 目标：先评估旧绑定数据是否有迁移价值，不直接改数据。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) 旧链路覆盖面：每个 SOP 绑定了多少检查项、多少手段。
SELECT
  b.tenant_id,
  b.sop_template_id AS sop_id,
  s.code AS sop_code,
  s.name AS sop_name,
  COUNT(*) AS binding_rows,
  COUNT(DISTINCT b.subject_id) AS inspection_item_count,
  COUNT(DISTINCT b.dimension_value) AS dimension_count
FROM dynamic_sop_method_binding b
LEFT JOIN ent_sop_t1 s
  ON s.id = b.sop_template_id
 AND s.deleted = FALSE
WHERE b.deleted = FALSE
  AND b.subject_type = 'inspection_item'
GROUP BY b.tenant_id, b.sop_template_id, s.code, s.name
ORDER BY inspection_item_count DESC, b.sop_template_id;

-- 2) SOP 信息完整度（名称/说明）：
--    说明字段按当前常见列尝试，便于判断是否可作为标准包来源继续复用。
SELECT
  s.id AS sop_id,
  s.code AS sop_code,
  s.name AS sop_name,
  CASE
    WHEN COALESCE(
      NULLIF(TRIM(COALESCE(to_jsonb(s)->>'description', '')), ''),
      NULLIF(TRIM(COALESCE(to_jsonb(s)->>'standard_pdf_url', '')), '')
    ) IS NULL
      THEN 'MISSING'
    ELSE 'OK'
  END AS description_quality,
  CASE WHEN s.name IS NULL OR TRIM(s.name) = '' THEN 'MISSING' ELSE 'OK' END AS name_quality
FROM ent_sop_t1 s
WHERE s.deleted = FALSE
ORDER BY s.id;

-- 3) 分类挂接覆盖（可作为 SOP 适用范围 CATEGORY 的初始候选）。
SELECT
  cer.entity_id AS sop_id,
  COUNT(*) AS linked_category_count
FROM dynamic_category_entity_relation cer
WHERE cer.deleted = FALSE
  AND cer.entity_type_code = 'sop'
GROUP BY cer.entity_id
ORDER BY linked_category_count DESC, cer.entity_id;

-- 4) 脏数据提示：无 SOP / 无检查项 / 维度空值。
SELECT
  b.id,
  b.tenant_id,
  b.subject_id AS inspection_item_id,
  b.dimension_key,
  b.dimension_value,
  b.sop_template_id AS sop_id,
  CASE
    WHEN i.id IS NULL THEN 'MISSING_INSPECTION_ITEM'
    WHEN s.id IS NULL THEN 'MISSING_SOP'
    WHEN b.dimension_value IS NULL OR TRIM(b.dimension_value) = '' THEN 'EMPTY_DIMENSION_VALUE'
    ELSE 'OK'
  END AS quality
FROM dynamic_sop_method_binding b
LEFT JOIN ent_inspection_item_t1 i
  ON i.id = b.subject_id
 AND i.deleted = FALSE
LEFT JOIN ent_sop_t1 s
  ON s.id = b.sop_template_id
 AND s.deleted = FALSE
WHERE b.deleted = FALSE
  AND b.subject_type = 'inspection_item'
  AND (
    i.id IS NULL
    OR s.id IS NULL
    OR b.dimension_value IS NULL
    OR TRIM(b.dimension_value) = ''
  )
ORDER BY b.id;
