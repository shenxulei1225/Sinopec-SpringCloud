-- 去掉误加在「场站部门人员管理」布局里的场站栏。
-- 可见场站列表不写进某一本目录的布局，由数据页按「数据管理方式」决定出不出。
SET search_path TO dynamicbusiness;

UPDATE dm_data_tab_layout
SET deleted = true, updater = 'V244', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND layout_id = 5 AND tab_id = 'tab-visible-stations';

UPDATE platformresource.pr_component_props
SET deleted = true, updater = 'V244', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND name = 'dm-tree:department:facility:场站';
