-- =====================================================
-- 补全 system_category_type：将 system_category 中出现的 category_type_code 同步进去（仅插入不存在的）
-- =====================================================

INSERT INTO system_category_type (category_type_code, name, description, status, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT
  c.category_type_code,
  c.category_type_code,
  '从 system_category 同步的分类维度',
  1,
  'system',
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  FALSE,
  0
FROM (
  SELECT DISTINCT category_type_code
  FROM system_category
  WHERE deleted = FALSE
    AND category_type_code IS NOT NULL
    AND category_type_code <> ''
) c
WHERE NOT EXISTS (
  SELECT 1
  FROM system_category_type t
  WHERE t.category_type_code = c.category_type_code
    AND t.deleted = FALSE
);
