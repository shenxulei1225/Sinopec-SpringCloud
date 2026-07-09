-- 增加业务类型编码到实体分类关联表
-- 用于按业务类型过滤,提高查询性能

ALTER TABLE system_entity_category_relation
    ADD COLUMN IF NOT EXISTS entity_type_code VARCHAR(64);

COMMENT ON COLUMN system_entity_category_relation.entity_type_code IS '业务类型编码';

CREATE INDEX IF NOT EXISTS idx_entity_category_relation_business_type
    ON system_entity_category_relation(entity_type_code);

CREATE INDEX IF NOT EXISTS idx_entity_category_relation_category_business
    ON system_entity_category_relation(category_id, entity_type_code);
