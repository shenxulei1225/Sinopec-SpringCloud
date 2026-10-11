-- 巡检设备下把机场、无人机、相机、遥控分成同级四类，型号归到对应的一类。
-- 负责：分类树和型号与分类的对应。
-- 不负责：挂载弹窗的名单。机器人仍留在「巡检设备」。

SET search_path TO dynamicbusiness;

BEGIN;

UPDATE dynamic_category
SET deleted = true,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'EQCAT-L2-PATROL-UAV';

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code, deleted
)
SELECT p.id, v.name, v.code, 'equipment', v.sort, 1, p.tenant_id, 'seed', p.code, false
FROM dynamic_category p
CROSS JOIN (VALUES
  ('EQCAT-L2-PATROL-DOCK', '机场', 2),
  ('EQCAT-L3-PATROL-UAV-BODY', '无人机', 3),
  ('EQCAT-L3-PATROL-UAV-CAMERA', '相机', 4),
  ('EQCAT-L2-PATROL-RC', '遥控', 5)
) AS v(code, name, sort)
WHERE p.deleted = false AND p.code = 'EQCAT-L1-MISC'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  sort = EXCLUDED.sort,
  parent_code = EXCLUDED.parent_code,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_category c
SET tree_path = p.tree_path || c.id || '/',
    level = p.level + 1,
    parent_code = p.code
FROM dynamic_category p
WHERE c.deleted = false
  AND p.deleted = false
  AND c.parent_id = p.id
  AND p.code = 'EQCAT-L1-MISC'
  AND c.code IN (
    'EQCAT-L2-PATROL-DOCK',
    'EQCAT-L3-PATROL-UAV-BODY',
    'EQCAT-L3-PATROL-UAV-CAMERA',
    'EQCAT-L2-PATROL-RC'
  );

UPDATE dynamic_model_category_relation_t1 r
SET category_id = cat.id,
    category_code = cat.code,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
JOIN dynamic_category cat
  ON cat.deleted = false
 AND cat.tenant_id = m.tenant_id
 AND cat.code = CASE
      WHEN m.code LIKE 'dji-dock-%' THEN 'EQCAT-L2-PATROL-DOCK'
      WHEN m.code LIKE 'dji-m%' OR m.code IN ('donggu-uav-small', 'donggu-uav-large')
        THEN 'EQCAT-L3-PATROL-UAV-BODY'
      WHEN m.code LIKE 'dji-pl-%' OR m.code = 'hik-uav-cam'
        THEN 'EQCAT-L3-PATROL-UAV-CAMERA'
      WHEN m.code LIKE 'dji-rc-%' THEN 'EQCAT-L2-PATROL-RC'
    END
WHERE r.deleted = false
  AND m.deleted = false
  AND r.model_id = m.id
  AND r.tenant_id = m.tenant_id
  AND r.category_code IN ('EQCAT-L1-MISC', 'EQCAT-L2-PATROL-UAV', 'EQCAT-L3-PATROL-UAV-BODY', 'EQCAT-L3-PATROL-UAV-CAMERA')
  AND (
    m.code LIKE 'dji-dock-%'
    OR m.code LIKE 'dji-m%'
    OR m.code IN ('donggu-uav-small', 'donggu-uav-large')
    OR m.code LIKE 'dji-pl-%'
    OR m.code = 'hik-uav-cam'
    OR m.code LIKE 'dji-rc-%'
  );

COMMIT;
