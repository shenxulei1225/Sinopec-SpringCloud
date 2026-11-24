-- 告警记录页的型号栏是关掉的。分类到型号再到实体的筛选线经过关掉的栏，打开页面时这条线不参与查数，中间列表不发请求。
-- 分类栏直接筛实体栏。点左侧大类，只列出挂在这一类和它下面分类上的告警。只改租户 123 的告警记录页。
SET search_path TO dynamicbusiness;

INSERT INTO dm_data_tab_column_relation (
    entity_type_code, edge_id, from_column_identity, to_column_identity,
    relation_kind, from_type_code, to_type_code, relation_meta,
    creator, updater, deleted, tenant_id, layout_id)
SELECT 'alarm', 'edge-alarm-cat-entity-filter',
       'CATEGORY:category:tab-category-1', 'ENTITY:tab-1ec76c',
       'CATEGORY_ENTITY', 'alarm', 'alarm',
       '{"edgeAction": "filter", "enabledInteractions": []}'::jsonb,
       'V308', 'V308', false, 123, 245
WHERE NOT EXISTS (
    SELECT 1 FROM dm_data_tab_column_relation own
    WHERE own.tenant_id = 123
      AND own.layout_id = 245
      AND own.deleted = false
      AND own.relation_kind = 'CATEGORY_ENTITY'
      AND own.from_column_identity = 'CATEGORY:category:tab-category-1'
      AND own.to_column_identity = 'ENTITY:tab-1ec76c'
      AND own.relation_meta->>'edgeAction' = 'filter'
);
