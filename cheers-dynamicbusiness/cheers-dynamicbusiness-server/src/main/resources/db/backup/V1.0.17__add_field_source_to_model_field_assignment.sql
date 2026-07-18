-- ============================================================================
-- V1.0.17: 为 dynamic_model_field_assignment 表添加 field_source 字段
-- 描述：支持 Template-Model 复制机制,标记字段来源（TEMPLATE_COPIED / USER_ADDED）
-- 需求：FR-209 ~ FR-214
-- 执行日期：2026-01-06
-- ============================================================================

-- 添加 field_source 列（字段来源）
ALTER TABLE dynamic_model_field_assignment 
ADD COLUMN IF NOT EXISTS field_source VARCHAR(32) DEFAULT 'USER_ADDED';

-- 添加字段注释
COMMENT ON COLUMN dynamic_model_field_assignment.field_source IS '字段来源：TEMPLATE_COPIED-从模板复制,USER_ADDED-用户添加';

-- 为现有数据设置默认值（所有现有字段都视为用户添加）
UPDATE dynamic_model_field_assignment 
SET field_source = 'USER_ADDED' 
WHERE field_source IS NULL;

-- 创建索引（便于按来源查询字段）
CREATE INDEX IF NOT EXISTS idx_model_field_assignment_source 
    ON dynamic_model_field_assignment(model_id, field_source) 
    WHERE deleted = FALSE;
