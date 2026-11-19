-- 站内应急资源页要能直接看到预案分类和存放点。
-- 分类树默认展开到各预案。实体名单增加放置位置列，格子里是点位名称。
-- 不新增坐标，不把预案名称再抄一份到资源上。

UPDATE platformresource.pr_component_props
SET props = (
        jsonb_set(
            props::jsonb,
            '{defaultExpandAll}',
            '{"expandAll": true, "expandLevel": 2}'::jsonb,
            true
        )
    )::text,
    update_time = CURRENT_TIMESTAMP
WHERE id = 18192;

UPDATE platformresource.pr_component_props
SET props = (
        jsonb_set(
            props::jsonb,
            '{displayContent}',
            '["name", "location_point_id"]'::jsonb,
            true
        )
    )::text,
    update_time = CURRENT_TIMESTAMP
WHERE id = 18193;

UPDATE dynamicbusiness.dm_data_tab_layout
SET column_meta = jsonb_set(column_meta, '{label}', '"应急预案"', true),
    update_time = CURRENT_TIMESTAMP
WHERE id = 3011
  AND column_kind = 'CATEGORY';
