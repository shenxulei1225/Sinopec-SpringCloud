-- 功能页面归属业务；废弃业务表单一 entityTypeCode（方案 A）
ALTER TABLE dynamic_page_config
    ADD COLUMN IF NOT EXISTS business_id BIGINT;

COMMENT ON COLUMN dynamic_page_config.business_id IS '所属门户业务 id（dynamic_business.id）';

CREATE INDEX IF NOT EXISTS idx_dynamic_page_config_business_id
    ON dynamic_page_config (business_id)
    WHERE deleted = false;

-- 从业务入口回填页面归属
UPDATE dynamic_page_config pc
SET business_id = e.business_id
FROM dynamic_business_entry e
WHERE pc.id = e.page_config_id
  AND e.deleted = false
  AND pc.business_id IS NULL;

-- 撤销 V24：业务不绑定唯一数据类型
ALTER TABLE dynamic_business DROP COLUMN IF EXISTS entity_type_code;
DROP INDEX IF EXISTS idx_dynamic_business_entity_type_code;
