-- ============================================================================
-- 系统 · region Pattern C（分类即实体）
-- 国家管网运维组织：集团 → 区域/省公司 → 作业区
-- 每个分类节点（除 region_root）同步创建 ent_region + dynamic_category_entity_link
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  NULL, '运营区域', 'region_root',
  'region', 0,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'region', '运营区域分类',
  '国家管网组织层级：集团 → 区域/省公司 → 作业区；Pattern C 分类即实体', 1,
  (SELECT c.id FROM dynamic_category c
   WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'region_root' LIMIT 1),
  1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- Pattern C 主数据：分类码 / 父分类 / 显示名 / 实体 id / 实体码 / 类型 / 模型码 / 排序
DROP TABLE IF EXISTS tmp_region_pattern_c;
CREATE TEMP TABLE tmp_region_pattern_c AS
SELECT *
FROM (VALUES
  ('REG-CAT-GROUP-001', 'region_root', '国家管网集团', 100001::bigint, 'REG-GROUP-001', 'group', 'MODEL-REGION-GROUP', 1),
  ('REG-CAT-PROV-NORTH', 'REG-CAT-GROUP-001', '北方管道有限责任公司', 100011, 'REG-PROV-NORTH', 'provincial', 'MODEL-REGION-PROVINCIAL', 10),
  ('REG-CAT-PROV-EAST', 'REG-CAT-GROUP-001', '东部原油储运有限公司', 100012, 'REG-PROV-EAST', 'provincial', 'MODEL-REGION-PROVINCIAL', 20),
  ('REG-CAT-PROV-WESTGAS', 'REG-CAT-GROUP-001', '西气东输分公司', 100013, 'REG-PROV-WESTGAS', 'provincial', 'MODEL-REGION-PROVINCIAL', 30),
  ('REG-CAT-PROV-WEST', 'REG-CAT-GROUP-001', '西部管道有限责任公司', 100014, 'REG-PROV-WEST', 'provincial', 'MODEL-REGION-PROVINCIAL', 40),
  ('REG-CAT-PROV-BJPIPE', 'REG-CAT-GROUP-001', '北京管道有限公司', 100015, 'REG-PROV-BJPIPE', 'provincial', 'MODEL-REGION-PROVINCIAL', 50),
  ('REG-CAT-PROV-SW', 'REG-CAT-GROUP-001', '西南管道有限责任公司', 100016, 'REG-PROV-SW', 'provincial', 'MODEL-REGION-PROVINCIAL', 60),
  ('REG-CAT-PROV-LNG', 'REG-CAT-GROUP-001', '液化天然气接收站管理分公司', 100017, 'REG-PROV-LNG', 'provincial', 'MODEL-REGION-PROVINCIAL', 70),
  ('REG-CAT-PROV-HN-S', 'REG-CAT-GROUP-001', '华南分公司', 100018, 'REG-PROV-HN-S', 'provincial', 'MODEL-REGION-PROVINCIAL', 80),
  ('REG-CAT-PROV-HZ', 'REG-CAT-GROUP-001', '华中分公司', 100019, 'REG-PROV-HZ', 'provincial', 'MODEL-REGION-PROVINCIAL', 90),
  ('REG-CAT-PROV-HD', 'REG-CAT-GROUP-001', '华东分公司', 100020, 'REG-PROV-HD', 'provincial', 'MODEL-REGION-PROVINCIAL', 100),
  ('REG-CAT-PROV-HB', 'REG-CAT-GROUP-001', '华北分公司', 100021, 'REG-PROV-HB', 'provincial', 'MODEL-REGION-PROVINCIAL', 110),
  ('REG-CAT-PROV-SD', 'REG-CAT-GROUP-001', '山东省公司', 100022, 'REG-PROV-SD', 'provincial', 'MODEL-REGION-PROVINCIAL', 120),
  ('REG-CAT-PROV-GD', 'REG-CAT-GROUP-001', '广东省管网有限公司', 100023, 'REG-PROV-GD', 'provincial', 'MODEL-REGION-PROVINCIAL', 130),
  ('REG-CAT-PROV-FJ', 'REG-CAT-GROUP-001', '福建省管网有限公司', 100024, 'REG-PROV-FJ', 'provincial', 'MODEL-REGION-PROVINCIAL', 140),
  ('REG-CAT-PROV-ZJ', 'REG-CAT-GROUP-001', '浙江省天然气管网有限公司', 100025, 'REG-PROV-ZJ', 'provincial', 'MODEL-REGION-PROVINCIAL', 150),
  ('REG-CAT-PROV-HN', 'REG-CAT-GROUP-001', '湖南分公司', 100026, 'REG-PROV-HN', 'provincial', 'MODEL-REGION-PROVINCIAL', 160),
  ('REG-CAT-PROV-XJ', 'REG-CAT-GROUP-001', '新疆煤制天然气外输管道有限责任公司', 100027, 'REG-PROV-XJ', 'provincial', 'MODEL-REGION-PROVINCIAL', 170),
  ('REG-CAT-OP-SD-DY', 'REG-CAT-PROV-SD', '东营作业区', 100101, 'REG-OP-SD-DY', 'operation', 'MODEL-REGION-OPERATION', 1),
  ('REG-CAT-OP-SD-DZ', 'REG-CAT-PROV-SD', '德州作业区', 100102, 'REG-OP-SD-DZ', 'operation', 'MODEL-REGION-OPERATION', 2),
  ('REG-CAT-OP-SD-ZZ', 'REG-CAT-PROV-SD', '枣庄作业区', 100103, 'REG-OP-SD-ZZ', 'operation', 'MODEL-REGION-OPERATION', 3),
  ('REG-CAT-OP-SD-TA', 'REG-CAT-PROV-SD', '泰安作业区', 100104, 'REG-OP-SD-TA', 'operation', 'MODEL-REGION-OPERATION', 4),
  ('REG-CAT-OP-NORTH-HH', 'REG-CAT-PROV-NORTH', '黑河作业区', 100105, 'REG-OP-NORTH-HH', 'operation', 'MODEL-REGION-OPERATION', 5),
  ('REG-CAT-OP-WGAS-GL', 'REG-CAT-PROV-WESTGAS', '高陵作业区', 100106, 'REG-OP-WGAS-GL', 'operation', 'MODEL-REGION-OPERATION', 6),
  ('REG-CAT-OP-WEST-LZ', 'REG-CAT-PROV-WEST', '兰州作业区', 100107, 'REG-OP-WEST-LZ', 'operation', 'MODEL-REGION-OPERATION', 7),
  ('REG-CAT-OP-BJ-AP', 'REG-CAT-PROV-BJPIPE', '安平作业区', 100108, 'REG-OP-BJ-AP', 'operation', 'MODEL-REGION-OPERATION', 8),
  ('REG-CAT-OP-BJ-YQ', 'REG-CAT-PROV-BJPIPE', '永清作业区', 100109, 'REG-OP-BJ-YQ', 'operation', 'MODEL-REGION-OPERATION', 9),
  ('REG-CAT-OP-SW-JJ', 'REG-CAT-PROV-SW', '江津作业区', 100110, 'REG-OP-SW-JJ', 'operation', 'MODEL-REGION-OPERATION', 10),
  ('REG-CAT-OP-SW-NJ', 'REG-CAT-PROV-SW', '内江作业区', 100111, 'REG-OP-SW-NJ', 'operation', 'MODEL-REGION-OPERATION', 11)
) AS v(
  category_code, parent_code, display_name, entity_id, entity_code,
  region_type, model_code, sort_order
);

-- 1) 分类树
INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
)
SELECT
  p.id, n.display_name, n.category_code, 'region', n.sort_order, 1, 1, 'seed', n.parent_code
FROM tmp_region_pattern_c n
JOIN dynamic_category p
  ON p.deleted = false AND p.tenant_id = 1 AND p.code = n.parent_code
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  sort = EXCLUDED.sort,
  parent_code = EXCLUDED.parent_code,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 2) 模型 ↔ 分类
INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'region', m.code, c.code, 1, 1, 'seed'
FROM tmp_region_pattern_c n
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = n.category_code
JOIN dynamic_model m
  ON m.deleted = false AND m.tenant_id = 1 AND m.code = n.model_code
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 3) ent_region 实体（与分类 1:1）
INSERT INTO ent_region (
  id, entity_type_code, model_id, name, code, region_code, region_name, region_type,
  tenant_id, creator, tree_path, sort, status, deleted, custom_fields
)
OVERRIDING SYSTEM VALUE
SELECT
  n.entity_id, 'region', m.id, n.display_name, n.entity_code, n.entity_code, n.display_name, n.region_type,
  1, 'seed', '/' || n.entity_id::text || '/', n.sort_order, 1, false, '{}'::text
FROM tmp_region_pattern_c n
JOIN dynamic_model m
  ON m.deleted = false AND m.code = n.model_code
ON CONFLICT (id) DO UPDATE SET
  model_id = EXCLUDED.model_id,
  name = EXCLUDED.name,
  code = EXCLUDED.code,
  region_code = EXCLUDED.region_code,
  region_name = EXCLUDED.region_name,
  region_type = EXCLUDED.region_type,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 4) Pattern C 绑定
UPDATE dynamic_category_entity_link l
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM dynamic_category c
WHERE l.category_id = c.id
  AND c.deleted = false AND c.tenant_id = 1
  AND c.category_type_code = 'region'
  AND l.deleted = false AND l.tenant_id = 1;

INSERT INTO dynamic_category_entity_link (category_id, entity_id, entity_model_id, tenant_id, creator)
SELECT c.id, n.entity_id, e.model_id, 1, 'seed'
FROM tmp_region_pattern_c n
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = n.category_code
JOIN ent_region e
  ON e.id = n.entity_id AND e.deleted = false;

-- 清理废弃数据
UPDATE dynamic_category
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1
  AND code IN ('REG-CAT-BR-DY', 'REG-CAT-OP-DY', 'REG-CAT-PROV-SD-LEGACY');

UPDATE dynamic_model_category_relation r
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE r.deleted = false AND r.tenant_id = 1
  AND (r.model_code = 'MODEL-REGION-BRANCH' OR r.category_code IN ('REG-CAT-BR-DY', 'REG-CAT-OP-DY'));

UPDATE ent_region
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND id IN (100002, 100003) AND deleted = false;

SELECT setval(
  pg_get_serial_sequence('dynamicbusiness.ent_region', 'id'),
  GREATEST((SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_region), 100111)
);

-- Pattern C 自检：除 region_root 外每个分类必须有实体绑定
DO $$
DECLARE missing_count integer;
BEGIN
  SELECT COUNT(*) INTO missing_count
  FROM dynamic_category c
  WHERE c.deleted = false
    AND c.tenant_id = 1
    AND c.category_type_code = 'region'
    AND c.code <> 'region_root'
    AND NOT EXISTS (
      SELECT 1
      FROM dynamic_category_entity_link l
      JOIN ent_region e ON e.id = l.entity_id AND e.deleted = false
      WHERE l.category_id = c.id AND l.deleted = false
    );
  IF missing_count > 0 THEN
    RAISE EXCEPTION 'region Pattern C 未完成：% 个分类节点缺少 ent_region 绑定', missing_count;
  END IF;
END $$;
