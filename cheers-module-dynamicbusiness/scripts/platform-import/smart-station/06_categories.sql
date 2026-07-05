-- ============================================================================
-- 智慧站场 · 06_categories.sql
-- Generated: 2026-07-05 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 依赖：先导入 system/ 全包（字段库已在 system/03_fields.sql）
-- 本包仅含站场 Region 模型与分类
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_category_type: (empty)


-- dynamic_category: 6 row(s), upsert by code; parent by parent_code

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '油库区域', 'CAT-OIL-REGION-ROOT',
  'region', 0,
  1, '油库站场 Region 根节点；子节点为库区/罐组实体', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-OIL-REGION-ROOT' LIMIT 1), '库区/站场', 'CAT-OIL-REGION-SITE',
  'region', 1,
  1, '绑定 MODEL-REGION-SITE', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-OIL-REGION-ROOT' LIMIT 1), '罐组', 'CAT-OIL-REGION-TANK-GROUP',
  'region', 2,
  1, '绑定 MODEL-REGION-TANK-GROUP', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-OIL-REGION-SITE' LIMIT 1), '东营油库', 'REGION-SITE-DY',
  'region', 1,
  1, '示例库区实体', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'REGION-SITE-DY' LIMIT 1), '1#罐组', 'REGION-TG-01',
  'region', 1,
  1, '示例罐组', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'REGION-SITE-DY' LIMIT 1), '2#罐组', 'REGION-TG-02',
  'region', 2,
  1, '示例罐组', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;


-- rebuild tree_path / level after category upsert (id-agnostic)
WITH RECURSIVE cat_tree AS (
  SELECT c.id, c.code, c.parent_id, ARRAY[c.id] AS path_ids, 0 AS lvl
  FROM dynamic_category c
  WHERE c.deleted = false AND c.tenant_id = 1
    AND (c.parent_id IS NULL OR c.parent_id = 0
         OR NOT EXISTS (
           SELECT 1 FROM dynamic_category p
           WHERE p.id = c.parent_id AND p.deleted = false AND p.tenant_id = 1
         ))
  UNION ALL
  SELECT c.id, c.code, c.parent_id, ct.path_ids || c.id, ct.lvl + 1
  FROM dynamic_category c
  JOIN cat_tree ct ON c.parent_id = ct.id
  WHERE c.deleted = false AND c.tenant_id = 1
)
UPDATE dynamic_category c
SET
  tree_path = '/' || array_to_string(ct.path_ids, '/') || '/',
  level = ct.lvl + 1,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM cat_tree ct
WHERE c.id = ct.id;


-- dynamic_model_category_relation: 2 row(s), resolve by model_code + category_code

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'region', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-SITE'
  AND c.code = 'CAT-OIL-REGION-SITE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'region', 2, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-TANK-GROUP'
  AND c.code = 'CAT-OIL-REGION-TANK-GROUP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );


-- dynamic_page_config: 1 row(s), upsert by page_code

INSERT INTO dynamic_page_config (config_code, page_code, menu_id, page_type, config, tenant_id, creator)
VALUES ('region-oil-depot', 'PAGE-REGION-OIL-DEPOT', NULL, 'data_management', '{"pattern": "C", "dragBehavior": "entity-category", "leftTreeType": "category", "showModelManage": true, "addChildBehavior": "entity-category", "rightContentType": "entity-list-with-detail", "showDeviceTypeSelect": false, "categoryDrawerEntityMode": true, "entitySourceBusinessType": "facility"}', 1, 'seed')
ON CONFLICT (page_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  config_code = EXCLUDED.config_code,
  menu_id = EXCLUDED.menu_id,
  page_type = EXCLUDED.page_type,
  config = EXCLUDED.config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
