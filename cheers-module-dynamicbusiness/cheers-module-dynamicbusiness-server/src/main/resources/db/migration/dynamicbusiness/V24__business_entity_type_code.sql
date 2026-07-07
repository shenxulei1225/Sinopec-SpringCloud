-- 业务直接关联数据类型（entityTypeCode），不再依赖用户手动配置「业务入口」
ALTER TABLE dynamic_business
    ADD COLUMN IF NOT EXISTS entity_type_code VARCHAR(64);

COMMENT ON COLUMN dynamic_business.entity_type_code IS '关联的数据类型编码（entityTypeCode）；业务管理的数据来源';

CREATE INDEX IF NOT EXISTS idx_dynamic_business_entity_type_code
    ON dynamic_business (entity_type_code)
    WHERE deleted = false;

-- 从已有 ENTITY_ADMIN 入口回填
UPDATE dynamic_business b
SET entity_type_code = sub.entity_type_code
FROM (
    SELECT DISTINCT ON (business_id) business_id, entity_type_code
    FROM dynamic_business_entry
    WHERE deleted = false
      AND entity_type_code IS NOT NULL
      AND entry_type = 'ENTITY_ADMIN'
    ORDER BY business_id, sort NULLS LAST, id
) sub
WHERE b.id = sub.business_id
  AND (b.entity_type_code IS NULL OR b.entity_type_code = '');
