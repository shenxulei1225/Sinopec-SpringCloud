-- =====================================================
-- 初始化 dynamic_category_type 数据
-- 描述：将 dynamic_category 表中出现过的 category_type_code（去重）同步到 dynamic_category_type,
--       每个 code 只插入一条记录,name 使用 category_type_code,后续可在管理端修改。
--       表唯一索引为 category_type_code,故按 code 维度补全即可。
-- =====================================================

INSERT INTO dynamic_category_type (category_type_code, name, description, status, top_level_category_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT
  c.category_type_code,
  c.category_type_code,
  '从 dynamic_category 同步的分类维度',
  1,
  NULL,
  'system',
  CURRENT_TIMESTAMP,
  'system',
  CURRENT_TIMESTAMP,
  FALSE,
  0
FROM (
  SELECT DISTINCT category_type_code
  FROM dynamic_category
  WHERE deleted = FALSE
    AND category_type_code IS NOT NULL
    AND category_type_code <> ''
) c
WHERE NOT EXISTS (
  SELECT 1
  FROM dynamic_category_type t
  WHERE t.category_type_code = c.category_type_code
    AND t.deleted = FALSE
);
