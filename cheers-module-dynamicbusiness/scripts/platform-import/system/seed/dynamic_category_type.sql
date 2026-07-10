-- ============================================================================
-- 系统 · dynamic_category_type
-- Generated from zhgl.system_category_type (equipment)
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_category_type: equipment (from zhgl system_category_type id=9)

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'equipment', '设备分类',
  '设备管理模型分组（categoryTypeCode=equipment，来源 zhgl 管廊）', 1,
  (SELECT c.id FROM dynamic_category c
   WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'equipment_root' LIMIT 1),
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

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'task', '任务分类',
  '任务定义分类（categoryTypeCode=task，供任务创建与数据管理）', 1,
  (SELECT c.id FROM dynamic_category c
   WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'task_root' LIMIT 1),
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
