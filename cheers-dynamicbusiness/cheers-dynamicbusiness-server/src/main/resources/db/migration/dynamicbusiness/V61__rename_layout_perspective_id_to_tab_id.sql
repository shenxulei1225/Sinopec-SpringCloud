-- 布局行上的标签页编号：perspective_id → tab_id（分类/型号/实体列每个标签页一行）

SET search_path TO dynamicbusiness, public;

ALTER TABLE dynamicbusiness.dm_data_tab_layout
    RENAME COLUMN perspective_id TO tab_id;
COMMENT ON COLUMN dynamicbusiness.dm_data_tab_layout.tab_id IS
    '标签页编号：分类/型号/实体列上每一个标签页；DETAIL 为空';

ALTER TABLE dynamicbusiness.dm_five_w_who_layout
    RENAME COLUMN perspective_id TO tab_id;
COMMENT ON COLUMN dynamicbusiness.dm_five_w_who_layout.tab_id IS
    '标签页编号，与数据 Tab 布局行 tab_id 同义';

ALTER TABLE dynamicbusiness.dm_five_w_filter_layout
    RENAME COLUMN perspective_id TO tab_id;
COMMENT ON COLUMN dynamicbusiness.dm_five_w_filter_layout.tab_id IS
    '标签页编号，与数据 Tab 布局行 tab_id 同义';

DROP INDEX IF EXISTS dynamicbusiness.uk_dm_data_tab_layout_scope;
CREATE UNIQUE INDEX IF NOT EXISTS uk_dm_data_tab_layout_scope
    ON dynamicbusiness.dm_data_tab_layout (
        tenant_id,
        entity_type_code,
        layout_scene,
        column_kind,
        COALESCE(tab_id, '')
    )
    WHERE deleted = false;

DROP INDEX IF EXISTS dynamicbusiness.uk_dm_five_w_who_layout_scope;
CREATE UNIQUE INDEX IF NOT EXISTS uk_dm_five_w_who_layout_scope
    ON dynamicbusiness.dm_five_w_who_layout (
        tenant_id, entity_type_code, column_kind, COALESCE(tab_id, ''), COALESCE(slot_ref, '')
    )
    WHERE deleted = FALSE;

DROP INDEX IF EXISTS dynamicbusiness.uk_dm_five_w_filter_layout_scope;
CREATE UNIQUE INDEX IF NOT EXISTS uk_dm_five_w_filter_layout_scope
    ON dynamicbusiness.dm_five_w_filter_layout (
        tenant_id, entity_type_code, column_kind, COALESCE(tab_id, ''), COALESCE(slot_ref, '')
    )
    WHERE deleted = FALSE;
