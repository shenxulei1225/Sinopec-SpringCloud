-- ============================================================================
-- 系统 · 巡检管理分类树
-- - patrol_point：按运营区域（省公司 → 作业区）分类
-- - patrol_schedule：按排期模式分类
-- - patrol_object：按巡检场景分类
-- ============================================================================

SET search_path TO dynamicbusiness;

-- ---------- patrol_point 按区域分类 ----------
INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  NULL, '巡检点区域', 'patrol_point_root',
  'patrol_point', 0,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

DROP TABLE IF EXISTS tmp_patrol_point_region_cat;
CREATE TEMP TABLE tmp_patrol_point_region_cat AS
SELECT *
FROM (VALUES
  ('PP-CAT-PROV-SD', 'patrol_point_root', '山东省公司', 10),
  ('PP-CAT-PROV-NORTH', 'patrol_point_root', '北方管道有限责任公司', 20),
  ('PP-CAT-PROV-EAST', 'patrol_point_root', '东部原油储运有限公司', 30),
  ('PP-CAT-OP-SD-DY', 'PP-CAT-PROV-SD', '东营作业区', 1),
  ('PP-CAT-OP-SD-DZ', 'PP-CAT-PROV-SD', '德州作业区', 2),
  ('PP-CAT-OP-SD-ZZ', 'PP-CAT-PROV-SD', '枣庄作业区', 3),
  ('PP-CAT-OP-SD-TA', 'PP-CAT-PROV-SD', '泰安作业区', 4),
  ('PP-CAT-OP-NORTH-HH', 'PP-CAT-PROV-NORTH', '黑河作业区', 5),
  ('PP-CAT-OP-EAST-LY', 'PP-CAT-PROV-EAST', '洛阳作业区', 1),
  ('PP-CAT-OP-EAST-JQ', 'PP-CAT-PROV-EAST', '金桥作业区', 2)
) AS v(category_code, parent_code, display_name, sort_order);

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
)
SELECT
  p.id, n.display_name, n.category_code, 'patrol_point', n.sort_order, 1, 1, 'seed', n.parent_code
FROM tmp_patrol_point_region_cat n
JOIN dynamic_category p
  ON p.deleted = false AND p.tenant_id = 1 AND p.code = n.parent_code
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  sort = EXCLUDED.sort,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'patrol_point', m.code, n.category_code, 1, 1, 'seed'
FROM tmp_patrol_point_region_cat n
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = n.category_code
JOIN dynamic_model m
  ON m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_point'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------- patrol_schedule 分类 ----------
INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  NULL, '排期分类', 'patrol_schedule_root',
  'patrol_schedule', 0,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
)
SELECT p.id, v.name, v.code, 'patrol_schedule', v.sort, 1, 1, 'seed', 'patrol_schedule_root'
FROM dynamic_category p
CROSS JOIN (VALUES
  ('psch_cat_cron', '周期排期', 1),
  ('psch_cat_once', '单次排期', 2)
) AS v(code, name, sort)
WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'patrol_schedule_root'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  sort = EXCLUDED.sort,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'patrol_schedule', m.code, c.code, 1, 1, 'seed'
FROM dynamic_category c
JOIN dynamic_model m
  ON m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_schedule'
WHERE c.deleted = false AND c.tenant_id = 1
  AND c.category_type_code = 'patrol_schedule'
  AND c.code <> 'patrol_schedule_root'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------- patrol_object 分类 ----------
INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  NULL, '巡检对象分类', 'patrol_object_root',
  'patrol_object', 0,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
)
SELECT p.id, v.name, v.code, 'patrol_object', v.sort, 1, 1, 'seed', 'patrol_object_root'
FROM dynamic_category p
CROSS JOIN (VALUES
  ('pobj_cat_tank', '储罐区', 1),
  ('pobj_cat_pump', '泵房管廊', 2),
  ('pobj_cat_fire', '消防设施', 3),
  ('pobj_cat_valve', '阀门专项', 4)
) AS v(code, name, sort)
WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'patrol_object_root'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  sort = EXCLUDED.sort,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'patrol_object', m.code, c.code, 1, 1, 'seed'
FROM dynamic_category c
JOIN dynamic_model m
  ON m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_object'
WHERE c.deleted = false AND c.tenant_id = 1
  AND c.category_type_code = 'patrol_object'
  AND c.code <> 'patrol_object_root'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'patrol_point', '巡检点区域分类',
  '按运营区域（省公司→作业区）组织巡检点', 1,
  (SELECT c.id FROM dynamic_category c
   WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'patrol_point_root' LIMIT 1),
  1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'patrol_schedule', '排期分类',
  '按排期模式（周期/单次）组织排期模板', 1,
  (SELECT c.id FROM dynamic_category c
   WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'patrol_schedule_root' LIMIT 1),
  1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'patrol_object', '巡检对象分类',
  '按巡检场景组织巡检对象模板', 1,
  (SELECT c.id FROM dynamic_category c
   WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'patrol_object_root' LIMIT 1),
  1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
