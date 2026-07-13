-- 福建设施 Pattern C：管道线路（facility 管道模型）→ 站场（facility 站型模型）
-- 前置：dynamic_entity_facility_fujian_dev_sample.sql、dynamic_model_facility.sql（含 PIPELINE 模型）
-- 组织维度：站场 region_id 已在设施 seed 挂 REG-PROV-FJ（100024）

SET search_path TO dynamicbusiness;

DROP TABLE IF EXISTS tmp_facility_pattern_c;
CREATE TEMP TABLE tmp_facility_pattern_c AS
SELECT *
FROM (VALUES
  ('FAC-CAT-PIPE-FJ-CPY', '福建成品油管道', 42, 'FAC-PIPE-FJ-CPY', 'MODEL-FACILITY-PIPELINE-CP', 1, 'facility_root'),
  ('FAC-CAT-PIPE-FJ-W3', '西三线', 40, 'FAC-PIPE-FJ-W3', 'MODEL-FACILITY-PIPELINE-NG', 2, 'facility_root'),
  ('FAC-CAT-PIPE-FJ-HX2', '海西二期', 41, 'FAC-PIPE-FJ-HX2', 'MODEL-FACILITY-PIPELINE-NG', 3, 'facility_root'),
  ('FAC-CAT-PIPE-FJ-LNGL', '漳州LNG联络线', 43, 'FAC-PIPE-FJ-LNGL', 'MODEL-FACILITY-PIPELINE-NG', 4, 'facility_root'),
  ('FAC-CAT-ST-FJ-CPY-001', '泉港油库', 34, 'FAC-FJ-CPY-001', 'MODEL-FACILITY-REFINED-DEPOT', 1, 'FAC-CAT-PIPE-FJ-CPY'),
  ('FAC-CAT-ST-FJ-CPY-002', '兴闽站', 35, 'FAC-FJ-CPY-002', 'MODEL-FACILITY-CP-DISTRIBUTION', 2, 'FAC-CAT-PIPE-FJ-CPY'),
  ('FAC-CAT-ST-FJ-CPY-003', '石湖山站', 36, 'FAC-FJ-CPY-003', 'MODEL-FACILITY-CP-DISTRIBUTION', 3, 'FAC-CAT-PIPE-FJ-CPY'),
  ('FAC-CAT-ST-FJ-CPY-004', '东孚站', 37, 'FAC-FJ-CPY-004', 'MODEL-FACILITY-CP-DISTRIBUTION', 4, 'FAC-CAT-PIPE-FJ-CPY'),
  ('FAC-CAT-ST-FJ-W3-001', '福州末站', 3, 'FAC-FJ-W3-001', 'MODEL-FACILITY-NG-TERMINAL', 1, 'FAC-CAT-PIPE-FJ-W3'),
  ('FAC-CAT-ST-FJ-W3-002', '福清分输站', 4, 'FAC-FJ-W3-002', 'MODEL-FACILITY-NG-DISTRIBUTION', 2, 'FAC-CAT-PIPE-FJ-W3'),
  ('FAC-CAT-ST-FJ-W3-003', '莆田分输清管站', 5, 'FAC-FJ-W3-003', 'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING', 3, 'FAC-CAT-PIPE-FJ-W3'),
  ('FAC-CAT-ST-FJ-W3-004', '仙游站', 6, 'FAC-FJ-W3-004', 'MODEL-FACILITY-NG-DISTRIBUTION', 4, 'FAC-CAT-PIPE-FJ-W3'),
  ('FAC-CAT-ST-FJ-W3-005', '泉州分输清管站', 7, 'FAC-FJ-W3-005', 'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING', 5, 'FAC-CAT-PIPE-FJ-W3'),
  ('FAC-CAT-ST-FJ-W3-006', '同安分输站', 8, 'FAC-FJ-W3-006', 'MODEL-FACILITY-NG-DISTRIBUTION', 6, 'FAC-CAT-PIPE-FJ-W3'),
  ('FAC-CAT-ST-FJ-W3-007', '海沧分输站', 9, 'FAC-FJ-W3-007', 'MODEL-FACILITY-NG-DISTRIBUTION', 7, 'FAC-CAT-PIPE-FJ-W3'),
  ('FAC-CAT-ST-FJ-W3-008', '漳州分输清管站', 10, 'FAC-FJ-W3-008', 'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING', 8, 'FAC-CAT-PIPE-FJ-W3'),
  ('FAC-CAT-ST-FJ-W3-009', '龙岩分输清管站', 11, 'FAC-FJ-W3-009', 'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING', 9, 'FAC-CAT-PIPE-FJ-W3'),
  ('FAC-CAT-ST-FJ-W3-010', '长汀站', 12, 'FAC-FJ-W3-010', 'MODEL-FACILITY-NG-DISTRIBUTION', 10, 'FAC-CAT-PIPE-FJ-W3'),
  ('FAC-CAT-ST-FJ-W3-011', '南安站', 13, 'FAC-FJ-W3-011', 'MODEL-FACILITY-NG-DISTRIBUTION', 11, 'FAC-CAT-PIPE-FJ-W3'),
  ('FAC-CAT-ST-FJ-W3-012', '东田站', 14, 'FAC-FJ-W3-012', 'MODEL-FACILITY-NG-DISTRIBUTION', 12, 'FAC-CAT-PIPE-FJ-W3'),
  ('FAC-CAT-ST-FJ-W3-013', '角美站', 15, 'FAC-FJ-W3-013', 'MODEL-FACILITY-NG-DISTRIBUTION', 13, 'FAC-CAT-PIPE-FJ-W3'),
  ('FAC-CAT-ST-FJ-HX2-001', '旧镇分输站', 16, 'FAC-FJ-HX2-001', 'MODEL-FACILITY-NG-DISTRIBUTION', 1, 'FAC-CAT-PIPE-FJ-HX2'),
  ('FAC-CAT-ST-FJ-HX2-002', '常山分输站', 17, 'FAC-FJ-HX2-002', 'MODEL-FACILITY-NG-DISTRIBUTION', 2, 'FAC-CAT-PIPE-FJ-HX2'),
  ('FAC-CAT-ST-FJ-HX2-003', '诏安末站', 18, 'FAC-FJ-HX2-003', 'MODEL-FACILITY-NG-TERMINAL', 3, 'FAC-CAT-PIPE-FJ-HX2'),
  ('FAC-CAT-ST-FJ-HX2-004', '天宝站', 19, 'FAC-FJ-HX2-004', 'MODEL-FACILITY-NG-DISTRIBUTION', 4, 'FAC-CAT-PIPE-FJ-HX2'),
  ('FAC-CAT-ST-FJ-HX2-005', '南靖站', 20, 'FAC-FJ-HX2-005', 'MODEL-FACILITY-NG-DISTRIBUTION', 5, 'FAC-CAT-PIPE-FJ-HX2'),
  ('FAC-CAT-ST-FJ-HX2-006', '龙岩站', 21, 'FAC-FJ-HX2-006', 'MODEL-FACILITY-NG-DISTRIBUTION', 6, 'FAC-CAT-PIPE-FJ-HX2'),
  ('FAC-CAT-ST-FJ-HX2-007', '德化站', 22, 'FAC-FJ-HX2-007', 'MODEL-FACILITY-NG-DISTRIBUTION', 7, 'FAC-CAT-PIPE-FJ-HX2'),
  ('FAC-CAT-ST-FJ-HX2-008', '琅岐分输站', 23, 'FAC-FJ-HX2-008', 'MODEL-FACILITY-NG-DISTRIBUTION', 8, 'FAC-CAT-PIPE-FJ-HX2'),
  ('FAC-CAT-ST-FJ-HX2-009', '连江分输站', 24, 'FAC-FJ-HX2-009', 'MODEL-FACILITY-NG-DISTRIBUTION', 9, 'FAC-CAT-PIPE-FJ-HX2'),
  ('FAC-CAT-ST-FJ-HX2-010', '罗源分输站', 25, 'FAC-FJ-HX2-010', 'MODEL-FACILITY-NG-DISTRIBUTION', 10, 'FAC-CAT-PIPE-FJ-HX2'),
  ('FAC-CAT-ST-FJ-HX2-011', '宁德分输站', 26, 'FAC-FJ-HX2-011', 'MODEL-FACILITY-NG-DISTRIBUTION', 11, 'FAC-CAT-PIPE-FJ-HX2'),
  ('FAC-CAT-ST-FJ-HX2-012', '双木洋清管站', 27, 'FAC-FJ-HX2-012', 'MODEL-FACILITY-NG-PIGGING', 12, 'FAC-CAT-PIPE-FJ-HX2'),
  ('FAC-CAT-ST-FJ-HX2-013', '福安分输站', 28, 'FAC-FJ-HX2-013', 'MODEL-FACILITY-NG-DISTRIBUTION', 13, 'FAC-CAT-PIPE-FJ-HX2'),
  ('FAC-CAT-ST-FJ-HX2-014', '柘荣分输站', 29, 'FAC-FJ-HX2-014', 'MODEL-FACILITY-NG-DISTRIBUTION', 14, 'FAC-CAT-PIPE-FJ-HX2'),
  ('FAC-CAT-ST-FJ-HX2-015', '福鼎末站', 30, 'FAC-FJ-HX2-015', 'MODEL-FACILITY-NG-TERMINAL', 15, 'FAC-CAT-PIPE-FJ-HX2'),
  ('FAC-CAT-ST-FJ-HX2-016', '安溪分输站', 31, 'FAC-FJ-HX2-016', 'MODEL-FACILITY-NG-DISTRIBUTION', 16, 'FAC-CAT-PIPE-FJ-HX2'),
  ('FAC-CAT-ST-FJ-HX2-017', '南安分输站', 32, 'FAC-FJ-HX2-017', 'MODEL-FACILITY-NG-DISTRIBUTION', 17, 'FAC-CAT-PIPE-FJ-HX2'),
  ('FAC-CAT-ST-FJ-HX2-018', '永春分输站', 33, 'FAC-FJ-HX2-018', 'MODEL-FACILITY-NG-DISTRIBUTION', 18, 'FAC-CAT-PIPE-FJ-HX2'),
  ('FAC-CAT-ST-FJ-LNGL-001', '程溪分输站', 38, 'FAC-FJ-LNGL-001', 'MODEL-FACILITY-NG-DISTRIBUTION', 1, 'FAC-CAT-PIPE-FJ-LNGL'),
  ('FAC-CAT-ST-FJ-LNGL-002', '港尾分输站', 39, 'FAC-FJ-LNGL-002', 'MODEL-FACILITY-NG-DISTRIBUTION', 2, 'FAC-CAT-PIPE-FJ-LNGL')
) AS v(
  category_code, display_name, entity_id, entity_code, model_code, sort_order, parent_code
);

-- 1) 管道线路实体（facility · 管道模型）
INSERT INTO ent_facility (
  id, entity_type_code, model_id, name, code, tenant_id, creator,
  tree_path, sort, status, deleted, region_id, facility_type, custom_fields
)
SELECT
  n.entity_id, 'facility', m.id, n.display_name,
  COALESCE(n.entity_code, 'FAC-PIPE-' || n.entity_id::text),
  1, 'seed', '/' || n.entity_id::text || '/', n.sort_order, 1, false,
  100024,
  CASE m.code
    WHEN 'MODEL-FACILITY-PIPELINE-CP' THEN 'pipeline_cp'
    WHEN 'MODEL-FACILITY-PIPELINE-CR' THEN 'pipeline_cr'
    ELSE 'pipeline_ng'
  END,
  '{}'::jsonb
FROM tmp_facility_pattern_c n
JOIN dynamic_model m ON m.deleted = false AND m.code = n.model_code
WHERE n.model_code LIKE 'MODEL-FACILITY-PIPELINE-%'
ON CONFLICT (id) DO UPDATE SET
  model_id = EXCLUDED.model_id,
  name = EXCLUDED.name,
  code = EXCLUDED.code,
  region_id = EXCLUDED.region_id,
  facility_type = EXCLUDED.facility_type,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 2a) 管道线路分类（挂 facility_root）
INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
)
SELECT
  p.id, n.display_name, n.category_code, 'facility', n.sort_order, 1, 1, 'seed', n.parent_code
FROM tmp_facility_pattern_c n
JOIN dynamic_category p
  ON p.deleted = false AND p.tenant_id = 1
 AND p.code = n.parent_code AND p.category_type_code = 'facility'
WHERE n.model_code LIKE 'MODEL-FACILITY-PIPELINE-%'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  sort = EXCLUDED.sort,
  parent_code = EXCLUDED.parent_code,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 2b) 站场分类（挂管道线路分类）
INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
)
SELECT
  p.id, n.display_name, n.category_code, 'facility', n.sort_order, 1, 1, 'seed', n.parent_code
FROM tmp_facility_pattern_c n
JOIN dynamic_category p
  ON p.deleted = false AND p.tenant_id = 1
 AND p.code = n.parent_code AND p.category_type_code = 'facility'
WHERE n.model_code NOT LIKE 'MODEL-FACILITY-PIPELINE-%'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  sort = EXCLUDED.sort,
  parent_code = EXCLUDED.parent_code,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 3) 模型 ↔ 分类
INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'facility', m.code, c.code, 1, 1, 'seed'
FROM tmp_facility_pattern_c n
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1 AND c.code = n.category_code
JOIN dynamic_model m ON m.deleted = false AND m.code = n.model_code
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 4) Pattern C 绑定（清理旧 facility 绑定后重建福建节点）
UPDATE dynamic_category_entity_link l
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM dynamic_category c
WHERE l.category_id = c.id
  AND c.deleted = false AND c.tenant_id = 1
  AND c.category_type_code = 'facility'
  AND c.code LIKE 'FAC-CAT-%'
  AND l.deleted = false;

INSERT INTO dynamic_category_entity_link (category_id, entity_id, entity_model_id, tenant_id, creator)
SELECT c.id, n.entity_id, e.model_id, 1, 'seed'
FROM tmp_facility_pattern_c n
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1 AND c.code = n.category_code
JOIN ent_facility e ON e.id = n.entity_id AND e.deleted = false;

-- 5) 废弃 region 下误挂的管道节点与 ent_region pipeline 行
UPDATE dynamic_category
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code LIKE 'REG-CAT-PIPE-%';

UPDATE ent_region
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND region_type = 'pipeline';

UPDATE dynamic_model
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code = 'MODEL-REGION-PIPELINE';

-- 6) 废弃独立 pipeline 数据类型及 ent_pipeline 演示数据
UPDATE dynamic_entity_type
SET status = 'inactive', deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE code = 'pipeline' AND tenant_id = 1;

UPDATE ent_pipeline SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1;

-- 7) 重建 facility 分类 tree_path / level（本 seed 在 import 全量 repair 之后执行）
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

-- 8) Pattern C：facility 分类种类说明（数据管理读 ORG_RECORD 语义）
UPDATE dynamic_category_type
SET
  description = '管道线路 → 站场层级；Pattern C 分类即实体（与运营区域相同建立方式）',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE category_type_code = 'facility' AND tenant_id = 1 AND deleted = false;

-- 9) 数据管理布局：关闭型号列，与 region Pattern C 一致（分类 → 实体）
UPDATE dm_entity_dimension
SET enabled = false, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'facility' AND dimension_kind = 'MODEL' AND deleted = false;

UPDATE dm_entity_dimension
SET enabled = false, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'facility' AND dimension_kind = 'ENTITY' AND deleted = false;

INSERT INTO dm_entity_dimension (entity_type_code, dimension_kind, enabled, tenant_id, creator, deleted)
SELECT 'facility', 'DETAIL', true, 1, 'seed', false
WHERE NOT EXISTS (
  SELECT 1 FROM dm_entity_dimension
  WHERE entity_type_code = 'facility' AND dimension_kind = 'DETAIL' AND deleted = false
);

UPDATE dm_entity_dimension
SET enabled = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'facility' AND dimension_kind = 'DETAIL' AND deleted = false;

SELECT setval(
  pg_get_serial_sequence('dynamicbusiness.ent_facility', 'id'),
  GREATEST((SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_facility), 43)
);
