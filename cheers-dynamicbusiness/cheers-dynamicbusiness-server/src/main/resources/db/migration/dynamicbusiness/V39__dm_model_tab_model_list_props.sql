-- V39: 模型管理 Tab 型号列表独立 props（与数据 Tab MODEL 列解耦）
-- 「模型管理」管本类型号；「数据」Tab 型号列可配外类型业务源，不得共用同一 propsId。

ALTER TABLE dynamicbusiness.dm_model_tab_category
    ADD COLUMN IF NOT EXISTS model_list_props_id bigint;

COMMENT ON COLUMN dynamicbusiness.dm_model_tab_category.model_list_props_id IS
    '模型管理 Tab 型号列表 component-props id；与数据 Tab 布局 MODEL.propsId 分离';
