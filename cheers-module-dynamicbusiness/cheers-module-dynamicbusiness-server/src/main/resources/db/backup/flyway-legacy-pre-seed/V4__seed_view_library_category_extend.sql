-- 扩展视图库预置分类：仪表盘、监控、统计等常用业务分组
-- 并修正已有节点的名称/说明（与 viewType 布局类型无关，仅为资源库分组）
-- 幂等：按 code 跳过已存在节点

-- ── 修正 V3 已有节点文案 ───────────────────────────────────────────────────
UPDATE dynamicbusiness.dynamic_category
SET name = '其他',
    description = '暂未归入具体业务场景的视图；非布局类型 viewType，仅为资源库兜底分组',
    sort = 990,
    updater = 'system',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'view_general'
  AND category_type_code = 'view'
  AND deleted = false
  AND tenant_id = 1;

UPDATE dynamicbusiness.dynamic_category
SET description = '业务看板、任务看板、运营概览（卡片/列表组合页）',
    sort = 50,
    updater = 'system',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'view_dashboard'
  AND category_type_code = 'view'
  AND deleted = false
  AND tenant_id = 1;

UPDATE dynamicbusiness.dynamic_category
SET description = 'CRUD、主数据维护、台账类（含树表、列表等组合）',
    sort = 10,
    updater = 'system',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'view_data_management'
  AND category_type_code = 'view'
  AND deleted = false
  AND tenant_id = 1;

UPDATE dynamicbusiness.dynamic_category
SET description = '导航 hub、模块入口、信息聚合页',
    sort = 100,
    updater = 'system',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'view_portal'
  AND category_type_code = 'view'
  AND deleted = false
  AND tenant_id = 1;

-- ── 新增常用子分类 ─────────────────────────────────────────────────────────
INSERT INTO dynamicbusiness.dynamic_category (
    id, parent_id, name, code, category_type_code, tree_path, level, sort, status,
    description, creator, create_time, updater, update_time, deleted, tenant_id
)
SELECT
    v.id,
    root.id,
    v.name,
    v.code,
    'view',
    CONCAT('/', root.id, '/', v.id, '/'),
    2,
    v.sort,
    1,
    v.description,
    'system',
    CURRENT_TIMESTAMP,
    'system',
    CURRENT_TIMESTAMP,
    false,
    1
FROM (
    VALUES
        (4908, 'view_tree_table',      '树表',   20, '左树右表、分类导航 + 明细列表'),
        (4909, 'view_list',            '列表',   30, '单列表、台账、查询结果页'),
        (4910, 'view_form_detail',     '表单详情', 40, '单条录入、编辑、详情展示页'),
        (4911, 'view_instrument',      '仪表盘', 60, 'KPI 指标、图表卡片、管理驾驶舱'),
        (4912, 'view_monitoring',      '监控',   70, '实时状态、告警、运行监视大屏'),
        (4913, 'view_statistics',      '统计',   80, '报表、分析、汇总统计页'),
        (4914, 'view_workbench',       '工作台', 90, '多区协作、场景工作台、任务处理页')
) AS v(id, code, name, sort, description)
CROSS JOIN (
    SELECT id
    FROM dynamicbusiness.dynamic_category
    WHERE code = 'view_root'
      AND category_type_code = 'view'
      AND deleted = false
      AND tenant_id = 1
    LIMIT 1
) AS root
WHERE root.id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_category existing
      WHERE existing.code = v.code
        AND existing.deleted = false
        AND existing.tenant_id = 1
  );

SELECT pg_catalog.setval(
    'dynamicbusiness.dynamic_category_id_seq',
    GREATEST(
        COALESCE((SELECT MAX(id) FROM dynamicbusiness.dynamic_category), 1),
        4914
    ),
    true
);
