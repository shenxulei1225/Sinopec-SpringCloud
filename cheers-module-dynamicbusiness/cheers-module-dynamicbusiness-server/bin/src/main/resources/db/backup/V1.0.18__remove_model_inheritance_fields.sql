-- ============================================================================
-- V1.0.18: 移除 Model 继承机制相关字段
-- 
-- 背景：
-- - 实现 Template-Model 复制机制（FR-211）
-- - Model 创建时可选择 Template,复制字段后完全独立
-- - 不再使用继承机制（parentModelId 和 isTemplate 字段）
-- 
-- 变更内容：
-- 1. 移除 system_model 表的 parent_model_id 和 is_template 字段
-- 2. 移除相关索引
-- 
-- 注意：
-- - 此迁移不会删除数据,只是移除不再使用的字段
-- - 如果有 isTemplate=true 的 Model,应先迁移到 Template 表
-- ============================================================================

-- 1. 移除索引（如果存在）
DROP INDEX IF EXISTS idx_model_parent;
DROP INDEX IF EXISTS idx_model_template;

-- 2. 移除 parent_model_id 字段
ALTER TABLE system_model DROP COLUMN IF EXISTS parent_model_id;

-- 3. 移除 is_template 字段
ALTER TABLE system_model DROP COLUMN IF EXISTS is_template;

-- 4. 添加注释说明变更
COMMENT ON TABLE system_model IS '业务模型表（Template-Model 复制机制,移除继承字段）';
