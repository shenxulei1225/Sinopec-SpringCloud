-- ============================================================================
-- 系统 · task 分类树（categoryTypeCode=task，供任务创建页分类下拉）
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  NULL, '任务分类', 'task_root',
  'task', 0,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
)
SELECT
  p.id, '日常巡检', 'task_cat_daily',
  'task', 1,
  1, 1, 'seed', 'task_root'
FROM dynamic_category p
WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'task_root'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
)
SELECT
  p.id, '专项巡检', 'task_cat_special',
  'task', 2,
  1, 1, 'seed', 'task_root'
FROM dynamic_category p
WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'task_root'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
