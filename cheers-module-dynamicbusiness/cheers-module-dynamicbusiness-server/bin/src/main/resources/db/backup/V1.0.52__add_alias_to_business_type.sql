-- =====================================================
-- 为 system_business_type 表添加 alias 字段（别名）
-- 版本: V1.0.52
-- 描述: 添加 alias 字段,用于业务类型的别名展示（非必填）
-- =====================================================

-- 添加 alias 字段
ALTER TABLE system_business_type
ADD COLUMN IF NOT EXISTS alias VARCHAR(128) DEFAULT NULL;

-- 添加字段注释
COMMENT ON COLUMN system_business_type.alias IS '别名';

-- 分析表以更新统计信息
ANALYZE system_business_type;
