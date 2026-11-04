-- 点位对照补上创建目录时本应一起写入的分类和数据页布局。
-- 数据页按这份布局的区域清单分栏。模型页按分类种类列出型号。
-- 不改已经写好的 5060 行点位。

SET search_path TO dynamicbusiness, public;

INSERT INTO dynamicbusiness.dynamic_category_type (
    category_type_code, name, description, status, creator, updater, deleted, tenant_id,
    category_mode, entity_association_mode
)
SELECT
    'point_binding', '点位对照', '数据类型「点位对照」默认分类', 1,
    'point-table-seed', 'point-table-seed', false, 123,
    'SIMPLE', 'MULTI'
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_category_type
    WHERE tenant_id = 123 AND category_type_code = 'point_binding' AND deleted = false
);

INSERT INTO dynamicbusiness.dynamic_category (
    name, code, category_type_code, sort, status, creator, updater, deleted, tenant_id,
    parent_id, level, description
)
SELECT
    '点位对照', 'point_binding_root', 'point_binding', 1, 1,
    'point-table-seed', 'point-table-seed', false, 123,
    NULL, 1, '数据类型「点位对照」默认分类'
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_category
    WHERE tenant_id = 123 AND code = 'point_binding_root' AND deleted = false
);

UPDATE dynamicbusiness.dynamic_category
SET tree_path = '/' || id::text || '/'
WHERE tenant_id = 123 AND code = 'point_binding_root' AND deleted = false
  AND (tree_path IS NULL OR tree_path = '');

UPDATE dynamicbusiness.dynamic_category_type type_row
SET top_level_category_id = category_row.id
FROM dynamicbusiness.dynamic_category category_row
WHERE type_row.tenant_id = 123
  AND type_row.category_type_code = 'point_binding'
  AND type_row.deleted = false
  AND category_row.tenant_id = 123
  AND category_row.code = 'point_binding_root'
  AND category_row.deleted = false
  AND type_row.top_level_category_id IS NULL;

INSERT INTO dynamicbusiness.dynamic_model_category_relation_t123 (
    model_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id,
    model_code, category_code
)
SELECT
    model.id, category_row.id, 'point_binding', 0,
    'point-table-seed', 'point-table-seed', false, 123,
    model.code, category_row.code
FROM dynamicbusiness.dynamic_model model
JOIN dynamicbusiness.dynamic_category category_row
  ON category_row.tenant_id = 123
 AND category_row.code = 'point_binding_root'
 AND category_row.deleted = false
WHERE model.tenant_id = 123
  AND model.code = 'MODEL-point-binding'
  AND model.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_category_relation_t123 rel
      WHERE rel.tenant_id = 123
        AND rel.model_id = model.id
        AND rel.category_id = category_row.id
        AND rel.deleted = false
  );

INSERT INTO dynamicbusiness.dm_workbench_layout (
    name, is_template, source_template_id, tenant_id, creator, updater, deleted, settings_json
)
SELECT
    '数据页签·point_binding', false, source.source_template_id, 123,
    'point-table-seed', 'point-table-seed', false, source.settings_json
FROM dynamicbusiness.dm_workbench_layout source
WHERE source.id = 227
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_entity_type
      WHERE tenant_id = 123 AND code = 'point_binding' AND deleted = false
        AND data_layout_id IS NOT NULL
  )
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dm_workbench_layout
      WHERE tenant_id = 123 AND name = '数据页签·point_binding' AND deleted = false
  );

INSERT INTO platformresource.pr_component_props (
    is_template, component_code, data_source, schema_version, props, name, status, sort,
    creator, updater, deleted, tenant_id
)
SELECT
    true, source.component_code,
    replace(source.data_source, 'event_topic', 'point_binding'),
    source.schema_version, source.props,
    replace(replace(source.name, '事件主题', '点位对照'), 'event_topic', 'point_binding'),
    1, 0, 'point-table-seed', 'point-table-seed', false, 0
FROM platformresource.pr_component_props source
WHERE source.id IN (17010, 17011, 17012)
  AND NOT EXISTS (
      SELECT 1 FROM platformresource.pr_component_props existing
      WHERE existing.deleted = false
        AND existing.tenant_id = 0
        AND existing.name = replace(source.name, '事件主题', '点位对照')
        AND existing.component_code = source.component_code
        AND existing.data_source = replace(source.data_source, 'event_topic', 'point_binding')
  );

INSERT INTO dynamicbusiness.dm_data_tab_layout (
    tenant_id, layout_id, entity_type_code, column_kind, tab_id, enabled, deleted, creator, updater, column_meta, props_id
)
SELECT
    123,
    layout.id,
    'point_binding',
    seeded.column_kind,
    seeded.tab_id,
    seeded.enabled,
    false,
    'point-table-seed',
    'point-table-seed',
    seeded.column_meta::jsonb,
    props.id
FROM dynamicbusiness.dm_workbench_layout layout
JOIN (
    VALUES
        ('CATEGORY', 'tab-category-1', true,
         '{"label":"点位对照","widthPx":240,"columnKey":"category","columnSection":"section-a","sectionWidthPx":240,"categoryTypeCode":"point_binding"}',
         'tree', '点位对照-point_binding'),
        ('MODEL', 'tab-c4e8a1', true,
         '{"label":"点位对照","widthPx":192,"columnSection":"section-a","modelEntityTypeCode":"point_binding"}',
         'list', '点位对照-point_binding'),
        ('ENTITY', 'tab-d19f02', true,
         '{"label":"点位对照","widthPx":240,"columnSection":"section-b","sectionWidthPx":400,"entityEntityTypeCode":"point_binding"}',
         'list', '点位对照-point_binding'),
        ('DETAIL', 'tab-detail-1', true,
         '{"label":"区段C栏","columnSection":"section-c","sectionWidthPx":360,"entityEntityTypeCode":"point_binding"}',
         NULL, NULL)
) AS seeded(column_kind, tab_id, enabled, column_meta, component_code, props_name)
  ON true
LEFT JOIN platformresource.pr_component_props props
  ON props.deleted = false
 AND props.tenant_id = 0
 AND props.component_code = seeded.component_code
 AND props.data_source LIKE '%"entityTypeCode":"point_binding"%'
 AND (
      (seeded.column_kind = 'CATEGORY' AND props.data_source LIKE '%"businessCategory":"category"%')
      OR (seeded.column_kind = 'MODEL' AND props.data_source LIKE '%"dataKind":"model"%')
      OR (seeded.column_kind = 'ENTITY' AND props.component_code = 'list' AND props.data_source LIKE '%"dataKind":"entity"%')
 )
WHERE layout.tenant_id = 123
  AND layout.name = '数据页签·point_binding'
  AND layout.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dm_data_tab_layout row
      WHERE row.layout_id = layout.id AND row.column_kind = seeded.column_kind AND row.deleted = false
  );

UPDATE dynamicbusiness.dynamic_entity_type
SET data_layout_id = layout.id,
    updater = 'point-table-seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamicbusiness.dm_workbench_layout layout
WHERE dynamic_entity_type.tenant_id = 123
  AND dynamic_entity_type.code = 'point_binding'
  AND dynamic_entity_type.deleted = false
  AND dynamic_entity_type.data_layout_id IS NULL
  AND layout.tenant_id = 123
  AND layout.name = '数据页签·point_binding'
  AND layout.deleted = false;

INSERT INTO dynamicbusiness.dm_data_tab_column_relation (
    entity_type_code, edge_id, from_column_identity, to_column_identity, relation_kind,
    from_type_code, to_type_code, relation_meta, creator, updater, deleted, tenant_id, layout_id
)
SELECT
    'point_binding',
    seeded.edge_id,
    seeded.from_identity,
    seeded.to_identity,
    seeded.relation_kind,
    'point_binding',
    'point_binding',
    seeded.relation_meta::jsonb,
    'point-table-seed',
    'point-table-seed',
    false,
    123,
    layout.id
FROM dynamicbusiness.dm_workbench_layout layout
JOIN (
    VALUES
        ('edge-pb-cat-model', 'CATEGORY:category:tab-category-1', 'MODEL:tab-c4e8a1', 'CATEGORY_MODEL',
         '{"edgeAction":"filter","enabledInteractions":[]}'),
        ('edge-pb-model-cat', 'MODEL:tab-c4e8a1', 'CATEGORY:category:tab-category-1', 'CATEGORY_MODEL',
         '{"edgeAction":"write","enabledInteractions":["dragAssociate"]}'),
        ('edge-pb-model-ent', 'MODEL:tab-c4e8a1', 'ENTITY:tab-d19f02', 'MODEL_ENTITY',
         '{"edgeAction":"filter","enabledInteractions":[]}'),
        ('edge-pb-ent-cat', 'ENTITY:tab-d19f02', 'CATEGORY:category:tab-category-1', 'CATEGORY_ENTITY',
         '{"edgeAction":"write","enabledInteractions":["dragAssociate"]}')
) AS seeded(edge_id, from_identity, to_identity, relation_kind, relation_meta)
  ON true
WHERE layout.tenant_id = 123
  AND layout.name = '数据页签·point_binding'
  AND layout.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dm_data_tab_column_relation rel
      WHERE rel.layout_id = layout.id AND rel.edge_id = seeded.edge_id AND rel.deleted = false
  );

UPDATE dynamicbusiness.dm_data_tab_layout row
SET props_id = props.id
FROM platformresource.pr_component_props props
WHERE row.deleted = false
  AND row.entity_type_code = 'point_binding'
  AND row.column_kind = 'ENTITY'
  AND row.props_id IS NULL
  AND props.deleted = false
  AND props.component_code = 'list'
  AND props.data_source = '{"businessCategory":"dynamic","entityTypeCode":"point_binding","dataKind":"entity"}';
