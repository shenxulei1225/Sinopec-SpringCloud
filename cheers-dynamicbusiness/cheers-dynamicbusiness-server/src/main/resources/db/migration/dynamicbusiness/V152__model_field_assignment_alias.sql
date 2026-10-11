-- 型号字段的显示别名。空着时详情仍用字段库名称；基础字段未填时仍用数据类型上的显示名称。

SET search_path TO dynamicbusiness, public;

ALTER TABLE dynamicbusiness.dynamic_model_field_assignment
    ADD COLUMN IF NOT EXISTS alias character varying(200);

COMMENT ON COLUMN dynamicbusiness.dynamic_model_field_assignment.alias IS
    '这个型号上该字段的显示别名。空则用字段库名称';
