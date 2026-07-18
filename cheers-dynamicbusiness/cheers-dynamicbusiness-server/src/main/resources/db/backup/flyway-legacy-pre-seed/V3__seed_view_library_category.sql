-- 视图库分类基础数据（dynamicbusiness.dynamic_category_type / dynamic_category）
-- categoryTypeCode 固定为 view；以下为平台预置分组，用户可继续增删改。
-- 幂等：已存在同 code / category_type_code 时跳过。

-- ── 1. 分类类型 ─────────────────────────────────────────────────────────────
INSERT INTO dynamicbusiness.dynamic_category_type (
    id, category_type_code, name, description, status,
    top_level_category_id, creator, create_time, updater, update_time, deleted, tenant_id
)
SELECT
    18,
    'view',
    '视图分类',
    '平台视图库资源分组（categoryTypeCode=view）',
    1,
    NULL,
    'system',
    CURRENT_TIMESTAMP,
    'system',
    CURRENT_TIMESTAMP,
    false,
    1
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.dynamic_category_type
    WHERE category_type_code = 'view'
      AND deleted = false
      AND tenant_id = 1
);

-- ── 2. 顶级分类节点 ─────────────────────────────────────────────────────────
INSERT INTO dynamicbusiness.dynamic_category (
    id, parent_id, name, code, category_type_code, tree_path, level, sort, status,
    description, creator, create_time, updater, update_time, deleted, tenant_id
)
SELECT
    4903,
    0,
    '视图分类',
    'view_root',
    'view',
    '/4903/',
    1,
    0,
    1,
    '视图库分类根节点',
    'system',
    CURRENT_TIMESTAMP,
    'system',
    CURRENT_TIMESTAMP,
    false,
    1
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.dynamic_category
    WHERE code = 'view_root'
      AND category_type_code = 'view'
      AND deleted = false
      AND tenant_id = 1
);

UPDATE dynamicbusiness.dynamic_category_type ct
SET top_level_category_id = c.id,
    updater = 'system',
    update_time = CURRENT_TIMESTAMP
FROM dynamicbusiness.dynamic_category c
WHERE ct.category_type_code = 'view'
  AND ct.deleted = false
  AND ct.tenant_id = 1
  AND c.code = 'view_root'
  AND c.category_type_code = 'view'
  AND c.deleted = false
  AND c.tenant_id = 1
  AND (ct.top_level_category_id IS NULL OR ct.top_level_category_id <> c.id);

-- ── 3. 默认子分类（视图卡片分组） ───────────────────────────────────────────
INSERT INTO dynamicbusiness.dynamic_category (
    id, parent_id, name, code, category_type_code, tree_path, level, sort, status,
    description, creator, create_time, updater, update_time, deleted, tenant_id
)
SELECT
    v.id,
    root.id,
    v.name,
    v.code,
    'view',
    CONCAT('/', root.id, '/', v.id, '/'),
    2,
    v.sort,
    1,
    v.description,
    'system',
    CURRENT_TIMESTAMP,
    'system',
    CURRENT_TIMESTAMP,
    false,
    1
FROM (
    VALUES
        (4904, 'view_data_management', '数据管理', 1, '树表、列表等数据维护类视图'),
        (4905, 'view_dashboard',       '看板',     2, '业务看板、任务看板、运营概览'),
        (4906, 'view_portal',          '门户',     3, '导航 hub、模块入口、信息聚合'),
        (4907, 'view_general',         '其他',     4, '暂未归入具体业务场景；非 viewType，仅为资源库兜底分组')
) AS v(id, code, name, sort, description)
CROSS JOIN (
    SELECT id
    FROM dynamicbusiness.dynamic_category
    WHERE code = 'view_root'
      AND category_type_code = 'view'
      AND deleted = false
      AND tenant_id = 1
    LIMIT 1
) AS root
WHERE root.id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_category existing
      WHERE existing.code = v.code
        AND existing.deleted = false
        AND existing.tenant_id = 1
  );

-- ── 4. 序列对齐 ─────────────────────────────────────────────────────────────
SELECT pg_catalog.setval(
    'dynamicbusiness.dynamic_category_id_seq',
    GREATEST(
        COALESCE((SELECT MAX(id) FROM dynamicbusiness.dynamic_category), 1),
        4907
    ),
    true
);

SELECT pg_catalog.setval(
    'dynamicbusiness.dynamic_category_type_id_seq',
    GREATEST(
        COALESCE((SELECT MAX(id) FROM dynamicbusiness.dynamic_category_type), 1),
        18
    ),
    true
);
