ALTER TABLE dynamicbusiness.dm_workbench_layout
    ADD COLUMN IF NOT EXISTS settings_json jsonb NOT NULL DEFAULT '{}'::jsonb;

COMMENT ON COLUMN dynamicbusiness.dm_workbench_layout.settings_json IS
    '布局头设置：sectionHidden.FILTER|OBJECT|WHAT = true 表示区段配置隐藏';
