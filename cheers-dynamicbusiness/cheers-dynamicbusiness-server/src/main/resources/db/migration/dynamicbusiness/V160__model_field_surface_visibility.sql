-- 型号上已分配的扩展字段，在创建、编辑、详情里出不出现。
-- 空表示画出。基础字段的开关仍在数据类型基础字段 type_config，不写这两列。

SET search_path TO dynamicbusiness, public;

ALTER TABLE dynamicbusiness.dynamic_model_field_assignment
    ADD COLUMN IF NOT EXISTS create_visible boolean,
    ADD COLUMN IF NOT EXISTS edit_visible boolean,
    ADD COLUMN IF NOT EXISTS detail_visible boolean;

COMMENT ON COLUMN dynamicbusiness.dynamic_model_field_assignment.create_visible IS
    '创建弹窗是否画出这一行。空表示画出。只用于扩展字段';
COMMENT ON COLUMN dynamicbusiness.dynamic_model_field_assignment.edit_visible IS
    '编辑弹窗是否画出这一行。空表示画出。只用于扩展字段';
COMMENT ON COLUMN dynamicbusiness.dynamic_model_field_assignment.detail_visible IS
    '详情是否画出这一行。空表示画出。只用于扩展字段';
