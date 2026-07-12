-- 业务类型级 DEDICATED 表不绑定 model，允许 model_id 为空

SET search_path TO dynamicbusiness;

ALTER TABLE dynamicbusiness.dynamic_dynamic_table
    ALTER COLUMN model_id DROP NOT NULL;

COMMENT ON COLUMN dynamicbusiness.dynamic_dynamic_table.model_id IS
    '关联业务模型 id；业务类型级专用表（按 entity_type_code 一张 ent_* 表）可为空';
