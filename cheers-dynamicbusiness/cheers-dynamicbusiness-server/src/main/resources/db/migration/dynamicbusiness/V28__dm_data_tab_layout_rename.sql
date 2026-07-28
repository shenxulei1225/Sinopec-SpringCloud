-- 数据 Tab 布局表：dm_entity_dimension → dm_data_tab_layout（弃用 dimension 表述）

SET search_path TO dynamicbusiness;

ALTER TABLE IF EXISTS dynamicbusiness.dm_entity_dimension
    RENAME TO dm_data_tab_layout;

COMMENT ON TABLE dynamicbusiness.dm_data_tab_layout IS '数据管理·数据 Tab 布局（分类/型号/实体/详情列开关与配置）';

DROP INDEX IF EXISTS dynamicbusiness.uk_dm_entity_dimension_scope;

CREATE UNIQUE INDEX IF NOT EXISTS uk_dm_data_tab_layout_scope
    ON dynamicbusiness.dm_data_tab_layout (tenant_id, entity_type_code, column_kind, COALESCE(perspective_id, ''))
    WHERE deleted = false;
