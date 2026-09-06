-- ============================================================================
-- standard · 05 分类：根 + 国标/行标/企标
-- 若本机已有「国标规范」等 UUID 编码节点，按名称改成稳定编码，便于 seed 挂接
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 分类种类
INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, category_mode,
  entity_association_mode, tenant_id, creator, deleted
) VALUES (
  'standard', '规范标准', '规范标准分类（国标/行标/企标）', 1, 'SIMPLE', 'MULTI', 1, 'seed', false
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 根节点
INSERT INTO dynamic_category (
  code, name, category_type_code, parent_id, status, sort, tenant_id, creator, deleted
)
SELECT 'standard_root', '规范标准', 'standard', NULL, 1, 0, 1, 'seed', false
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_category c
  WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'standard_root'
);

-- 历史界面创建的子节点：按名称对齐稳定编码
UPDATE dynamic_category
SET code = 'cat-standard-national',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1
  AND category_type_code = 'standard'
  AND name = '国标规范'
  AND code <> 'cat-standard-national'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_category x
    WHERE x.deleted = false AND x.tenant_id = 1 AND x.code = 'cat-standard-national'
  );

UPDATE dynamic_category
SET code = 'cat-standard-industry',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1
  AND category_type_code = 'standard'
  AND name = '行标规范'
  AND code <> 'cat-standard-industry'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_category x
    WHERE x.deleted = false AND x.tenant_id = 1 AND x.code = 'cat-standard-industry'
  );

UPDATE dynamic_category
SET code = 'cat-standard-enterprise',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1
  AND category_type_code = 'standard'
  AND name = '企标规范'
  AND code <> 'cat-standard-enterprise'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_category x
    WHERE x.deleted = false AND x.tenant_id = 1 AND x.code = 'cat-standard-enterprise'
  );

-- 缺则插入三级分类
INSERT INTO dynamic_category (
  code, name, category_type_code, parent_id, status, sort, tenant_id, creator, deleted
)
SELECT v.code, v.name, 'standard', p.id, 1, v.sort, 1, 'seed', false
FROM (
  VALUES
    ('cat-standard-national', '国标规范', 10),
    ('cat-standard-industry', '行标规范', 20),
    ('cat-standard-enterprise', '企标规范', 30)
) AS v(code, name, sort)
JOIN dynamic_category p
  ON p.deleted = false AND p.tenant_id = 1 AND p.code = 'standard_root'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_category c
  WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = v.code
);

-- 种类顶层指向根
UPDATE dynamic_category_type ct
SET top_level_category_id = root.id,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_category root
WHERE ct.deleted = false AND ct.tenant_id = 1 AND ct.category_type_code = 'standard'
  AND root.deleted = false AND root.tenant_id = 1 AND root.code = 'standard_root';
