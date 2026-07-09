-- =====================================================
-- 扩展 system_entity_relation 表,支持高效的反向查询和统计
-- 需求：FR-BDA-074, FR-BDA-090
-- =====================================================

-- 新增字段：关联来源字段编码
ALTER TABLE system_entity_relation 
ADD COLUMN IF NOT EXISTS field_code VARCHAR(64);

-- 新增字段：源 Model 编码
ALTER TABLE system_entity_relation 
ADD COLUMN IF NOT EXISTS source_model_code VARCHAR(64);

-- 新增字段：目标 Model 编码
ALTER TABLE system_entity_relation 
ADD COLUMN IF NOT EXISTS target_model_code VARCHAR(64);

-- 新增字段：源业务类型编码
ALTER TABLE system_entity_relation 
ADD COLUMN IF NOT EXISTS source_entity_type_code VARCHAR(64);

-- 新增字段：目标业务类型编码
ALTER TABLE system_entity_relation 
ADD COLUMN IF NOT EXISTS target_entity_type_code VARCHAR(64);

-- 添加字段注释
COMMENT ON COLUMN system_entity_relation.field_code IS '关联来源字段编码,标识是哪个字段产生的关联';
COMMENT ON COLUMN system_entity_relation.source_model_code IS '源 Model 编码,用于按 Model 分组统计';
COMMENT ON COLUMN system_entity_relation.target_model_code IS '目标 Model 编码,用于反向查询过滤';
COMMENT ON COLUMN system_entity_relation.source_entity_type_code IS '源业务类型编码,用于跨业务类型查询';
COMMENT ON COLUMN system_entity_relation.target_entity_type_code IS '目标业务类型编码,用于跨业务类型查询';

-- =====================================================
-- 创建索引,优化反向查询和统计性能
-- =====================================================

-- 索引1：按目标实体和源 Model 查询（反向查询核心索引）
CREATE INDEX IF NOT EXISTS idx_entity_relation_target_source_model 
ON system_entity_relation(target_entity_id, source_model_code) 
WHERE deleted = FALSE;

-- 索引2：按目标实体和字段编码查询（按字段分组统计）
CREATE INDEX IF NOT EXISTS idx_entity_relation_target_field 
ON system_entity_relation(target_entity_id, field_code) 
WHERE deleted = FALSE;

-- 索引3：按源实体和字段编码查询（正向查询优化）
CREATE INDEX IF NOT EXISTS idx_entity_relation_source_field 
ON system_entity_relation(source_entity_id, field_code) 
WHERE deleted = FALSE;

-- 索引4：按目标业务类型查询（跨业务类型统计）
CREATE INDEX IF NOT EXISTS idx_entity_relation_target_business_type 
ON system_entity_relation(target_entity_type_code, target_entity_id) 
WHERE deleted = FALSE;

-- 索引5：按源业务类型查询（跨业务类型统计）
CREATE INDEX IF NOT EXISTS idx_entity_relation_source_business_type 
ON system_entity_relation(source_entity_type_code, source_entity_id) 
WHERE deleted = FALSE;

-- 索引6：复合索引用于精确统计（目标实体 + 源 Model + 字段）
CREATE INDEX IF NOT EXISTS idx_entity_relation_target_model_field 
ON system_entity_relation(target_entity_id, source_model_code, field_code) 
WHERE deleted = FALSE;
