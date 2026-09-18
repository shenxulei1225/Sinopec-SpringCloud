-- ============================================================================
-- data_protocol 目录修复：补齐分类类型 / 编排 / 布局绑定
--
-- 背景：
-- 新增 data_protocol 时若只写了实体与字段、未完成目录 bootstrap，
-- 数据管理打开该目录会出现空白工作区（无可用区段）。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) 顶级分类根节点
INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, tree_path, level, sort, status,
  description, tenant_id, creator, parent_code
)
SELECT
  NULL,
  '数据协议管理',
  'data_protocol_root',
  'data_protocol',
  '/0/',
  1,
  0,
  1,
  '数据协议管理顶级分类',
  1,
  'flyway',
  NULL
WHERE EXISTS (
  SELECT 1
  FROM dynamic_entity_type
  WHERE deleted = false
    AND tenant_id = 1
    AND code = 'data_protocol'
)
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_category
    WHERE deleted = false
      AND tenant_id = 1
      AND code = 'data_protocol_root'
  );

UPDATE dynamic_category
SET tree_path = '/' || id || '/',
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'data_protocol_root'
  AND tree_path = '/0/';

-- 2) 分类类型
INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
)
SELECT
  'data_protocol',
  '数据协议管理',
  '数据协议管理默认分类',
  1,
  c.id,
  1,
  'flyway'
FROM dynamic_category c
WHERE c.deleted = false
  AND c.tenant_id = 1
  AND c.code = 'data_protocol_root'
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_category_type ct
    WHERE ct.deleted = false
      AND ct.tenant_id = 1
      AND ct.category_type_code = 'data_protocol'
  );

UPDATE dynamic_category_type ct
SET top_level_category_id = c.id,
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_category c
WHERE ct.deleted = false
  AND ct.tenant_id = 1
  AND ct.category_type_code = 'data_protocol'
  AND c.deleted = false
  AND c.tenant_id = 1
  AND c.code = 'data_protocol_root'
  AND ct.top_level_category_id IS DISTINCT FROM c.id;

-- 3) 编排头
INSERT INTO dm_catalog_orchestration (
  entity_type_code, enabled, selection_source, tenant_id, creator
)
SELECT
  'data_protocol',
  true,
  'LIST_ROW',
  1,
  'flyway'
WHERE EXISTS (
  SELECT 1
  FROM dynamic_entity_type
  WHERE deleted = false
    AND tenant_id = 1
    AND code = 'data_protocol'
)
  AND NOT EXISTS (
    SELECT 1
    FROM dm_catalog_orchestration
    WHERE deleted = false
      AND tenant_id = 1
      AND entity_type_code = 'data_protocol'
  );

-- 4) data_layout_id 回挂
UPDATE dynamic_entity_type et
SET data_layout_id = x.layout_id,
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT layout_id
  FROM dm_data_tab_layout
  WHERE deleted = false
    AND tenant_id = 1
    AND entity_type_code = 'data_protocol'
  ORDER BY id
  LIMIT 1
) x
WHERE et.deleted = false
  AND et.tenant_id = 1
  AND et.code = 'data_protocol'
  AND et.data_layout_id IS NULL;

-- 5) 兜底创建默认布局头并绑定
INSERT INTO dm_workbench_layout (name, is_template, source_template_id, tenant_id, creator, settings_json)
SELECT
  '数据页签·data_protocol',
  false,
  1,
  1,
  'flyway',
  '{"sections":[{"id":"filter","name":"筛选","arrange":"horizontal"},{"id":"who","name":"对象","arrange":"horizontal"},{"id":"what","name":"详情","arrange":"free"}],"sectionHidden":{}}'::jsonb
WHERE EXISTS (
  SELECT 1
  FROM dynamic_entity_type
  WHERE deleted = false
    AND tenant_id = 1
    AND code = 'data_protocol'
    AND data_layout_id IS NULL
)
  AND NOT EXISTS (
    SELECT 1
    FROM dm_workbench_layout
    WHERE deleted = false
      AND tenant_id = 1
      AND name = '数据页签·data_protocol'
  );

UPDATE dynamic_entity_type et
SET data_layout_id = w.id,
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
FROM dm_workbench_layout w
WHERE et.deleted = false
  AND et.tenant_id = 1
  AND et.code = 'data_protocol'
  AND et.data_layout_id IS NULL
  AND w.deleted = false
  AND w.tenant_id = 1
  AND w.name = '数据页签·data_protocol';

-- 6) 布局栏骨架（仅在该布局无任何栏行时补齐）
INSERT INTO dm_data_tab_layout (
  tenant_id, entity_type_code, column_kind, tab_id, props_id, enabled, column_meta, layout_id, creator
)
SELECT
  1,
  'data_protocol',
  v.column_kind,
  v.tab_id,
  NULL,
  true,
  v.column_meta::jsonb,
  et.data_layout_id,
  'flyway'
FROM dynamic_entity_type et
CROSS JOIN (
  VALUES
    ('CATEGORY', 'category-1', '{"label":"数据协议管理","columnSection":"filter","categoryTypeCode":"data_protocol"}'),
    ('MODEL', 'data_protocol', '{"label":"数据协议管理","columnSection":"filter","modelEntityTypeCode":"data_protocol"}'),
    ('ENTITY', 'data_protocol', '{"label":"数据协议管理","columnSection":"who","entityEntityTypeCode":"data_protocol"}'),
    ('DETAIL', 'detail', '{"label":"详情","columnSection":"what","entityEntityTypeCode":"data_protocol"}')
) AS v(column_kind, tab_id, column_meta)
WHERE et.deleted = false
  AND et.tenant_id = 1
  AND et.code = 'data_protocol'
  AND et.data_layout_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM dm_data_tab_layout d
    WHERE d.deleted = false
      AND d.layout_id = et.data_layout_id
  );

