-- =====================================================
-- 为 dynamic_business_type 表添加 association_fields 字段
-- 版本: V1.0.44
-- 描述: 添加关联字段定义列,用于存储业务类型可供其他业务类型关联的字段定义
-- =====================================================

-- 添加 association_fields 字段（JSONB 类型）
ALTER TABLE dynamic_business_type 
ADD COLUMN IF NOT EXISTS association_fields JSONB DEFAULT '{}'::jsonb;

-- 添加字段注释
COMMENT ON COLUMN dynamic_business_type.association_fields IS '可用的关联字段定义（JSONB格式）。存储该业务类型可供其他业务类型关联的字段定义。格式：{"字段key": {"fieldType": "REF_Multi", "refBusinessTypeCode": "xxx", ...}}。Model创建时可以从这些字段中选择使用。';

-- 创建 GIN 索引以加速 JSONB 查询
-- GIN 索引支持以下操作符：
--   @>  : 包含（检查 JSONB 是否包含指定的键值对）
--   ?   : 存在（检查 JSONB 是否包含指定的键）
--   ?&  : 所有键都存在
--   ?|  : 任一键存在
--   #>  : 路径查询
--   #>> : 路径查询（返回文本）
-- 使用示例：
--   SELECT * FROM dynamic_business_type WHERE association_fields ? 'equipment_ref';
--   SELECT * FROM dynamic_business_type WHERE association_fields @> '{"equipment_ref": {"refBusinessTypeCode": "equipment"}}';
CREATE INDEX IF NOT EXISTS idx_dynamic_business_type_association_fields 
    ON dynamic_business_type USING GIN (association_fields);

-- 分析表以更新统计信息
ANALYZE dynamic_business_type;
