-- V132：巡检开跑与上报改走条件策略。平台预置「人点了开始」三连和采集后更新账/步。
SET search_path TO dynamicbusiness, public;

COMMENT ON COLUMN dynamic_condition_strategy.action_code IS
    '已登记动作编码：CREATE_EXECUTION / DISPATCH_TO_DEVICE / UPDATE_EXECUTION_STATUS / UPDATE_STEP_STATUS / APPEND_PROCESS / RAISE_ALARM / SEND_NOTIFICATION';

INSERT INTO dynamic_condition_strategy (
    tenant_id, is_platform, name, enabled, event_type, condition_json, action_code, action_params_json, priority
) VALUES
(
    0, TRUE, '人点了开始就新建这次执行的账', TRUE, 'EXECUTION_START',
    '{"all":[{"type":"ALWAYS"}]}', 'CREATE_EXECUTION', NULL, 10
),
(
    0, TRUE, '人点了开始就把任务发给设备', TRUE, 'EXECUTION_START',
    '{"all":[{"type":"ALWAYS"}]}', 'DISPATCH_TO_DEVICE', NULL, 20
),
(
    0, TRUE, '发给设备后把这次执行标成进行中', TRUE, 'EXECUTION_START',
    '{"all":[{"type":"ALWAYS"}]}', 'UPDATE_EXECUTION_STATUS', '{"executionStatus":"in_progress"}', 30
),
(
    0, TRUE, '采集到某一步结果就更新这一步', TRUE, 'COLLECTION_RECEIVED',
    '{"all":[{"type":"HAS_EXECUTION_RECORD"},{"type":"HAS_STEP_UPDATES"}]}',
    'UPDATE_STEP_STATUS', NULL, 20
),
(
    0, TRUE, '采集到整次状态就更新这次执行', TRUE, 'COLLECTION_RECEIVED',
    '{"all":[{"type":"HAS_EXECUTION_RECORD"},{"type":"HAS_EXECUTION_STATUS"}]}',
    'UPDATE_EXECUTION_STATUS', NULL, 30
);
