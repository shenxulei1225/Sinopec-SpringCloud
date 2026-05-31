-- =====================================================
-- V1.0.58: 移除 Model/ModelFieldAssignment 中已废弃的展示配置列
--
-- 背景：
-- 1) Model 不再存储默认展示字段（name_field_code）
-- 2) Model 不再存储树形开关（enable_tree,仅前端展示用途）
-- 3) ModelFieldAssignment 不再冗余存储关联目标信息（target_* / display_field_code）,关联信息以字段库/模型关联为准
-- =====================================================

-- system_model：移除已废弃列
ALTER TABLE system_model
    DROP COLUMN IF EXISTS name_field_code;

ALTER TABLE system_model
    DROP COLUMN IF EXISTS enable_tree;

ALTER TABLE system_model_field_assignment
    DROP COLUMN IF EXISTS target_business_type;

ALTER TABLE system_model_field_assignment
    DROP COLUMN IF EXISTS target_model_code;

ALTER TABLE system_model_field_assignment
    DROP COLUMN IF EXISTS display_field_code;

-- 相关索引清理（存在则删除）
DROP INDEX IF EXISTS idx_model_field_assignment_target;

