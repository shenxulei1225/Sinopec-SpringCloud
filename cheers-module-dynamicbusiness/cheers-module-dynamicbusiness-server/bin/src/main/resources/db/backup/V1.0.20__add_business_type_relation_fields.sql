-- =====================================================
-- 为 BusinessType 关联表添加零代码核心字段
-- 日期：2026-01-06
-- 需求：FR-BDA-070~075
-- =====================================================

-- 为 system_business_type_relation 表添加字段
-- auto_create_field: 是否自动创建关联字段
-- default_field_name: 默认关联字段名称

-- 添加 auto_create_field 字段
ALTER TABLE system_business_type_relation 
ADD COLUMN IF NOT EXISTS auto_create_field BOOLEAN DEFAULT TRUE;

-- 添加 default_field_name 字段
ALTER TABLE system_business_type_relation 
ADD COLUMN IF NOT EXISTS default_field_name VARCHAR(64);

-- 添加字段注释
COMMENT ON COLUMN system_business_type_relation.auto_create_field IS '是否自动创建关联字段,默认 true';
COMMENT ON COLUMN system_business_type_relation.default_field_name IS '默认关联字段名称（如"所属计划"）';
