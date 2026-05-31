-- ============================================================================
-- V1.0.8: 为 system_model_field_assignment 表添加缺失的列
-- 描述：添加 sort、ref_library_id、is_inherited 列以支持排序和关联字段功能
-- 执行日期：2026-01-05
-- ============================================================================

-- 添加 sort 列（排序值）
ALTER TABLE system_model_field_assignment 
ADD COLUMN IF NOT EXISTS sort INTEGER DEFAULT 0;

-- 添加关联字段库引用字段
ALTER TABLE system_model_field_assignment 
ADD COLUMN IF NOT EXISTS ref_library_id BIGINT;

-- 添加是否继承标记（用于标识从模板继承的关联字段）
ALTER TABLE system_model_field_assignment 
ADD COLUMN IF NOT EXISTS is_inherited BOOLEAN DEFAULT FALSE;

-- 添加字段注释
COMMENT ON COLUMN system_model_field_assignment.sort IS '排序值';
COMMENT ON COLUMN system_model_field_assignment.ref_library_id IS '关联字段库ID,当字段类型为ENTITY_REF时使用';
COMMENT ON COLUMN system_model_field_assignment.is_inherited IS '是否继承自模板,继承的关联字段不可移除';

-- 为现有数据设置默认排序值（按 id 顺序）
UPDATE system_model_field_assignment 
SET sort = id 
WHERE sort IS NULL OR sort = 0;

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_model_field_assignment_sort 
    ON system_model_field_assignment(model_id, sort) 
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_model_field_assignment_ref_library 
    ON system_model_field_assignment(ref_library_id) 
    WHERE deleted = FALSE AND ref_library_id IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_model_field_assignment_inherited 
    ON system_model_field_assignment(model_id, is_inherited) 
    WHERE deleted = FALSE;
