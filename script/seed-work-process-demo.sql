-- 作业流程平台演示数据（可重复执行）
-- 覆盖：已发布现场标准、维护手册、绑定规则、日历条目、故障单、可执行工单（含步骤）
-- 租户：1

BEGIN;

-- ========== maintenance ==========
SET search_path TO maintenance;

-- 清理同编码演示数据（保留其他业务数据）
DELETE FROM mm_corrective_case WHERE case_no LIKE 'CASE-DEMO-%' AND tenant_id = 1;
DELETE FROM mm_calendar_entry WHERE handbook_id IN (
    SELECT id FROM mm_handbook WHERE code = 'HB-PUMP-MONTHLY' AND tenant_id = 1
) AND tenant_id = 1;
DELETE FROM mm_binding_rule WHERE code = 'BR-PUMP-MONTHLY' AND tenant_id = 1;
DELETE FROM mm_handbook WHERE code = 'HB-PUMP-MONTHLY' AND tenant_id = 1;
DELETE FROM mm_field_work_standard WHERE code = 'STD-PUMP-MONTHLY' AND tenant_id = 1;

-- 现场作业标准（已发布）
INSERT INTO mm_field_work_standard (
    id, code, name, version_no, scope, steps_json, status, creator, updater, tenant_id
) VALUES (
    nextval('mm_field_work_standard_seq'),
    'STD-PUMP-MONTHLY',
    '离心泵月度点检标准',
    1,
    'maintenance',
    $json$[
      {"code":"STEP-VISUAL","title":"外观与泄漏检查","required":true,"controlType":"checkbox"},
      {"code":"STEP-VIBE","title":"振动与异响确认","required":true,"controlType":"checkbox"},
      {"code":"STEP-LUBE","title":"润滑与油位检查","required":true,"controlType":"checkbox"},
      {"code":"STEP-PHOTO","title":"现场拍照（可选）","required":false,"controlType":"checkbox"}
    ]$json$::jsonb,
    1,
    'demo-seed',
    'demo-seed',
    1
);

-- 维护手册（已发布）
INSERT INTO mm_handbook (
    id, code, name, scope, asset_type_code, frequency_code, field_standard_id,
    crew_hint, material_hint, version_no, status, creator, updater, tenant_id
) VALUES (
    nextval('mm_handbook_seq'),
    'HB-PUMP-MONTHLY',
    '离心泵月度维护手册',
    'maintenance',
    'pump',
    'MONTHLY',
    (SELECT id FROM mm_field_work_standard WHERE code = 'STD-PUMP-MONTHLY' AND tenant_id = 1 AND deleted = FALSE ORDER BY id DESC LIMIT 1),
    '2 人作业组',
    '听诊棒、润滑油、手电筒',
    1,
    1,
    'demo-seed',
    'demo-seed',
    1
);

-- 绑定规则（已发布：资产类型+频率）
INSERT INTO mm_binding_rule (
    id, code, name, scope, asset_id, asset_type_code, frequency_code,
    handbook_id, field_standard_id, orchestration_template_code, priority,
    status, creator, updater, tenant_id
) VALUES (
    nextval('mm_binding_rule_seq'),
    'BR-PUMP-MONTHLY',
    '泵类月度绑定',
    'maintenance',
    NULL,
    'pump',
    'MONTHLY',
    (SELECT id FROM mm_handbook WHERE code = 'HB-PUMP-MONTHLY' AND tenant_id = 1 AND deleted = FALSE ORDER BY id DESC LIMIT 1),
    (SELECT id FROM mm_field_work_standard WHERE code = 'STD-PUMP-MONTHLY' AND tenant_id = 1 AND deleted = FALSE ORDER BY id DESC LIMIT 1),
    NULL,
    10,
    1,
    'demo-seed',
    'demo-seed',
    1
);

-- 预防性日历（今日可触发）
INSERT INTO mm_calendar_entry (
    id, handbook_id, asset_id, scope, planned_date, status,
    runtime_job_id, work_order_ids, last_error, creator, updater, tenant_id
) VALUES (
    nextval('mm_calendar_entry_seq'),
    (SELECT id FROM mm_handbook WHERE code = 'HB-PUMP-MONTHLY' AND tenant_id = 1 AND deleted = FALSE ORDER BY id DESC LIMIT 1),
    1001,
    'maintenance',
    CURRENT_DATE,
    'PLANNED',
    NULL,
    NULL,
    NULL,
    'demo-seed',
    'demo-seed',
    1
);

-- 故障主单（已批准，待派工）
INSERT INTO mm_corrective_case (
    id, case_no, title, description, asset_id, asset_type_code, priority,
    status, field_standard_id, work_order_id, process_instance_key,
    creator, updater, tenant_id
) VALUES (
    nextval('mm_corrective_case_seq'),
    'CASE-DEMO-001',
    '1#离心泵异响报修',
    '现场反馈运行异响，需按月检标准排查并处理。',
    1001,
    'pump',
    'HIGH',
    'APPROVED',
    (SELECT id FROM mm_field_work_standard WHERE code = 'STD-PUMP-MONTHLY' AND tenant_id = 1 AND deleted = FALSE ORDER BY id DESC LIMIT 1),
    NULL,
    NULL,
    'demo-seed',
    'demo-seed',
    1
);

-- ========== work_order ==========
SET search_path TO work_order;

DELETE FROM wo_work_order_step_result WHERE work_order_id IN (
    SELECT id FROM wo_work_order WHERE wo_no LIKE 'WO-DEMO-%' AND tenant_id = 1
);
DELETE FROM wo_work_order WHERE wo_no LIKE 'WO-DEMO-%' AND tenant_id = 1;

-- 工单 A：已派发，可点「开工」
WITH std AS (
    SELECT id, version_no, steps_json
    FROM maintenance.mm_field_work_standard
    WHERE code = 'STD-PUMP-MONTHLY' AND tenant_id = 1 AND deleted = FALSE
    ORDER BY id DESC LIMIT 1
),
ins AS (
    INSERT INTO wo_work_order (
        id, wo_no, scope, status, asset_id, asset_type_code, frequency_code,
        standard_id, standard_version_no, standard_snapshot_json,
        runtime_job_id, schedule_slot_id, business_key, assignee_user_id,
        title, creator, updater, tenant_id, corrective_case_id
    )
    SELECT
        nextval('wo_work_order_seq'),
        'WO-DEMO-0001',
        'maintenance',
        'DISPATCHED',
        1001,
        'pump',
        'MONTHLY',
        std.id,
        std.version_no,
        std.steps_json,
        'demo-job-0001',
        'demo-slot-0001',
        'demo-seed-dispatched',
        1,
        '【演示】1#离心泵月度点检（待开工）',
        'demo-seed',
        'demo-seed',
        1,
        NULL
    FROM std
    RETURNING id
)
INSERT INTO wo_work_order_step_result (
    id, work_order_id, step_code, step_order, completed, result_json, creator, updater, tenant_id
)
SELECT nextval('wo_work_order_step_result_seq'), ins.id, s.code, s.ord, FALSE, NULL, 'demo-seed', 'demo-seed', 1
FROM ins
CROSS JOIN (VALUES
    ('STEP-VISUAL', 1),
    ('STEP-VIBE', 2),
    ('STEP-LUBE', 3),
    ('STEP-PHOTO', 4)
) AS s(code, ord);

-- 工单 B：执行中，可直接勾步骤 / 完工
WITH std AS (
    SELECT id, version_no, steps_json
    FROM maintenance.mm_field_work_standard
    WHERE code = 'STD-PUMP-MONTHLY' AND tenant_id = 1 AND deleted = FALSE
    ORDER BY id DESC LIMIT 1
),
ins AS (
    INSERT INTO wo_work_order (
        id, wo_no, scope, status, asset_id, asset_type_code, frequency_code,
        standard_id, standard_version_no, standard_snapshot_json,
        runtime_job_id, schedule_slot_id, business_key, assignee_user_id,
        title, creator, updater, tenant_id, corrective_case_id
    )
    SELECT
        nextval('wo_work_order_seq'),
        'WO-DEMO-0002',
        'maintenance',
        'IN_PROGRESS',
        1001,
        'pump',
        'MONTHLY',
        std.id,
        std.version_no,
        std.steps_json,
        'demo-job-0002',
        'demo-slot-0002',
        'demo-seed-in-progress',
        1,
        '【演示】1#离心泵月度点检（执行中）',
        'demo-seed',
        'demo-seed',
        1,
        NULL
    FROM std
    RETURNING id
)
INSERT INTO wo_work_order_step_result (
    id, work_order_id, step_code, step_order, completed, result_json, creator, updater, tenant_id
)
SELECT nextval('wo_work_order_step_result_seq'), ins.id, s.code, s.ord, FALSE, NULL, 'demo-seed', 'demo-seed', 1
FROM ins
CROSS JOIN (VALUES
    ('STEP-VISUAL', 1),
    ('STEP-VIBE', 2),
    ('STEP-LUBE', 3),
    ('STEP-PHOTO', 4)
) AS s(code, ord);

COMMIT;

-- 摘要
SELECT 'standard' AS kind, id::text, code, status::text FROM maintenance.mm_field_work_standard WHERE code = 'STD-PUMP-MONTHLY' AND tenant_id = 1
UNION ALL
SELECT 'handbook', id::text, code, status::text FROM maintenance.mm_handbook WHERE code = 'HB-PUMP-MONTHLY' AND tenant_id = 1
UNION ALL
SELECT 'binding', id::text, code, status::text FROM maintenance.mm_binding_rule WHERE code = 'BR-PUMP-MONTHLY' AND tenant_id = 1
UNION ALL
SELECT 'calendar', id::text, planned_date::text, status FROM maintenance.mm_calendar_entry WHERE tenant_id = 1 AND handbook_id IN (SELECT id FROM maintenance.mm_handbook WHERE code = 'HB-PUMP-MONTHLY')
UNION ALL
SELECT 'corrective', id::text, case_no, status FROM maintenance.mm_corrective_case WHERE case_no = 'CASE-DEMO-001'
UNION ALL
SELECT 'work_order', id::text, wo_no, status FROM work_order.wo_work_order WHERE wo_no LIKE 'WO-DEMO-%' AND tenant_id = 1
ORDER BY 1, 2;
