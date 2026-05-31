-- ========================================
-- 添加字段分组ID字段到 dynamic_model_field_assignment 表
-- 创建日期: 2026-01-27
-- 说明: 为模型字段分配表添加字段分组ID字段,用于将字段分配到模型的分组中
-- ========================================

-- 为 dynamic_model_field_assignment 表添加字段分组ID字段
-- 用于将字段分配到模型的分组中
ALTER TABLE dynamic_model_field_assignment 
ADD COLUMN IF NOT EXISTS field_group_id BIGINT NULL;

-- 添加注释
COMMENT ON COLUMN dynamic_model_field_assignment.field_group_id IS '字段分组ID,关联到模型的分组配置';

-- 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_model_field_assignment_field_group_id 
    ON dynamic_model_field_assignment(field_group_id) 
    WHERE deleted = FALSE AND field_group_id IS NOT NULL;

-- 创建复合索引,用于查询某个模型下某个分组的所有字段
CREATE INDEX IF NOT EXISTS idx_model_field_assignment_model_group 
    ON dynamic_model_field_assignment(model_id, field_group_id) 
    WHERE deleted = FALSE AND field_group_id IS NOT NULL;
