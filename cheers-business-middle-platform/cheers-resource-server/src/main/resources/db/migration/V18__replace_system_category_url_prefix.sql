SET search_path TO platformresource;

-- 清除历史 /system/category 路径残留：一律改为 /dynamicbusiness/category。
-- 不再运行时改写兼容；本迁移把库内已存 props / override 中的旧前缀迁干净。

UPDATE pr_component_props
SET
    props = replace(props, '/system/category', '/dynamicbusiness/category'),
    updater = 'repair',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND props LIKE '%/system/category%';

UPDATE pr_component_props
SET
    props_override = replace(props_override, '/system/category', '/dynamicbusiness/category'),
    updater = 'repair',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND props_override IS NOT NULL
  AND props_override LIKE '%/system/category%';
