-- ============================================================================
-- data_protocol 目录补齐创建链路（分类 / 布局引用 / 编排）
--
-- 现象：数据目录可见，但打开后中区空白。
-- 根因：seed 仅写了类型与字段，未完整补齐分类类型、顶级分类、编排与 layout 绑定。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) 顶级分类根（tenant=1）
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
  '数据协议管理顶级目录',
  1,
  'flyway',
  NULL
WHERE NOT EXISTS (
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
  AND (tree_path IS NULL OR tree_path = '' OR tree_path = '/0/');

-- 2) 分类类型（指向顶级分类根）
UPDATE dynamic_category_type
SET name = '数据协议管理',
    top_level_category_id = root.id,
    category_mode = 'SIMPLE',
    entity_association_mode = 'MULTI',
    status = 1,
    deleted = false,
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT id
  FROM dynamic_category
  WHERE deleted = false
    AND tenant_id = 1
    AND code = 'data_protocol_root'
  ORDER BY id DESC
  LIMIT 1
) root
WHERE dynamic_category_type.deleted = false
  AND dynamic_category_type.tenant_id = 1
  AND dynamic_category_type.category_type_code = 'data_protocol';

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id,
  creator, tenant_id, category_mode, entity_association_mode
)
SELECT
  'data_protocol',
  '数据协议管理',
  '数据协议管理默认分类',
  1,
  root.id,
  'flyway',
  1,
  'SIMPLE',
  'MULTI'
FROM (
  SELECT id
  FROM dynamic_category
  WHERE deleted = false
    AND tenant_id = 1
    AND code = 'data_protocol_root'
  ORDER BY id DESC
  LIMIT 1
) root
WHERE NOT EXISTS (
  SELECT 1
  FROM dynamic_category_type
  WHERE deleted = false
    AND tenant_id = 1
    AND category_type_code = 'data_protocol'
);

-- 3) data_layout_id 回填：指向 data_protocol 已有 layout 行
UPDATE dynamic_entity_type et
SET data_layout_id = l.layout_id,
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT MIN(layout_id) AS layout_id
  FROM dm_data_tab_layout
  WHERE deleted = false
    AND tenant_id = 1
    AND entity_type_code = 'data_protocol'
) l
WHERE et.deleted = false
  AND et.tenant_id = 1
  AND et.code = 'data_protocol'
  AND l.layout_id IS NOT NULL
  AND et.data_layout_id IS DISTINCT FROM l.layout_id;

-- 4) 编排头补齐
UPDATE dm_catalog_orchestration
SET enabled = true,
    selection_source = 'LIST_ROW',
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND entity_type_code = 'data_protocol';

INSERT INTO dm_catalog_orchestration (
  entity_type_code, enabled, selection_source, tenant_id, creator
)
SELECT 'data_protocol', true, 'LIST_ROW', 1, 'flyway'
WHERE NOT EXISTS (
  SELECT 1
  FROM dm_catalog_orchestration
  WHERE deleted = false
    AND tenant_id = 1
    AND entity_type_code = 'data_protocol'
);

