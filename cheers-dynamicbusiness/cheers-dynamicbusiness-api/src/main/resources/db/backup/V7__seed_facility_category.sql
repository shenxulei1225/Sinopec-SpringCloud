-- =====================================================
-- V8：分类全量种子
-- - system_category 根节点 id 从 1 起连续（1..20 对应各 category_type）
-- - system_category_type.top_level_category_id = 对应根 id
-- - tree_path = category id 路径，如 /1/、/1/21/
--
-- id 规划：
--   1..20   各类型根（facility=1, equipment=2, … schedule_policy=20）
--   21..27  设施业务子类（parent=1；台账旧 id 1..7 映射见设施库 V9）
--
-- 重灌：DELETE FROM system_category; 再 migrate / 手工执行。
-- =====================================================

-- ---------- 1. 分类类型 ----------
INSERT INTO system_category_type (
    id, category_type_code, name, description, status, top_level_category_id,
    creator, create_time, updater, update_time, deleted, tenant_id
)
VALUES
    (1,  'facility',        '设施分类',     '设施台账、巡检对象',       1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (2,  'equipment',       '设备分类',     '设备类型、设备台账',       1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (3,  'region',          '区域分类',     '站场分区、区域树',         1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (4,  'inspection_item', '检查项分类',   '检查项库',                 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (5,  'task',            '任务分类',     '巡检/作业任务',            1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (6,  'site',            '站场分类',     '站场组织、站场类型树',     1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (7,  'alarm',           '告警分类',     '告警类型、等级分组',       1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (8,  'asset',           '资产分类',     '固定资产、备件',           1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (9,  'model',           '模型分类',     '数字孪生/三维模型',        1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (10, 'component',       '组件分类',     '页面组件、UI 组件库',      1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (11, 'view',            '视图分类',     '视图库、工作台视图',       1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (12, 'zone',            '分区分类',     '罐区、管线分区',           1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (13, 'bpm',             '流程分类',     'BPM 流程模板',             1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (14, 'organization',    '组织分类',     '部门、班组',               1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (15, 'document',        '文档分类',     '规程、图纸、附件',         1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (16, 'material',        '物料分类',     '物料、耗材',               1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (17, 'supplier',        '供应商分类',   '供应商名录',               1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (18, 'risk',            '风险分类',     '风险辨识、管控措施',       1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (19, 'work_order',      '工单分类',     '维保、抢修工单',           1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (20, 'schedule_policy', '排期策略分类', '排期模板、策略分组',       1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1)
ON CONFLICT (category_type_code) WHERE deleted = FALSE DO UPDATE SET
    name        = EXCLUDED.name,
    description = EXCLUDED.description,
    status      = EXCLUDED.status,
    updater     = 'seed',
    update_time = CURRENT_TIMESTAMP,
    deleted     = FALSE;

-- ---------- 2. 根分类 id=1..20（tree_path 仅 id）----------
INSERT INTO system_category (
    id, parent_id, name, code, category_type_code, tree_path, level, sort, status, description,
    creator, create_time, updater, update_time, deleted, tenant_id
)
VALUES
    (1,  NULL, '设施分类',     'ROOT-facility',         'facility',         '/1/',  1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (2,  NULL, '设备分类',     'ROOT-equipment',        'equipment',        '/2/',  1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (3,  NULL, '区域分类',     'ROOT-region',           'region',           '/3/',  1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (4,  NULL, '检查项分类',   'ROOT-inspection_item',  'inspection_item',  '/4/',  1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (5,  NULL, '任务分类',     'ROOT-task',             'task',             '/5/',  1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (6,  NULL, '站场分类',     'ROOT-site',             'site',             '/6/',  1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (7,  NULL, '告警分类',     'ROOT-alarm',            'alarm',            '/7/',  1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (8,  NULL, '资产分类',     'ROOT-asset',            'asset',            '/8/',  1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (9,  NULL, '模型分类',     'ROOT-model',            'model',            '/9/',  1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (10, NULL, '组件分类',     'ROOT-component',        'component',        '/10/', 1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (11, NULL, '视图分类',     'ROOT-view',             'view',             '/11/', 1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (12, NULL, '分区分类',     'ROOT-zone',             'zone',             '/12/', 1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (13, NULL, '流程分类',     'ROOT-bpm',              'bpm',              '/13/', 1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (14, NULL, '组织分类',     'ROOT-organization',     'organization',     '/14/', 1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (15, NULL, '文档分类',     'ROOT-document',         'document',         '/15/', 1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (16, NULL, '物料分类',     'ROOT-material',         'material',         '/16/', 1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (17, NULL, '供应商分类',   'ROOT-supplier',         'supplier',         '/17/', 1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (18, NULL, '风险分类',     'ROOT-risk',             'risk',             '/18/', 1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (19, NULL, '工单分类',     'ROOT-work_order',       'work_order',       '/19/', 1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (20, NULL, '排期策略分类', 'ROOT-schedule_policy',  'schedule_policy',  '/20/', 1, 0, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1)
ON CONFLICT (code) WHERE deleted = FALSE DO UPDATE SET
    id                 = EXCLUDED.id,
    parent_id          = EXCLUDED.parent_id,
    name               = EXCLUDED.name,
    category_type_code = EXCLUDED.category_type_code,
    tree_path          = EXCLUDED.tree_path,
    level              = EXCLUDED.level,
    sort               = EXCLUDED.sort,
    status             = EXCLUDED.status,
    updater            = 'seed',
    update_time        = CURRENT_TIMESTAMP,
    deleted            = FALSE;

UPDATE system_category_type SET top_level_category_id = 1,  updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'facility'         AND tenant_id = 1 AND deleted = FALSE;
UPDATE system_category_type SET top_level_category_id = 2,  updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'equipment'        AND tenant_id = 1 AND deleted = FALSE;
UPDATE system_category_type SET top_level_category_id = 3,  updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'region'           AND tenant_id = 1 AND deleted = FALSE;
UPDATE system_category_type SET top_level_category_id = 4,  updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'inspection_item'  AND tenant_id = 1 AND deleted = FALSE;
UPDATE system_category_type SET top_level_category_id = 5,  updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'task'             AND tenant_id = 1 AND deleted = FALSE;
UPDATE system_category_type SET top_level_category_id = 6,  updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'site'             AND tenant_id = 1 AND deleted = FALSE;
UPDATE system_category_type SET top_level_category_id = 7,  updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'alarm'            AND tenant_id = 1 AND deleted = FALSE;
UPDATE system_category_type SET top_level_category_id = 8,  updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'asset'            AND tenant_id = 1 AND deleted = FALSE;
UPDATE system_category_type SET top_level_category_id = 9,  updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'model'            AND tenant_id = 1 AND deleted = FALSE;
UPDATE system_category_type SET top_level_category_id = 10, updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'component'        AND tenant_id = 1 AND deleted = FALSE;
UPDATE system_category_type SET top_level_category_id = 11, updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'view'             AND tenant_id = 1 AND deleted = FALSE;
UPDATE system_category_type SET top_level_category_id = 12, updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'zone'             AND tenant_id = 1 AND deleted = FALSE;
UPDATE system_category_type SET top_level_category_id = 13, updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'bpm'              AND tenant_id = 1 AND deleted = FALSE;
UPDATE system_category_type SET top_level_category_id = 14, updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'organization'     AND tenant_id = 1 AND deleted = FALSE;
UPDATE system_category_type SET top_level_category_id = 15, updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'document'         AND tenant_id = 1 AND deleted = FALSE;
UPDATE system_category_type SET top_level_category_id = 16, updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'material'         AND tenant_id = 1 AND deleted = FALSE;
UPDATE system_category_type SET top_level_category_id = 17, updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'supplier'         AND tenant_id = 1 AND deleted = FALSE;
UPDATE system_category_type SET top_level_category_id = 18, updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'risk'             AND tenant_id = 1 AND deleted = FALSE;
UPDATE system_category_type SET top_level_category_id = 19, updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'work_order'       AND tenant_id = 1 AND deleted = FALSE;
UPDATE system_category_type SET top_level_category_id = 20, updater = 'seed', update_time = CURRENT_TIMESTAMP WHERE category_type_code = 'schedule_policy'  AND tenant_id = 1 AND deleted = FALSE;

-- ---------- 3. 设施子类 id=21..27（parent=1，与 V2/V3 台账旧 category_id 1..7 对应，跑设施 V9 后对齐）----------
INSERT INTO system_category (
    id, parent_id, name, code, category_type_code, tree_path, level, sort, status, description,
    creator, create_time, updater, update_time, deleted, tenant_id
)
VALUES
    (21, 1, '物理压力表', 'facility_physical_pressure', 'facility', '/1/21/', 2, 1, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (22, 1, '阀门',       'facility_valve',             'facility', '/1/22/', 2, 2, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (23, 1, '罐顶',       'facility_tank_top',          'facility', '/1/23/', 2, 3, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (24, 1, '仪表',       'facility_meter',             'facility', '/1/24/', 2, 4, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (25, 1, '管道',       'facility_pipeline',          'facility', '/1/25/', 2, 5, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (26, 1, '罐体',       'facility_tank',              'facility', '/1/26/', 2, 6, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1),
    (27, 1, '连接处',     'facility_joint',             'facility', '/1/27/', 2, 7, 1, NULL, 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, FALSE, 1)
ON CONFLICT (code) WHERE deleted = FALSE DO UPDATE SET
    id                 = EXCLUDED.id,
    parent_id          = EXCLUDED.parent_id,
    name               = EXCLUDED.name,
    category_type_code = EXCLUDED.category_type_code,
    tree_path          = EXCLUDED.tree_path,
    level              = EXCLUDED.level,
    sort               = EXCLUDED.sort,
    status             = EXCLUDED.status,
    updater            = 'seed',
    update_time        = CURRENT_TIMESTAMP,
    deleted            = FALSE;

SELECT setval(
    'system_category_seq',
    GREATEST(COALESCE((SELECT MAX(id) FROM system_category), 0), 1000)
);