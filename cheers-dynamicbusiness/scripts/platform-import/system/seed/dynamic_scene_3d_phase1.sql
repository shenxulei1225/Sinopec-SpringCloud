-- ============================================================================
-- 三维场景 / 三维摆放 · 阶段 1 元数据 + 洛阳圣瑞场景（分类即实体）
-- 设计：docs/superpowers/specs/2026-07-22-dynamic-business-scene-3d-design.md
-- 前置：Flyway V17（ent_scene / ent_scene_placement）
-- 幂等。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- ---------------------------------------------------------------------------
-- 1. 数据类型
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_entity_type (
  code, name, parent_id, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  physical_column_mapping, tenant_id, creator, entry_kind, group_name
) VALUES
(
  'scene', '三维场景', NULL,
  '站场三维世界；分类即实体：树上选场景，属性在实体（站场/原点/发布）',
  'ep:office-building', '场景', 30, 'active', 'USER', '{}',
  'DEDICATED', 'ent_scene', false, NULL, 1, 'seed', 'NATIVE', '三维'
),
(
  'scene_placement', '三维摆放', NULL,
  '场景内摆放实体：位姿/材质等；挂场景分类；构筑物与设备用不同 model',
  'ep:box', '摆放', 31, 'active', 'USER', '{}',
  'DEDICATED', 'ent_scene_placement', false, NULL, 1, 'seed', 'NATIVE', '三维'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  storage_type = 'DEDICATED',
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  entry_kind = 'NATIVE',
  group_name = '三维',
  status = 'active',
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, enable_rule_engine,
  description, status, tenant_id, creator
) VALUES
  ('scene', '三维场景', 'DEDICATED', 'ent_scene', false, '三维场景存储', 1, 1, 'seed'),
  ('scene_placement', '三维摆放', 'DEDICATED', 'ent_scene_placement', false, '三维摆放存储', 1, 1, 'seed')
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  storage_type = 'DEDICATED',
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 2. 模型（场景壳 + 构筑物/设备外形）
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES
  (
    'MODEL-SCENE-WORLD', '站场场景', 'scene',
    '三维场景分类即实体所用模型', 1, 1, 1, 'seed'
  ),
  (
    'MODEL-SCENE-STRUCTURE-TANK', '储罐（构筑物）', 'scene_placement',
    '构筑物：储罐外形摆放', 1, 10, 1, 'seed'
  ),
  (
    'MODEL-SCENE-STRUCTURE-BUILDING', '建筑（构筑物）', 'scene_placement',
    '构筑物：厂房/办公楼等外形摆放', 1, 11, 1, 'seed'
  ),
  (
    'MODEL-SCENE-EQUIPMENT-MESH', '设备外观', 'scene_placement',
    '设备类三维外形摆放（非业务设备台账）', 1, 20, 1, 'seed'
  )
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 3. 场景分类种类 + 根 + 洛阳圣瑞（分类即实体）
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  NULL, '三维场景', 'scene_root',
  'scene', 0, 1, 1, 'seed', NULL
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
  'scene', '三维场景分类',
  '分类即实体：节点对应 ent_scene（站场/原点/发布）', 1,
  (SELECT c.id FROM dynamic_category c
   WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'scene_root' LIMIT 1),
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

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
)
SELECT
  r.id, '洛阳圣瑞场景', 'SCENE-CAT-LUOYANG-SHENGRUI', 'scene', 1, 1, 1, 'seed', 'scene_root'
FROM dynamic_category r
WHERE r.deleted = false AND r.tenant_id = 1 AND r.code = 'scene_root'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  sort = EXCLUDED.sort,
  parent_code = EXCLUDED.parent_code,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 场景实体（对账 SCENE-LUOYANG-SHENGRUI / facility 45）
INSERT INTO ent_scene (
  tenant_id, entity_type_code, model_id, name, code, status,
  fld_base_scene_ref_facility, scene_code,
  origin_lng, origin_lat, origin_height, origin_height_source, publish_status,
  creator, updater, deleted, sort
)
SELECT
  1, 'scene', m.id, '洛阳圣瑞场景', 'SCENE-LUOYANG-SHENGRUI', 1,
  45, 'SCENE-LUOYANG-SHENGRUI',
  112.453900, 34.619700, NULL, NULL, 'PUBLISHED',
  'seed', 'seed', false, 1
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-SCENE-WORLD'
  AND NOT EXISTS (
    SELECT 1 FROM ent_scene e
    WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = 'SCENE-LUOYANG-SHENGRUI'
  );

UPDATE ent_scene e
SET
  name = '洛阳圣瑞场景',
  model_id = m.id,
  fld_base_scene_ref_facility = 45,
  scene_code = 'SCENE-LUOYANG-SHENGRUI',
  origin_lng = 112.453900,
  origin_lat = 34.619700,
  publish_status = 'PUBLISHED',
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = 'SCENE-LUOYANG-SHENGRUI'
  AND m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-SCENE-WORLD';

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'scene', m.code, c.code, 1, 1, 'seed'
FROM dynamic_category c
JOIN dynamic_model m
  ON m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-SCENE-WORLD'
WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'SCENE-CAT-LUOYANG-SHENGRUI'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_entity_link (
  category_id, entity_id, entity_model_id, tenant_id, creator
)
SELECT c.id, e.id, e.model_id, 1, 'seed'
FROM dynamic_category c
JOIN ent_scene e
  ON e.deleted = false AND e.tenant_id = 1 AND e.code = 'SCENE-LUOYANG-SHENGRUI'
WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'SCENE-CAT-LUOYANG-SHENGRUI'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_category_entity_link l
    WHERE l.deleted = false AND l.tenant_id = 1
      AND l.category_id = c.id AND l.entity_id = e.id
  );

-- 自检
DO $$
DECLARE
  scene_cnt int;
  cat_cnt int;
BEGIN
  SELECT COUNT(*) INTO scene_cnt FROM ent_scene
  WHERE deleted = false AND code = 'SCENE-LUOYANG-SHENGRUI';
  SELECT COUNT(*) INTO cat_cnt FROM dynamic_category
  WHERE deleted = false AND code = 'SCENE-CAT-LUOYANG-SHENGRUI';
  IF scene_cnt < 1 OR cat_cnt < 1 THEN
    RAISE EXCEPTION 'dynamic_scene_3d_phase1 self-check failed: scene=% cat=%', scene_cnt, cat_cnt;
  END IF;
END $$;
