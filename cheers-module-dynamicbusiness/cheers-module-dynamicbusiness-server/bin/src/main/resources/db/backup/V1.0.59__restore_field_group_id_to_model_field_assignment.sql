-- =====================================================
-- V1.0.59: 恢复 ModelFieldAssignment 的字段分组列
--
-- 背景：
-- - 为了保留字段分组功能,撤销对 system_model_field_assignment.field_group_id 的删除
-- - 兼容两种场景：
--   1) 旧库已执行过 V1.0.58 并删除了 field_group_id -> 本脚本负责重新添加列和索引
--   2) 新库从头执行全部迁移（已由 V1.0.50 添加列且未被删除）-> 本脚本通过 IF NOT EXISTS 保证幂等
-- =====================================================

-- 1. 恢复字段分组ID列（如果不存在则创建）
ALTER TABLE system_model_field_assignment
ADD COLUMN IF NOT EXISTS field_group_id BIGINT NULL;

COMMENT ON COLUMN system_model_field_assignment.field_group_id IS '字段分组ID,关联到模型的分组配置';

-- 2. 恢复索引（如果不存在则创建）
CREATE INDEX IF NOT EXISTS idx_model_field_assignment_field_group_id 
    ON system_model_field_assignment(field_group_id) 
    WHERE deleted = FALSE AND field_group_id IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_model_field_assignment_model_group 
    ON system_model_field_assignment(model_id, field_group_id) 
    WHERE deleted = FALSE AND field_group_id IS NOT NULL;

