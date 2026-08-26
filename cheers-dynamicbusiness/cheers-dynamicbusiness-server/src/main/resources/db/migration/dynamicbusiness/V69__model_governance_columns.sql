-- 型号治理权威只由 governance_status 与来源身份表达。
-- origin_facility_id 仅记录本地型号发起站，不得作为型号列表按场站裁切的权威字段。
ALTER TABLE dynamic_model
    ADD COLUMN IF NOT EXISTS governance_status VARCHAR(16),
    ADD COLUMN IF NOT EXISTS origin_facility_id BIGINT,
    ADD COLUMN IF NOT EXISTS creator_user_id BIGINT;

-- 迁移前已有型号均视为公司规格；新建型号的治理状态由命令服务按权限定稿。
UPDATE dynamic_model
SET governance_status = 'COMPANY'
WHERE governance_status IS NULL;

ALTER TABLE dynamic_model
    ALTER COLUMN governance_status SET DEFAULT 'COMPANY',
    ALTER COLUMN governance_status SET NOT NULL;

ALTER TABLE dynamic_model
    DROP CONSTRAINT IF EXISTS chk_dynamic_model_governance_status;

ALTER TABLE dynamic_model
    ADD CONSTRAINT chk_dynamic_model_governance_status
        CHECK (governance_status IN ('LOCAL', 'COMPANY'));

COMMENT ON COLUMN dynamic_model.governance_status IS
    '型号治理状态：LOCAL=站场本地型号，COMPANY=公司规格；型号列表治理裁切权威';
COMMENT ON COLUMN dynamic_model.origin_facility_id IS
    '本地型号发起设施 ID；公司规格为空；不得作为型号列表按场站裁切权威';
COMMENT ON COLUMN dynamic_model.creator_user_id IS
    '创建用户 ID；用于本地型号删除权限判断';
