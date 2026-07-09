-- 为 system_model 增加业务内显示顺序字段
-- 说明：sort 表示模型在同一个 entity_type_code 下的显示顺序（值越小越靠前）

ALTER TABLE system_model
    ADD COLUMN IF NOT EXISTS sort INTEGER NOT NULL DEFAULT 0;

COMMENT ON COLUMN system_model.sort IS '模型在 entityTypeCode 下的显示顺序（值越小越靠前）';

CREATE INDEX IF NOT EXISTS idx_system_model_business_type_sort
    ON system_model (entity_type_code, sort, id);
