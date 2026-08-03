-- 福建管道设施 · 2026-08-03 对齐（参考资料：海西二期发电机/地震监测方案）
-- 1) 天宝清管站型号修正  2) 补 4 站  3) 统一 region_id → REG-PROV-FJ(100024)
-- 目标表：tenant 1 物理表 ent_facility_t1 / dynamic_category_entity_link_t1
-- 幂等：按 code upsert；可重复执行

SET search_path TO dynamicbusiness;

-- ── 1. 修正天宝清管站（id=19）────────────────────────────────────────
UPDATE ent_facility_t1 f
SET
  name = '天宝清管站',
  model_id = m.id,
  facility_type = 'ng_pigging',
  region_id = 100024,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE f.deleted = false
  AND f.code = 'FAC-FJ-HX2-004'
  AND m.deleted = false
  AND m.code = 'MODEL-FACILITY-NG-PIGGING';

UPDATE dynamic_category
SET name = '天宝清管站', updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code = 'FAC-CAT-ST-FJ-HX2-004';

UPDATE dynamic_model_category_relation r
SET model_id = m.id, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE r.deleted = false AND r.tenant_id = 1
  AND r.category_code = 'FAC-CAT-ST-FJ-HX2-004'
  AND r.entity_type_code = 'facility'
  AND m.deleted = false AND m.code = 'MODEL-FACILITY-NG-PIGGING';

-- ── 2. 统一福建管道站场/管线 region_id ───────────────────────────────
UPDATE ent_facility_t1
SET region_id = 100024, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND (code LIKE 'FAC-FJ-%' OR code LIKE 'FAC-PIPE-FJ-%')
  AND (region_id IS DISTINCT FROM 100024);

-- ── 3. 追加 4 个站场实体 ─────────────────────────────────────────────
INSERT INTO ent_facility_t1 (
  id, entity_type_code, model_id, name, code, tenant_id, creator,
  tree_path, sort, status, deleted, region_id, address, facility_type, custom_fields
)
SELECT
  v.id, 'facility', m.id, v.name, v.code, 1, 'seed',
  v.tree_path, v.sort, 1, false, 100024, '福建省', v.facility_type, '{}'::jsonb
FROM (
  VALUES
    (46, 'FAC-FJ-HX2-019', '漳州首站', '/46/', 19, 'MODEL-FACILITY-NG-HEAD', 'ng_head'),
    (47, 'FAC-FJ-HX2-020', '水头分输站', '/47/', 20, 'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING', 'ng_distribution_pigging'),
    (48, 'FAC-FJ-LNGL-003', '漳州LNG接收站', '/48/', 0, 'MODEL-FACILITY-NG-RECEIVING', 'ng_receiving'),
    (49, 'FAC-FJ-CPY-005', '厦门集美分输阀室', '/49/', 5, 'MODEL-FACILITY-CP-VALVE-CHAMBER', 'cp_valve_chamber')
) AS v(id, code, name, tree_path, sort, model_code, facility_type)
JOIN dynamic_model m ON m.deleted = false AND m.code = v.model_code
ON CONFLICT (id) DO UPDATE SET
  model_id = EXCLUDED.model_id,
  name = EXCLUDED.name,
  code = EXCLUDED.code,
  region_id = EXCLUDED.region_id,
  facility_type = EXCLUDED.facility_type,
  sort = EXCLUDED.sort,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ── 4. 追加分类节点 ───────────────────────────────────────────────────
INSERT INTO dynamic_category (parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code)
SELECT p.id, n.display_name, n.category_code, 'facility', n.sort_order, 1, 1, 'seed', n.parent_code
FROM (
  VALUES
    ('FAC-CAT-ST-FJ-HX2-019', '漳州首站', 19, 'FAC-CAT-PIPE-FJ-HX2'),
    ('FAC-CAT-ST-FJ-HX2-020', '水头分输站', 20, 'FAC-CAT-PIPE-FJ-HX2'),
    ('FAC-CAT-ST-FJ-LNGL-003', '漳州LNG接收站', 0, 'FAC-CAT-PIPE-FJ-LNGL'),
    ('FAC-CAT-ST-FJ-CPY-005', '厦门集美分输阀室', 5, 'FAC-CAT-PIPE-FJ-CPY')
) AS n(category_code, display_name, sort_order, parent_code)
JOIN dynamic_category p
  ON p.deleted = false AND p.tenant_id = 1
 AND p.code = n.parent_code AND p.category_type_code = 'facility'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  sort = EXCLUDED.sort,
  parent_code = EXCLUDED.parent_code,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ── 5. 模型 ↔ 分类 ───────────────────────────────────────────────────
INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'facility', m.code, c.code, 1, 1, 'seed'
FROM (
  VALUES
    ('FAC-CAT-ST-FJ-HX2-019', 'MODEL-FACILITY-NG-HEAD'),
    ('FAC-CAT-ST-FJ-HX2-020', 'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING'),
    ('FAC-CAT-ST-FJ-LNGL-003', 'MODEL-FACILITY-NG-RECEIVING'),
    ('FAC-CAT-ST-FJ-CPY-005', 'MODEL-FACILITY-CP-VALVE-CHAMBER')
) AS v(category_code, model_code)
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1 AND c.code = v.category_code
JOIN dynamic_model m ON m.deleted = false AND m.code = v.model_code
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ── 6. Pattern C 绑定 ────────────────────────────────────────────────
INSERT INTO dynamic_category_entity_link_t1 (category_id, entity_id, entity_model_id, tenant_id, creator)
SELECT c.id, e.id, e.model_id, 1, 'seed'
FROM (
  VALUES
    ('FAC-CAT-ST-FJ-HX2-019', 46),
    ('FAC-CAT-ST-FJ-HX2-020', 47),
    ('FAC-CAT-ST-FJ-LNGL-003', 48),
    ('FAC-CAT-ST-FJ-CPY-005', 49)
) AS v(category_code, entity_id)
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1 AND c.code = v.category_code
JOIN ent_facility_t1 e ON e.deleted = false AND e.id = v.entity_id
ON CONFLICT DO NOTHING;

-- ── 7. 重建 facility 分类 tree_path / level ──────────────────────────
WITH RECURSIVE cat_tree AS (
  SELECT c.id, c.parent_id, ARRAY[c.id] AS path_ids, 0 AS lvl
  FROM dynamic_category c
  WHERE c.deleted = false AND c.tenant_id = 1
    AND c.category_type_code = 'facility'
    AND (c.parent_id IS NULL OR c.parent_id = 0
         OR NOT EXISTS (
           SELECT 1 FROM dynamic_category p
           WHERE p.id = c.parent_id AND p.deleted = false AND p.tenant_id = 1
         ))
  UNION ALL
  SELECT c.id, c.parent_id, ct.path_ids || c.id, ct.lvl + 1
  FROM dynamic_category c
  JOIN cat_tree ct ON c.parent_id = ct.id
  WHERE c.deleted = false AND c.tenant_id = 1 AND c.category_type_code = 'facility'
)
UPDATE dynamic_category c
SET
  tree_path = '/' || array_to_string(ct.path_ids, '/') || '/',
  level = ct.lvl + 1,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM cat_tree ct
WHERE c.id = ct.id;

SELECT setval(
  pg_get_serial_sequence('dynamicbusiness.ent_facility_t1', 'id'),
  GREATEST((SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_facility_t1), 49)
);
