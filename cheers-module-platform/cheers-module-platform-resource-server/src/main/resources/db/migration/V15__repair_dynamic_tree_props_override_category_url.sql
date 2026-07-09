SET search_path TO platformresource;

-- V14 清理 props 列；本迁移清理 props_override 中同类误存（用户/设计器差量保存）。
-- 动态 model/entity 树误带 POC 分类树读 URL 时清空，由能力投影 / 配置器重新 apply 写入。
-- 分类域树（data_source.businessCategory = category）保留 category/tree。
UPDATE pr_component_props
SET
    props_override = jsonb_set(
        props_override::jsonb,
        '{dataSource,dataSourceEndpoint,url}',
        '""'::jsonb,
        true
    )::text,
    updater = 'repair',
    update_time = CURRENT_TIMESTAMP
WHERE component_code = 'tree'
  AND deleted = FALSE
  AND props_override IS NOT NULL
  AND props_override::jsonb <> '{}'::jsonb
  AND COALESCE(data_source::jsonb->>'businessCategory', '') = 'dynamic'
  AND COALESCE(data_source::jsonb->>'dataKind', '') IN ('model', 'entity')
  AND (
    props_override::jsonb->'dataSource'->'dataSourceEndpoint'->>'url' LIKE '%/dynamicbusiness/category/tree%'
    OR props_override::jsonb->'dataSource'->'dataSourceEndpoint'->>'url' LIKE '%/system/category/tree%'
  );

UPDATE pr_component_props
SET
    props_override = jsonb_set(
        props_override::jsonb,
        '{apiConfig,dataEndpoint,url}',
        '""'::jsonb,
        true
    )::text,
    updater = 'repair',
    update_time = CURRENT_TIMESTAMP
WHERE component_code = 'tree'
  AND deleted = FALSE
  AND props_override IS NOT NULL
  AND props_override::jsonb <> '{}'::jsonb
  AND COALESCE(data_source::jsonb->>'businessCategory', '') = 'dynamic'
  AND COALESCE(data_source::jsonb->>'dataKind', '') IN ('model', 'entity')
  AND (
    props_override::jsonb->'apiConfig'->'dataEndpoint'->>'url' LIKE '%/dynamicbusiness/category/tree%'
    OR props_override::jsonb->'apiConfig'->'dataEndpoint'->>'url' LIKE '%/system/category/tree%'
  );
