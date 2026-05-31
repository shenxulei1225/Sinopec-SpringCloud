-- =====================================================
-- V1.0.34: 添加 max_relations 字段到 system_field 表
-- 
-- 功能说明：
-- 支持多选关联字段（ENTITY_REF_MULTI）的最大关联数量限制
-- 
-- 需求：多选关联支持
-- =====================================================

-- 添加 max_relations 字段
ALTER TABLE system_field ADD COLUMN IF NOT EXISTS max_relations INTEGER;

-- 添加字段注释
COMMENT ON COLUMN system_field.max_relations IS '最大关联数量（ENTITY_REF_MULTI 类型专用）,null 或 0 表示不限制';

-- 创建索引（用于查询多选关联字段）
CREATE INDEX IF NOT EXISTS idx_field_type_max_relations ON system_field(type, max_relations) WHERE deleted = FALSE;
