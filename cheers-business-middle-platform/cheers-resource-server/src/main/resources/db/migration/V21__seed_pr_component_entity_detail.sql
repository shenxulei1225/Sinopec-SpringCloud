SET search_path TO platformresource;

-- 空库预置详情栏目录行（标题后字段 / 启用）。
-- 不是使用前提：建配置时目录没有该编码，写路径会自动补登记。
-- 不是查数能力码；能力投影仍只认 list/tree/table/card。

INSERT INTO pr_component (component_code, type, name, icon, status, sort, description)
VALUES
('entity-detail', 'entity-detail', '详情', 'detail', 1, 30, '数据组件：详情栏壳（标题后字段、启用）')
ON CONFLICT (component_code) DO UPDATE SET
    type = EXCLUDED.type,
    name = EXCLUDED.name,
    icon = EXCLUDED.icon,
    status = EXCLUDED.status,
    sort = EXCLUDED.sort,
    description = EXCLUDED.description,
    updater = 'flyway-v21',
    update_time = CURRENT_TIMESTAMP,
    deleted = FALSE;
