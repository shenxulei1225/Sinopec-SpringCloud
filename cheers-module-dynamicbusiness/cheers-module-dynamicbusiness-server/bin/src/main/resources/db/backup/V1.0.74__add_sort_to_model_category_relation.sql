-- ========================================
-- 添加模型-分类关联排序字段
-- 创建日期: 2026-03-19
-- 说明: 用于支持分类内模型拖拽排序
-- ========================================

-- 为 system_model_category_relation 表添加 sort 字段
ALTER TABLE system_model_category_relation
ADD COLUMN IF NOT EXISTS sort INTEGER DEFAULT 0;

-- 添加注释
COMMENT ON COLUMN system_model_category_relation.sort IS '分类内模型排序值（数值越小越靠前）';

-- 可选：初始化已有关联的排序值（按 id 升序）
-- 说明：使用窗口函数为每个分类内的模型排序赋值
WITH ranked AS (
  SELECT id,
         ROW_NUMBER() OVER (PARTITION BY category_id ORDER BY id ASC) AS rn
  FROM system_model_category_relation
)
UPDATE system_model_category_relation r
SET sort = ranked.rn
FROM ranked
WHERE r.id = ranked.id;
