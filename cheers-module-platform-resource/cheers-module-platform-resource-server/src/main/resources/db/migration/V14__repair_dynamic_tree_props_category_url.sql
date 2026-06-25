SET search_path TO platformresource;

-- 动态 model/entity 树组件 props 中误存 POC 分类树读 URL：清空后由能力投影 / 配置器补全写入正确端点。
-- 分类域树（data_source.businessCategory = category）保留 category/tree。
UPDATE pr_component_props
SET
    props = jsonb_set(
        props,
        '{dataSource,dataSourceEndpoint,url}',
        '""'::jsonb,
        true
    ),
    updater = 'repair',
    update_time = CURRENT_TIMESTAMP
WHERE component_code = 'tree'
  AND deleted = FALSE
  AND COALESCE(data_source->>'businessCategory', '') = 'dynamic'
  AND COALESCE(data_source->>'dataKind', '') IN ('model', 'entity')
  AND (
    props->'dataSource'->'dataSourceEndpoint'->>'url' LIKE '%/dynamicbusiness/category/tree%'
    OR props->'dataSource'->'dataSourceEndpoint'->>'url' LIKE '%/system/category/tree%'
  );

-- 同步清理 apiConfig.dataEndpoint 中同类误存（历史保存形态）
UPDATE pr_component_props
SET
    props = jsonb_set(
        props,
        '{apiConfig,dataEndpoint,url}',
        '""'::jsonb,
        true
    ),
    updater = 'repair',
    update_time = CURRENT_TIMESTAMP
WHERE component_code = 'tree'
  AND deleted = FALSE
  AND COALESCE(data_source->>'businessCategory', '') = 'dynamic'
  AND COALESCE(data_source->>'dataKind', '') IN ('model', 'entity')
  AND (
    props->'apiConfig'->'dataEndpoint'->>'url' LIKE '%/dynamicbusiness/category/tree%'
    OR props->'apiConfig'->'dataEndpoint'->>'url' LIKE '%/system/category/tree%'
  );
