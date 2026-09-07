-- ============================================================================
-- V1.0.37: 将 is_searchable 和 is_sortable 从字段级别移到模型字段级别
-- 描述：支持同一字段在不同模型中配置不同的查询和排序属性
-- 需求：字段复用场景下,不同模型对同一字段的查询和排序需求可能不同
-- ============================================================================

-- 1. 添加 is_searchable 字段
-- 用途：标记字段在该模型中是否可查询,如果为 NULL 则使用字段定义中的默认值
ALTER TABLE dynamic_model_field_assignment 
ADD COLUMN IF NOT EXISTS is_searchable BOOLEAN;

-- 2. 添加 is_sortable 字段
-- 用途：标记字段在该模型中是否可排序,如果为 NULL 则使用字段定义中的默认值
ALTER TABLE dynamic_model_field_assignment 
ADD COLUMN IF NOT EXISTS is_sortable BOOLEAN;

-- 3. 添加字段注释
COMMENT ON COLUMN dynamic_model_field_assignment.is_searchable 
IS '是否可查询（如果为 NULL,则使用字段定义中的默认值）';

COMMENT ON COLUMN dynamic_model_field_assignment.is_sortable 
IS '是否可排序（如果为 NULL,则使用字段定义中的默认值）';

-- 4. 数据迁移：将现有字段定义中的 is_searchable 和 is_sortable 值迁移到模型字段中
-- 注意：只迁移已存在的字段分配记录,新创建的字段分配将使用 NULL（表示使用字段定义中的默认值）
UPDATE dynamic_model_field_assignment mfa
SET 
    is_searchable = f.is_searchable,
    is_sortable = f.is_sortable
FROM dynamic_field f
WHERE mfa.field_id = f.id
  AND mfa.deleted = FALSE
  AND f.deleted = FALSE;

-- 5. 从 dynamic_field 表中移除 is_searchable 和 is_sortable 字段
-- 注意：这些字段现在在模型字段级别配置,不再在字段定义级别配置
ALTER TABLE dynamic_field 
DROP COLUMN IF EXISTS is_searchable;

ALTER TABLE dynamic_field 
DROP COLUMN IF EXISTS is_sortable;

