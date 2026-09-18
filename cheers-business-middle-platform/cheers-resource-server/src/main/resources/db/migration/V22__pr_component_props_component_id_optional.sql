SET search_path TO platformresource;

-- 第一次给某一栏建配置：只写「这是哪种组件」，不绑组件库目录那一行。
-- 打开数据页、读已有配置本来就不查目录。
-- 目录仍给人在「组件库」里浏览、改默认样子。
-- 已有配置行若绑过目录，编号保留；外键在有值时仍须指向真实目录行。

ALTER TABLE pr_component_props
    ALTER COLUMN component_id DROP NOT NULL;

COMMENT ON COLUMN pr_component_props.component_id IS
    '可选。以前绑过组件库目录行才有值。新建配置不写。不是打开页面或建配置的前提。';

COMMENT ON COLUMN pr_component_props.component_code IS
    '这是哪种组件（list / tree / entity-detail 等）。建配置只写这一项。';
