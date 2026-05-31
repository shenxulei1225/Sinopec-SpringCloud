-- =====================================================
-- 移除 system_category_type 表冗余字段 creator_user_id
-- 版本：V1.0.57
-- 描述：由于 CategoryTypeDO 已迁移至 BaseDO.creator,现移除数据库冗余列及索引
-- =====================================================

-- 1. 移除索引
DROP INDEX IF EXISTS idx_system_category_type_creator_user_id;

-- 2. 移除列
ALTER TABLE system_category_type DROP COLUMN IF EXISTS creator_user_id;

