-- ============================================================================
-- 系统 · scene_asset 纯分类（三维模型类型，供 scene-platform.asset_resource.asset_type 引用节点 code）
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 避免序列落后导致 pkey 冲突
SELECT setval(
  'dynamicbusiness.dynamic_category_id_seq',
  (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.dynamic_category)
);

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
)
SELECT
  NULL, '场景模型类型', 'scene_asset_root',
  'scene_asset', 0,
  1, 1, 'seed', NULL
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_category c
  WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'scene_asset_root'
);

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
)
SELECT p.id, v.name, v.code, 'scene_asset', v.sort, 1, 1, 'seed', 'scene_asset_root'
FROM dynamic_category p
CROSS JOIN (VALUES
  ('建筑', 'BUILDING', 1),
  ('储罐', 'TANK', 2),
  ('围墙', 'WALL', 3),
  ('高防火墙', 'WALL_HIGH', 4),
  ('矮防火墙', 'WALL_LOW', 5),
  ('道路', 'ROAD', 6),
  ('设备外观', 'EQUIPMENT_MESH', 7)
) AS v(name, code, sort)
WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'scene_asset_root'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_category c
    WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = v.code
  );

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'scene_asset', '场景模型类型',
  '三维模型管理纯分类（categoryTypeCode=scene_asset）；asset_resource.asset_type 存叶子节点 code', 1,
  (SELECT c.id FROM dynamic_category c
   WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'scene_asset_root' LIMIT 1),
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
