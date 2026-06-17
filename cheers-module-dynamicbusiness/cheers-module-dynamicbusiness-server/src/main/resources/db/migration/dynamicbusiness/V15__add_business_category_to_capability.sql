-- V15: 业务能力表增加 business_category（dynamic / system）
SET search_path TO dynamicbusiness;

ALTER TABLE business_capability
    ADD COLUMN IF NOT EXISTS business_category VARCHAR(16) NOT NULL DEFAULT 'dynamic';

UPDATE business_capability
SET business_category = 'dynamic'
WHERE business_category IS NULL OR TRIM(business_category) = '';

COMMENT ON COLUMN business_capability.business_category IS '业务分类：dynamic（动态业务）/ system（系统业务）';

CREATE INDEX IF NOT EXISTS idx_business_capability_category
    ON business_capability (business_category, tenant_id)
    WHERE deleted = FALSE;
