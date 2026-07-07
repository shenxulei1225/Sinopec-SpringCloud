-- ============================================================================
-- 管廊 · dynamic_category
-- Generated: 2026-07-08 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 幂等 upsert；关联字段按 code 解析 id，不写 surrogate id
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_category: 12 row(s), upsert by code; parent by parent_code

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '视图分类', 'view_root',
  'view', 0,
  1, '视图库分类根节点', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'view_root' LIMIT 1), '看板', 'view_dashboard',
  'view', 50,
  1, '业务看板、任务看板、运营概览（卡片/列表组合页）', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'view_root' LIMIT 1), '数据管理', 'view_data_management',
  'view', 10,
  1, 'CRUD、主数据维护、台账类（含树表、列表等组合）', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'view_root' LIMIT 1), '表单详情', 'view_form_detail',
  'view', 40,
  1, '单条录入、编辑、详情展示页', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'view_root' LIMIT 1), '其他', 'view_general',
  'view', 990,
  1, '暂未归入具体业务场景的视图；非布局类型 viewType，仅为资源库兜底分组', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'view_root' LIMIT 1), '仪表盘', 'view_instrument',
  'view', 60,
  1, 'KPI 指标、图表卡片、管理驾驶舱', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'view_root' LIMIT 1), '列表', 'view_list',
  'view', 30,
  1, '单列表、台账、查询结果页', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'view_root' LIMIT 1), '监控', 'view_monitoring',
  'view', 70,
  1, '实时状态、告警、运行监视大屏', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'view_root' LIMIT 1), '门户', 'view_portal',
  'view', 100,
  1, '导航 hub、模块入口、信息聚合页', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'view_root' LIMIT 1), '统计', 'view_statistics',
  'view', 80,
  1, '报表、分析、汇总统计页', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'view_root' LIMIT 1), '树表', 'view_tree_table',
  'view', 20,
  1, '左树右表、分类导航 + 明细列表', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'view_root' LIMIT 1), '工作台', 'view_workbench',
  'view', 90,
  1, '多区协作、场景工作台、任务处理页', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;


-- rebuild tree_path / level after category upsert (id-agnostic)
WITH RECURSIVE cat_tree AS (
  SELECT c.id, c.code, c.parent_id, ARRAY[c.id] AS path_ids, 0 AS lvl
  FROM dynamic_category c
  WHERE c.deleted = false AND c.tenant_id = 1
    AND (c.parent_id IS NULL OR c.parent_id = 0
         OR NOT EXISTS (
           SELECT 1 FROM dynamic_category p
           WHERE p.id = c.parent_id AND p.deleted = false AND p.tenant_id = 1
         ))
  UNION ALL
  SELECT c.id, c.code, c.parent_id, ct.path_ids || c.id, ct.lvl + 1
  FROM dynamic_category c
  JOIN cat_tree ct ON c.parent_id = ct.id
  WHERE c.deleted = false AND c.tenant_id = 1
)
UPDATE dynamic_category c
SET
  tree_path = '/' || array_to_string(ct.path_ids, '/') || '/',
  level = ct.lvl + 1,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM cat_tree ct
WHERE c.id = ct.id;
