-- 字段治理身份在创建时定稿；读取字段或模型字段时不得反推或补写。
ALTER TABLE dynamic_field
    ADD COLUMN IF NOT EXISTS governance_status VARCHAR(16),
    ADD COLUMN IF NOT EXISTS origin_facility_id BIGINT,
    ADD COLUMN IF NOT EXISTS creator_user_id BIGINT;

-- 迁移前已有字段均视为公司字段；本地字段由带型号上下文的创建命令显式写入。
UPDATE dynamic_field
SET governance_status = 'COMPANY'
WHERE governance_status IS NULL;

ALTER TABLE dynamic_field
    ALTER COLUMN governance_status SET DEFAULT 'COMPANY',
    ALTER COLUMN governance_status SET NOT NULL;

ALTER TABLE dynamic_field
    DROP CONSTRAINT IF EXISTS chk_dynamic_field_governance_status;

ALTER TABLE dynamic_field
    ADD CONSTRAINT chk_dynamic_field_governance_status
        CHECK (governance_status IN ('LOCAL', 'COMPANY'));

COMMENT ON COLUMN dynamic_field.governance_status IS
    '字段治理状态：LOCAL=站场本地字段，COMPANY=公司字段；字段治理与可见性权威';
COMMENT ON COLUMN dynamic_field.origin_facility_id IS
    '本地字段发起设施 ID；公司字段为空；创建本地包时与型号发起设施一致';
COMMENT ON COLUMN dynamic_field.creator_user_id IS
    '创建用户 ID；用于后续本地字段治理权限判断';
