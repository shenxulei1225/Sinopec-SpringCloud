-- 数据类型 Scope：侧边栏 NATIVE / SCOPED 入口 + 模型 data_scope 维度

ALTER TABLE dynamicbusiness.dynamic_entity_type
    ADD COLUMN IF NOT EXISTS entry_kind character varying(16) DEFAULT 'NATIVE' NOT NULL;

ALTER TABLE dynamicbusiness.dynamic_entity_type
    ADD COLUMN IF NOT EXISTS base_entity_type_code character varying(64);

ALTER TABLE dynamicbusiness.dynamic_entity_type
    ADD COLUMN IF NOT EXISTS data_scope character varying(128);

COMMENT ON COLUMN dynamicbusiness.dynamic_entity_type.entry_kind IS '入口类型：NATIVE=独立数据类型；SCOPED=已有类型的业务域入口';
COMMENT ON COLUMN dynamicbusiness.dynamic_entity_type.base_entity_type_code IS 'SCOPED 时指向的存储数据类型编码（如 task）';
COMMENT ON COLUMN dynamicbusiness.dynamic_entity_type.data_scope IS 'SCOPED 时的业务域标识（如 巡检）；NATIVE 为空';

UPDATE dynamicbusiness.dynamic_entity_type
SET entry_kind = 'NATIVE'
WHERE entry_kind IS NULL OR btrim(entry_kind) = '';

ALTER TABLE dynamicbusiness.dynamic_model
    ADD COLUMN IF NOT EXISTS data_scope character varying(128);

COMMENT ON COLUMN dynamicbusiness.dynamic_model.data_scope IS '模型所属业务域；与 SCOPED 数据类型入口的 data_scope 对齐，NATIVE 全量入口可为空';

CREATE INDEX IF NOT EXISTS idx_dynamic_model_entity_type_data_scope
    ON dynamicbusiness.dynamic_model (entity_type_code, data_scope)
    WHERE deleted = false;

CREATE INDEX IF NOT EXISTS idx_dynamic_entity_type_base_scope
    ON dynamicbusiness.dynamic_entity_type (base_entity_type_code, data_scope)
    WHERE deleted = false AND entry_kind = 'SCOPED';
