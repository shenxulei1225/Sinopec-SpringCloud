-- 「模型管理」Tab 分类栏配置，与 CATEGORY 维独立
ALTER TABLE dynamicbusiness.dm_entity_dimension
    ADD COLUMN IF NOT EXISTS model_admin_category_meta JSONB;

COMMENT ON COLUMN dynamicbusiness.dm_entity_dimension.model_admin_category_meta IS
    '仅 MODEL 维：模型管理 Tab 分类栏 enabled/label/categoryTypeCode/propsId';
