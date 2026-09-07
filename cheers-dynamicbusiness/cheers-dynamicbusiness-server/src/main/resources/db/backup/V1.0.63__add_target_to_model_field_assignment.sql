-- 为模型字段表增加关联目标兜底字段
-- 说明：
-- 1. 正常情况下,关联字段的目标信息来自 RelationFieldLibrary / ModelRelation / Field.refModelCodes 等“权威来源”
-- 2. 当前端在拖拽 ENTITY_REF / ENTITY_REF_MULTI / BATCH_ENTITY_REF 字段到模型时,会选择 targetBusinessType / targetModelCode
-- 3. 这两个字段用于在缺少权威来源时兜底,避免出现“关联字段缺少 targetBusinessType”的错误

ALTER TABLE dynamic_model_field_assignment
    ADD COLUMN IF NOT EXISTS target_business_type VARCHAR(64) NULL,
    ADD COLUMN IF NOT EXISTS target_model_code    VARCHAR(128) NULL;

