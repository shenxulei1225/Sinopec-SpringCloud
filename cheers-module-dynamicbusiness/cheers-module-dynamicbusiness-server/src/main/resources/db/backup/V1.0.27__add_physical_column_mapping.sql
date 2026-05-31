-- =====================================================
-- 添加物理列映射配置字段
-- 版本：V1.0.27
-- 日期：2026-01-08
-- 需求：存储类型统一 - DEDICATED_DYNAMIC 扩展
-- =====================================================

-- 为 dynamic_business_type_config 表添加 physical_column_mapping 列
-- 该字段用于配置 DEDICATED_DYNAMIC 类型的物理列映射
-- 配置的字段将存储到物理列（B-Tree 索引）,其他字段存储到 JSONB

ALTER TABLE dynamic_business_type_config 
ADD COLUMN IF NOT EXISTS physical_column_mapping JSONB;

-- 添加字段注释
COMMENT ON COLUMN dynamic_business_type_config.physical_column_mapping IS '物理列映射配置（JSON格式）,仅DEDICATED_DYNAMIC类型使用。配置的字段存入物理列,其他字段存入JSONB。格式示例：{"code":{"column":"code","type":"VARCHAR","length":100}}';
