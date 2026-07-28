-- 浏览列配置字段与前端对齐：column_kind / category_column / model_tab_category

SET search_path TO dynamicbusiness;

ALTER TABLE dynamicbusiness.dm_entity_dimension
    RENAME COLUMN dimension_kind TO column_kind;

ALTER TABLE dynamicbusiness.dm_entity_dimension
    RENAME COLUMN category_dimension_meta TO category_column;

ALTER TABLE dynamicbusiness.dm_entity_dimension
    RENAME COLUMN model_admin_category_meta TO model_tab_category;

COMMENT ON COLUMN dynamicbusiness.dm_entity_dimension.column_kind IS 'CATEGORY|MODEL|ENTITY|DETAIL';
COMMENT ON COLUMN dynamicbusiness.dm_entity_dimension.category_column IS '仅 CATEGORY：「数据」Tab 分类列 label/categoryTypeCode/displayMode';
COMMENT ON COLUMN dynamicbusiness.dm_entity_dimension.model_tab_category IS '仅 MODEL：「模型管理」Tab 左侧分类栏 enabled/label/categoryTypeCode/propsId';

DROP INDEX IF EXISTS dynamicbusiness.uk_dm_entity_dimension_scope;

CREATE UNIQUE INDEX IF NOT EXISTS uk_dm_entity_dimension_scope
    ON dynamicbusiness.dm_entity_dimension (tenant_id, entity_type_code, column_kind, COALESCE(perspective_id, ''))
    WHERE deleted = false;
